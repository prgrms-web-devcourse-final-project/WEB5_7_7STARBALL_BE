package sevenstar.marineleisure.alert.util;

import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.alert.dto.vo.ParsedJellyfishVo;

@Slf4j
@Component
@RequiredArgsConstructor
public class JellyfishExtractor {

	private final OpenAiChatModel chatModel;
	private final ObjectMapper objectMapper;

	public List<ParsedJellyfishVo> extractJellyfishData(String text) {
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

			// JSON을 DTO 리스트로 파싱
			return objectMapper.readValue(
				jsonResponse,
				new TypeReference<List<ParsedJellyfishVo>>() {
				}
			);

		} catch (Exception e) {
			log.error("데이터 추출 실패", e);
			throw new RuntimeException("데이터 추출 실패", e);
		}
	}
}