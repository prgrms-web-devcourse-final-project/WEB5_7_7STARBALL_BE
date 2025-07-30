package sevenstar.marineleisure.meeting.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.meeting.service.util.MeetingStatusScheduler;

@WebMvcTest(controllers = MeetingSchedulerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Slf4j
class MeetingSchedulerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingStatusScheduler meetingStatusScheduler;

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 만료된 미팅 상태 업데이트 성공")
    void completeExpiredMeetings_Success() throws Exception {
        // given
        doNothing().when(meetingStatusScheduler).updateExpiredMeetingsToCompleted();

        // when & then
        MvcResult mvcResult = mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Object jsonObject = objectMapper.readValue(responseBody, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(jsonObject);

        log.info("Complete Expired Meetings Success Response:");
        log.info("prettyJson == {}", prettyJson);

        verify(meetingStatusScheduler, times(1)).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 스케줄러 실행 중 예외 발생")
    void completeExpiredMeetings_Exception() throws Exception {
        // given
        doThrow(new RuntimeException("Database connection error"))
            .when(meetingStatusScheduler).updateExpiredMeetingsToCompleted();

        // when & then
        MvcResult mvcResult = mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isInternalServerError())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Object jsonObject = objectMapper.readValue(responseBody, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(jsonObject);

        log.info("Complete Expired Meetings Exception Response:");
        log.info("prettyJson == {}", prettyJson);

        verify(meetingStatusScheduler, times(1)).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 여러 번 연속 호출")
    void completeExpiredMeetings_MultipleCalls() throws Exception {
        // given
        doNothing().when(meetingStatusScheduler).updateExpiredMeetingsToCompleted();

        // when & then - 첫 번째 호출
        mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk());

        // when & then - 두 번째 호출
        mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk());

        // when & then - 세 번째 호출
        MvcResult mvcResult = mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Object jsonObject = objectMapper.readValue(responseBody, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(jsonObject);

        log.info("Multiple Calls Response:");
        log.info("prettyJson == {}", prettyJson);

        // 총 3번 호출되었는지 확인
        verify(meetingStatusScheduler, times(3)).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 잘못된 HTTP 메서드 사용")
    void completeExpiredMeetings_WrongHttpMethod() throws Exception {
        // when & then - GET 메서드로 호출 시 405 Method Not Allowed
        mockMvc.perform(
            get("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isMethodNotAllowed());

        // when & then - PUT 메서드로 호출 시 405 Method Not Allowed
        mockMvc.perform(
            put("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isMethodNotAllowed());

        // when & then - DELETE 메서드로 호출 시 405 Method Not Allowed
        mockMvc.perform(
            delete("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isMethodNotAllowed());

        // 스케줄러가 호출되지 않았는지 확인
        verify(meetingStatusScheduler, never()).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 잘못된 URL 경로")
    void completeExpiredMeetings_WrongPath() throws Exception {
        // when & then - 잘못된 경로로 호출 시 404 Not Found
        mockMvc.perform(
            post("/api/admin/meetings/complete-expired-wrong")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound());

        mockMvc.perform(
            post("/api/admin/meeting/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound());

        // 스케줄러가 호출되지 않았는지 확인
        verify(meetingStatusScheduler, never()).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - Content-Type 헤더 테스트")
    void completeExpiredMeetings_ContentType() throws Exception {
        // given
        doNothing().when(meetingStatusScheduler).updateExpiredMeetingsToCompleted();

        // when & then - JSON Content-Type으로 호출
        MvcResult mvcResult = mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Object jsonObject = objectMapper.readValue(responseBody, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(jsonObject);

        log.info("Content-Type Test Response:");
        log.info("prettyJson == {}", prettyJson);

        verify(meetingStatusScheduler, times(1)).updateExpiredMeetingsToCompleted();
    }

    @Test
    @DisplayName("POST /api/admin/meetings/complete-expired - 응답 메시지 확인")
    void completeExpiredMeetings_ResponseMessage() throws Exception {
        // given
        doNothing().when(meetingStatusScheduler).updateExpiredMeetingsToCompleted();

        // when & then
        MvcResult mvcResult = mockMvc.perform(
            post("/api/admin/meetings/complete-expired")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.body").value("만료된 미팅들의 상태가 성공적으로 COMPLETED로 변경되었습니다."))
            .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.info("Response Message Test: {}", responseBody);

        verify(meetingStatusScheduler, times(1)).updateExpiredMeetingsToCompleted();
    }
}