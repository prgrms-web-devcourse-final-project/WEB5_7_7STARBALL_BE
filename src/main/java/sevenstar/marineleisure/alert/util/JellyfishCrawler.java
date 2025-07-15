// package sevenstar.marineleisure.alert.util;
//
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
//
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// public class JellyfishCrawler {
// 	private final RestTemplate template = new RestTemplate();
// 	private final String siteUrl = "https://www.nifs.go.kr";
// 	private final String boardUrl = siteUrl + "/board/actionBoard0022List.do";
//
// 	/**
// 	 * @return 최신게시글의 첨부파일 pdf객체
// 	 * @throws IOException
// 	 */
// 	public File downloadLastedPdf() throws IOException {
//
// 		Document doc = Jsoup.connect(boardUrl).get();
//
// 		// 첫 게시글 연결
// 		Element firstRow = doc.select("div.board-list table tbody tr").first();
// 		if (firstRow == null) {
// 			log.warn("게시글 행을 찾을수 없습니다.");
// 			return null;
// 		}
//
// 		//	첫 게시글의 첨부파일
// 		Element fileLink = firstRow.selectFirst("td[data-label = 원본] a");
// 		if (fileLink == null) {
// 			log.warn("첨부파일 링크를 찾을수 없습니다.");
// 			return null;
// 		}
// 		String fileUrl = siteUrl + fileLink.attr("href");
// 		log.info("최신 해파리 리포트 pdf 링크 : {}", fileUrl);
//
// 		// 첫 게시글의 업로드 일자 추출
// 		Element dateElement = firstRow.selectFirst("td[data-label = 작성일]");
// 		LocalDate uploadDate = null;
// 		if (dateElement != null) {
// 			try {
// 				uploadDate = LocalDate.parse(dateElement.text().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
// 			} catch (Exception e) {
// 				log.warn("업로드 날짜 파싱 실패: {}", dateElement.text());
// 			}
// 			if (uploadDate == null) {
// 				uploadDate = LocalDate.now(); // fallback
// 			}
// 		}
// 		String formattedDate = uploadDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
// 		String fileName = "jellyfish_" + formattedDate + ".pdf";
//
// 		ResponseEntity<byte[]> response = template.getForEntity(fileUrl, byte[].class);
// 		byte[] pdfBytes = response.getBody();
//
// 		File savedFile = new File(System.getProperty("java.io.tmpdir"), fileName);
// 		Files.write(savedFile.toPath(), pdfBytes);
// 		log.info("PDF 파일 다운로드 완료: {}", savedFile.getAbsolutePath());
//
// 		return savedFile;
// 	}
//
// }
