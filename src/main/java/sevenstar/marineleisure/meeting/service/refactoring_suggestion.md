## `getAllMyMeetings` 메서드 리팩토링 제안

현재의 '모든 참여 정보를 가져와 애플리케이션에서 필터링하는' 방식은 성능 저하의 우려가 있습니다.

아래와 같이 데이터베이스에서 처음부터 필요한 데이터만 조회하도록 로직을 개선하는 것을 권장합니다.

### 1. `MeetingServiceImpl.java` 수정 제안

`switch` 문이나 `if` 문으로 분기할 필요 없이, `meetingStatus`를 Repository 메서드에 파라미터로 직접 전달하여 코드를 간결하게 만들 수 있습니다.

```java
// MeetingServiceImpl.java

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
// ... other imports

@Override
public Slice<Meeting> getAllMyMeetings(Member member, Long cursorId, int size, MeetingStatus meetingStatus) {
    Pageable pageable = PageRequest.of(0, size);
    existMember(member.getId());

    // 커서가 null이거나 0이면 Long의 최댓값을 사용하여 첫 페이지부터 조회하도록 함
    Long currentCursorId = (cursorId == null || cursorId == 0L) ? Long.MAX_VALUE : cursorId;

    // 1. [개선점] Repository에 status와 cursorId를 직접 전달하여 DB에서 필터링된 결과를 바로 받습니다.
    Slice<Participant> participants = participantRepository.findAllByMemberAndMeetingStatusWithCursor(
        member,
        meetingStatus,
        currentCursorId,
        pageable
    );

    // 2. [개선점] 조회 결과(Slice<Participant>)를 최종 반환 타입(Slice<Meeting>)으로 변환하기만 하면 됩니다.
    return participants.map(Participant::getMeeting);
}
```

### 2. `ParticipantRepository.java` 추가 메서드 제안

위 Service 코드가 동작하려면 `ParticipantRepository`에 아래와 같은 JPQL 쿼리 메서드를 추가해야 합니다.

- **JPQL (Java Persistence Query Language):** 엔티티 객체 모델을 기준으로 쿼리를 작성하는 방식입니다.
- **`JOIN`**: `Participant`와 `Meeting` 엔티티를 연결하여 `Meeting`의 `status`를 조건으로 사용할 수 있게 합니다.
- **`WHERE`**: `member`, `meeting.status`, `meeting.id` 세 가지 조건으로 필터링하여 필요한 데이터만 정확히 찾아냅니다.

```java
// ParticipantRepository.java

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// ... other imports

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // ... 기존 메서드들

    /**
     * 특정 멤버가 참여한 모임을 상태(status)별로 커서 기반 페이징하여 조회합니다.
     * @param member 조회할 멤버
     * @param status 조회할 모임의 상태 (RECRUITING, COMPLETED 등)
     * @param cursorId 현재 페이지의 시작점이 될 모임 ID (이 ID보다 작은 값들을 조회)
     * @param pageable 페이지 사이즈 정보
     * @return Participant의 Slice 객체
     */
    @Query("SELECT p FROM Participant p JOIN p.meeting m " +
           "WHERE p.member = :member AND m.status = :status AND m.id < :cursorId " +
           "ORDER BY m.id DESC")
    Slice<Participant> findAllByMemberAndMeetingStatusWithCursor(
        @Param("member") Member member,
        @Param("status") MeetingStatus status,
        @Param("cursorId") Long cursorId,
        Pageable pageable
    );

    // 참고: 멤버가 참여한 모든 모임을 조회하기 위한 기본 메서드 (비효율적인 방식에서 사용)
    List<Participant> findAllByMember(Member member);
}
```
