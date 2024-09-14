CREATE TABLE IF NOT EXISTS eventlog
(
    event_id  UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    issued_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
