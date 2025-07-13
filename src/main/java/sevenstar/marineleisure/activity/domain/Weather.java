package sevenstar.marineleisure.activity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import sevenstar.marineleisure.global.domain.BaseEntity;

@Entity
@Getter
public class Weather extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outdoor_spotId")
    private Long outdoorSpotId;

    @Column(name = "wind_speed")
    private float windSpeed;

    @Column(name = "wave_height")
    private float waveHeight;

    @Column(name = "water_temp")
    private int waterTemp;

    @Column(name = "visibility")
    private int visibility;


}
