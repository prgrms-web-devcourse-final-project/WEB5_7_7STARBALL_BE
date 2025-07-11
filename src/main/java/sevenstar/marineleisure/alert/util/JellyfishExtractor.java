package sevenstar.marineleisure.alert.util;

import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishVO;

@Slf4j
@Component
@RequiredArgsConstructor
public class JellyfishExtractor {

	private final OpenAiChatModel chatModel;
	private final ObjectMapper objectMapper;

	public List<ParsedJellyfishVO> extractJellyfishData(String text) {
		try {
			String instruction = """
				다음은 해파리 주간 보고서의 일부입니다.
				
				대량출현해파리 및 독성해파리 항목을 보고, 종 이름, 출현 지역, 밀도를 다음 JSON 배열 형식으로 반환해주세요.
				
				형식:
				[
				  {
				   "species": "보름달물해파리",
				   "region": "부산",
				   "densityType": "HIGH"
				  }
				]
				
				규칙:
				- 한 종이 여러 지역에 나타나면, 지역마다 별도 객체로 나눠주세요.
				- densityType은 고밀도 → HIGH, 저밀도 → LOW
				텍스트:
				""" + text;

			Prompt prompt = new Prompt(instruction);
			String jsonResponse = chatModel.call(prompt).getResult().getOutput().getText();

			log.info("AI Response: {}", jsonResponse);

			// AI 응답에서 JSON 배열만 추출 (간단 정규식 예시)
			int start = jsonResponse.indexOf('[');
			int end = jsonResponse.lastIndexOf(']');

			if (start == -1 || end == -1) {
				log.error("JSON 배열이 응답에서 발견되지 않았습니다.");
				return List.of();
			}

			String jsonArrayOnly = jsonResponse.substring(start, end + 1);

			return objectMapper.readValue(
				jsonArrayOnly,
				new TypeReference<List<ParsedJellyfishVO>>() {
				}
			);

		} catch (Exception e) {
			log.error("pdf에서 AI를 통해 JSON으로 파싱하는 도중 에러가 발생하였습니다.", e);

			// 무료 모델에서 응답이 불안정할 수 있으므로 빈 리스트 반환
			return List.of();
		}
	}
}