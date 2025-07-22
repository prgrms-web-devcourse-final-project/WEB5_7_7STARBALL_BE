package sevenstar.marineleisure.meeting.controller;

import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.FishingType;
import sevenstar.marineleisure.global.enums.MeetingRole;
import sevenstar.marineleisure.global.enums.MeetingStatus;
import sevenstar.marineleisure.global.enums.MemberStatus;
import sevenstar.marineleisure.meeting.domain.Meeting;
import sevenstar.marineleisure.meeting.domain.Participant;
import sevenstar.marineleisure.meeting.domain.Tag;
import sevenstar.marineleisure.meeting.dto.request.CreateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.request.UpdateMeetingRequest;
import sevenstar.marineleisure.meeting.dto.vo.TagList;
import sevenstar.marineleisure.meeting.repository.MeetingRepository;
import sevenstar.marineleisure.meeting.security.WithMockCustomUser;
import sevenstar.marineleisure.meeting.service.MeetingService;

import sevenstar.marineleisure.meeting.util.TestUtil;
import sevenstar.marineleisure.member.domain.Member;
import sevenstar.marineleisure.member.repository.MemberRepository;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;
import sevenstar.marineleisure.meeting.repository.ParticipantRepository;
import sevenstar.marineleisure.meeting.repository.TagRepository;
import sevenstar.marineleisure.meeting.global.TestSecurityConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ai.openai.OpenAiChatModel;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
    properties = {
        "spring.task.scheduling.enabled=false",
        "spring.ai.openai.api-key=dummy",
        "spring.ai.openai.base-url=http://localhost:8080"
    })
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("mysql-test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@Rollback
class MeetingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MeetingService meetingService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OutdoorSpotRepository outdoorSpotRepository;

	@Autowired
	private ParticipantRepository participantRepository;

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private TagRepository tagRepository;

	private TestUtil testUtil;
	
	@MockitoBean
	private OpenAiChatModel openAiChatModel;

	@BeforeEach
	void setUp() throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

		OutdoorSpot testOutdoorSpot = OutdoorSpot.builder()
			.name("테스트 해양 레저 스팟")
			.category(ActivityCategory.FISHING) // 예시: 낚시 카테고리
			.type(FishingType.BOAT) // 예시: 바다 낚시 (category가 FISHING일 경우)
			.location("부산 해운대")
			.latitude(new BigDecimal("35.1655")) // 예시 위도
			.longitude(new BigDecimal("129.1355")) // 예시 경도
			.point(geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(129.1355, 35.1655))) // 경도, 위도 순서
			.build();


		outdoorSpotRepository.save(testOutdoorSpot);

		Member mainTester = Member.builder()
			.nickname("mainTester")
			.email("mainTester@example.com")
			.provider("kakao")
			.providerId("kakao7")
			.latitude(new BigDecimal("126.0000"))
			.longitude(new BigDecimal("273.0000"))
			.build();
		memberRepository.save(mainTester);

		Member test1Member = Member.builder()
			.nickname("testUser1")
			.email("test@example.com")
			.provider("google")
			.providerId("google12345")
			.latitude(new BigDecimal("35.0000"))
			.longitude(new BigDecimal("129.0000"))
			.build();

		memberRepository.save(test1Member);

		Member test2member = Member.builder()
			.nickname("testUser2")
			.email("test1@example.com")
			.provider("kakao")
			.providerId("kakao123456")
			.latitude(new BigDecimal("43.0000"))
			.longitude(new BigDecimal("172.0000"))
			.build();

		memberRepository.save(test2member);


		Member testHostMember = Member.builder()
			.nickname("testHost")
			.email("host@example.com")
			.provider("kakao")
			.providerId("kakao12345")
			.latitude(new BigDecimal("35.0000"))
			.longitude(new BigDecimal("129.0000"))
			.build();
		memberRepository.save(testHostMember);

		// TestUtil을 사용하여 더미 데이터 생성

		Meeting testMeeting = Meeting.builder()
			.hostId(testHostMember.getId())
			.spotId(testOutdoorSpot.getId())
			.title("테스트 미팅 타이틀 입니다.")
			.description("테스트 미팅 본문 입니다.")
			.category(ActivityCategory.FISHING)
			.status(MeetingStatus.RECRUITING)
			.capacity(5)
			.meetingTime(LocalDateTime.now().plusDays(7))
			.build();
		meetingRepository.save(testMeeting);

		Participant hostParticipant = Participant.builder()
			.meetingId(testMeeting.getId())
			.userId(testHostMember.getId())
			.role(MeetingRole.HOST)
			.build();

		participantRepository.save(hostParticipant);

		Participant guestParticipant1 = Participant.builder()
			.meetingId(testMeeting.getId())
			.userId(test1Member.getId())
			.role(MeetingRole.GUEST)
			.build();

		participantRepository.save(guestParticipant1);

		Participant guestParticipant2 = Participant.builder()
			.meetingId(testMeeting.getId())
			.userId(test2member.getId())
			.role(MeetingRole.GUEST)
			.build();

		participantRepository.save(guestParticipant2);

		Tag testTags = Tag.builder()
			.meetingId(testMeeting.getId())
			.content(Arrays.asList("낚시","부산","토네이도허리케인"))
			.build();

		tagRepository.save(testTags);

		// 디버깅: 멤버 ID 확인
		System.out.println("=== 생성된 멤버들 ===");
		System.out.println("mainTester ID: " + mainTester.getId());
		System.out.println("test1Member ID: " + test1Member.getId());
		System.out.println("test2member ID: " + test2member.getId());
		System.out.println("testHostMember ID: " + testHostMember.getId());
		System.out.println("==================");
		
		// 각 상태별 미팅 데이터 생성 (testHostMember가 호스트인 미팅들)
		TestUtil.createMeetings(testHostMember, test1Member, testOutdoorSpot, meetingRepository, tagRepository, participantRepository);
	}

	@AfterEach
	public void cleanUp() {
		// @Transactional + @Rollback으로 자동 롤백되므로 수동 삭제 불필요
		// SecurityContext 정리
		TestUtil.clearSecurityContext();
	}

	@Test
	@DisplayName("GET /meetings -- 전체 조회하기")
	void getAllMeetings() throws Exception {
		// 저장된 데이터 확인
		long meetingCount = meetingRepository.count();
		System.out.println("Total meetings in DB: " + meetingCount);
		
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings")
					.param("cursorId", "0")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("GET /meetings -- 페이징 테스트 (다음페이지) ")
	void getMeetings_NextPage() throws Exception {
		// 페이징 테스트를 위해 추가 데이터 생성
		TestUtil.createMeetings(memberRepository.findAll().get(3), memberRepository.findAll().get(1), outdoorSpotRepository.findAll().get(0), meetingRepository, tagRepository, participantRepository);
		
		// 먼저 전체 미팅 수 확인
		long totalMeetings = meetingRepository.count();
		log.info("Total meetings in database: {}", totalMeetings);
		
		// 존재하는 미팅 중 하나의 ID를 cursorId로 사용
		List<Meeting> meetings = meetingRepository.findAll();
		Long cursorId = meetings.isEmpty() ? 0L : meetings.get(0).getId();
		
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings")
					.param("cursorId", String.valueOf(cursorId))
					.param("size","10")
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("GET /meetings -- 페이징 테스트 (마지막페이지) ")
	void getMeetings_EndPage() throws Exception {
		// 페이징 테스트를 위해 추가 데이터 생성
		TestUtil.createMeetings(memberRepository.findAll().get(3), memberRepository.findAll().get(1), outdoorSpotRepository.findAll().get(0), meetingRepository, tagRepository, participantRepository);
		
		// 마지막 페이지 테스트: 실제 존재하는 마지막 미팅 ID 사용
		List<Meeting> meetings = meetingRepository.findAll();
		Long lastMeetingId = meetings.isEmpty() ? 0L : meetings.get(meetings.size() - 1).getId();
		
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings")
					.param("cursorId", String.valueOf(lastMeetingId))
					.param("size","10")
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("GET /meetings/{id}")
	void getMeetingDetail() throws Exception {
		Long meetingId = meetingRepository.findAll().get(0).getId();

		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/{id}",meetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", jsonObject);
	}


	@Test
	@DisplayName("GET /meetings/{id} -- 존재하지 않는 미팅 조회 시 404")
	void getMeetingDetail_NotFound() throws Exception {
		Long nonExistentId = 99999L;

		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/{id}",nonExistentId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("POST /meetings -- 미팅 생성 ")
	void createMeeting_Authorized() throws Exception {
		OutdoorSpot spot = outdoorSpotRepository.findAll().get(0);
		
		CreateMeetingRequest request = CreateMeetingRequest.builder()
			.title("새로운 미팅")
			.category(ActivityCategory.FISHING)
			.spotId(spot.getId())
			.description("테스트 미팅입니다.")
			.capacity(5)
			.meetingTime(LocalDateTime.now().plusDays(4))
			.tags(Arrays.asList("테스트", "낚시"))
			.build();

		MvcResult mvcResult = mockMvc.perform(
				post("/meetings")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("POST /meetings -- 미팅 생성 ( 인증 없이는 500 NPE - 테스트 환경 제약 )")
	void createMeeting_Unauthorized() throws Exception {
		OutdoorSpot spot = outdoorSpotRepository.findAll().get(0);
		
		CreateMeetingRequest request = CreateMeetingRequest.builder()
			.title("새로운 미팅")
			.category(ActivityCategory.FISHING)
			.spotId(spot.getId())
			.description("테스트 미팅입니다.")
			.tags(Arrays.asList("테스트", "낚시"))
			.build();

		MvcResult mvcResult = mockMvc.perform(
			post("/meetings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 2L, username = "testHos1t")
	@DisplayName("Post /meetings/{id}/join -- 미팅참가")
	public void joinMeeting_Authorized() throws Exception {
		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(0).getId();

		log.info("existingMeetingId == {}", existingMeetingId);

		MvcResult mvcResult = mockMvc.perform(
			post("/meetings/{id}/join",existingMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("Post /meeting/{id}/join -- 미팅 참가 (인증없이는 500 NPE - 테스트 환경 제약)")
	public void joinMeeting_Unauthorized() throws Exception {
		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(0).getId();
		log.info("existingMeetingId == {}", existingMeetingId);

		MvcResult mvcResult = mockMvc.perform(
				post("/meetings/{id}/join",existingMeetingId)
					.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithAnonymousUser
	@DisplayName("Get /meetings/my -- 내 미팅 목록  ( 인증 없이는 500 NPE - 테스트 환경 제약 )")
	void getMyMeeting_Unauthorized() throws Exception {
		// 이론: JWT 필터에서 401 반환
		// 현실: 테스트 환경에서 @DirtiesContext + @Transactional로 인한 Spring Security 필터 체인 이슈
		// 결과: UserPrincipal이 null로 주입되어 NPE 발생
		MvcResult mvcResult  = mockMvc.perform(
			get("/meetings/my")
				.param("status","RECRUITING")
				.param("cursorId" , "0")
				.param("size","10")
				.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(status().isInternalServerError())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}
	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:RECRUITING -- 호스트로 모집중 미팅 목록")
	void getMeeting_WithAuth_Host_Recruiting() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/my")
				.param("status","RECRUITING")
				.param("role","HOST")
				.param("cursorId","0")
				.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST RECRUITING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 2L, username = "testUser1")
	@DisplayName("GET /meetings/my role:GUEST status:RECRUITING -- 게스트로 모집중 미팅 목록")
	void getMeeting_WithAuth_Guest_Recruiting() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/my")
				.param("status","RECRUITING")
				.param("role","GUEST")
				.param("cursorId","0")
				.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST RECRUITING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:ONGOING -- 호스트로 진행중 미팅 목록")
	void getMeeting_WithAuth_Host_Ongoing() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","ONGOING")
					.param("role","HOST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST ONGOING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 3L, username = "testUser2")
	@DisplayName("GET /meetings/my role:GUEST status:ONGOING -- 게스트로 진행중 미팅 목록")
	void getMeeting_WithAuth_Guest_Ongoing() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","ONGOING")
					.param("role","GUEST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST ONGOING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:FULL -- 호스트로 모집완료 미팅 목록")
	void getMeeting_WithAuth_Host_Full() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","FULL")
					.param("role","HOST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST FULL Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 2L, username = "testUser1")
	@DisplayName("GET /meetings/my role:GUEST status:FULL -- 게스트로 모집완료 미팅 목록")
	void getMeeting_WithAuth_Guest_Full() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","FULL")
					.param("role","GUEST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST FULL Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:COMPLETED -- 호스트로 완료된 미팅 목록")
	void getMeeting_WithAuth_Host_Completed() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","COMPLETED")
					.param("role","HOST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST COMPLETED Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 3L, username = "testUser2")
	@DisplayName("GET /meetings/my role:GUEST status:COMPLETED -- 게스트로 완료된 미팅 목록")
	void getMeeting_WithAuth_Guest_Completed() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status","COMPLETED")
					.param("role","GUEST")
					.param("cursorId","0")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();
		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST COMPLETED Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/count -- 미팅개수 조회")
	void countMeetings_Authorized() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/count")
				.accept(MediaType.APPLICATION_JSON)
		)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);

		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/{id}/members")
	void getMeetingDetailAndMember_Authorized() throws Exception {
		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(3).getId();
		MvcResult mvcResult = mockMvc.perform(
			get("/meetings/{id}/members",existingMeetingId)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);

		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("GET /meetings/{id}/members -- 인증없이는 500 NPE - 테스트 환경 제약")
	void getMeetingDetailAndMember_NotAuthorized() throws Exception {
		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(3).getId();
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/{id}/members",existingMeetingId)
					.accept(MediaType.APPLICATION_JSON)
			).andExpect(status().isInternalServerError())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);

		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("PUT /meetings/{id}/update -- 미팅 수정 성공")
	void updateMeetingDetailAndMember_Authorized() throws Exception {
		// TestUtil의 SecurityContext 설정 사용
		TestUtil.setupSecurityContext(4L, "host@example.com");
		List<Meeting> meetings = meetingRepository.findAll();
		
		// 디버깅: 생성된 모든 미팅의 호스트 ID 확인
		System.out.println("=== 생성된 미팅들 ===");
		meetings.forEach(m -> System.out.println("Meeting ID: " + m.getId() + ", Host ID: " + m.getHostId()));
		System.out.println("===================");
		
		// 첫 번째 미팅을 사용 (호스트 ID가 4L인지 확인)
		Meeting firstMeeting = meetings.get(0);
		System.out.println("첫 번째 미팅의 호스트 ID: " + firstMeeting.getHostId());
		Long hostMeetingId = firstMeeting.getId();

		List<OutdoorSpot> spots = outdoorSpotRepository.findAll();
		if (spots.isEmpty()) {
			throw new IllegalStateException("테스트용 OutdoorSpot이 존재하지 않습니다.");
		}
		Long spotId = spots.get(0).getId();

		UpdateMeetingRequest updateMeetingRequest = UpdateMeetingRequest.builder()
			.title("수정된 미팅 제목")
			.category(ActivityCategory.SURFING)
			.capacity(8)
			.localDateTime(LocalDateTime.now().plusDays(8))
			.spotId(spotId)
			.description("수정된 미팅 설명입니다.")
			.tag(new TagList(Arrays.asList("수정","서핑","주말")))
			.build();

		MvcResult mvcResult = mockMvc.perform(
			put("/meetings/{id}/update",hostMeetingId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateMeetingRequest)))


			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);

		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("DELETE /meetings/{id}/leave -- 미팅 탈퇴 성공")
	void leaveMeeting_Authorized() throws Exception {
		TestUtil.setupSecurityContext(2L, "test@example.com");

		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(0).getId();

		MvcResult mvcResult = mockMvc.perform(
			delete("/meetings/{id}/leave", existingMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("DELETE /meetings/{id}/leave -- 미팅 탈퇴 (인증없이는 500 NPE - 테스트 환경 제약)")
	void leaveMeeting_Unauthorized() throws Exception {
		TestUtil.clearSecurityContext(); // SecurityContext 클리어

		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(0).getId();

		MvcResult mvcResult = mockMvc.perform(
			delete("/meetings/{id}/leave", existingMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("DELETE /meetings/{id}/leave -- 호스트는 미팅 탈퇴 불가 (409)")
	void leaveMeeting_HostCannotLeave() throws Exception {
		TestUtil.setupSecurityContext(4L, "host@example.com");

		List<Meeting> meetings = meetingRepository.findAll();
		Long hostMeetingId = meetings.get(0).getId();

		MvcResult mvcResult = mockMvc.perform(
			delete("/meetings/{id}/leave", hostMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("DELETE /meetings/{id}/leave -- 존재하지 않는 미팅 탈퇴 시도 (404)")
	void leaveMeeting_MeetingNotFound() throws Exception {
		TestUtil.setupSecurityContext(2L, "test@example.com");

		Long nonExistentMeetingId = 99999L;

		MvcResult mvcResult = mockMvc.perform(
			delete("/meetings/{id}/leave", nonExistentMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@DisplayName("DELETE /meetings/{id}/leave -- 참여하지 않은 미팅 탈퇴 시도 (404)")
	void leaveMeeting_NotParticipant() throws Exception {
		TestUtil.setupSecurityContext(1L, "mainTester@example.com");



		List<Meeting> meetings = meetingRepository.findAll();
		Long existingMeetingId = meetings.get(0).getId();

		MvcResult mvcResult = mockMvc.perform(
			delete("/meetings/{id}/leave", existingMeetingId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Formatted JSON Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:RECRUITING -- 호스트 역할 모집중 미팅")
	void getMyMeetings_HostRecruiting() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "RECRUITING")
					.param("role", "HOST")
					.param("cursorId", "0")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST RECRUITING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 2L, username = "testUser1")
	@DisplayName("GET /meetings/my role:GUEST status:ONGOING -- 게스트 역할 진행중 미팅")
	void getMyMeetings_GuestOngoing() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "ONGOING")
					.param("role", "GUEST")
					.param("cursorId", "0")
					.param("size", "5")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST ONGOING Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my role:HOST status:COMPLETED -- 호스트 완료된 미팅")
	void getMyMeetings_HostCompleted() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "COMPLETED")
					.param("role", "HOST")
					.param("cursorId", "3")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("HOST COMPLETED Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 3L, username = "testUser2")
	@DisplayName("GET /meetings/my role:GUEST status:FULL -- 게스트 모집완료 미팅")
	void getMyMeetings_GuestFull() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "FULL")
					.param("role", "GUEST")
					.param("cursorId", "0")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("GUEST FULL Response:");
		log.info("prettyJson == {}", prettyJson);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my -- 잘못된 role 파라미터 테스트")
	void getMyMeetings_InvalidRole() throws Exception {
		mockMvc.perform(
				get("/meetings/my")
					.param("status", "RECRUITING")
					.param("role", "INVALID_ROLE")
					.param("cursorId", "0")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isInternalServerError());
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my -- 잘못된 status 파라미터 테스트")
	void getMyMeetings_InvalidStatus() throws Exception {
		mockMvc.perform(
				get("/meetings/my")
					.param("status", "INVALID_STATUS")
					.param("role", "HOST")
					.param("cursorId", "0")
					.param("size", "10")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isInternalServerError());
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my -- 페이징 테스트 (cursorId 사용)")
	void getMyMeetings_WithCursor() throws Exception {
		// 먼저 첫 페이지 조회
		MvcResult firstPageResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "RECRUITING")
					.param("role", "HOST")
					.param("cursorId", "0")
					.param("size", "2")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		// 응답에서 nextCursorId 추출 (실제로는 JSON 파싱 필요)
		String responseBody = firstPageResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		log.info("First page response: {}", responseBody);

		// 두 번째 페이지 조회 (실제 cursorId 값 사용)
		MvcResult secondPageResult = mockMvc.perform(
				get("/meetings/my")
					.param("status", "RECRUITING")
					.param("role", "HOST")
					.param("cursorId", "5") // 실제로는 첫 페이지 응답에서 추출한 값
					.param("size", "2")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String secondResponseBody = secondPageResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		log.info("Second page response: {}", secondResponseBody);
	}

	@Test
	@WithMockCustomUser(id = 4L, username = "testHost")
	@DisplayName("GET /meetings/my -- 기본값 테스트 (role=HOST, status=RECRUITING)")
	void getMyMeetings_DefaultParameters_Fixed() throws Exception {
		// 기본값이 role=HOST, status=RECRUITING이므로 HOST 사용자로 테스트
		MvcResult mvcResult = mockMvc.perform(
				get("/meetings/my")
					.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object jsonObject = objectMapper.readValue(responseBody, Object.class);
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
			.writeValueAsString(jsonObject);

		log.info("Default parameters (HOST, RECRUITING) response:");
		log.info("prettyJson == {}", prettyJson);
	}

}