package sevenstar.marineleisure.meeting.service.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;

@ExtendWith(MockitoExtension.class)
class MeetingStatusSchedulerTest {

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private MeetingStatusScheduler meetingStatusScheduler;

    private Meeting expiredMeeting1;
    private Meeting expiredMeeting2;
    private Meeting expiredMeeting3;

    @BeforeEach
    void setUp() {
        LocalDateTime pastTime = LocalDateTime.now().minusHours(2);
        
        expiredMeeting1 = Meeting.builder()
            .id(1L)
            .title("만료된 모집중 미팅")
            .hostId(1L)
            .spotId(1L)
            .status(MeetingStatus.RECRUITING)
            .capacity(5)
            .meetingTime(pastTime)
            .build();

        expiredMeeting2 = Meeting.builder()
            .id(2L)
            .title("만료된 진행중 미팅")
            .hostId(2L)
            .spotId(1L)
            .status(MeetingStatus.ONGOING)
            .capacity(5)
            .meetingTime(pastTime.minusHours(1))
            .build();

        expiredMeeting3 = Meeting.builder()
            .id(3L)
            .title("만료된 모집완료 미팅")
            .hostId(3L)
            .spotId(1L)
            .status(MeetingStatus.FULL)
            .capacity(5)
            .meetingTime(pastTime.minusMinutes(30))
            .build();
    }

    @Test
    @DisplayName("만료된 미팅들을 COMPLETED 상태로 성공적으로 변경")
    void updateExpiredMeetingsToCompleted_Success() {
        // given
        List<Meeting> expiredMeetings = Arrays.asList(expiredMeeting1, expiredMeeting2, expiredMeeting3);
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(expiredMeetings);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        verify(meetingRepository).findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED));
        
        // 각 미팅의 상태가 COMPLETED로 변경되었는지 확인
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        List<Meeting> savedMeetings = meetingCaptor.getValue();
        assertEquals(3, savedMeetings.size());
        assertEquals(MeetingStatus.COMPLETED, savedMeetings.get(0).getStatus());
        assertEquals(MeetingStatus.COMPLETED, savedMeetings.get(1).getStatus());
        assertEquals(MeetingStatus.COMPLETED, savedMeetings.get(2).getStatus());
    }

    @Test
    @DisplayName("만료된 미팅이 없을 때는 아무 작업 수행하지 않음")
    void updateExpiredMeetingsToCompleted_NoExpiredMeetings() {
        // given
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(Collections.emptyList());

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        verify(meetingRepository).findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED));
        verify(meetingRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("단일 만료된 미팅 처리")
    void updateExpiredMeetingsToCompleted_SingleMeeting() {
        // given
        List<Meeting> expiredMeetings = Arrays.asList(expiredMeeting1);
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(expiredMeetings);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        verify(meetingRepository).findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED));
        
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        List<Meeting> savedMeetings = meetingCaptor.getValue();
        assertEquals(1, savedMeetings.size());
        assertEquals(MeetingStatus.COMPLETED, savedMeetings.get(0).getStatus());
        assertEquals(1L, savedMeetings.get(0).getId());
    }

    @Test
    @DisplayName("RECRUITING 상태 미팅이 COMPLETED로 변경")
    void updateExpiredMeetingsToCompleted_RecruitingToCompleted() {
        // given
        List<Meeting> expiredMeetings = Arrays.asList(expiredMeeting1);
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(expiredMeetings);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        Meeting savedMeeting = meetingCaptor.getValue().get(0);
        assertEquals(MeetingStatus.COMPLETED, savedMeeting.getStatus());
        assertEquals("만료된 모집중 미팅", savedMeeting.getTitle());
    }

    @Test
    @DisplayName("ONGOING 상태 미팅이 COMPLETED로 변경")
    void updateExpiredMeetingsToCompleted_OngoingToCompleted() {
        // given
        List<Meeting> expiredMeetings = Arrays.asList(expiredMeeting2);
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(expiredMeetings);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        Meeting savedMeeting = meetingCaptor.getValue().get(0);
        assertEquals(MeetingStatus.COMPLETED, savedMeeting.getStatus());
        assertEquals("만료된 진행중 미팅", savedMeeting.getTitle());
    }

    @Test
    @DisplayName("FULL 상태 미팅이 COMPLETED로 변경")
    void updateExpiredMeetingsToCompleted_FullToCompleted() {
        // given
        List<Meeting> expiredMeetings = Arrays.asList(expiredMeeting3);
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(expiredMeetings);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        Meeting savedMeeting = meetingCaptor.getValue().get(0);
        assertEquals(MeetingStatus.COMPLETED, savedMeeting.getStatus());
        assertEquals("만료된 모집완료 미팅", savedMeeting.getTitle());
    }

    @Test
    @DisplayName("Repository 메서드가 올바른 파라미터로 호출되는지 확인")
    void updateExpiredMeetingsToCompleted_CorrectParameters() {
        // given
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(Collections.emptyList());

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<MeetingStatus> statusCaptor = ArgumentCaptor.forClass(MeetingStatus.class);
        
        verify(meetingRepository).findExpiredMeetingsNotCompleted(timeCaptor.capture(), statusCaptor.capture());
        
        assertEquals(MeetingStatus.COMPLETED, statusCaptor.getValue());
        assertNotNull(timeCaptor.getValue());
        // 현재 시간과 거의 비슷한 시간이 전달되었는지 확인 (5초 이내 차이)
        assertTrue(Math.abs(timeCaptor.getValue().compareTo(LocalDateTime.now())) < 5);
    }

    @Test
    @DisplayName("대량의 만료된 미팅 처리")
    void updateExpiredMeetingsToCompleted_LargeAmount() {
        // given
        List<Meeting> largeMeetingList = Arrays.asList(
            expiredMeeting1, expiredMeeting2, expiredMeeting3,
            expiredMeeting1, expiredMeeting2 // 5개 미팅
        );
        when(meetingRepository.findExpiredMeetingsNotCompleted(any(LocalDateTime.class), eq(MeetingStatus.COMPLETED)))
            .thenReturn(largeMeetingList);

        // when
        meetingStatusScheduler.updateExpiredMeetingsToCompleted();

        // then
        ArgumentCaptor<List<Meeting>> meetingCaptor = ArgumentCaptor.forClass(List.class);
        verify(meetingRepository).saveAll(meetingCaptor.capture());
        
        List<Meeting> savedMeetings = meetingCaptor.getValue();
        assertEquals(5, savedMeetings.size());
        
        // 모든 미팅이 COMPLETED 상태로 변경되었는지 확인
        savedMeetings.forEach(meeting -> 
            assertEquals(MeetingStatus.COMPLETED, meeting.getStatus())
        );
    }
}