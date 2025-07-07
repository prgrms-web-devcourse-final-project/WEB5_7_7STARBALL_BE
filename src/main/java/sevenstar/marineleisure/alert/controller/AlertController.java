package sevenstar.marineleisure.alert.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.service.JellyfishService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {
	private final JellyfishService jellyfishService;
}
