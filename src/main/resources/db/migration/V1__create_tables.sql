-- =================================================================================
--  Blacklisted Refresh Tokens
-- =================================================================================
CREATE TABLE IF NOT EXISTS blacklisted_refresh_tokens
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  DATETIME              NOT NULL,
    updated_at  DATETIME              NULL,
    jti         VARCHAR(255)          NOT NULL,
    member_id   BIGINT                NOT NULL,
    expiry_date DATETIME              NOT NULL,
    CONSTRAINT pk_blacklisted_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uc_blacklisted_refresh_tokens_jti UNIQUE (jti)
);

-- =================================================================================
--  Favorite Spots
-- =================================================================================
CREATE TABLE IF NOT EXISTS favorite_spots
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME              NOT NULL,
    updated_at   DATETIME              NULL,
    member_id    BIGINT                NOT NULL,
    spot_id      BIGINT                NOT NULL,
    notification BIT(1)                NOT NULL,
    CONSTRAINT pk_favorite_spots PRIMARY KEY (id)
);

-- =================================================================================
--  Fishing Forecast
-- =================================================================================
CREATE TABLE IF NOT EXISTS fishing_forecast
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        DATETIME              NOT NULL,
    updated_at        DATETIME              NULL,
    spot_id           BIGINT                NOT NULL,
    target_id         BIGINT                NULL,
    forecast_date     DATE                  NOT NULL,
    time_period       VARCHAR(10)           NULL,
    tide              VARCHAR(255)          NULL,
    total_index       VARCHAR(255)          NOT NULL,
    wave_height_min   FLOAT                 NULL,
    wave_height_max   FLOAT                 NULL,
    sea_temp_min      FLOAT                 NULL,
    sea_temp_max      FLOAT                 NULL,
    air_temp_min      FLOAT                 NULL,
    air_temp_max      FLOAT                 NULL,
    current_speed_min FLOAT                 NULL,
    current_speed_max FLOAT                 NULL,
    wind_speed_min    FLOAT                 NULL,
    wind_speed_max    FLOAT                 NULL,
    uv_index          FLOAT                 NULL,
    CONSTRAINT pk_fishing_forecast PRIMARY KEY (id),
    CONSTRAINT uc_fishing_forecast_spot_date_time UNIQUE (spot_id, forecast_date, time_period)
);

-- =================================================================================
--  Fishing Targets
-- =================================================================================
CREATE TABLE IF NOT EXISTS fishing_targets
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50)           NULL,
    CONSTRAINT pk_fishing_targets PRIMARY KEY (id),
    CONSTRAINT uc_fishing_targets_name UNIQUE (name)
);

-- =================================================================================
--  Jellyfish Region Density
-- =================================================================================
CREATE TABLE IF NOT EXISTS jellyfish_region_density
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   DATETIME              NOT NULL,
    updated_at   DATETIME              NULL,
    region_name  VARCHAR(100)          NOT NULL,
    species      BIGINT                NOT NULL,
    species_id   BIGINT                NOT NULL,
    report_date  DATE                  NOT NULL,
    density_type VARCHAR(10)           NOT NULL,
    CONSTRAINT pk_jellyfish_region_density PRIMARY KEY (id)
);

-- =================================================================================
--  Jellyfish Species
-- =================================================================================
CREATE TABLE IF NOT EXISTS jellyfish_species
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at DATETIME              NOT NULL,
    updated_at DATETIME              NULL,
    name       VARCHAR(20)           NOT NULL,
    toxicity   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_jellyfish_species PRIMARY KEY (id),
    CONSTRAINT uc_jellyfish_species_name UNIQUE (name)
);

-- =================================================================================
--  Meeting Participants
-- =================================================================================
CREATE TABLE IF NOT EXISTS meeting_participants
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at DATETIME              NOT NULL,
    updated_at DATETIME              NULL,
    meeting_id BIGINT                NOT NULL,
    user_id    BIGINT                NOT NULL,
    `role`     SMALLINT              NOT NULL,
    CONSTRAINT pk_meeting_participants PRIMARY KEY (id)
);

-- =================================================================================
--  Meetings
-- =================================================================================
CREATE TABLE IF NOT EXISTS meetings
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    DATETIME              NOT NULL,
    updated_at    DATETIME              NULL,
    title         VARCHAR(20)           NOT NULL,
    category      SMALLINT              NOT NULL,
    capacity      INT                   NOT NULL,
    host_id       BIGINT                NOT NULL,
    meeting_time  DATETIME              NOT NULL,
    status        SMALLINT              NOT NULL,
    spot_id       BIGINT                NOT NULL,
    `description` TEXT                  NULL,
    CONSTRAINT pk_meetings PRIMARY KEY (id)
);

-- =================================================================================
--  Members
-- =================================================================================
CREATE TABLE IF NOT EXISTS members
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  DATETIME              NOT NULL,
    updated_at  DATETIME              NULL,
    nickname    VARCHAR(20)           NOT NULL,
    email       VARCHAR(50)           NOT NULL,
    provider    VARCHAR(255)          NULL,
    provider_id VARCHAR(255)          NULL,
    status      SMALLINT              NOT NULL,
    latitude    DECIMAL(9, 6)         NULL,
    longitude   DECIMAL(9, 6)         NULL,
    CONSTRAINT pk_members PRIMARY KEY (id),
    CONSTRAINT uc_members_email UNIQUE (email),
    CONSTRAINT uc_members_nickname UNIQUE (nickname)
);

-- =================================================================================
--  Mudflat Forecast
-- =================================================================================
CREATE TABLE IF NOT EXISTS mudflat_forecast
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     DATETIME              NOT NULL,
    updated_at     DATETIME              NULL,
    spot_id        BIGINT                NOT NULL,
    forecast_date  DATE                  NOT NULL,
    start_time     TIME                  NULL,
    end_time       TIME                  NULL,
    uv_index       FLOAT                 NULL,
    air_temp_min   FLOAT                 NULL,
    air_temp_max   FLOAT                 NULL,
    wind_speed_min FLOAT                 NULL,
    wind_speed_max FLOAT                 NULL,
    weather        VARCHAR(255)          NULL,
    total_index    VARCHAR(255)          NULL,
    CONSTRAINT pk_mudflat_forecast PRIMARY KEY (id),
    CONSTRAINT uc_mudflat_forecast_spot_date UNIQUE (spot_id, forecast_date)
);

-- =================================================================================
--  Observatories
-- =================================================================================
CREATE TABLE IF NOT EXISTS observatories
(
    id         VARCHAR(7)    NOT NULL,
    created_at DATETIME      NOT NULL,
    updated_at DATETIME      NULL,
    name       VARCHAR(255)  NOT NULL,
    latitude   DECIMAL(9, 6) NOT NULL,
    longitude  DECIMAL(9, 6) NOT NULL,
    hl_code    SMALLINT      NOT NULL,
    time       TIME          NOT NULL,
    CONSTRAINT pk_observatories PRIMARY KEY (id)
);

-- =================================================================================
--  Outdoor Spots
-- =================================================================================
CREATE TABLE IF NOT EXISTS outdoor_spots
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at DATETIME              NOT NULL,
    updated_at DATETIME              NULL,
    name       VARCHAR(255)          NOT NULL,
    category   VARCHAR(255)          NULL,
    type       VARCHAR(255)          NULL,
    location   VARCHAR(100)          NULL,
    latitude   DECIMAL(9, 6)         NULL,
    longitude  DECIMAL(9, 6)         NULL,
    geo_point  POINT SRID 4326       NOT NULL,
    CONSTRAINT pk_outdoor_spots PRIMARY KEY (id),
    CONSTRAINT uk_lat_lon_category UNIQUE (latitude, longitude, category),
    SPATIAL INDEX (geo_point)
);

CREATE INDEX idx_lat_lon ON outdoor_spots (latitude, longitude);
CREATE INDEX idx_point ON outdoor_spots (geo_point);

-- =================================================================================
--  Refresh Tokens
-- =================================================================================
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    DATETIME              NOT NULL,
    updated_at    DATETIME              NULL,
    refresh_token VARCHAR(512)          NOT NULL,
    user_id       BIGINT                NOT NULL,
    expired       BIT(1)                NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

-- =================================================================================
--  Scuba Forecast
-- =================================================================================
CREATE TABLE IF NOT EXISTS scuba_forecast
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_at        DATETIME              NOT NULL,
    updated_at        DATETIME              NULL,
    spot_id           BIGINT                NOT NULL,
    forecast_date     DATE                  NOT NULL,
    time_period       VARCHAR(10)           NOT NULL,
    sunrise           TIME                  NULL,
    sunset            TIME                  NULL,
    tide              VARCHAR(255)          NULL,
    total_index       VARCHAR(255)          NULL,
    wave_height_min   FLOAT                 NULL,
    wave_height_max   FLOAT                 NULL,
    sea_temp_min      FLOAT                 NULL,
    sea_temp_max      FLOAT                 NULL,
    current_speed_min FLOAT                 NULL,
    current_speed_max FLOAT                 NULL,
    CONSTRAINT pk_scuba_forecast PRIMARY KEY (id),
    CONSTRAINT uc_scuba_forecast_spot_date_time UNIQUE (spot_id, forecast_date, time_period)
);

-- =================================================================================
--  Spot Preset
-- =================================================================================
CREATE TABLE spot_preset
(
    region              VARCHAR(255) NOT NULL,
    fishing_spot_id     BIGINT       NULL,
    fishing_name        VARCHAR(255) NULL,
    fishing_total_index VARCHAR(255) NULL,
    mudflat_spot_id     BIGINT       NULL,
    mudflat_name        VARCHAR(255) NULL,
    mudflat_total_index VARCHAR(255) NULL,
    scuba_spot_id       BIGINT       NULL,
    scuba_name          VARCHAR(255) NULL,
    scuba_total_index   VARCHAR(255) NULL,
    surfing_spot_id     BIGINT       NULL,
    surfing_name        VARCHAR(255) NULL,
    surfing_total_index VARCHAR(255) NULL,
    CONSTRAINT pk_spot_preset PRIMARY KEY (region)
);

-- =================================================================================
--  Spot View Quartile
-- =================================================================================
CREATE TABLE IF NOT EXISTS spot_view_quartile
(
    spot_id        BIGINT   NOT NULL,
    month_quartile INT      NULL,
    week_quartile  INT      NULL,
    updated_at     DATETIME NULL,
    CONSTRAINT pk_spot_view_quartile PRIMARY KEY (spot_id)
);

-- =================================================================================
--  Spot View Stats
-- =================================================================================
CREATE TABLE IF NOT EXISTS spot_view_stats
(
    spot_id    BIGINT NOT NULL,
    view_date  DATE   NOT NULL,
    view_count INT    NOT NULL,
    CONSTRAINT pk_spot_view_stats PRIMARY KEY (spot_id, view_date)
);

-- =================================================================================
--  Surfing Forecast
-- =================================================================================
CREATE TABLE IF NOT EXISTS surfing_forecast
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    DATETIME              NOT NULL,
    updated_at    DATETIME              NULL,
    spot_id       BIGINT                NOT NULL,
    forecast_date DATE                  NOT NULL,
    time_period   VARCHAR(10)           NOT NULL,
    wave_height   FLOAT                 NULL,
    wave_period   FLOAT                 NULL,
    wind_speed    FLOAT                 NULL,
    sea_temp      FLOAT                 NULL,
    total_index   VARCHAR(255)          NULL,
    uv_index      FLOAT                 NULL,
    CONSTRAINT pk_surfing_forecast PRIMARY KEY (id),
    CONSTRAINT uc_surfing_forecast_spot_date_time UNIQUE (spot_id, forecast_date, time_period)
);

-- =================================================================================
--  Tags (Commented out)
-- =================================================================================
CREATE TABLE if not exists tags
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NULL,
    meeting_id BIGINT                NOT NULL,
    content    TEXT                  NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);
