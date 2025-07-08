package sevenstar.marineleisure.alert.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import sevenstar.marineleisure.alert.domain.JellyfishSpecies;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishData;
import sevenstar.marineleisure.alert.repository.JellyfishSpeciesRepository;
import sevenstar.marineleisure.global.enums.DensityLevel;
import sevenstar.marineleisure.global.enums.ToxicityLevel;

/**
 * 해파리 주간보고pdf를 파싱하여 DB에 적재할수 있도록 ParsedJellusifhData를 만들어 주는 파서입니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JellyfishPdfParser {

	private static final Pattern SPECIES_PATTERN =
		Pattern.compile("-\\s*(.+?)\\([^\\)]*%[^\\)]*\\):");
	private final JellyfishSpeciesRepository repository;

	/**
	 *
	 * @param file : PDF파일
	 * @return 파싱된 해파리 데이터 리스트
	 * @throws IOException PDF파싱중 오류 발생시
	 */
	public List<ParsedJellyfishData> parse(File file) throws IOException {
		List<ParsedJellyfishData> parsedDataList = new ArrayList<>();
		try (PDDocument document = Loader.loadPDF(file)) {

			LocalDate reportDate = extractReportDate(file);
			log.info("보고서 날짜: {}", reportDate);

			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			pdfTextStripper.setStartPage(1);
			pdfTextStripper.setEndPage(1);

			String text = pdfTextStripper.getText(document);
			log.debug("pdf first page text extraction complete");

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
		Long currentSpeciesId = 0L;
		DensityLevel currentDensity = null;
		List<String> regionBuffer = new ArrayList<>();

		for (String rawLine : lines) {
			String line = rawLine.trim();

			// 1. 새로운 해파리 종 시작
			if (line.startsWith("-")) {
				// 이전 buffer 처리
				if (currentSpecies != null && !regionBuffer.isEmpty() && currentDensity != null) {
					for (String region : regionBuffer) {
						dataList.add(ParsedJellyfishData.builder()
							.species(currentSpecies)
							.speciesId(currentSpeciesId)
							.toxicity(toxicityLevel)
							.reportDate(reportDate)
							.region(region)
							.densityType(currentDensity)
							.build());
					}
				}
				regionBuffer.clear();
				currentDensity = null;

				// 종 추출
				Matcher matcher = SPECIES_PATTERN.matcher(line);
				if (matcher.find()) {
					currentSpecies = matcher.group(1).trim();
					JellyfishSpecies found = repository.findByName(currentSpecies).orElse(null);
					if (found != null) {
						toxicityLevel = found.getToxicity();
						currentSpeciesId = found.getId();
					} else {
						log.warn("해파리 종 정보 없음: {}", currentSpecies);
						currentSpecies = null;
						currentSpeciesId = null;
					}
				}
				continue;
			}

			// 2. '/' → 다음 밀도 섹션 시작
			if (line.contains("/")) {
				// '/' 기준으로 밀도별 블록 분리
				String[] parts = line.split("/");
				for (String part : parts) {
					currentDensity = extractDensity(part);
					List<String> regions = extractRegions(part);
					for (String region : regions) {
						dataList.add(ParsedJellyfishData.builder()
							.species(currentSpecies)
							.speciesId(currentSpeciesId)
							.toxicity(toxicityLevel)
							.reportDate(reportDate)
							.region(region)
							.densityType(currentDensity)
							.build());
					}
				}
				regionBuffer.clear();
				currentDensity = null;
				continue;
			}

			// 3. 밀도 있는 라인 → 바로 저장
			if (line.contains("고밀도") || line.contains("저밀도")) {
				currentDensity = extractDensity(line);
				List<String> regions = extractRegions(line);
				for (String region : regions) {
					dataList.add(ParsedJellyfishData.builder()
						.species(currentSpecies)
						.speciesId(currentSpeciesId)
						.toxicity(toxicityLevel)
						.reportDate(reportDate)
						.region(region)
						.densityType(currentDensity)
						.build());
				}
				continue;
			}

			// 4. 밀도 언급 없는 경우 → buffer에 지역 추가
			if (!line.isBlank()) {
				List<String> regions = extractRegions(line);
				regionBuffer.addAll(regions);
			}
		}

		return dataList;
	}

	/**
	 * 텍스트에서 지역명들을 추출합니다.
	 * @param text 텍스트 라인
	 * @return 추출된 지역명 리스트
	 */
	private List<String> extractRegions(String text) {
		text = text.replaceAll("(고밀도|저밀도)\\s*출현", "")
			.replaceAll("[/]", "") // 혹시 남아 있으면 제거
			.trim();
		String[] raw = text.split(",");
		List<String> result = new ArrayList<>();
		for (String region : raw) {
			if (!region.isBlank())
				result.add(region.trim());
		}
		return result;
	}

	private DensityLevel extractDensity(String text) {
		if (text.contains("고밀도"))
			return DensityLevel.HIGH;
		if (text.contains("저밀도"))
			return DensityLevel.LOW;
		return null;
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
	 * @param file : PDF 파일
	 * @return 보고서 날짜
	 */
	private LocalDate extractReportDate(File file) {
		String fileName = file.getName(); // 예: jellyfish_20250703.pdf
		Pattern filenamePattern = Pattern.compile("jellyfish_(\\d{8})\\.pdf");
		Matcher matcher = filenamePattern.matcher(fileName);

		if (matcher.find()) {
			String dateStr = matcher.group(1); // 20250703
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				return LocalDate.parse(dateStr, formatter);
			} catch (Exception e) {
				log.warn("파일명에서 날짜 파싱 실패: {}", e.getMessage());
			}
		}

		return LocalDate.now(); // fallback
	}
}
