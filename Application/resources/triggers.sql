CREATE OR REPLACE TRIGGER outside_the_limit_alert
AFTER INSERT OR UPDATE
  ON OBSERVATION
FOR EACH ROW
  DECLARE
    lowval  OBSERVATION.VALUE%TYPE;
    highval OBSERVATION.VALUE%TYPE;
    id      RECOMMENDATION.RECOMMENDATIONID%TYPE;
  BEGIN
    SELECT
      re.LOWERVALUE,
      re.HIGHERVALUE,
      re.RECOMMENDATIONID
    INTO lowval, highval, id
    FROM RECOMMENDATION re
    WHERE recommendationId IN
          (
            SELECT p.RECOMMENDATIONID
            FROM PATIENTRECOMMENDATION p
            WHERE p.PATIENTID = :new.PATIENTID
            UNION
            (
              SELECT r.RECOMMENDATIONID
              FROM DISEASERECOMMENDATION d, RECOMMENDATION r
              WHERE d.DISEASEID IN (SELECT s.DISEASEID
                                    FROM SICK s
                                    WHERE s.PATIENTID = :new.PATIENTID) AND r.RECOMMENDATIONID = d.RECOMMENDATIONID AND
                    r.TYPEID NOT IN
                    (
                      SELECT r1.TYPEID
                      FROM PATIENTRECOMMENDATION p, RECOMMENDATION r1
                      WHERE p.PATIENTID = :new.PATIENTID AND p.RECOMMENDATIONID = r1.RECOMMENDATIONID
                    )
            )
          )
          AND :new.TYPEID = re.TYPEID;
    IF lowval > :new.VALUE OR highval < :new.VALUE
    THEN
      INSERT INTO ALERT VALUES (AlertIDSequence.nextval, 'outside-the-limit', :new.OID, 1, :new.PATIENTID, id);
    END IF;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
    lowval := 0;
  END;


CREATE OR REPLACE TRIGGER uniquePrimary
BEFORE INSERT
  ON SUPPORTS
FOR EACH ROW
  DECLARE
    isPrimaryValue SUPPORTS.isPrimary%TYPE;
    cnt            INTEGER;
      manyHealthSupportersException EXCEPTION;
    PRAGMA EXCEPTION_INIT ( manyHealthSupportersException, -123123 );
  BEGIN
    SELECT count(DISTINCT HEALTHSUPPORTERID)
    INTO cnt
    FROM SUPPORTS
    WHERE PATIENTID = :NEW.PATIENTID;
    IF (cnt = 0)
    THEN
      :NEW.isPrimary := 1;
    ELSIF (cnt = 1)
      THEN
        :NEW.ISPRIMARY := 0;
    ELSE RAISE manyHealthSupportersException;
    END IF;
  END;


CREATE OR REPLACE TRIGGER mustHaveHealthSupporter
BEFORE INSERT
  ON SICK
FOR EACH ROW
  DECLARE
    cnt INTEGER;
      healthSupporterException EXCEPTION;
    PRAGMA EXCEPTION_INIT ( healthSupporterException, -124124 );
  BEGIN
    SELECT count(DISTINCT HEALTHSUPPORTERID)
    INTO cnt
    FROM SUPPORTS
    WHERE PATIENTID = :NEW.PATIENTID;
    IF (cnt = 0)
    THEN
      RAISE healthSupporterException;
    END IF;
  END;