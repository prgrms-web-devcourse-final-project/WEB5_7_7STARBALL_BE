## 커서 기반 페이징: 중복 가능한 값으로 정렬 시의 함정과 해결책

커서 기반 페이징(Cursor-based Pagination)은 `id`와 같이 고유(Unique)한 값을 기준으로 할 때 가장 간단하고 효율적입니다. 하지만 `modifiedAt`, `viewCount`처럼 **중복될 가능성이 있는 값**을 정렬 기준으로 사용하면 데이터가 누락되거나 중복되는 심각한 문제가 발생할 수 있습니다.

### 문제 상황: 왜 데이터가 누락될까?

`modifiedAt`으로만 내림차순 정렬한다고 가정해 보겠습니다.

**데이터 예시:**

| id (PK) | modifiedAt         | content |
| :------ | :----------------- | :------ |
| 155L    | `2025-07-08 10:00` | 글 A    |
| 5L      | `2025-07-08 10:00` | 글 B    |
| 10L     | `2025-07-08 10:00` | 글 C    |
| 140L    | `2025-07-08 09:00` | 글 D    |

- **첫 페이지 조회 (size=2)**: `ORDER BY modifiedAt DESC` 쿼리는 `[글 A, 글 B]`를 반환할 수도, `[글 A, 글 C]`를 반환할 수도 있습니다. DB는 `modifiedAt`이 같을 때 `글 B`와 `글 C`의 순서를 보장하지 않기 때문입니다. 마지막 아이템이 `글 B` (modifiedAt=`10:00`)였다고 가정합시다.

- **두 번째 페이지 조회**: `WHERE modifiedAt < '10:00'` 조건으로 조회하면, `modifiedAt`이 `'10:00'`인 `글 C`는 건너뛰고 `'09:00'`인 `글 D`만 조회됩니다. **데이터 누락이 발생합니다.**

### 해결책: 고유성 보장 컬럼 추가 (Composite Key)

이 문제를 해결하려면, 정렬 순서의 **고유성(Uniqueness)**을 보장해야 합니다. 이를 위해 기본 정렬 기준에 더해, 절대로 중복되지 않는 컬럼(보통 Primary Key인 `id`)을 **두 번째 정렬 조건**으로 추가합니다.

1.  **`ORDER BY` 절 수정**: `ORDER BY modifiedAt DESC, id DESC`
    - 주 정렬 기준(`modifiedAt`)이 같을 경우, 보조 정렬 기준(`id`)으로 다시 정렬하여 항상 일관된 순서를 보장합니다.

2.  **`WHERE` 절 수정**: `WHERE` 절도 두 컬럼을 모두 사용하여 비교해야 합니다. 이를 "Seek Method" 또는 "Keyset Pagination"이라고도 부릅니다.

    ```sql
    WHERE (modifiedAt < :cursorModifiedAt) OR (modifiedAt = :cursorModifiedAt AND id < :cursorId)
    ```

    - **해석**: 
      1. 수정 시간이 커서의 수정 시간보다 명확히 **이전**이거나 (`modifiedAt < :cursorModifiedAt`)
      2. 수정 시간은 커서와 **같지만**, id가 커서의 id보다 **작은** 경우 (`modifiedAt = :cursorModifiedAt AND id < :cursorId`)

### 최종 JPQL 쿼리 예시

이 해결책을 JPQL에 적용하면 다음과 같습니다.

```java
@Query("SELECT m FROM Meeting m " +
       "WHERE (m.modifiedAt < :cursorModifiedAt) OR (m.modifiedAt = :cursorModifiedAt AND m.id < :cursorId) " +
       "ORDER BY m.modifiedAt DESC, m.id DESC")
Slice<Meeting> findWithModifiedAtCursor(
    @Param("cursorModifiedAt") LocalDateTime cursorModifiedAt,
    @Param("cursorId") Long cursorId,
    Pageable pageable
);
```

- **첫 페이지 요청 시**: `cursorModifiedAt`에는 현재 시간, `cursorId`에는 `Long.MAX_VALUE`를 전달하여 모든 데이터를 대상으로 조회할 수 있습니다.

### 결론

- **단일 고유 키 정렬 (예: `id`)**: `WHERE id < :cursorId` 로 간단하게 구현할 수 있습니다.
- **중복 가능 키 정렬 (예: `modifiedAt`)**: 반드시 고유 키를 보조 정렬 기준으로 추가하고, `WHERE` 절을 두 키를 모두 비교하는 복합 조건으로 만들어야 데이터의 정합성을 보장할 수 있습니다.
