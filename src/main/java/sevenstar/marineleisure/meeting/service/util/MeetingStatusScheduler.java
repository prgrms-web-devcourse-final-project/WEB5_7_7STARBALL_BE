package sevenstar.marineleisure.meeting.service.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeetingStatusScheduler {
    
    private final MeetingRepository meetingRepository;
    
    @Scheduled(cron = "0 */10 * * * *") // 10분마다 실행
    @Transactional
    public void updateExpiredMeetingsToCompleted() {
        LocalDateTime now = LocalDateTime.now();
        
        // 미팅 시간이 지났지만 COMPLETED가 아닌 모든 미팅 조회
        List<Meeting> expiredMeetings = meetingRepository.findExpiredMeetingsNotCompleted(now, MeetingStatus.COMPLETED);
        
        if (!expiredMeetings.isEmpty()) {
            log.info("미팅 시간이 지난 {} 개의 미팅을 COMPLETED 상태로 변경합니다.", expiredMeetings.size());
            
            for (Meeting meeting : expiredMeetings) {
                log.debug("미팅 ID: {}, 제목: {}, 미팅시간: {}, 현재상태: {} -> COMPLETED로 변경", 
                    meeting.getId(), meeting.getTitle(), meeting.getMeetingTime(), meeting.getStatus());
                
                meeting.changeStatus(MeetingStatus.COMPLETED);
            }
            
            meetingRepository.saveAll(expiredMeetings);
            log.info("총 {} 개 미팅의 상태를 COMPLETED로 변경 완료", expiredMeetings.size());
        }
    }
}
