// package sevenstar.marineleisure.alert.util;
//
// import java.io.File;
// import java.io.IOException;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import org.apache.pdfbox.Loader;
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.text.PDFTextStripper;
// import org.springframework.stereotype.Component;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishVO;
//
// /**
//  * 해파리 주간보고pdf를 파싱하여 DB에 적재할수 있도록 ParsedJellusifhData를 만들어 주는 파서입니다.
//  */
// @Component
// @Slf4j
// @RequiredArgsConstructor
// public class JellyfishParser {
//
// 	private final JellyfishExtractor extractor;
// 	private final ObjectMapper objectMapper;
//
// 	public List<ParsedJellyfishVO> parsePdfToJson(File pdfFile) {
// 		// 파일에서 ai호출할 부분 추출
// 		String rawString = extractSummarySection(pdfFile);
//
// 		//추출한 텍스트에서 json 형태로 데이터 정형화후 List<Dto>형태로 반환
// 		return extractor.extractJellyfishData(rawString);
// 	}
//
// 	public String extractSummarySection(File pdfFile) {
// 		try (PDDocument document = Loader.loadPDF(pdfFile)) {
// 			PDFTextStripper stripper = new PDFTextStripper();
// 			stripper.setStartPage(1);
// 			stripper.setEndPage(1);
//
// 			String text = stripper.getText(document);
//
// 			int start = text.indexOf("◇ 대량출현해파리");
// 			int end = text.indexOf("■ 해파리 주간 동향");
//
// 			if (start != -1 && end != -1 && start < end) {
// 				return text.substring(start, end).trim();
// 			}
//
// 			return text;
// 		} catch (IOException e) {
// 			throw new RuntimeException("PDF 읽기 실패", e);
// 		}
// 	}
//
// 	public LocalDate extractDateFromFileName(String name) {
// 		Pattern pattern = Pattern.compile("(\\d{8})");
// 		Matcher matcher = pattern.matcher(name);
//
// 		if (matcher.find()) {
// 			String dateStr = matcher.group(1);
// 			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
// 			return LocalDate.parse(dateStr, formatter);
// 		} else {
// 			throw new IllegalArgumentException("파일 이름에 날짜가 없습니다: " + name);
// 		}
// 	}
// }