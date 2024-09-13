-- DEMO data
INSERT INTO parking(id, name, address, location)
VALUES (1, 'Parking1', 'str. Puskin 32, 2012', 'Chişinău');

INSERT INTO space(id, name, number, floor, parking_id, available)
VALUES (1, 'Twin with view', 38, 5, 1, true),
       (2, 'For One Lux', 25, 3, 1, false),
       (3, 'On one floor', 27, 2, 1, true);
