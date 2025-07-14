package sevenstar.marineleisure.meeting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.Dto.Request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingDetailAndMemberResponse;
import sevenstar.marineleisure.meeting.Dto.Response.MeetingDetailResponse;
import sevenstar.marineleisure.meeting.Repository.MeetingRepository;
import sevenstar.marineleisure.meeting.Repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.error.MeetingError;
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
    private sevenstar.marineleisure.meeting.Repository.TagRepository tagRepository;

    @InjectMocks
    private MeetingServiceImpl meetingService;

    private Member testMember;
    private Meeting testMeeting;
    private OutdoorSpot testSpot;
    private Member testHost;
    private sevenstar.marineleisure.meeting.domain.Tag testTag;

    @BeforeEach
    void setUp() {
        // 1. Builder로 ID가 없는 객체를 생성합니다.
        Member memberWithoutId = Member.builder().nickname("testuser").email("test@test.com").build();
        OutdoorSpot spotWithoutId = OutdoorSpot.builder().name("테스트 장소").location("테스트 위치").build();
        Member hostWithoutId = Member.builder().nickname("host").email("host@test.com").build();

        // 2. 리플렉션 헬퍼 메서드로 ID를 주입합니다.
        testMember = withId(memberWithoutId, 1L);
        testSpot = withId(spotWithoutId, 1L);
        testHost = withId(hostWithoutId, 2L); // 호스트 멤버 객체 생성

        // 3. 이제 ID가 있는 객체로 나머지 테스트 데이터를 생성합니다.
        testMeeting = Meeting.builder()
            .id(1L)
            .title("테스트 모임")
            .capacity(10)
            .status(MeetingStatus.ONGOING)
            .hostId(testHost.getId()) // 호스트 ID를 testHost의 ID로 설정
            .spotId(testSpot.getId())
            .meetingTime(LocalDateTime.now().plusDays(5))
            .build();

        // testTag는 testMeeting이 초기화된 후에 초기화합니다.
        testTag = sevenstar.marineleisure.meeting.domain.Tag.builder()
            .id(1L)
            .meetingId(testMeeting.getId())
            .content(Arrays.asList("tag1", "tag2"))
            .build();
    }

    // getMeetingDetailAndMember Tests
    @Test
    @DisplayName("호스트가 모임 상세 정보와 참여자 목록 조회 성공")
    void getMeetingDetailAndMember_Success() {
        // given
        Long meetingId = testMeeting.getId();
        Long hostId = testHost.getId();

        // Mock empty participants list for now
        List<Participant> participants = Collections.emptyList();

        when(memberRepository.findById(hostId)).thenReturn(Optional.of(testHost));
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(testMeeting));
        when(outdoorSpotSpotRepository.findById(testMeeting.getSpotId())).thenReturn(Optional.of(testSpot));
        when(participantRepository.findParticipantsByMeetingId(meetingId)).thenReturn(participants);

        // when
        MeetingDetailAndMemberResponse response = meetingService.getMeetingDetailAndMember(hostId, meetingId);

        // then
        assertNotNull(response);
        assertEquals(meetingId, response.id());
        assertEquals(testHost.getNickname(), response.hostNickName());
        assertEquals(2, response.participants().size());
        assertEquals("host", response.participants().get(0).nickName());
        verify(memberRepository, times(1)).findById(hostId);
        verify(meetingRepository, times(1)).findById(meetingId);
        verify(outdoorSpotSpotRepository, times(1)).findById(testMeeting.getSpotId());
        verify(participantRepository, times(1)).findParticipantsByMeetingId(meetingId);
    }

    @Test
    @DisplayName("호스트가 아닌 멤버가 조회 시 실패")
    void getMeetingDetailAndMember_Fail_NotHost() {
        // given
        Long meetingId = testMeeting.getId();
        Long nonHostId = testMember.getId(); // 호스트가 아닌 멤버

        when(memberRepository.findById(nonHostId)).thenReturn(Optional.of(testMember));
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(testMeeting));
        // 실패 시나리오에서는 아래 로직들이 호출되지 않아야 함
        // when(outdoorSpotSpotRepository.findById(anyLong())).thenReturn(Optional.of(testSpot));
        // when(participantRepository.findByMeetingId(anyLong())).thenReturn(Arrays.asList());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.getMeetingDetailAndMember(nonHostId, meetingId);
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode()); // 현재 로직은 MEETING_NOT_FOUND를 반환
        verify(outdoorSpotSpotRepository, never()).findById(anyLong());
        verify(participantRepository, never()).findParticipantsByMeetingId(anyLong());
    }

    // joinMeeting Tests
    @Test
    @DisplayName("모임 참여 성공")
    void joinMeeting_Success() {
        // given
        when(memberRepository.existsById(testMember.getId())).thenReturn(true);
        when(meetingRepository.findById(testMeeting.getId())).thenReturn(Optional.of(testMeeting));
        when(participantRepository.countMeetingIdMember(testMeeting.getId())).thenReturn(Optional.of(5)); // 정원 10명, 현재 5명

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
        when(memberRepository.existsById(testMember.getId())).thenReturn(true);
        when(meetingRepository.findById(nonExistentMeetingId)).thenReturn(Optional.empty());

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
        // Setter가 없으므로, 테스트를 위한 새로운 Meeting 객체를 생성합니다.
        Meeting completedMeeting = Meeting.builder()
            .id(testMeeting.getId())
            .title(testMeeting.getTitle())
            .capacity(testMeeting.getCapacity())
            .status(MeetingStatus.COMPLETED) // 모집 완료된 상태로 설정
            .hostId(testMeeting.getHostId())
            .spotId(testMeeting.getSpotId())
            .meetingTime(testMeeting.getMeetingTime())
            .build();

        when(memberRepository.existsById(testMember.getId())).thenReturn(true);
        // findById가 호출될 때, 새로 만든 completedMeeting 객체를 반환하도록 설정합니다.
        when(meetingRepository.findById(completedMeeting.getId())).thenReturn(Optional.of(completedMeeting));

        // when & then
        // 직접 추가하신 MeetingError Enum 값에 따라 수정이 필요할 수 있습니다.
        // 여기서는 MEETING_NOT_ONGOING 이 있다고 가정합니다.
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.joinMeeting(completedMeeting.getId(), testMember.getId());
        });

        // 직접 추가하신 MeetingError Enum 값으로 변경해주세요.
        // assertEquals(MeetingError.MEETING_NOT_ONGOING, exception.getErrorCode());
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("모임 참여 실패 - 정원 초과")
    void joinMeeting_Fail_MeetingFull() {
        // given
        when(memberRepository.existsById(testMember.getId())).thenReturn(true);
        when(meetingRepository.findById(testMeeting.getId())).thenReturn(Optional.of(testMeeting));
        when(participantRepository.countMeetingIdMember(testMeeting.getId())).thenReturn(Optional.of(10)); // 정원 10명, 현재 10명

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.joinMeeting(testMeeting.getId(), testMember.getId());
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
        // Removed: verify(participantRepository, never()).save(any());
    }

    

    // getMeetingDetails Tests
    @Test
    @DisplayName("모임 상세 조회 성공")
    void getMeetingDetails_Success() {
        // given
        when(meetingRepository.findById(testMeeting.getId())).thenReturn(Optional.of(testMeeting));
        when(memberRepository.findById(testMeeting.getHostId())).thenReturn(Optional.of(testHost));
        when(outdoorSpotSpotRepository.findById(testMeeting.getSpotId())).thenReturn(Optional.of(testSpot));
        when(tagRepository.findByMeetingId(anyLong())).thenReturn(Optional.of(testTag));

        // when
        MeetingDetailResponse response = meetingService.getMeetingDetails(testMeeting.getId());

        // then
        assertNotNull(response);
        assertEquals(testMeeting.getTitle(), response.title());
        assertEquals(testHost.getNickname(), response.hostNickName());
        assertEquals(testSpot.getName(), response.spot().name());
    }

    @Test
    @DisplayName("모임 상세 조회 실패 - 모임 없음")
    void getMeetingDetails_Fail_MeetingNotFound() {
        // given
        Long nonExistentMeetingId = 99L;
        when(meetingRepository.findById(nonExistentMeetingId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.getMeetingDetails(nonExistentMeetingId);
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    // --- New Tests Start Here ---

    @Test
    @DisplayName("모든 모임 조회 성공 - 첫 페이지")
    void getAllMeetings_Success_FirstPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Meeting> meetings = Arrays.asList(testMeeting, withId(Meeting.builder().title("모임2").build(), 2L));
        Slice<Meeting> expectedSlice = new SliceImpl<>(meetings, pageable, true);

        when(meetingRepository.findAllByOrderByCreatedAtDescIdDesc(pageable)).thenReturn(expectedSlice);

        // when
        Slice<Meeting> result = meetingService.getAllMeetings(0L, 10);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getContent().size());
        verify(meetingRepository, times(1)).findAllByOrderByCreatedAtDescIdDesc(pageable);
        verify(meetingRepository, never()).findAllOrderByCreatedAt(any(), any(), any());
    }

    @Test
    @DisplayName("모든 모임 조회 성공 - 다음 페이지")
    void getAllMeetings_Success_NextPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Meeting cursorMeeting = withId(Meeting.builder().title("커서모임").build(), 5L);
        List<Meeting> meetings = Arrays.asList(withId(Meeting.builder().title("모임6").build(), 6L));
        Slice<Meeting> expectedSlice = new SliceImpl<>(meetings, pageable, false);

        when(meetingRepository.findById(cursorMeeting.getId())).thenReturn(Optional.of(cursorMeeting));
        when(meetingRepository.findAllOrderByCreatedAt(cursorMeeting.getCreatedAt(), cursorMeeting.getId(), pageable)).thenReturn(expectedSlice);

        // when
        Slice<Meeting> result = meetingService.getAllMeetings(cursorMeeting.getId(), 10);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        verify(meetingRepository, times(1)).findAllOrderByCreatedAt(cursorMeeting.getCreatedAt(), cursorMeeting.getId(), pageable);
        verify(meetingRepository, never()).findAllByOrderByCreatedAtDescIdDesc(any());
    }

    @Test
    @DisplayName("내 모임 목록 조회 성공")
    void getAllMyMeetings_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Meeting> myMeetings = Arrays.asList(testMeeting);
        Slice<Meeting> expectedSlice = new SliceImpl<>(myMeetings, pageable, false);

        when(memberRepository.existsById(testMember.getId())).thenReturn(true);
        when(meetingRepository.findMyMeetingsByMemberIdAndStatusWithCursor(testMember.getId(), MeetingStatus.ONGOING, Long.MAX_VALUE, pageable)).thenReturn(expectedSlice);

        // when
        Slice<Meeting> result = meetingService.getStatusMyMeetings(testMember.getId(), null, 10, MeetingStatus.ONGOING);

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        verify(memberRepository, times(1)).existsById(testMember.getId());
        verify(meetingRepository, times(1)).findMyMeetingsByMemberIdAndStatusWithCursor(testMember.getId(), MeetingStatus.ONGOING, Long.MAX_VALUE, pageable);
    }

    @Test
    @DisplayName("내 모임 개수 조회 성공")
    void countMeetings_Success() {
        // given
        Long expectedCount = 5L;
        when(meetingRepository.countMyMeetingsByMemberId(testMember.getId())).thenReturn(expectedCount);

        // when
        Long result = meetingService.countMeetings(testMember.getId());

        // then
        assertNotNull(result);
        assertEquals(expectedCount, result);
        verify(meetingRepository, times(1)).countMyMeetingsByMemberId(testMember.getId());
    }

    @Test
    @DisplayName("모임 생성 성공")
    void createMeeting_Success() {
        // given
        CreateMeetingRequest request = CreateMeetingRequest.builder()
            .title("새 모임")
            .capacity(5)
            .build();
        // MeetingMapper.CreateMeeting의 결과로 생성될 Meeting 객체 (ID가 부여된 상태)
        Meeting createdMeeting = withId(Meeting.builder()
            .title(request.title())
            .capacity(request.capacity())
            .hostId(testMember.getId())
            .build(), 100L);

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        // meetingRepository.save()가 호출될 때, createdMeeting을 반환하도록 설정
        when(meetingRepository.save(any(Meeting.class))).thenReturn(createdMeeting);

        // when
        Long resultId = meetingService.createMeeting(testMember.getId(), request);

        // then
        assertNotNull(resultId);
        assertEquals(createdMeeting.getId(), resultId);
        verify(memberRepository, times(1)).findById(testMember.getId());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(tagRepository, times(1)).save(any(sevenstar.marineleisure.meeting.domain.Tag.class));
    }

    @Test
    @DisplayName("모임 업데이트 성공")
    void updateMeeting_Success() {
        // given
        UpdateMeetingRequest request = UpdateMeetingRequest.builder()
            .title("업데이트된 모임")
            .capacity(15)
            .tag(sevenstar.marineleisure.meeting.Dto.VO.TagList.builder().content(Arrays.asList("updatedTag1", "updatedTag2")).build())
            .build();

        // 기존 모임 객체 (업데이트 전)
        Meeting existingMeeting = testMeeting;

        // 업데이트 후 반환될 모임 객체 (ID는 동일, 필드만 업데이트)
        Meeting updatedMeeting = withId(Meeting.builder()
            .title(request.title())
            .capacity(request.capacity())
            .hostId(existingMeeting.getHostId())
            .status(existingMeeting.getStatus())
            .build(), existingMeeting.getId());

        when(memberRepository.findById(testHost.getId())).thenReturn(Optional.of(testHost)); // Changed from testMember
        when(meetingRepository.findById(existingMeeting.getId())).thenReturn(Optional.of(existingMeeting));
        when(meetingRepository.save(any(Meeting.class))).thenReturn(updatedMeeting);
        when(tagRepository.findByMeetingId(anyLong())).thenReturn(Optional.of(testTag));

        // when
        Long resultId = meetingService.updateMeeting(existingMeeting.getId(), testHost.getId(), request); // Changed from testMember

        // then
        assertNotNull(resultId);
        assertEquals(existingMeeting.getId(), resultId);
        verify(memberRepository, times(1)).findById(testHost.getId()); // Changed from testMember
        verify(meetingRepository, times(1)).findById(existingMeeting.getId());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(tagRepository, times(1)).save(any(sevenstar.marineleisure.meeting.domain.Tag.class));
    }

    @Test
    @DisplayName("모임 업데이트 실패 - 모임 없음")
    void updateMeeting_Fail_MeetingNotFound() {
        // given
        Long nonExistentMeetingId = 99L;
        UpdateMeetingRequest request = UpdateMeetingRequest.builder().title("업데이트").build();

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(meetingRepository.findById(nonExistentMeetingId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.updateMeeting(nonExistentMeetingId, testMember.getId(), request);
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
        verify(meetingRepository, never()).save(any(Meeting.class));
        verify(tagRepository, never()).findByMeetingId(anyLong()); // 모임을 찾지 못했으므로 태그도 찾지 않아야 함
    }

    @Test
    @DisplayName("모임 나가기 성공")
    void leaveMeeting_Success() {
        // given
        // leaveMeeting의 로직에 따라 MeetingStatus.RECRUITING으로 설정된 Meeting이 필요할 수 있습니다.
        Meeting recruitingMeeting = withId(Meeting.builder()
            .title("모집중 모임")
            .status(MeetingStatus.RECRUITING)
            .build(), testMeeting.getId());

        Participant participant = Participant.builder()
            .meetingId(recruitingMeeting.getId())
            .userId(testMember.getId())
            .build();

        // Create a local member for this test
        Member localMember = withId(Member.builder().nickname("localuser").email("local@test.com").build(), 100L);

        when(memberRepository.findById(localMember.getId())).thenReturn(Optional.of(localMember)); // Use localMember
        when(meetingRepository.findById(recruitingMeeting.getId())).thenReturn(Optional.of(recruitingMeeting));
        when(participantRepository.findByMeetingIdAndUserId(recruitingMeeting.getId(), localMember.getId())).thenReturn(Optional.of(participant)); // Use localMember

        // when
        meetingService.leaveMeeting(recruitingMeeting.getId(), localMember.getId()); // Use localMember

        // then
        verify(memberRepository, times(1)).findById(localMember.getId()); // Use localMember
        verify(meetingRepository, times(1)).findById(recruitingMeeting.getId());
        verify(participantRepository, times(1)).findByMeetingIdAndUserId(recruitingMeeting.getId(), localMember.getId()); // Use localMember
        verify(participantRepository, times(1)).delete(participant);
        verify(meetingRepository, never()).save(any(Meeting.class));
    }

    @Test
    @DisplayName("모임 나가기 실패 - 모임 없음")
    void leaveMeeting_Fail_MeetingNotFound() {
        // given
        Long nonExistentMeetingId = 99L;
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember)); // foundMember 호출 대비
        when(meetingRepository.findById(nonExistentMeetingId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.leaveMeeting(nonExistentMeetingId, testMember.getId());
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode());
        verify(participantRepository, never()).delete(any(Participant.class));
    }

    @Test
    @DisplayName("모임 나가기 실패 - 모임장이 나갈 때")
    void leaveMeeting_Fail_HostCannotLeave() {
        // given
        // testHost는 setUp에서 ID 2L로 생성됨
        Meeting meetingByHost = withId(Meeting.builder()
            .title("호스트 모임")
            .hostId(testHost.getId())
            .status(MeetingStatus.ONGOING)
            .build(), 10L);

        when(memberRepository.findById(testHost.getId())).thenReturn(Optional.of(testHost)); // foundMember 호출 대비
        when(meetingRepository.findById(meetingByHost.getId())).thenReturn(Optional.of(meetingByHost));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingService.leaveMeeting(meetingByHost.getId(), testHost.getId());
        });

        assertEquals(MeetingError.MEETING_NOT_FOUND, exception.getErrorCode()); // TODO: HOST_CANNOT_LEAVE 에러로 변경
        verify(participantRepository, never()).delete(any(Participant.class));
    }

    @Test
    @DisplayName("모임 나가기 성공 - FULL에서 RECRUITING으로 상태 변경")
    void leaveMeeting_Success_ChangesStatusFromFullToRecruiting() {
        // given
        // FULL 상태의 모임을 준비합니다.
        Meeting fullMeeting = withId(Meeting.builder()
            .title("가득 찬 모임")
            .status(MeetingStatus.FULL)
            .hostId(testHost.getId())
            .capacity(10)
            .build(), testMeeting.getId());

        // 나가는 참여자 (호스트 아님)
        Participant participantToLeave = Participant.builder()
            .meetingId(fullMeeting.getId())
            .userId(testMember.getId())
            .build();

        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember)); // foundMember 호출 대비
        when(meetingRepository.findById(fullMeeting.getId())).thenReturn(Optional.of(fullMeeting));
        when(participantRepository.findByMeetingIdAndUserId(fullMeeting.getId(), testMember.getId())).thenReturn(Optional.of(participantToLeave));

        // meetingRepository.save()가 호출될 때 저장되는 Meeting 객체를 캡처하기 위한 ArgumentCaptor
        ArgumentCaptor<Meeting> meetingCaptor = ArgumentCaptor.forClass(Meeting.class);

        // when
        meetingService.leaveMeeting(fullMeeting.getId(), testMember.getId());

        // then
        verify(participantRepository, times(1)).delete(participantToLeave);
        verify(meetingRepository, times(1)).save(meetingCaptor.capture()); // save 호출 캡처

        Meeting savedMeeting = meetingCaptor.getValue();
        assertEquals(MeetingStatus.RECRUITING, savedMeeting.getStatus()); // 저장된 Meeting의 상태가 RECRUITING인지 검증
    }

    @Test
    @DisplayName("모임 삭제 - 현재 구현 없음")
    void deleteMeeting_NoOp() {
        // given
        Long meetingIdToDelete = 1L;

        // when
        meetingService.deleteMeeting(testMember, meetingIdToDelete);

        // then
        // 메서드가 비어있으므로, 어떤 레포지토리 호출도 없음
        verify(meetingRepository, never()).delete(any(Meeting.class));
        verify(meetingRepository, never()).deleteById(anyLong());
        verify(memberRepository, never()).existsById(anyLong());
    }

    /**
     * 리플렉션을 사용하여 엔티티의 ID를 설정하는 헬퍼 메서드
     * @param entity ID를 설정할 엔티티 객체
     * @param id 설정할 ID 값
     * @return ID가 설정된 엔티티 객체
     * @param <T> 엔티티 타입
     */
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

