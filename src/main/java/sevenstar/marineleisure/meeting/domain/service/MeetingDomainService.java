package sevenstar.marineleisure.meeting.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.error.ParticipantError;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;

@Service
@RequiredArgsConstructor
public class MeetingDomainService {
    
    private final ParticipantRepository participantRepository;

    private final MeetingRepository meetingRepository;

    public Participant addParticipant(Long meetingId, Long userId, MeetingRole role) {

        Meeting meeting = meetingRepository.findByIdWithLock(meetingId)
            .orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));

        validateForJoining(meeting, userId);


        int currentCount = getCurrentParticipantCount(meetingId);


        if (currentCount >= meeting.getCapacity()) {
            throw new CustomException(MeetingError.MEETING_ALREADY_FULL);
        }


        Participant newParticipant = Participant.builder()
            .meetingId(meetingId)
            .userId(userId)
            .role(role)
            .build();

        Participant savedParticipant = participantRepository.save(newParticipant);


        int finalCount = getCurrentParticipantCount(meetingId);
        if (finalCount >= meeting.getCapacity() && meeting.getStatus() == MeetingStatus.RECRUITING) {
            meeting.changeStatus(MeetingStatus.FULL);
        }

        return savedParticipant;
    }

    public void removeParticipant(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findByIdWithLock(meetingId)
            .orElseThrow(() -> new CustomException(MeetingError.MEETING_NOT_FOUND));

        validateForLeaving(meeting, userId);

        Participant participant = participantRepository.findByMeetingIdAndUserId(meetingId, userId)
            .orElseThrow(() -> new CustomException(ParticipantError.PARTICIPANT_NOT_FOUND));

        participantRepository.delete(participant);

        if (meeting.getStatus() == MeetingStatus.FULL) {
            meeting.changeStatus(MeetingStatus.RECRUITING);
        }
    }



    
    public boolean isParticipating(Long meetingId, Long userId) {
        return participantRepository.existsByMeetingIdAndUserId(meetingId, userId);
    }
    
    public int getCurrentParticipantCount(Long meetingId) {
        return participantRepository.countMeetingId(meetingId).orElse(0);
    }
    
    private void validateForJoining(Meeting meeting, Long userId) {
        if (!meeting.canJoin()) {
            throw new CustomException(MeetingError.MEETING_NOT_RECRUITING);
        }
        
        if (isParticipating(meeting.getId(), userId)) {
            throw new CustomException(ParticipantError.ALREADY_PARTICIPATING);
        }
        
        if (getCurrentParticipantCount(meeting.getId()) >= meeting.getCapacity()) {
            throw new CustomException(MeetingError.MEETING_ALREADY_FULL);
        }
    }
    
    private void validateForLeaving(Meeting meeting, Long userId) {
        if (meeting.isHost(userId)) {
            throw new CustomException(MeetingError.MEETING_NOT_LEAVE_HOST);
        }
        
        if (!meeting.canLeave()) {
            throw new CustomException(MeetingError.CANNOT_LEAVE_COMPLETED_MEETING);
        }
        
        if (!isParticipating(meeting.getId(), userId)) {
            throw new CustomException(ParticipantError.PARTICIPANT_NOT_FOUND);
        }
    }
}