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

select *
from jellyfish_region_density;
desc jellyfish_region_density;
select *
from jellyfish_species;