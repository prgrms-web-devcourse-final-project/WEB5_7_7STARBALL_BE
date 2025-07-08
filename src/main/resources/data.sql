use marine;
INSERT INTO jellyfish_species (id, name, toxicity, created_at, updated_at)
VALUES (1, '노무라입깃해파리', 'HIGH', NOW(), NOW()),
       (2, '보름달물해파리', 'LOW', NOW(), NOW()),
       (3, '관해파리류', 'LETHAL', NOW(), NOW()),
       (4, '두빛보름달해파리', 'HIGH', NOW(), NOW()),
       (5, '야광원양해파리', 'HIGH', NOW(), NOW()),
       (6, '유령해파리류', 'HIGH', NOW(), NOW()),
       (7, '커튼원양해파리', 'HIGH', NOW(), NOW()),
       (8, '기수식용해파리', 'LOW', NOW(), NOW()),
       (9, '송곳살파', 'NONE', NOW(), NOW()),
       (10, '큰살파', 'NONE', NOW(), NOW());

select *
from jellyfish_region_density;
desc jellyfish_region_density;
select *
from jellyfish_species;