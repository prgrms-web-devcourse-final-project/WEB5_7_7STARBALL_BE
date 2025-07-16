package sevenstar.marineleisure.meeting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.ReflectionUtils;

import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.dto.mapper.MeetingMapper;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.dto.response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.dto.response.ParticipantResponse;
import sevenstar.marineleisure.meeting.dto.vo.TagList;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.validate.MeetingValidate;
import sevenstar.marineleisure.meeting.validate.MemberValidate;
import sevenstar.marineleisure.meeting.validate.ParticipantValidate;
import sevenstar.marineleisure.meeting.validate.SpotValidate;
import sevenstar.marineleisure.meeting.validate.TagValidate;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private OutdoorSpotRepository outdoorSpotSpotRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ParticipantValidate participantValidate;
    @Mock
    private MeetingMapper meetingMapper;
    @Mock
    private MeetingValidate meetingValidate;
    @Mock
    private MemberValidate memberValidate;
    @Mock
    private TagValidate tagValidate;
    @Mock
    private SpotValidate spotValidate;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Member testMember;
    private Meeting testMeeting;
    private OutdoorSpot testSpot;
    private Member testHost;
    private sevenstar.marineleisure.meeting.domain.Tag testTag;

    @BeforeEach
    void setUp() {
        Member memberWithoutId = Member.builder().nickname("testuser").email("test@test.com").build();
        OutdoorSpot spotWithoutId = OutdoorSpot.builder().name("테스트 장소").location("테스트 위치").build();
        Member hostWithoutId = Member.builder().nickname("host").email("host@test.com").build();

        testMember = withId(memberWithoutId, 1L);
        testSpot = withId(spotWithoutId, 1L);
        testHost = withId(hostWithoutId, 2L);

        testMeeting = Meeting.builder()
            .id(1L)
            .title("테스트 모임")
            .capacity(10)
            .status(MeetingStatus.ONGOING)
            .hostId(testHost.getId())
            .spotId(testSpot.getId())
            .meetingTime(LocalDateTime.now().plusDays(5))
            .build();

        testTag = sevenstar.marineleisure.meeting.domain.Tag.builder()
            .id(1L)
            .meetingId(testMeeting.getId())
            .content(Arrays.asList("tag1", "tag2"))
            .build();
    }

    @Test
    @DisplayName("호스트가 모임 상세 정보와 참여자 목록 조회 성공")
    void getMeetingDetailAndMember_Success() {
        // given
        Long meetingId = testMeeting.getId();
        Long hostId = testHost.getId();

        Member guestMember = withId(Member.builder().nickname("guest").email("guest@test.com").build(), 3L);
        Participant hostParticipant = Participant.builder().meetingId(meetingId).userId(hostId).role(MeetingRole.HOST).build();
        Participant guestParticipant = Participant.builder().meetingId(meetingId).userId(guestMember.getId()).role(MeetingRole.GUEST).build();
        List<Participant> participants = Arrays.asList(hostParticipant, guestParticipant);
        List<Long> participantUserIds = Arrays.asList(hostId, guestMember.getId());
        List<Member> participantMembers = Arrays.asList(testHost, guestMember);
        Map<Long, String> participantNicknames = Map.of(hostId, testHost.getNickname(), guestMember.getId(), guestMember.getNickname());

        List<ParticipantResponse> participantResponses = Arrays.asList(
            new ParticipantResponse(hostId, MeetingRole.HOST, testHost.getNickname()),
            new ParticipantResponse(guestMember.getId(), MeetingRole.GUEST, guestMember.getNickname())
        );

        MeetingDetailAndMemberResponse expectedResponse = MeetingDetailAndMemberResponse.builder()
            .id(meetingId)
            .title(testMeeting.getTitle())
            .hostNickName(testHost.getNickname())
            .participants(participantResponses)
            .build();

        when(memberValidate.foundMember(hostId)).thenReturn(testHost);
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(testMeeting);
        doNothing().when(meetingValidate).verifyIsHost(anyLong(), anyLong());
        when(spotValidate.foundOutdoorSpot(testMeeting.getSpotId())).thenReturn(testSpot);
        when(participantRepository.findParticipantsByMeetingId(meetingId)).thenReturn(participants);
        doNothing().when(participantValidate).existParticipant(hostId);
        when(memberRepository.findAllById(anyList())).thenReturn(participantMembers);
        when(meetingMapper.toParticipantResponseList(anyList(), anyMap())).thenReturn(participantResponses);
        //when(meetingMapper.meetingDetailAndMemberResponseMapper(any(), any(), any(), any())).thenReturn(expectedResponse);

        // when
        MeetingDetailAndMemberResponse response = meetingService.getMeetingDetailAndMember(hostId, meetingId);

        // then
        assertNotNull(response);
        assertEquals(meetingId, response.id());
        assertEquals(testHost.getNickname(), response.hostNickName());
        assertEquals(2, response.participants().size());
        assertEquals("host", response.participants().get(0).nickName());

        verify(memberValidate).foundMember(hostId);
        verify(meetingValidate).foundMeeting(meetingId);
        verify(meetingValidate).verifyIsHost(hostId, meetingId);
        verify(spotValidate).foundOutdoorSpot(testMeeting.getSpotId());
        verify(participantRepository).findParticipantsByMeetingId(meetingId);
        verify(memberRepository).findAllById(participantUserIds);
        verify(meetingMapper).toParticipantResponseList(participants, participantNicknames);
        //verify(meetingMapper).meetingDetailAndMemberResponseMapper(testMeeting, testHost, testSpot, participantResponses);
    }

    @Test
    @DisplayName("호스트가 아닌 멤버가 조회 시 실패")
    void getMeetingDetailAndMember_Fail_NotHost() {
        // given
        Long meetingId = testMeeting.getId();
        Long nonHostId = testMember.getId(); // 호스트가 아닌 멤버

        when(memberValidate.foundMember(nonHostId)).thenReturn(testMember);
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(testMeeting);
        doThrow(new CustomException(MeetingError.MEETING_NOT_HOST)).when(meetingValidate).verifyIsHost(nonHostId, meetingId);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.getMeetingDetailAndMember(nonHostId, meetingId);
        });

        assertEquals(MeetingError.MEETING_NOT_HOST, exception.getErrorCode());
        verify(spotValidate, never()).foundOutdoorSpot(anyLong());
        verify(participantRepository, never()).findParticipantsByMeetingId(anyLong());
    }

    // joinMeeting Tests
    @Test
    @DisplayName("모임 참여 성공")
    void joinMeeting_Success() {
        // given
        doNothing().when(memberValidate).existMember(testMember.getId());
        when(meetingValidate.foundMeeting(testMeeting.getId())).thenReturn(testMeeting);
        doNothing().when(meetingValidate).verifyRecruiting(testMeeting);
        doNothing().when(participantValidate).verifyNotAlreadyParticipant(testMember.getId(), testMeeting.getId());
        when(participantValidate.getParticipantCount(testMeeting.getId())).thenReturn(5);
        doNothing().when(meetingValidate).verifyMeetingCount(5, testMeeting);
        when(meetingMapper.saveParticipant(testMember.getId(), testMeeting.getId(), MeetingRole.GUEST)).thenReturn(Participant.builder().build());

        // when
        Long resultMeetingId = meetingService.joinMeeting(testMeeting.getId(), testMember.getId());

        // then
        assertNotNull(resultMeetingId);
        assertEquals(testMeeting.getId(), resultMeetingId);
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @Test
    @DisplayName("모임 참여 실패 - 모임 없음")
    void joinMeeting_Fail_MeetingNotFound() {
        // given
        Long nonExistentMeetingId = 99L;
        doNothing().when(memberValidate).existMember(testMember.getId());
        when(meetingValidate.foundMeeting(nonExistentMeetingId)).thenThrow(new CustomException(MeetingError.MEETING_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.joinMeeting(nonExistentMeetingId, testMember.getId());
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("모임 참여 실패 - 모집 중이 아님")
    void joinMeeting_Fail_NotOngoing() {
        // given
        Meeting completedMeeting = Meeting.builder().status(MeetingStatus.COMPLETED).build();

        doNothing().when(memberValidate).existMember(testMember.getId());
        when(meetingValidate.foundMeeting(completedMeeting.getId())).thenReturn(completedMeeting);
        doThrow(new CustomException(MeetingError.MEETING_NOT_RECRUITING)).when(meetingValidate).verifyRecruiting(completedMeeting);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.joinMeeting(completedMeeting.getId(), testMember.getId());
        });

        assertEquals(MeetingError.MEETING_NOT_RECRUITING, exception.getErrorCode());
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("모임 참여 실패 - 정원 초과")
    void joinMeeting_Fail_MeetingFull() {
        // given
        doNothing().when(memberValidate).existMember(testMember.getId());
        when(meetingValidate.foundMeeting(testMeeting.getId())).thenReturn(testMeeting);
        doNothing().when(meetingValidate).verifyRecruiting(testMeeting);
        doNothing().when(participantValidate).verifyNotAlreadyParticipant(testMember.getId(), testMeeting.getId());
        when(participantValidate.getParticipantCount(testMeeting.getId())).thenReturn(10);
        doThrow(new CustomException(MeetingError.MEETING_ALREADY_FULL)).when(meetingValidate).verifyMeetingCount(10, testMeeting);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.joinMeeting(testMeeting.getId(), testMember.getId());
        });

        assertEquals(MeetingError.MEETING_ALREADY_FULL, exception.getErrorCode());
        verify(participantRepository, never()).save(any());
    }

    // getMeetingDetails Tests
    @Test
    @DisplayName("모임 상세 조회 성공")
    void getMeetingDetails_Success() {
        // given
        when(meetingValidate.foundMeeting(testMeeting.getId())).thenReturn(testMeeting);
        when(memberValidate.foundMember(testMeeting.getHostId())).thenReturn(testHost);
        when(spotValidate.foundOutdoorSpot(testMeeting.getSpotId())).thenReturn(testSpot);
        when(tagValidate.findByMeetingId(anyLong())).thenReturn(Optional.of(testTag));
        when(meetingMapper.MeetingDetailResponseMapper(testMeeting, testHost, testSpot, testTag))
            .thenReturn(MeetingDetailResponse.builder().title(testMeeting.getTitle()).hostNickName(testHost.getNickname()).build());

        // when
        MeetingDetailResponse response = meetingService.getMeetingDetails(testMeeting.getId());

        // then
        assertNotNull(response);
        assertEquals(testMeeting.getTitle(), response.title());
        assertEquals(testHost.getNickname(), response.hostNickName());
    }

    @Test
    @DisplayName("모임 상세 조회 실패 - 모임 없음")
    void getMeetingDetails_Fail_MeetingNotFound() {
        // given
        Long nonExistentMeetingId = 99L;
        when(meetingValidate.foundMeeting(nonExistentMeetingId)).thenThrow(new CustomException(MeetingError.MEETING_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.getMeetingDetails(nonExistentMeetingId);
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    private <T> T withId(T entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            ReflectionUtils.setField(idField, entity, id);
            return entity;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Entity does not have an 'id' field", e);
        }
    }
}
