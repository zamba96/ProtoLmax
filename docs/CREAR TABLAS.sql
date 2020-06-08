/*
 * CREA LAS TABLAS
 */

CREATE TABLE usr(
    cedula numeric(12) PRIMARY KEY,
    nombre varchar(50),
    direccion varchar(50),
    email varchar(50),
    telefono varchar(20)
);

CREATE TABLE usr_dueno(
    cedula numeric(12) PRIMARY KEY REFERENCES usr(cedula)
);

CREATE TABLE spot(
    id int PRIMARY KEY,
    disponible boolean,
    direccion varchar(50),
    lat float,
    lon float,
    precio int,
    dueno numeric(12) REFERENCES usr_dueno(cedula)
);

CREATE TABLE usr_prq(
    cedula numeric(12) PRIMARY KEY REFERENCES usr(cedula)
);
    
CREATE TABLE usr_port(
    cedula numeric(12) PRIMARY KEY REFERENCES usr(cedula),
    spotId int REFERENCES spot(id)
);

CREATE TABLE res(
    id int PRIMARY KEY,
    fecha Timestamp,
    duracion interval,
    spot int REFERENCES spot(id),
    usuario numeric(12) REFERENCES usr_prq(cedula)
);