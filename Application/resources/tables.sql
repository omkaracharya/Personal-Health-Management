DROP TABLE PERSON CASCADE CONSTRAINTS;
DROP TABLE PATIENT CASCADE CONSTRAINTS;
DROP TABLE HEALTHSUPPORTER CASCADE CONSTRAINTS;
DROP TABLE DISEASE CASCADE CONSTRAINTS;
DROP TABLE OBSERVATIONTYPE CASCADE CONSTRAINTS;
DROP TABLE RECOMMENDATION CASCADE CONSTRAINTS;
DROP TABLE DISEASERECOMMENDATION CASCADE CONSTRAINTS;
DROP TABLE ALERT CASCADE CONSTRAINTS;
DROP TABLE OBSERVATION CASCADE CONSTRAINTS;
DROP TABLE SICK CASCADE CONSTRAINTS;
DROP TABLE SUPPORTS CASCADE CONSTRAINTS;
DROP TABLE PATIENTRECOMMENDATION CASCADE CONSTRAINTS;


CREATE TABLE Person (
  personID INTEGER PRIMARY KEY,
  userName VARCHAR2(30) UNIQUE,
  password VARCHAR2(30)
);

CREATE TABLE Patient (
  patientId   INTEGER PRIMARY KEY REFERENCES Person,
  dateOfBirth DATE,
  firstName   VARCHAR2(30),
  lastName    VARCHAR2(30),
  address     VARCHAR2(100),
  gender      CHAR(1)
);

CREATE TABLE HealthSupporter (
  healthSupporterID INTEGER PRIMARY KEY REFERENCES Person,
  contactInfo       VARCHAR2(30)
);

CREATE TABLE Disease (
  diseaseID INTEGER PRIMARY KEY,
  name      VARCHAR2(30)
);

CREATE TABLE ObservationType (
  typeID      INTEGER PRIMARY KEY,
  name        VARCHAR2(30),
  description VARCHAR2(100),
  metric      VARCHAR2(30)
);


CREATE TABLE Observation (
  oid             INTEGER PRIMARY KEY,
  value           INTEGER,
  recordedTime    DATE,
  observationTime DATE,
  typeID          INTEGER REFERENCES ObservationType,
  patientID       INTEGER REFERENCES Patient
);


CREATE TABLE Recommendation (
  recommendationID INTEGER PRIMARY KEY,
  frequency        INTEGER,
  lowerValue       INTEGER,
  higherValue      INTEGER,
  typeID           INTEGER REFERENCES ObservationType
);

CREATE TABLE Supports (
  patientID           INTEGER REFERENCES Patient,
  healthSupporterID   INTEGER REFERENCES HealthSupporter,
  dateOfAuthorization DATE,
  isPrimary           INTEGER
);


CREATE TABLE Sick (
  patientID REFERENCES Patient,
  diseaseID REFERENCES Disease,
  sickDate DATE
);

CREATE TABLE DiseaseRecommendation (
  recommendationID INTEGER REFERENCES Recommendation,
  diseaseID        INTEGER REFERENCES Disease,
  UNIQUE (recommendationID, diseaseID)
);

CREATE TABLE PatientRecommendation (
  recommendationID INTEGER REFERENCES Recommendation,
  patientID        INTEGER REFERENCES Patient,
  UNIQUE (recommendationID, patientID)
);


CREATE TABLE Alert (
  alertID          INTEGER PRIMARY KEY,
  alertType        VARCHAR2(30),
  observationID    INTEGER REFERENCES Observation,
  isActive         INTEGER,
  patientID        INTEGER REFERENCES Patient,
  recommendationID INTEGER REFERENCES Recommendation,
  UNIQUE (observationID, recommendationID)
);