# MeetingServiceImpl Code Review and Improvement Suggestions

This document provides a comprehensive review of the `MeetingServiceImpl.java` file, focusing on potential performance bottlenecks (e.g., multiple selects), concurrency issues, and general code quality.

## General Observations

1.  **Error Handling Granularity:** A recurring theme is the use of `MeetingError.MEETING_NOT_FOUND` for various error conditions. This makes it difficult for API consumers (e.g., frontend) to understand the specific reason for a failure, leading to poor user experience and challenging debugging. **Strongly recommend defining and using more specific error codes.**
2.  **Helper Methods (`foundMeeting`, `foundMember`, `existMember`):** These methods centralize common lookup and error-throwing logic, which is good for code reuse. However, their repeated use can contribute to N+1 problems if not carefully managed within a larger query context.
3.  **Concurrency Concerns:** Several methods involve checking counts or statuses and then performing updates. Without explicit locking mechanisms (optimistic or pessimistic) or careful transaction isolation levels, race conditions could lead to inconsistent data (e.g., over-joining a full meeting).
4.  **`TODO` Comments:** Several `TODO` comments indicate areas for future improvement or unresolved questions. Addressing these systematically would enhance code quality.
5.  **Mapper Usage:** The service uses `MeetingMapper` for DTO-to-entity mapping, which is a good practice for separating concerns.

## Method-by-Method Review and Improvement Suggestions

### 1. `getAllMeetings(Long cursorId, int size)`

*   **Current Logic:** Uses cursor-based pagination. Calls `findAllByOrderByCreatedAtDescIdDesc` for the first page and `findAllOrderByCreatedAt` for subsequent pages.
*   **Performance/Concurrency:** Looks generally good for pagination. No obvious N+1 issues here unless `Meeting` entity has lazy-loaded collections that are accessed immediately after fetching.
*   **Improvement Suggestions:**
    *   Ensure `Meeting` entity's lazy-loaded collections (if any) are fetched eagerly or via `JOIN FETCH` in the repository if they are always needed with the `Meeting` object.
    *   Consider adding a `category` parameter if the `TODO` comment implies filtering by category.

### 2. `getMeetingDetails(Long id)`

*   **Current Logic:** Fetches `Meeting`, `Member` (host), `OutdoorSpot`, and `Tag` separately. Then constructs `MeetingDetailResponse`.
*   **Performance (N+1 Problem):** This method performs **multiple separate `SELECT` queries** (one for Meeting, one for Member, one for OutdoorSpot, one for Tag, one for Tag content). This is a classic N+1 problem if these related entities are always needed together.
*   **Improvement Suggestions:**
    *   **Use `JOIN FETCH`:** Modify `MeetingRepository` to fetch `Member` (host) and `OutdoorSpot` eagerly using `JOIN FETCH` in a single query.
    *   **Optimize Tag Fetch:** If `Tag` is always needed with `MeetingDetails`, consider fetching it with `JOIN FETCH` as well, or optimize `tagRepository.findByMeetingId` and `tagRepository.findContentsByMeetingId` to be more efficient if they are separate queries.
    *   **Error Handling:** The `TODO` comments about `errorCode` and `validate` are relevant. Use specific error codes for `MeetingError.MEETING_NOT_FOUND` if any of the lookups fail (e.g., `MEMBER_NOT_FOUND`, `SPOT_NOT_FOUND`, `TAG_NOT_FOUND`).

### 3. `getAllMyMeetings(Member member, Long cursorId, int size, MeetingStatus meetingStatus)`

*   **Current Logic:** Uses cursor-based pagination for a member's meetings by status. Calls `existMember` and `findMyMeetingsByMemberIdAndStatusWithCursor`.
*   **Performance/Concurrency:** Looks generally good for pagination. `existMember` performs an extra `existsById` check; `foundMember` (which uses `findById`) might be more appropriate if the `Member` object is needed later, but `existsById` is fine if only existence is checked.
*   **Improvement Suggestions:**
    *   Similar to `getAllMeetings`, ensure eager fetching for any lazy-loaded collections in `Meeting` if always needed.
    *   The `TODO` comment about `member` validation is relevant. If `existMember` is the only validation, it's fine.

### 4. `countMeetings(Member member)`

*   **Current Logic:** Calls `countMyMeetingsByMemberId`.
*   **Performance/Concurrency:** Efficient, performs a single count query.
*   **Improvement Suggestions:**
    *   The `TODO` comment about `member` validation is relevant. If `member` is guaranteed to exist by upstream logic (e.g., authentication filter), then `existMember` might be redundant here. Otherwise, it's a good check.

### 5. `joinMeeting(Long meetingId, Member member)`

*   **Current Logic:** Checks `existMember`, `foundMeeting`, `meeting.getStatus()`, `countMeetingIdMember`. Saves `Participant`. Throws `CustomException` if meeting is not `ONGOING` or if `targetCount >= meeting.getCapacity()`.
*   **Concurrency Concerns:**
    *   **Race Condition (Capacity Check):** The `targetCount = participantRepository.countMeetingIdMember(meetingId)` and `if (targetCount >= meeting.getCapacity())` check is vulnerable to a race condition. Between `count` and `save`, another user could join, leading to over-joining a full meeting.
    *   **Solution:** Implement pessimistic locking (e.g., `@Lock(LockModeType.PESSIMISTIC_WRITE)` on `meetingRepository.findById` within this transaction) or optimistic locking (versioning). Alternatively, use a database-level unique constraint on `(meetingId, userId)` in `Participant` table and handle the exception.
*   **Error Handling:** Uses `MEETING_NOT_FOUND` for "not ongoing", "participant count error", and "capacity exceeded".
*   **Improvement Suggestions:**
    *   **Address Concurrency:** This is critical for data integrity. Implement proper locking.
    *   **Specific Error Codes:** Use `MEETING_NOT_ONGOING`, `MEETING_FULL` (or `MEETING_CAPACITY_EXCEEDED`), `PARTICIPANT_COUNT_ERROR` (if `orElseThrow` for `countMeetingIdMember` is truly for an error in counting).

### 6. `leaveMeeting(Long meetingId, Member member)`

*   **Current Logic:** Checks `foundMember`, `foundMeeting`, host status, meeting status. Deletes `Participant`. Updates meeting status if it was `FULL`.
*   **Concurrency Concerns:** Less critical than `joinMeeting`, but if `meeting.getStatus()` is checked and then updated, a race condition could occur if multiple users leave simultaneously from a `FULL` meeting.
*   **Error Handling:** Uses `MEETING_NOT_FOUND` for various conditions (host cannot leave, cannot cancel, participant not found).
*   **Improvement Suggestions:**
    *   **Specific Error Codes:** Use `HOST_CANNOT_LEAVE`, `MEETING_STATUS_INVALID_FOR_LEAVE`, `PARTICIPANT_NOT_FOUND`.
    *   **Concurrency:** Consider pessimistic locking on `Meeting` if status updates are critical.

### 7. `createMeeting(Member member, CreateMeetingRequest request)`

*   **Current Logic:** Creates `Meeting` and `Tag`.
*   **Performance/Concurrency:** Looks straightforward. Two saves.
*   **Improvement Suggestions:**
    *   Ensure `request.tags()` is not null or empty if tags are mandatory. Add DTO validation.

### 8. `updateMeeting(Long meetingId, Member member, UpdateMeetingRequest request)`

*   **Current Logic:** Fetches `host`, `targetMeeting`, `targetTag`. Checks host. Updates `Meeting` and `Tag`.
*   **Performance:** Redundant fetches removed (Good!).
*   **Concurrency:** If `targetTag`'s content is updated, and another user updates it concurrently, the last one wins. Less critical for tags usually.
*   **Error Handling:** Uses `MEETING_NOT_FOUND` for unauthorized access and tag not found.
*   **Improvement Suggestions:**
    *   **Specific Error Codes:** Use `UNAUTHORIZED_ACCESS`, `TAG_NOT_FOUND`.
    *   **JPA Mapping for `Tag` content:** As discussed, ensure `meeting.domain.Tag.java`'s `List<String> content` is correctly mapped (e.g., `@ElementCollection`). This is a **critical fix**.
    *   **`UpdateTag` Mapper:** Ensure `MeetingMapper.UpdateTag` correctly updates the existing `targetTag` object rather than creating a new one and relying on `save` to update.

### 9. `deleteMeeting(Member member, Long meetingId)`

*   **Current Logic:** Empty.
*   **Improvement Suggestions:**
    *   Implement logic: Check member existence, meeting existence, host status, meeting status (e.g., cannot delete if ongoing). Perform deletion.
    *   Consider soft delete vs hard delete.

## Conclusion

The `MeetingServiceImpl` has a clear structure and uses good practices like mappers and helper methods. The recent changes to `updateMeeting` have significantly improved its efficiency.

The most critical areas for further improvement are:
1.  **Robust Concurrency Control:** Especially in `joinMeeting` to prevent over-joining.
2.  **Granular Error Handling:** Define and use specific error codes for all failure scenarios to improve API usability and debugging.
3.  **Correct JPA Mapping for `Tag` Entity's `content` field:** This is a potential data integrity issue that needs immediate attention.

Addressing these points will make the service more robust, maintainable, and user-friendly.
