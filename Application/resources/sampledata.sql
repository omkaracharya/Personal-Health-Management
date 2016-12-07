INSERT INTO Person VALUES (PersonIDSequence.nextval, 'P1', 'password');
INSERT INTO Person VALUES (PersonIDSequence.nextval, 'P2', 'password');
INSERT INTO Person VALUES (PersonIDSequence.nextval, 'P3', 'password');
INSERT INTO Person VALUES (PersonIDSequence.nextval, 'P4', 'password');

INSERT INTO Patient SELECT
                      personID                                           AS patientID,
                      TO_DATE('05/26/1984', 'MM/DD/YYYY')                AS dateOfBirth,
                      'Sheldon'                                          AS firstName,
                      'Cooper'                                           AS lastName,
                      '2500 Sacramento, Apt 903, Santa Cruz, CA - 90021' AS address,
                      'M'                                                AS gender
                    FROM Person
                    WHERE userName = 'P1';
INSERT INTO Patient SELECT
                      personID                                           AS patientID,
                      TO_DATE('04/19/1989', 'MM/DD/YYYY')                AS dateOfBirth,
                      'Leonard'                                          AS firstName,
                      'Hofstader'                                        AS lastName,
                      '2500 Sacramento, Apt 904, Santa Cruz, CA - 90021' AS address,
                      'M'                                                AS gender
                    FROM Person
                    WHERE userName = 'P2';
INSERT INTO Patient SELECT
                      personID                                           AS patientID,
                      TO_DATE('12/25/1990', 'MM/DD/YYYY')                AS dateOfBirth,
                      'Penny'                                            AS firstName,
                      'Hofstader'                                        AS lastName,
                      '2500 Sacramento, Apt 904, Santa Cruz, CA - 90021' AS address,
                      'F'                                                AS gender
                    FROM Person
                    WHERE userName = 'P3';
INSERT INTO Patient SELECT
                      personID                                           AS patientID,
                      TO_DATE('06/15/1992', 'MM/DD/YYYY')                AS dateOfBirth,
                      'Amy'                                              AS firstName,
                      'Farrahfowler'                                     AS lastName,
                      '2500 Sacramento, Apt 905, Santa Cruz, CA - 90021' AS address,
                      'F'                                                AS gender
                    FROM Person
                    WHERE userName = 'P4';

INSERT INTO Disease VALUES (1, 'Heart Disease');
INSERT INTO Disease VALUES (2, 'HIV');
INSERT INTO Disease VALUES (3, 'COPD');

INSERT INTO Sick SELECT
                   personID                            AS patientID,
                   1                                   AS diseaseID,
                   TO_DATE('10/22/2016', 'MM/DD/YYYY') AS sickDate
                 FROM Person
                 WHERE userName = 'P1';
INSERT INTO Sick SELECT
                   personID                            AS patientID,
                   2                                   AS diseaseID,
                   TO_DATE('10/10/2016', 'MM/DD/YYYY') AS sickDate
                 FROM Person
                 WHERE userName = 'P2';

INSERT INTO HealthSupporter SELECT
                              personID     AS healthSupporterID,
                              '9195150002' AS contactInfo
                            FROM Person
                            WHERE userName = 'P2';
INSERT INTO HealthSupporter SELECT
                              personID     AS healthSupporterID,
                              '9195150003' AS contactInfo
                            FROM Person
                            WHERE userName = 'P3';
INSERT INTO HealthSupporter SELECT
                              personID     AS healthSupporterID,
                              '9195150004' AS contactInfo
                            FROM Person
                            WHERE userName = 'P4';

INSERT INTO Supports SELECT
                       p.patientID,
                       h.healthSupporterID,
                       TO_DATE('10/21/2016', 'MM/DD/YYYY') AS dateOfAuthorization,
                       1                                   AS isPrimary
                     FROM Patient p, HealthSupporter h
                     WHERE p.firstName = 'Sheldon' AND p.lastName = 'Cooper' AND h.contactInfo = '9195150002';
INSERT INTO Supports SELECT
                       p.patientID,
                       h.healthSupporterID,
                       TO_DATE('10/21/2016', 'MM/DD/YYYY') AS dateOfAuthorization,
                       0                                   AS isPrimary
                     FROM Patient p, HealthSupporter h
                     WHERE p.firstName = 'Sheldon' AND p.lastName = 'Cooper' AND h.contactInfo = '9195150004';
INSERT INTO Supports SELECT
                       p.patientID,
                       h.healthSupporterID,
                       TO_DATE('10/09/2016', 'MM/DD/YYYY') AS dateOfAuthorization,
                       1                                   AS isPrimary
                     FROM Patient p, HealthSupporter h
                     WHERE p.firstName = 'Leonard' AND p.lastName = 'Hofstader' AND h.contactInfo = '9195150003';
INSERT INTO Supports SELECT
                       p.patientID,
                       h.healthSupporterID,
                       TO_DATE('10/21/2016', 'MM/DD/YYYY') AS dateOfAuthorization,
                       1                                   AS isPrimary
                     FROM Patient p, HealthSupporter h
                     WHERE p.firstName = 'Penny' AND p.lastName = 'Hofstader' AND h.contactInfo = '9195150004';

INSERT INTO ObservationType VALUES (1, 'Weight', 'Weight of a patient', 'lbs');
INSERT INTO ObservationType VALUES (2, 'Systolic Blood Pressure', 'Systolic blood pressure of a patient', 'count');
INSERT INTO ObservationType VALUES (3, 'Diastolic Blood Pressure', 'Diastolic blood pressure of a patient', 'count');
INSERT INTO ObservationType VALUES (4, 'Oxygen Saturation', 'Oxygen saturation of a patient', '%');
INSERT INTO ObservationType VALUES (5, 'Pain', 'Pain severity ', '0 - 10');
INSERT INTO ObservationType VALUES (6, 'Mood', 'Mood of a patient', '1: Sad, 2: Ok, 3: Happy');
INSERT INTO ObservationType VALUES (7, 'Temperature', 'Body temperature of a patient', 'F');

INSERT INTO Observation SELECT
                          ObservationIDSequence.nextval       AS oid,
                          180                                 AS value,
                          TO_DATE('10/11/2016', 'MM/DD/YYYY') AS recordedTime,
                          TO_DATE('10/11/2016', 'MM/DD/YYYY') AS observationTime,
                          t.typeID,
                          p.patientID
                        FROM ObservationType t, Patient p
                        WHERE p.firstName = 'Leonard' AND p.lastName = 'Hofstader' AND t.name = 'Weight';
INSERT INTO Observation SELECT
                          ObservationIDSequence.nextval       AS oid,
                          195                                 AS value,
                          TO_DATE('10/17/2016', 'MM/DD/YYYY') AS recordedTime,
                          TO_DATE('10/17/2016', 'MM/DD/YYYY') AS observationTime,
                          t.typeID,
                          p.patientID
                        FROM ObservationType t, Patient p
                        WHERE p.firstName = 'Leonard' AND p.lastName = 'Hofstader' AND t.name = 'Weight';

INSERT INTO Recommendation SELECT
                             RecommendationIDSequence.nextval AS recommendationID,
                             7,
                             120,
                             190,
                             t.typeID
                           FROM ObservationType t
                           WHERE t.name = 'Weight';
INSERT INTO PatientRecommendation SELECT
                                    r.recommendationID,
                                    p.patientID
                                  FROM Recommendation r, Patient p
                                  WHERE r.lowerValue = 120 AND r.higherValue = 190 AND p.firstName = 'Leonard' AND
                                        p.lastName = 'Hofstader';
INSERT INTO Recommendation SELECT
                             RecommendationIDSequence.nextval AS recommendationID,
                             1,
                             NULL,
                             NULL,
                             t.typeID
                           FROM ObservationType t
                           WHERE t.name = 'Systolic Blood Pressure';
INSERT INTO PatientRecommendation SELECT
                                    r.recommendationID,
                                    p.patientID
                                  FROM Recommendation r, Patient p
                                  WHERE r.typeID = 2 AND p.firstName = 'Leonard' AND p.lastName = 'Hofstader';
INSERT INTO Recommendation SELECT
                             RecommendationIDSequence.nextval AS recommendationID,
                             1,
                             NULL,
                             NULL,
                             t.typeID
                           FROM ObservationType t
                           WHERE t.name = 'Diastolic Blood Pressure';
INSERT INTO PatientRecommendation SELECT
                                    r.recommendationID,
                                    p.patientID
                                  FROM Recommendation r, Patient p
                                  WHERE r.typeID = 3 AND p.firstName = 'Leonard' AND p.lastName = 'Hofstader';
INSERT INTO Recommendation SELECT
                             RecommendationIDSequence.nextval AS recommendationID,
                             1,
                             NULL,
                             5,
                             t.typeID
                           FROM ObservationType t
                           WHERE t.name = 'Pain';
INSERT INTO PatientRecommendation SELECT
                                    r.recommendationID,
                                    p.patientID
                                  FROM Recommendation r, Patient p
                                  WHERE r.typeID = 5 AND p.firstName = 'Leonard' AND p.lastName = 'Hofstader';

INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 7, 120, 200, 1);
INSERT INTO DiseaseRecommendation VALUES (5, 1);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, 140, 159, 2);
INSERT INTO DiseaseRecommendation VALUES (6, 1);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, 120, 200, 3);
INSERT INTO DiseaseRecommendation VALUES (7, 1);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 7, 3, 1, 6);
INSERT INTO DiseaseRecommendation VALUES (8, 1);

INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 7, 120, 200, 1);
INSERT INTO DiseaseRecommendation VALUES (9, 2);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, NULL, NULL, 2);
INSERT INTO DiseaseRecommendation VALUES (10, 2);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, NULL, NULL, 3);
INSERT INTO DiseaseRecommendation VALUES (11, 2);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, 1, 10, 5);
INSERT INTO DiseaseRecommendation VALUES (12, 2);

INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, 90, 99, 4);
INSERT INTO DiseaseRecommendation VALUES (13, 3);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 1, 95, 100, 7);
INSERT INTO DiseaseRecommendation VALUES (14, 3);

INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 7, 120, 200, 1);
INSERT INTO PatientRecommendation VALUES (15, 3);
INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, 7, 120, 200, 1);
INSERT INTO PatientRecommendation VALUES (16, 4);

COMMIT;