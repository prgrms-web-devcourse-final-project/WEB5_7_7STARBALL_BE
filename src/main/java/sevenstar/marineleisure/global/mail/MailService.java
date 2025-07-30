package sevenstar.marineleisure.global.mail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sevenstar.marineleisure.favorite.repository.FavoriteRepository;
import sevenstar.marineleisure.global.enums.ActivityCategory;
import sevenstar.marineleisure.global.enums.TotalIndex;
import sevenstar.marineleisure.spot.dto.EmailContent;
import sevenstar.marineleisure.spot.dto.detail.provider.ActivityProvider;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
	private static final String MESSAGE_SUBJECT = "[MarineLeisure] 즐겨찾기한 스팟이 최상의 컨디션이에요!";

	private final JavaMailSender javaMailSender;
	private final FavoriteRepository favoriteRepository;
	private final List<ActivityProvider> providers;

	public void sendMail(String to, String subject, String htmlContent) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom("your_email@gmail.com");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true); // true = HTML

			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			log.error("메일 전송 실패", e);
		}
	}


	public void sendMailToHaveFavoriteBestSpot(LocalDate date) {
		TotalIndex totalIndex = TotalIndex.VERY_GOOD;
		List<EmailContent> emailContents = new ArrayList<>();
		for (ActivityProvider provider : providers) {
			emailContents.addAll(provider.findEmailContent(totalIndex, date));
		}
		Map<String, Map<ActivityCategory, Set<String>>> result = new HashMap<>();
		for (EmailContent emailContent : emailContents) {
			List<String> emails = favoriteRepository.findEmailByFavoriteBestSpot(emailContent.spotId());
			for (String email : emails) {
				if (result.containsKey(email)) {
					result.get(email).get(emailContent.category()).add(emailContent.spotName());
				} else {
					Map<ActivityCategory, Set<String>> map = new EnumMap<>(ActivityCategory.class);
					for (ActivityCategory value : ActivityCategory.values()) {
						map.put(value, new HashSet<>());
					}
					map.get(emailContent.category()).add(emailContent.spotName());
					result.put(email, map);
				}
			}
		}
		for (Map.Entry<String, Map<ActivityCategory, Set<String>>> entry : result.entrySet()) {
			sendMail(entry.getKey(), MESSAGE_SUBJECT, transformEmailContent(entry.getValue()));
		}
	}

	// private String transformEmailContent(Map<ActivityCategory, Set<String>> map) {
	// 	StringBuilder sb = new StringBuilder();
	// 	sb.append("<div style='font-family: Arial, sans-serif; font-size: 14px;'>");
	// 	sb.append("<p>안녕하세요, <strong>MarineLeisure</strong>입니다 🌊</p>");
	// 	sb.append("<p>고객님이 즐겨찾기한 장소 중, 오늘 같은 날 <strong>최상의 컨디션</strong>을 보이는 스팟들을 추천드립니다.</p>");
	//
	// 	sb.append("<ul>");
	// 	for (ActivityCategory category : ActivityCategory.values()) {
	// 		Set<String> spots = map.getOrDefault(category, Set.of());
	// 		String spotList = spots.isEmpty() ? "없어요 😢" : String.join(", ", spots);
	// 		sb.append("<li><strong>")
	// 			.append(category.getKoreanName())
	// 			.append("</strong>에 좋은 스팟: ")
	// 			.append(spotList)
	// 			.append("</li>");
	// 	}
	// 	sb.append("</ul>");
	//
	// 	sb.append("<p>👉 <a href=\"https://marineleisure.com\" target=\"_blank\">MarineLeisure 앱에서 자세히 보기</a></p>");
	// 	sb.append("<p>안전하고 즐거운 하루 보내세요 😊<br>MarineLeisure 드림</p>");
	// 	sb.append("</div>");
	//
	// 	return sb.toString();
	// }

	private String transformEmailContent(Map<ActivityCategory, Set<String>> map) {
		StringBuilder sb = new StringBuilder();

		sb.append("<div style='font-family: \"Apple SD Gothic Neo\", sans-serif; background-color: #f4f4f4; padding: 20px;'>")
			.append("<div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; padding: 30px; box-shadow: 0 0 10px rgba(0,0,0,0.05);'>")

			.append("<h2 style='color: #0077b6; text-align: center;'>🌊 MarineLeisure 추천 스팟 알림</h2>")
			.append("<p style='font-size: 15px; color: #333;'>")
			.append("고객님이 즐겨찾기한 해양 활동 스팟 중, 오늘 같은 날 <strong style='color: #0077b6;'>최고의 컨디션</strong>을 보이는 장소를 추천드릴게요!")
			.append("</p>");

		for (ActivityCategory category : ActivityCategory.values()) {
			Set<String> spots = map.getOrDefault(category, Set.of());
			if (!spots.isEmpty()) {
				sb.append("<div style='margin-top: 20px;'>")
					.append("<h3 style='color: #023e8a; font-size: 16px;'>")
					.append("✔️ ").append(category.getKoreanName()).append(" 추천 스팟")
					.append("</h3>")
					.append("<ul style='padding-left: 20px;'>");
				for (String spot : spots) {
					sb.append("<li>").append(spot).append("</li>");
				}
				sb.append("</ul></div>");
			}
		}

		sb.append("<div style='text-align: center; margin-top: 30px;'>")
			.append("<a href='https://marineleisure.vercel.app' target='_blank' style='background-color: #00b4d8; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;'>MarineLeisure 앱에서 확인하기</a>")
			.append("</div>")

			.append("<p style='margin-top: 30px; font-size: 14px; color: #555;'>")
			.append("안전하고 즐거운 하루 보내세요!<br><strong>MarineLeisure 드림</strong>")
			.append("</p>")

			.append("</div></div>");

		return sb.toString();
	}

}