## 내가 참여한 모임 조회 기능: 최적화 여정

이 문서는 '내가 참여한 모임 목록을 상태별로 조회'하는 기능의 로직을 초기 아이디어부터 최종 최적화 단계까지 발전시켜 나간 과정을 기록합니다.

### 여정 1: 가장 단순한 생각 (메모리 필터링)

- **아이디어**: "일단 내가 참여한 모든 모임을 다 가져와서, 그 다음에 상태별로 골라내면 되지 않을까?"
- **구현 방식**:
  1. `participantRepository.findAllByMember(member)`를 호출해서 내가 참여한 모든 `Participant` 정보를 DB에서 가져온다.
  2. Java Stream API의 `.filter()`를 사용해 `meeting.getStatus()`가 원하는 `meetingStatus`와 같은 것만 추려낸다.
  3. 결과를 수동으로 페이징 처리해서 반환한다.
- **문제점 발견**: 참여한 모임이 1000개인데 '모집중'인 모임 5개만 보고 싶을 때도, 1000개 데이터를 모두 DB에서 읽고, 네트워크로 전송하고, 메모리에 올려야 한다. **매우 비효율적이다.**

---

### 여정 2: DB 필터링으로의 전환 (ID 목록 조회)

- **아이디어**: "비효율을 개선하자. DB에서 처음부터 필터링하자. 그런데 엔티티 매핑이 없네? `JOIN`이 될까?"
- **깨달음 1**: JPA(JPQL)에서는 `@ManyToOne` 같은 연관관계 매핑이 없어도, `JOIN ... ON ...` 구문을 사용하면 **비연관관계 조인**이 가능하다!
- **구현 방식**:
  1. `ParticipantRepository`에 JPQL 쿼리를 작성한다.
     - `SELECT p.meetingId FROM Participant p JOIN Meeting m ON p.meetingId = m.id`
     - `WHERE` 절에 `p.userId`와 **`m.status`** 조건을 모두 넣는다.
  2. 이 쿼리로 조건에 맞는 `meetingId` 목록(`List<Long>`)을 가져온다. (DB 조회 #1)
  3. `meetingRepository.findAllById()`를 사용해 `meetingId` 목록으로 실제 `Meeting` 객체 목록을 가져온다. (DB 조회 #2)
- **문제점 발견**: 로직이 개선되었지만, 여전히 DB를 두 번 호출한다. `JOIN`으로 이미 `Meeting` 테이블에 접근했는데, `meetingId`만 가져와서 다시 `Meeting`을 조회하는 것은 낭비다.

---

### 여정 3: 최종 최적화 (객체 직접 조회)

- **아이디어**: "어차피 `JOIN`을 할 거면, `SELECT` 절에서 `meetingId`가 아니라 `Meeting` 객체 자체를 바로 가져오면 되지 않을까?"
- **깨달음 2**: 그렇다. JPQL의 `SELECT` 절에 엔티티 별칭(예: `m`)을 지정하면, JPA는 해당 엔티티 객체를 모든 필드가 채워진 상태로 조회해준다.
- **최종 구현 방식**:
  1. `MeetingRepository` (또는 `ParticipantRepository`)에 최종 JPQL 쿼리를 작성한다.
     - **`SELECT m FROM Meeting m`**: `p.meetingId`가 아닌, `Meeting` 객체 자체(`m`)를 선택한다.
     - `JOIN Participant p ON m.id = p.meetingId`: `Meeting`을 기준으로 `Participant`를 조인한다.
     - `WHERE` 절에 `p.userId`와 `m.status` 조건을 모두 넣는다.
  2. 이 쿼리 하나로, **단 한 번의 DB 조회**를 통해 우리가 최종적으로 원했던 `Slice<Meeting>`을 바로 얻는다.
  3. 서비스 계층의 코드는 Repository 호출 한 줄과 결과 반환만 남아 극도로 단순해진다.

### 최종 결론

초기 아이디어의 비효율성을 인지하고, JPQL의 `JOIN` 기능을 점진적으로 깊이 이해함으로써, DB 접근을 최소화하고 서비스 로직을 단순화하는 가장 효율적인 코드를 완성할 수 있었습니다.

**최종 코드 예시:**

```java
// MeetingRepository.java
@Query("SELECT m FROM Meeting m JOIN Participant p ON m.id = p.meetingId " +
       "WHERE p.userId = :userId AND m.status = :status AND m.id < :cursorId " +
       "ORDER BY m.id DESC")
Slice<Meeting> findMyMeetingsByUserIdAndStatusWithCursor(...);

// MeetingServiceImpl.java
public Slice<Meeting> getAllMyMeetings(...) {
    // ... pageable, cursorId 처리 ...
    return meetingRepository.findMyMeetingsByUserIdAndStatusWithCursor(...);
}
```
