use marine;
INSERT INTO jellyfish_species (name, toxicity, created_at, updated_at)
VALUES ('노무라입깃해파리', 'HIGH', NOW(), NOW()),
       ('보름달물해파리', 'LOW', NOW(), NOW()),
       ('관해파리류', 'LETHAL', NOW(), NOW()),
       ('두빛보름달해파리', 'HIGH', NOW(), NOW()),
       ('야광원양해파리', 'HIGH', NOW(), NOW()),
       ('유령해파리류', 'HIGH', NOW(), NOW()),
       ('커튼원양해파리', 'HIGH', NOW(), NOW()),
       ('기수식용해파리', 'LOW', NOW(), NOW()),
       ('송곳살파', 'NONE', NOW(), NOW()),
       ('큰살파', 'NONE', NOW(), NOW());
INSERT INTO jellyfish_region_density(species, region_name, report_date, density_type, updated_at, created_at)
VALUES (1, '인천', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '경기', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '전남', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '경남', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '부산', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '경북', '2025-07-03', 'LOW', NOW(), NOW()),
       (1, '제주', '2025-07-03', 'LOW', NOW(), NOW()),
       (2, '경기', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '전북', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '전남', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '경남', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '부산', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '울산', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '경북', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '제주', '2025-07-03', 'HIGH', NOW(), NOW()),
       (2, '인천', '2025-07-03', 'LOW', NOW(), NOW()),
       (2, '충남', '2025-07-03', 'LOW', NOW(), NOW()),
       (4, '강원', '2025-07-03', 'HIGH', NOW(), NOW()),
       (4, '경북', '2025-07-03', 'LOW', NOW(), NOW()),
       (5, '제주', '2025-07-03', 'LOW', NOW(), NOW()),
       (6, '부산', '2025-07-03', 'LOW', NOW(), NOW()),
       (6, '제주', '2025-07-03', 'LOW', NOW(), NOW()),
       (7, '경남', '2025-07-03', 'HIGH', NOW(), NOW()),
       (7, '전남', '2025-07-03', 'LOW', NOW(), NOW()),
       (7, '강원', '2025-07-03', 'LOW', NOW(), NOW());


select *
from jellyfish_region_density;
select *
from jellyfish_species;

desc jellyfish_region_density;