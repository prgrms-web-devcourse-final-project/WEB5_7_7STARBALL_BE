package sevenstar.marineleisure.meeting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.domain.BaseResponse;
import sevenstar.marineleisure.meeting.service.util.MeetingStatusScheduler;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class MeetingSchedulerController {
    
    private final MeetingStatusScheduler meetingStatusScheduler;
    
    @PostMapping("/meetings/complete-expired")
    public ResponseEntity<BaseResponse<String>> completeExpiredMeetings() {
        try {
            log.info("관리자에 의한 수동 미팅 상태 업데이트 요청");
            meetingStatusScheduler.updateExpiredMeetingsToCompleted();
            
            return BaseResponse.success(HttpStatus.OK, "만료된 미팅들의 상태가 성공적으로 COMPLETED로 변경되었습니다.");
        } catch (Exception e) {
            log.error("미팅 상태 업데이트 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(500, "미팅 상태 업데이트 중 오류가 발생했습니다.", null));
        }
    }
}