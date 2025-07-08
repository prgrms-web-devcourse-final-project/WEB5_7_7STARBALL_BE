package sevenstar.marineleisure.alert.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.alert.domain.JellyfishRegionDensity;
import sevenstar.marineleisure.alert.domain.JellyfishSpecies;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishData;
import sevenstar.marineleisure.alert.mapper.AlertMapper;
import sevenstar.marineleisure.alert.repository.JellyfishRegionDensityRepository;
import sevenstar.marineleisure.alert.repository.JellyfishSpeciesRepository;
import sevenstar.marineleisure.alert.util.JellyfishCrawler;
import sevenstar.marineleisure.alert.util.JellyfishPdfParser;

@Slf4j
@Service
@RequiredArgsConstructor
public class JellyfishService implements AlertService<JellyfishRegionDensity> {

	private final JellyfishRegionDensityRepository densityRepository;
	private final JellyfishSpeciesRepository speciesRepository;
	private final JellyfishPdfParser parser;
	private final JellyfishCrawler crawler;
	private final AlertMapper mapper;
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * [GET] /alerts/jellyfish
	 * @return 지역별해파리 발생리스트
	 */
	@Override
	@Transactional(readOnly = true)
	public List<JellyfishRegionDensity> search() {
		return List.of();
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
	 * 웹에서 크롤링 해와 Pdf를 DB에 적재합니다.
	 */
	public void updateLatestReport() {
		try {
			File pdf = crawler.downloadLastedPdf();
			if (pdf == null) {
				log.warn("pdf다운로드 실패");
				return;
			}
			List<ParsedJellyfishData> dataList = parser.parse(pdf);
			if (dataList == null) {
				log.warn("데이터베이스 적재 실패");
				return;
			}
			for (ParsedJellyfishData data : dataList) {
				densityRepository.save(mapper.toRegionDensityEntity(data));
			}
			log.info("총 {}건의 해파리 데이터 저장 완료", dataList.size());

			pdf.delete(); // 임시 파일 삭제
		} catch (IOException e) {
			log.error("해파리 리포트 크롤링 중 오류 발생", e);
		}
	}

}
