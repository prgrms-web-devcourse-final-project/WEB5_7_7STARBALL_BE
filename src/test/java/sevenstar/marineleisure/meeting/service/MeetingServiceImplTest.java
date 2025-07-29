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
import sevenstar.marineleisure.meeting.dto.response.GoingMeetingResponse;
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
    private OutdoorSpotRepository outdoorSpotRepository;
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
    @Mock
    private sevenstar.marineleisure.meeting.domain.service.MeetingDomainService meetingDomainService;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Member testMember;
    private Meeting testMeeting;
    private OutdoorSpot testSpot;
    private Member testHost;
    private sevenstar.marineleisure.meeting.domain.Tag testTag;
    private Long meetingId = 1L;
    private Long hostId = 1L;
    private Long nonHostId = 2L;
    private Meeting mockMeeting;

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
            
        mockMeeting = mock(Meeting.class);
    }

    @Test
    @DisplayName("goingMeeting - 정상 케이스: RECRUITING 상태에서 ONGOING으로 변경")
    void goingMeeting_Success_FromRecruiting() {
        // given
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(mockMeeting);
        doNothing().when(meetingValidate).validateHost(mockMeeting, hostId);
        doNothing().when(meetingValidate).validateStatus(mockMeeting);
        when(mockMeeting.getId()).thenReturn(meetingId);

        // when
        GoingMeetingResponse result = meetingService.goingMeeting(meetingId, hostId);

        // then
        assertNotNull(result);
        assertEquals(meetingId, result.meetingId());
        verify(mockMeeting).changeStatus(MeetingStatus.ONGOING);
    }

    @Test
    @DisplayName("goingMeeting - 정상 케이스: FULL 상태에서 ONGOING으로 변경")
    void goingMeeting_Success_FromFull() {
        // given
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(mockMeeting);
        doNothing().when(meetingValidate).validateHost(mockMeeting, hostId);
        doNothing().when(meetingValidate).validateStatus(mockMeeting);
        when(mockMeeting.getId()).thenReturn(meetingId);

        // when
        GoingMeetingResponse result = meetingService.goingMeeting(meetingId, hostId);

        // then
        assertNotNull(result);
        assertEquals(meetingId, result.meetingId());
        verify(mockMeeting).changeStatus(MeetingStatus.ONGOING);
    }

    @Test
    @DisplayName("goingMeeting - 실패: 호스트가 아닌 사용자가 요청")
    void goingMeeting_Fail_NotHost() {
        // given
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(mockMeeting);
        doThrow(new CustomException(MeetingError.MEETING_NOT_HOST))
            .when(meetingValidate).validateHost(mockMeeting, nonHostId);

        // when & then
        CustomException exception = assertThrows(
            CustomException.class,
            () -> meetingService.goingMeeting(meetingId, nonHostId)
        );
        
        assertEquals(MeetingError.MEETING_NOT_HOST, exception.getErrorCode());
        verify(mockMeeting, never()).changeStatus(any());
    }

    @Test
    @DisplayName("goingMeeting - 실패: 이미 COMPLETED 상태인 미팅")
    void goingMeeting_Fail_AlreadyCompleted() {
        // given
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(mockMeeting);
        doNothing().when(meetingValidate).validateHost(mockMeeting, hostId);
        doThrow(new CustomException(MeetingError.CANNOT_CHANGE_GOING_STATUS))
            .when(meetingValidate).validateStatus(mockMeeting);

        // when & then
        CustomException exception = assertThrows(
            CustomException.class,
            () -> meetingService.goingMeeting(meetingId, hostId)
        );
        
        assertEquals(MeetingError.CANNOT_CHANGE_GOING_STATUS, exception.getErrorCode());
        verify(mockMeeting, never()).changeStatus(any());
    }

    @Test
    @DisplayName("goingMeeting - 실패: 이미 ONGOING 상태인 미팅")
    void goingMeeting_Fail_AlreadyOngoing() {
        // given
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(mockMeeting);
        doNothing().when(meetingValidate).validateHost(mockMeeting, hostId);
        doThrow(new CustomException(MeetingError.CANNOT_CHANGE_GOING_STATUS))
            .when(meetingValidate).validateStatus(mockMeeting);

        // when & then
        CustomException exception = assertThrows(
            CustomException.class,
            () -> meetingService.goingMeeting(meetingId, hostId)
        );
        
        assertEquals(MeetingError.CANNOT_CHANGE_GOING_STATUS, exception.getErrorCode());
        verify(mockMeeting, never()).changeStatus(any());
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
        when(spotValidate.foundOutdoorSpot(testMeeting.getSpotId())).thenReturn(testSpot);
        when(participantRepository.findParticipantsByMeetingId(meetingId)).thenReturn(participants);
        doNothing().when(participantValidate).existParticipant(hostId);
        when(memberRepository.findAllById(anyList())).thenReturn(participantMembers);
        when(meetingMapper.toParticipantResponseList(anyList(), anyMap())).thenReturn(participantResponses);
        when(tagValidate.findByMeetingId(meetingId)).thenReturn(java.util.Optional.of(testTag));
        when(meetingMapper.meetingDetailAndMemberResponseMapper(any(), any(), any(), any(), any())).thenReturn(expectedResponse);

        // when
        MeetingDetailAndMemberResponse response = meetingService.getMeetingDetailAndMember(hostId, meetingId);

        // then
        assertNotNull(response);
        assertEquals(meetingId, response.id());
        assertEquals(testHost.getNickname(), response.hostNickName());
        assertEquals(2, response.participants().size());

        verify(memberValidate).foundMember(hostId);
        verify(meetingValidate).foundMeeting(meetingId);
        verify(spotValidate).foundOutdoorSpot(testMeeting.getSpotId());
        verify(participantRepository).findParticipantsByMeetingId(meetingId);
        verify(memberRepository).findAllById(participantUserIds);
        verify(meetingMapper).toParticipantResponseList(participants, participantNicknames);
    }

    @Test
    @DisplayName("호스트가 아닌 멤버가 조회 시 실패")
    void getMeetingDetailAndMember_Fail_NotHost() {
        // given
        Long meetingId = testMeeting.getId();
        Long nonHostId = testMember.getId(); // 호스트가 아닌 멤버

        when(memberValidate.foundMember(nonHostId)).thenReturn(testMember);
        when(meetingValidate.foundMeeting(meetingId)).thenReturn(testMeeting);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            meetingService.getMeetingDetailAndMember(nonHostId, meetingId);
        });

        assertEquals("Only host can access member details", exception.getMessage());
        verify(spotValidate, never()).foundOutdoorSpot(anyLong());
        verify(participantRepository, never()).findParticipantsByMeetingId(anyLong());
    }

    // joinMeeting Tests - MeetingDomainService를 사용하는 실제 구현에 맞춤
    @Test
    @DisplayName("모임 참여 성공")
    void joinMeeting_Success() {
        // given
        doNothing().when(memberValidate).existMember(testMember.getId());
        when(meetingValidate.foundMeeting(testMeeting.getId())).thenReturn(testMeeting);
        when(meetingDomainService.addParticipant(testMeeting.getId(), testMember.getId(), MeetingRole.GUEST))
            .thenReturn(Participant.builder()
                .meetingId(testMeeting.getId())
                .userId(testMember.getId())
                .role(MeetingRole.GUEST)
                .build());

        // when
        Long resultMeetingId = meetingService.joinMeeting(testMeeting.getId(), testMember.getId());

        // then
        assertNotNull(resultMeetingId);
        assertEquals(testMeeting.getId(), resultMeetingId);
        verify(memberValidate).existMember(testMember.getId());
        verify(meetingValidate).foundMeeting(testMeeting.getId());
        verify(meetingDomainService).addParticipant(testMeeting.getId(), testMember.getId(), MeetingRole.GUEST);
        verify(meetingRepository).save(testMeeting);
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
        verify(meetingRepository, never()).save(any());
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
        when(participantRepository.countMeetingId(testMeeting.getId())).thenReturn(Optional.of(5));
        when(meetingMapper.MeetingDetailResponseMapper(testMeeting, testHost, 5, testSpot, testTag))
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

    // getStatusMyMeetings_role Tests
    @Test
    @DisplayName("역할과 상태별 내 모임 조회 성공 - HOST, RECRUITING")
    void getStatusMyMeetings_role_Success_HostRecruiting() {
        // given
        Long memberId = testHost.getId();
        MeetingRole role = MeetingRole.HOST;
        Long cursorId = 0L;
        int size = 10;
        MeetingStatus status = MeetingStatus.RECRUITING;
        Pageable pageable = PageRequest.of(0, size);
        
        List<Meeting> mockMeetings = Arrays.asList(testMeeting);
        Slice<Meeting> mockSlice = new SliceImpl<>(mockMeetings, pageable, false);
        
        doNothing().when(memberValidate).existMember(memberId);
        when(meetingRepository.findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, Long.MAX_VALUE, pageable))
            .thenReturn(mockSlice);
        
        // when
        Slice<Meeting> result = meetingService.getStatusMyMeetings_role(memberId, role, cursorId, size, status);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testMeeting.getId(), result.getContent().get(0).getId());
        assertFalse(result.hasNext());
        
        verify(memberValidate).existMember(memberId);
        verify(meetingRepository).findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, Long.MAX_VALUE, pageable);
    }

    @Test
    @DisplayName("역할과 상태별 내 모임 조회 성공 - GUEST, ONGOING, cursorId 유효값")
    void getStatusMyMeetings_role_Success_GuestOngoingWithCursor() {
        // given
        Long memberId = testMember.getId();
        MeetingRole role = MeetingRole.GUEST;
        Long cursorId = 5L;
        int size = 5;
        MeetingStatus status = MeetingStatus.ONGOING;
        Pageable pageable = PageRequest.of(0, size);
        
        List<Meeting> mockMeetings = Arrays.asList(testMeeting);
        Slice<Meeting> mockSlice = new SliceImpl<>(mockMeetings, pageable, true);
        
        doNothing().when(memberValidate).existMember(memberId);
        when(meetingRepository.findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, cursorId, pageable))
            .thenReturn(mockSlice);
        
        // when
        Slice<Meeting> result = meetingService.getStatusMyMeetings_role(memberId, role, cursorId, size, status);
        
        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.hasNext());
        
        verify(memberValidate).existMember(memberId);
        verify(meetingRepository).findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, cursorId, pageable);
    }

    @Test
    @DisplayName("역할과 상태별 내 모임 조회 - cursorId null일 때 Long.MAX_VALUE로 처리")
    void getStatusMyMeetings_role_Success_NullCursorId() {
        // given
        Long memberId = testHost.getId();
        MeetingRole role = MeetingRole.HOST;
        Long cursorId = null;
        int size = 10;
        MeetingStatus status = MeetingStatus.COMPLETED;
        Pageable pageable = PageRequest.of(0, size);
        
        List<Meeting> mockMeetings = Collections.emptyList();
        Slice<Meeting> mockSlice = new SliceImpl<>(mockMeetings, pageable, false);
        
        doNothing().when(memberValidate).existMember(memberId);
        when(meetingRepository.findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, Long.MAX_VALUE, pageable))
            .thenReturn(mockSlice);
        
        // when
        Slice<Meeting> result = meetingService.getStatusMyMeetings_role(memberId, role, cursorId, size, status);
        
        // then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertFalse(result.hasNext());
        
        verify(memberValidate).existMember(memberId);
        verify(meetingRepository).findMeetingsByParticipantRoleWithCursor(
            memberId, status, role, Long.MAX_VALUE, pageable);
    }

    @Test
    @DisplayName("역할과 상태별 내 모임 조회 실패 - 존재하지 않는 멤버")
    void getStatusMyMeetings_role_Fail_MemberNotFound() {
        // given
        Long nonExistentMemberId = 99L;
        MeetingRole role = MeetingRole.HOST;
        Long cursorId = 0L;
        int size = 10;
        MeetingStatus status = MeetingStatus.RECRUITING;
        
        doThrow(new CustomException(MeetingError.MEETING_MEMBER_NOT_FOUND))
            .when(memberValidate).existMember(nonExistentMemberId);
        
        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.getStatusMyMeetings_role(nonExistentMemberId, role, cursorId, size, status);
        });
        
        assertEquals(MeetingError.MEETING_MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(meetingRepository, never()).findMeetingsByParticipantRoleWithCursor(
            anyLong(), any(), any(), anyLong(), any());
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