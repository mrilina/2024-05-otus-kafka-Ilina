CREATE TABLE IF NOT EXISTS space
(
    id            SERIAL PRIMARY KEY,
    creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name          VARCHAR(255),
    number        INT       NOT NULL,
    floor         INT       NOT NULL,
    available     BOOL               DEFAULT TRUE,
    parking_id      INT       NOT NULL,
    FOREIGN KEY (parking_id) REFERENCES parking (id) ON UPDATE CASCADE ON DELETE CASCADE
);
