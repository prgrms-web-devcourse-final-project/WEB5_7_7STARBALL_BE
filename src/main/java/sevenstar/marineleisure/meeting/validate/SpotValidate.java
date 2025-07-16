package sevenstar.marineleisure.meeting.validate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sevenstar.marineleisure.global.exception.CustomException;
import sevenstar.marineleisure.meeting.error.MeetingError;
import sevenstar.marineleisure.meeting.error.SpotError;
import sevenstar.marineleisure.spot.domain.OutdoorSpot;
import sevenstar.marineleisure.spot.repository.OutdoorSpotRepository;

@Component
@RequiredArgsConstructor
public class SpotValidate {

	private final OutdoorSpotRepository outdoorSpotSpotRepository;

	@Transactional(readOnly = true)
	public OutdoorSpot foundOutdoorSpot(Long spotId){
		return outdoorSpotSpotRepository.findById(spotId)
			.orElseThrow(() -> new CustomException(SpotError.SPOT_NOT_FOUND));
	}
}
