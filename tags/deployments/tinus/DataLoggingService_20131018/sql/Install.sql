/*
This should be automated but for now: -
1). Create a DB called datalogger
2). Run section a, b and c below
*/

-- a). Setup tables and constraints
alter table sensorValues drop constraint FK_hhj4hhb43wmpouafayktt3dyu;
alter table sensors drop constraint FK_o0ph7r6o86us5cj4djxuq3htf;
alter table dataUsers drop constraint UK_mhn6ua1uvdrcnm6iujdmtmuaf;
drop table if exists datausers cascade;
drop table if exists sensorValues cascade;
drop table if exists sensors cascade;
create table dataUsers (id bigserial not null, username varchar(255), status boolean, readKey char(32), writeKey char(32), primary key (id));
create table sensorValues (id bigserial not null, sensorId int8 not null, timestamp timestamp not null, value varchar(255) not null, primary key (id));
create table sensors (id bigserial not null, userId int8 not null, name varchar(255), currentValue varchar(255), primary key (id));
alter table dataUsers add constraint UK_mhn6ua1uvdrcnm6iujdmtmuaf unique (username);
alter table sensorValues add constraint FK_hhj4hhb43wmpouafayktt3dyu foreign key (sensorId) references sensors;
alter table sensors add constraint FK_o0ph7r6o86us5cj4djxuq3htf foreign key (userId) references dataUsers;

-- b). Function for testing whether or not a string is actually a number
CREATE OR REPLACE FUNCTION isnumeric(text) RETURNS BOOLEAN AS $$
DECLARE x NUMERIC;
BEGIN
    x = $1::NUMERIC;
    RETURN TRUE;
EXCEPTION WHEN others THEN
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- c). Add a test user for now called openremote with pre-defined md5 hash API Key
INSERT INTO dataUsers(username, status, readKey, writeKey) VALUES ('openremote', true, '6fccf8cf3c864de0c857bbb3aad61a62', '6fccf8cf3c864de0c857bbb3aad61a62');

-- d). Example query for retrieving numeric sensor values 
SELECT cast(value as float) as value, timestamp
FROM sensorValues
INNER JOIN sensors
ON sensorValues.sensorId=sensors.id
INNER JOIN dataUsers
ON sensors.userId = dataUsers.id
where dataUsers.readKey = '6fccf8cf3c864de0c857bbb3aad61a62' and sensors.name = 'Rainfall' and isnumeric(sensorValues.value) and timestamp > (now() - interval '48 hour');