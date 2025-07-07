package sevenstar.marineleisure.alert.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.alert.service.JellyfishService;

/**
 * MarineLeisure - AlertController
 * create date:    25. 7. 7.
 * last update:    25. 7. 7.
 * author:  gigol
 * purpose: Controller for alert
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/alert")
public class AlertController {
	private final JellyfishService jellyfishService;
}
