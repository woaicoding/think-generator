DROP TABLE IF EXISTS TEST;
CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));
INSERT INTO TEST VALUES(1, 'Hello');
INSERT INTO TEST VALUES(2, 'World');
SELECT * FROM TEST ORDER BY ID;
UPDATE TEST SET NAME='Hi' WHERE ID=1;
DELETE FROM TEST WHERE ID=2;