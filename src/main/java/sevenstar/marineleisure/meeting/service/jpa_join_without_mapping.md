## JPA: 연관관계 매핑 없이 JOIN 사용하는 방법

JPA(JPQL)에서는 엔티티 간에 `@ManyToOne`, `@OneToMany` 같은 연관관계 매핑이 설정되어 있지 않아도 `JOIN`을 사용할 수 있습니다. 이를 **"비연관관계 조인(Unrelated Join)"** 또는 **"세타 조인(Theta Join)"**이라고 부르며, `ON` 절을 명시적으로 사용하여 조인 조건을 직접 지정하는 방식입니다.

이는 일반 SQL에서 `JOIN ... ON ...` 구문을 사용하는 것과 매우 유사합니다.

### 두 가지 JOIN 방식 비교

#### 1. 연관관계 조인 (Association Join) - 일반적인 경우

- **전제**: 엔티티 간에 `@ManyToOne` 등의 연관관계 매핑이 **필수**입니다.
- **문법**: `JOIN` 뒤에 엔티티가 가진 **연관 필드명**을 사용합니다. `ON` 절은 JPA가 자동으로 생성합니다.
- **예시**:
  ```java
  // Participant.java
  // @ManyToOne
  // private Meeting meeting;

  // JPQL
  @Query("SELECT p FROM Participant p JOIN p.meeting m")
  ```

#### 2. 비연관관계 조인 (Unrelated Join) - 현재 우리의 경우

- **전제**: 엔티티 간에 연관관계 매핑이 **없어도 됩니다**.
- **문법**: `JOIN` 뒤에 **엔티티 클래스명**을 사용하고, `ON` 절로 **조인 조건을 직접 명시**합니다.
- **예시**:
  ```java
  // Participant.java
  // private Long meetingId; // 매핑 정보 없음

  // JPQL
  @Query("SELECT p.meetingId FROM Participant p JOIN Meeting m ON p.meetingId = m.id")
  ```

### 현재 코드 분석 (`findMeetingIdsByUserIdAndStatusWithCursor`)

우리가 작성한 아래의 JPQL 쿼리는 바로 이 **비연관관계 조인**을 활용한 것입니다.

```java
@Query("SELECT p.meetingId FROM Participant p JOIN Meeting m ON p.meetingId = m.id " +
       "WHERE p.userId = :userId " +
       "AND m.status = :status " +
       "AND m.id < :cursorId " +
       "ORDER BY m.id DESC")
List<Long> findMeetingIdsByUserIdAndStatusWithCursor(...);
```

- `JOIN Meeting m`: `Participant`의 필드(`p.meeting`)가 아닌, `Meeting`이라는 **엔티티 클래스**를 직접 조인 대상으로 지정했습니다.
- `ON p.meetingId = m.id`: `ON` 절을 사용하여 `Participant`의 `meetingId` 필드와 `Meeting`의 `id` 필드가 같은 것을 조인 조건으로 **수동 설정**했습니다.

### 결론

JPA 연관관계 매핑은 객체 그래프 탐색(`participant.getMeeting()`)을 편하게 해주는 기능이지만, 그것이 없다고 해서 두 테이블을 연결할 수 없는 것은 아닙니다.

이처럼 `ON` 절을 명시한 JPQL 조인을 사용하면, 엔티티 설계의 유연성을 유지하면서도 필요한 데이터를 효율적으로 조회할 수 있습니다.
