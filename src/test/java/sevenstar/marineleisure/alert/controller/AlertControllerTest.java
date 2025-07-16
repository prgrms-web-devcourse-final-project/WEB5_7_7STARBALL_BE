package sevenstar.marineleisure.alert.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.databind.ObjectMapper;

import sevenstar.marineleisure.alert.dto.response.JellyfishResponseDto;
import sevenstar.marineleisure.alert.dto.vo.JellyfishDetailVO;
import sevenstar.marineleisure.alert.dto.vo.JellyfishRegionVO;
import sevenstar.marineleisure.alert.dto.vo.JellyfishSpeciesVO;
import sevenstar.marineleisure.alert.mapper.AlertMapper;
import sevenstar.marineleisure.alert.service.JellyfishService;

@WebMvcTest(AlertController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlertControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JellyfishService jellyfishService;

	@MockitoBean
	private AlertMapper alertMapper;

	@Autowired
	private Validator validator;

	@Test
	@DisplayName("해파리 경보를 성공적으로 반환합니다.")
	void sendAlert_Sucess() throws Exception {
		// List<JellyfishDetailVO> items = jellyfishService.search();
		// JellyfishResponseDto result = alertMapper.toResponseDto(items);
		// return BaseResponse.success(result);

		//given
		JellyfishDetailVO mockVO = new JellyfishDetailVO() {
			@Override
			public String getSpecies() {
				return "노무라입깃해파리";
			}

			@Override
			public String getRegion() {
				return "부산";
			}

			@Override
			public String getDensityType() {
				return "LOW";
			}

			@Override
			public String getToxicity() {
				return "HIGH";
			}

			@Override
			public LocalDate getReportDate() {
				return LocalDate.of(2025, 7, 10);
			}
		};

		List<JellyfishDetailVO> voList = List.of(mockVO);

		JellyfishResponseDto responseDto = new JellyfishResponseDto(
			LocalDate.of(2025, 7, 10),
			List.of(new JellyfishRegionVO(
				"부산",
				new JellyfishSpeciesVO(
					"노무라입깃해파리",
					"강독성",   // ToxicityLevel.HIGH.getDescription()
					"저밀도"    // DensityLevel.LOW.getDescription()
				)
			))
		);

		given(jellyfishService.search()).willReturn(voList);
		given(alertMapper.toResponseDto(voList)).willReturn(responseDto);

		//when & then
		mockMvc.perform(get("/alerts/jellyfish"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("Success"))
			.andExpect(jsonPath("$.body.reportDate").value("2025-07-10"))
			.andExpect(jsonPath("$.body.regions[0].regionName").value("부산"))
			.andExpect(jsonPath("$.body.regions[0].species.name").value("노무라입깃해파리"))
			.andExpect(jsonPath("$.body.regions[0].species.toxicity").value("강독성"))
			.andExpect(jsonPath("$.body.regions[0].species.density").value("저밀도"));

	}
}