package sevenstar.marineleisure.alert.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishData;
import sevenstar.marineleisure.alert.service.JellyfishService;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

/**
 * 해파리 주간보고pdf를 파싱하여 DB에 적재하는 파서입니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JellyfishPdfParser {

	private final JellyfishService service;

	private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})");
	private static final Pattern SPECIES_PATTERN = Pattern.compile("- (.+?)\\((\\d+)%.*?\\):");

	/**
	 *
	 * @param file : PDF파일
	 * @return 파싱된 해파리 데이터 리스트
	 * @throws IOException PDF파싱중 오류 발생시
	 */
	public List<ParsedJellyfishData> parse(File file) throws IOException {
		List<ParsedJellyfishData> parsedDataList = new ArrayList<>();
		try (PDDocument document = Loader.loadPDF(file)) {
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			pdfTextStripper.setStartPage(1);
			pdfTextStripper.setEndPage(1);

			String text = pdfTextStripper.getText(document);
			log.debug("pdf first page text extraction complete");

			LocalDate reportDate = extractReportDate(text);
			log.info("보고서 날짜: {}", reportDate);

			parsedDataList.addAll(parseSection(text, "◇ 대량출현해파리", reportDate, false));

			parsedDataList.addAll(parseSection(text, "◇ 독성해파리", reportDate, true));

			log.info("총 {}개의 해파리 데이터를 파싱.", parsedDataList.size());
		}

		return parsedDataList;
	}

	/**
	 *
	 * @param text PDF텍스트
	 * @param sectionTitle 섹션 제목
	 * @param reportDate 보고서 날짜
	 * @param isToxic 독성해파리 여부
	 * @return 파싱된 해파리 데이터 리스트
	 */
	private List<ParsedJellyfishData> parseSection(String text, String sectionTitle, LocalDate reportDate,
		boolean isToxic) {
		List<ParsedJellyfishData> dataList = new ArrayList<>();
		int sectionStart = text.indexOf(sectionTitle);
		if (sectionStart == -1) {
			log.warn("섹션을 찾을 수 없습니다: {}", sectionTitle);
			return dataList;
		}

		// 파싱할 부분 추출
		int sectionEnd = findNextSection(text, sectionStart + sectionTitle.length());
		String sectionText = text.substring(sectionStart, sectionEnd);

		String[] lines = sectionText.split("\n");
		String currentSpecies = null;
		ToxicityLevel toxicityLevel = null;

		for (String line : lines) {
			line = line.trim();

			Matcher speciesMatcher = SPECIES_PATTERN.matcher(line);
			if (speciesMatcher.find()) {
				currentSpecies = speciesMatcher.group(1).trim();
				if (service.searchByName(currentSpecies) != null) {
					toxicityLevel = service.searchByName(currentSpecies).getToxicity();
				}
				continue;
			}
			if (currentSpecies != null && line.contains("고밀도") || line.contains("저밀도")) {
				List<String> regions = extractRegions(line);
				DensityLevel densityLevel = line.contains("고밀도") ? DensityLevel.HIGH : DensityLevel.LOW;

				for (String region : regions) {
					ParsedJellyfishData data = ParsedJellyfishData.builder()
						.species(currentSpecies)
						.region(region)
						.reportDate(reportDate)
						.densityType(densityLevel)
						.toxicity(toxicityLevel)
						.build();

					dataList.add(data);
					log.debug("데이터 추가: {} - {} ({})", currentSpecies, region, densityLevel);
				}
			}
		}
		return dataList;
	}

	/**
	 * 텍스트에서 지역명들을 추출합니다.
	 * @param line 텍스트 라인
	 * @return 추출된 지역명 리스트
	 */
	private List<String> extractRegions(String line) {
		List<String> regions = new ArrayList<>();

		if (line.startsWith("-")) {
			String regionPart = line.substring(1).trim();

			regionPart = regionPart.replaceAll("(고밀도|저밀도)\\s+출현.*", "").trim();

			if (regionPart.contains(",")) {
				String[] regionArray = regionPart.split(",");
				for (String region : regionArray) {
					regions.add(region.trim());
				}
			} else {
				regions.add(regionPart);
			}
		}

		return regions;
	}

	/**
	 * 다음 섹션의 시작 위치를 찾습니다.
	 * @param text 텍스트
	 * @param startPos 시작 위치
	 * @return 다음 섹션 시작 위치
	 */
	private int findNextSection(String text, int startPos) {
		int nextSection = text.indexOf("◇", startPos);
		int nextMainSection = text.indexOf("■", startPos);

		if (nextSection == -1 && nextMainSection == -1) {
			return text.length();
		}

		if (nextSection == -1) {
			return nextMainSection;
		}

		if (nextMainSection == -1) {
			return nextSection;
		}

		return Math.min(nextSection, nextMainSection);
	}

	/**
	 *
	 * @param text : PDF 텍스트
	 * @return 보고서 날짜
	 */
	private LocalDate extractReportDate(String text) {
		Matcher matcher = DATE_PATTERN.matcher(text);
		if (matcher.find()) {
			try {
				int year = Integer.parseInt(matcher.group(1));
				int month = Integer.parseInt(matcher.group(2));
				int day = Integer.parseInt(matcher.group(3));

				if (year < 100) {
					year += 2000;
				}
				return LocalDate.of(year, month, day);
			} catch (Exception e) {
				log.warn("Error occurred while parse reportDate", e.getMessage());
			}
		}
		return LocalDate.now();
	}
}
