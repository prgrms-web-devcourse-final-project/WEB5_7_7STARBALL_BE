-- 테스트 데이터 생성을 위한 SQL (MySQL 기준)

-- 기존 테이블 삭제 (테스트용)
DROP TABLE IF EXISTS test_position;

-- 새로운 테이블 생성 (POINT 타입 추가)
-- MySQL 8.0+ InnoDB에서 공간 인덱스 지원
CREATE TABLE test_position
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    latitude   FLOAT                 NOT NULL,
    longitude  FLOAT                 NOT NULL,
    geom_point POINT SRID 4326       NOT NULL, -- SRID 4326은 WGS84 위경도 좌표계
    CONSTRAINT pk_testposition PRIMARY KEY (id)
);

-- geom_point 컬럼에 공간 인덱스 추가
CREATE SPATIAL INDEX sp_idx_geom_point ON test_position (geom_point);

-- -------------------------------------------------------------------
-- 중심점: 위도 37.5665, 경도 126.9780 (서울 시청 근처)
-- 테스트 반경: 5km
-- -------------------------------------------------------------------

-- 1. 반경 5km 이내에 있는 데이터 (총 4개)
--    (실제 거리는 Haversine 공식을 통해 계산해야 정확하지만,
--     테스트를 위해 중심점에서 아주 가까운 값들로 설정)
INSERT INTO test_position (latitude, longitude, geom_point)
VALUES (37.5665, 126.9780, ST_GeomFromText('POINT(37.5665 126.9780)', 4326)), -- 1-1. 정확히 중심점
       (37.5700, 126.9820, ST_GeomFromText('POINT(37.5700 126.9820)', 4326)), -- 1-2. 중심점에서 북동쪽으로 약 0.5km 이내
       (37.5620, 126.9740, ST_GeomFromText('POINT(37.5620 126.9740)', 4326)), -- 1-3. 중심점에서 남서쪽으로 약 0.5km 이내
       (37.5690, 126.9780, ST_GeomFromText('POINT(37.5690 126.9780)', 4326));
-- 1-4. 중심점에서 북쪽으로 약 0.2km 이내

-- 2. 반경 5km 밖에 있는 데이터 (총 3개)
--    (중심점에서 충분히 떨어진 값들로 설정)
INSERT INTO test_position (latitude, longitude, geom_point)
VALUES (37.6100, 127.0200, ST_GeomFromText('POINT(37.5000 126.9000)', 4326)), -- 2-1. 중심점에서 북동쪽으로 약 6-7km
       (37.5000, 126.9000, ST_GeomFromText('POINT(37.5000 126.9000)', 4326)), -- 2-2. 중심점에서 남서쪽으로 약 9-10km
       (37.5665, 127.0500, ST_GeomFromText('POINT(37.5665 127.0500)', 4326));
-- 2-3. 중심점에서 동쪽으로 약 6km


-- 공간 인덱스를 활용한 조회 쿼리 예시 (MySQL 8.0+)
SET @center_point = ST_GeomFromText('POINT(37.5665 126.9780)', 4326); -- 여기도 경도 위도 순서
SET @radius_meters = 5000; -- 5km = 5000 meters

-- 데이터 확인
SELECT latitude, longitude, ST_Distance_Sphere(geom_point, @center_point)
FROM test_position;

SELECT id,
       latitude,
       longitude,
       ST_Distance_Sphere(geom_point, @center_point) AS distance_meters
FROM test_position
WHERE ST_Distance_Sphere(geom_point, @center_point) <= @radius_meters
ORDER BY distance_meters;
