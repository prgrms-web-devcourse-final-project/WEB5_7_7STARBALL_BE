drop table if exists normal_index_table;
drop table if exists spatial_index_table;
drop procedure if exists generate_test_data;

CREATE TABLE normal_index_table
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude  FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    INDEX idx_lat_lon (latitude, longitude)
);

CREATE TABLE spatial_index_table
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    latitude  FLOAT           NOT NULL,
    longitude FLOAT           NOT NULL,
    geom      POINT SRID 4326 NOT NULL,
    SPATIAL INDEX idx_geom (geom)
);

DELIMITER $$
CREATE PROCEDURE generate_test_data()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 1000000
        DO
            SET @lat = 37.0 + RAND() * 0.1;
            SET @lon = 127.0 + RAND() * 0.1;

            -- 일반 인덱스용 테이블
            INSERT INTO normal_index_table (latitude, longitude)
            VALUES (@lat, @lon);

            -- 공간 인덱스용 테이블
            INSERT INTO spatial_index_table (latitude, longitude, geom)
            VALUES (@lat, @lon, ST_GeomFromText(CONCAT('POINT(', @lat, ' ', @lon, ')'), 4326));

            SET i = i + 1;
        END WHILE;
END$$
DELIMITER ;

CALL generate_test_data();

-- 성능 비교
select count(*) from normal_index_table;
select count(*) from spatial_index_table;

-- 일반 인덱스 사용 time
SET PROFILING = 1;
SELECT id, latitude, longitude
FROM normal_index_table
WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(127.05, 37.05)) < 1000;
SHOW PROFILES;
# 0.88029925초

EXPLAIN
SELECT id, latitude, longitude
FROM normal_index_table
WHERE ST_Distance_Sphere(POINT(longitude, latitude), POINT(127.05, 37.05)) < 1000;
# Type: index, rows: 993960

-- 그냥 공간 인덱스에서 사용
SET PROFILING = 1;
SELECT id, latitude, longitude
FROM spatial_index_table
WHERE ST_Distance_Sphere(geom, ST_GeomFromText('POINT(37.05 127.05)', 4326)) < 1000;
SHOW PROFILES;
# 0.06537475초

explain
SELECT id, latitude, longitude
FROM spatial_index_table
WHERE ST_Distance_Sphere(geom, ST_GeomFromText('POINT(37.05 127.05)', 4326)) < 1000;
# Type: All, rows: 901250

-- 공간 인덱스 + within 사용, 반경 1도 = 111.32km, 반경 0.009도 = 1km
SET PROFILING = 1;
SELECT id, latitude, longitude
FROM spatial_index_table
WHERE ST_Within(
              geom,
              ST_Buffer(ST_GeomFromText('POINT(37.05 127.05)', 4326), 0.009)
      );
SHOW PROFILES;
# 0.014768초

EXPLAIN
SELECT id, latitude, longitude
FROM spatial_index_table
WHERE ST_Within(
              geom,
              ST_Buffer(ST_GeomFromText('POINT(37.05 127.05)', 4326), 0.009)
      );
# Type: range, rows: 1
