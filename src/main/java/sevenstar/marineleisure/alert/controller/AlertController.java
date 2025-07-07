package sevenstar.marineleisure.alert.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.mapper.AlertMapper;
import sevenstar.marineleisure.alert.service.JellyfishService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertController {
	private final JellyfishService jellyfishService;
	private final AlertMapper alertMapper;
}
