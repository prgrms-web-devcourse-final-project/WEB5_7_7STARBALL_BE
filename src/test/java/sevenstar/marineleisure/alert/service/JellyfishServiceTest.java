package sevenstar.marineleisure.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.domain.JellyfishSpecies;
import sevenstar.marineleisure.alert.dto.vo.JellyfishDetailVO;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishVO;
import sevenstar.marineleisure.alert.repository.JellyfishRegionDensityRepository;
import sevenstar.marineleisure.alert.repository.JellyfishSpeciesRepository;
import sevenstar.marineleisure.alert.util.JellyfishCrawler;
import sevenstar.marineleisure.alert.util.JellyfishParser;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

@ExtendWith(MockitoExtension.class)
class JellyfishServiceTest {
	@Mock
	private JellyfishRegionDensityRepository densityRepository;

	@Mock
	private JellyfishSpeciesRepository speciesRepository;

	@Mock
	private JellyfishParser parser;

	@Mock
	private JellyfishCrawler crawler;
	@InjectMocks
	private JellyfishService service;

	private File mockFile;
	private ParsedJellyfishVO parsedJellyfishVO;

	private String species;
	private String regionName;
	private String density;

	@BeforeEach
	void setUp() {
		mockFile = new File("jellyfish_20250703.pdf");

		parsedJellyfishVO = new ParsedJellyfishVO("보름달물해파리", "부산", "고밀도");
	}

	@Test
	@DisplayName("가장 최신 해파리 발생 정보 검색")
	void searchLatestReport_success() {
		//given
		given(densityRepository.findLatestJellyfishDetails())
			.willReturn(List.of(mock(JellyfishDetailVO.class)));

		//when
		List<JellyfishDetailVO> result = service.search();

		//then
		assertEquals(1, result.size());
		verify(densityRepository).findLatestJellyfishDetails();
	}

	@Test
	@DisplayName("해파리 이름으로 종 검색 - 존재할 경우")
	void searchByName_found() {
		//given
		JellyfishSpecies species = JellyfishSpecies.builder().name("보름달물해파리").build();
		given(speciesRepository.findByName("보름달물해파리"))
			.willReturn(Optional.of(species));

		//when
		JellyfishSpecies result = service.searchByName("보름달물해파리");

		//then
		assertNotNull(result);
		assertEquals("보름달물해파리", result.getName());
	}

	@Test
	@DisplayName("보고서 PDF 크롤링 후 DB 저장 - 기존 종")
	void updateLatestReport_existingSpecies() throws IOException {
		LocalDate date = LocalDate.of(2025, 7, 3);

		given(crawler.downloadLastedPdf()).willReturn(mockFile);
		given(parser.extractDateFromFileName(mockFile.getName())).willReturn(date);
		given(parser.parsePdfToJson(mockFile)).willReturn(List.of(parsedJellyfishVO));

		JellyfishSpecies species = JellyfishSpecies.builder()
			.name("보름달물해파리")
			.toxicity(ToxicityLevel.NONE)
			.build();
		given(speciesRepository.findByName(parsedJellyfishVO.species())).willReturn(Optional.of(species));

		service.updateLatestReport();

		verify(densityRepository).save(any(JellyfishRegionDensity.class));
	}

	@Test
	@DisplayName("보고서 PDF 크롤링 후 DB 저장 - 신종 등록")
	void updateLatestReport_newSpecies() throws IOException {
		LocalDate date = LocalDate.of(2025, 7, 3);

		given(crawler.downloadLastedPdf()).willReturn(mockFile);
		given(parser.extractDateFromFileName(mockFile.getName())).willReturn(date);
		given(parser.parsePdfToJson(mockFile)).willReturn(List.of(parsedJellyfishVO));

		given(speciesRepository.findByName(parsedJellyfishVO.species())).willReturn(Optional.empty());
		given(speciesRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

		service.updateLatestReport();

		verify(speciesRepository).save(any(JellyfishSpecies.class));
		verify(densityRepository).save(any(JellyfishRegionDensity.class));
	}
}