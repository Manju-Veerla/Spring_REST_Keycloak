CREATE USER IF NOT EXISTS admin WITH PASSWORD 'admin';
CREATE DATABASE IF NOT EXISTS workshop_local_db WITH OWNER admin ENCODING 'UTF8' TABLESPACE pg_default;
GRANT ALL PRIVILEGES ON DATABASE workshop_local_db to admin;
ALTER DATABASE workshop_local_db OWNER TO admin;



CREATE SEQUENCE IF NOT EXISTS registrations_id_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS workshops_id_seq START 1 INCREMENT 1;

CREATE TABLE IF NOT EXISTS registrations
(
   id SERIAL,
    user_email character varying(50) NOT NULL,
    user_name character varying(30)  NOT NULL,
    user_phone character varying(20) ,
    user_preferred_contact character varying(30) ,
    workshop_code character varying(15) NOT NULL,
    CONSTRAINT registrations_id_pk PRIMARY KEY (id)
);

CREATE INDEX idx_workshop_code ON registrations(workshop_code);

CREATE TABLE IF NOT EXISTS workshops
(
    id SERIAL,
	capacity integer NOT NULL,
    start_time timestamp(6) with time zone NOT NULL,
	end_time timestamp(6) with time zone NOT NULL,
    code character varying(15) NOT NULL,
    description character varying(100)  NOT NULL,
    name character varying(50)  NOT NULL,
    CONSTRAINT workshops_id_pk PRIMARY KEY (id),
    CONSTRAINT workshops_code_key UNIQUE (code)
);


INSERT INTO workshops (capacity,end_time,start_time,code,description,"name") VALUES
	 (3,'2025-04-21 19:00:00+02','2025-04-11 11:00:00+02','WS2025','A meet up about the 2025','Catchup 2025'),
	 (3,'2025-06-21 19:00:00+02','2025-06-11 11:00:00+02','WS_100','A meet up about the Tech events','Tech catchUp 2025'),
	 (5,'2025-07-21 19:00:00+02','2025-07-11 11:00:00+02','WS_200','Hackathon the 2025','Hackathon 2025');


