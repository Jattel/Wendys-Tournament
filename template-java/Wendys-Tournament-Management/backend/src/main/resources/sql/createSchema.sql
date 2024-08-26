CREATE TABLE IF NOT EXISTS breed
(
  id BIGINT PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
  sex ENUM ('MALE', 'FEMALE') NOT NULL,
  date_of_birth DATE NOT NULL,
  height NUMERIC(4,2),
  weight NUMERIC(7,2),
  // TODO handle optional everywhere
  breed_id BIGINT REFERENCES breed(id) on delete set null
);

CREATE TABLE IF NOT EXISTS tournament
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS participants
(
    horse_id BIGINT REFERENCES horse(id) on delete cascade ,
    tournament_id BIGINT REFERENCES tournament(id) on delete cascade,
    entryNumber BIGINT,
    roundReached BIGINT,
    PRIMARY KEY (horse_id, tournament_id)
);



