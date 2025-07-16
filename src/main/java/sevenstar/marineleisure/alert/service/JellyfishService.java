package sevenstar.marineleisure.alert.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.domain.JellyfishSpecies;
import sevenstar.marineleisure.alert.dto.vo.JellyfishDetailVO;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishVO;
import sevenstar.marineleisure.alert.repository.JellyfishRegionDensityRepository;
import sevenstar.marineleisure.alert.repository.JellyfishSpeciesRepository;
import sevenstar.marineleisure.alert.util.JellyfishCrawler;
import sevenstar.marineleisure.alert.util.JellyfishParser;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

@Slf4j
@Service
@RequiredArgsConstructor
public class JellyfishService implements AlertService<JellyfishDetailVO> {

	private final JellyfishRegionDensityRepository densityRepository;
	private final JellyfishSpeciesRepository speciesRepository;
	private final JellyfishParser parser;
	private final JellyfishCrawler crawler;
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * 가장최신의 지역별 해파리 발생 리스트를 반환합니다.
	 * [GET] /alerts/jellyfish
	 * @return 지역별해파리 발생리스트
	 */
	@Override
	public List<JellyfishDetailVO> search() {
		return densityRepository.findLatestJellyfishDetails();
	}

	/**
	 *
	 * @param name : 이름으로 해파리종의 정보를 찾습니다.
	 * @return 해당 해파리 JellyfishSpecies객체
	 */
	@Transactional(readOnly = true)
	public JellyfishSpecies searchByName(String name) {
		return speciesRepository.findByName(name).orElse(null);
	}

	/**
	 * 웹에서 크롤링 해 Pdf를 DB에 적재합니다.
	 */
	// @Scheduled(cron = "0 0 0 ? * FRI")
	// 금요일 00시에 동작합니다.
	@Transactional
	public void updateLatestReport() {
		try {
			//웹에서 보고서파일 크롤링
			File pdfFile = crawler.downloadLastedPdf();

			//파일 명에서 보고일자 추출
			LocalDate reportDate = parser.extractDateFromFileName(pdfFile.getName());
			log.info("reportDate : {}", reportDate.toString());

			//OpenAI를 통해서 보고서 내용 Dto로 반환
			List<ParsedJellyfishVO> parsedJellyfishVOS = parser.parsePdfToJson(pdfFile);

			//Dto를 이용하여 기존 해파리 목록 검색후, 해파리 지역별 분포 DB에 적재
			for (ParsedJellyfishVO dto : parsedJellyfishVOS) {
				JellyfishSpecies species = searchByName(dto.species());

				//기존 DB에 없는 신종일경우, 새로 등록 후 data.sql에도 구문 추가
				if (species == null) {
					species = JellyfishSpecies.builder()
						.name(dto.species())
						.toxicity(ToxicityLevel.NONE)
						.build();
					speciesRepository.save(species);
					log.info("신종 해파리등록 : {}", dto.species());

					// appendToDataSql(dto.species(), ToxicityLevel.NONE);
				}

				DensityLevel densityLevel = dto.densityType().equals("HIGH") ? DensityLevel.HIGH : DensityLevel.LOW;

				//DB에 적재
				JellyfishRegionDensity regionDensity = JellyfishRegionDensity.builder()
					.regionName(dto.region())
					.reportDate(reportDate)
					.densityType(densityLevel)
					.species(species.getId())
					.build();

				densityRepository.save(regionDensity);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
