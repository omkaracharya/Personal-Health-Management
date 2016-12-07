package com.ncsu.dbms.project.healthsystem.controller;

import com.ncsu.dbms.project.healthsystem.entities.Disease;
import com.ncsu.dbms.project.healthsystem.entities.ObservationType;
import com.ncsu.dbms.project.healthsystem.entities.Recommendation;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by watve on 10/13/2016.
 */
public class PatientView implements View {

    public static final String countHealthSupporters = "SELECT count(DISTINCT HEALTHSUPPORTERID) FROM SUPPORTS WHERE PATIENTID = ?";
    private static final String selectPatient = "SELECT * FROM Patient WHERE patientID = ?";
    private static final String updatePatient1 = "UPDATE Patient SET ";
    private static final String updatePatient2 = "= ? WHERE patientID = ?";
    private static final String selectDiagnoses = "SELECT * FROM Disease WHERE diseaseID IN (SELECT diseaseID FROM Sick WHERE patientID = ?)";
    private static final String getAllDiseases = "SELECT * FROM Disease";
    private static final String addDiagnoses = "INSERT INTO Sick values(?, ?, ?)";
    private static final String deleteDiagnoses = "DELETE FROM Sick WHERE patientID = ? AND diseaseID = ?";
    private static final String selectAlerts = "SELECT a.alertType, ot.name, o.value, o.recordedTime, a.alertID FROM Alert a, Observation o, ObservationType ot WHERE a.patientID = ? AND a.isActive = 1 AND a.observationId = o.oid AND ot.typeId = o.typeId";
    private static final String insertAlert = "INSERT INTO Alert VALUES (AlertIDSequence.nextVal, ?, ?, 1, ?,?)";
    private static final String checkForObservation = "SELECT o1.oid FROM Observation o1 WHERE o1.typeID = ? AND o1.patientID = ? AND o1.recordedTime = (SELECT MAX(o2.recordedTime) FROM Observation o2 WHERE o2.typeId = ? AND o2.patientID = ?) AND (? - o1.recordedTime > ?)";
    private static final String selectHealthSupporters = "SELECT h.CONTACTINFO,h.HEALTHSUPPORTERID,s.DATEOFAUTHORIZATION,s.ISPRIMARY FROM HEALTHSUPPORTER h, SUPPORTS s WHERE h.HEALTHSUPPORTERID = s.HEALTHSUPPORTERID AND s.PATIENTID = ?";
    private static final String addHealthSupporter = "INSERT INTO Supports(PATIENTID,HEALTHSUPPORTERID,DATEOFAUTHORIZATION) VALUES (?, ?, ?)";
    private static final String editHealthSupporter = "UPDATE Supports SET dateOfAuthorization = ? WHERE patientId = ? AND healthSupporterId = ?";
    private static final String selectRecommendations = "SELECT ot.typeId, ot.name, ot.description, re.frequency, re.lowervalue, re.highervalue, re.RECOMMENDATIONID FROM recommendation re, observationtype ot WHERE recommendationId IN \n" +
            "(\n" +
            "\tSELECT p.recommendationId FROM patientrecommendation p WHERE p.patientId = ?\n" +
            "\tUNION\n" +
            "\t(\n" +
            "\t\tSELECT r.recommendationId FROM diseaseRecommendation d, recommendation r WHERE d.diseaseId IN (SELECT s.diseaseId FROM sick s WHERE s.patientId = ?) AND r.recommendationId = d.recommendationId AND r.typeId NOT IN\n" +
            "\t\t(\n" +
            "\t\tSELECT r1.typeId FROM patientrecommendation p, recommendation r1 WHERE p.patientId = ? AND p.recommendationId = r1.recommendationId\n" +
            "\t\t)\n" +
            "\t)\n" +
            ")\n" +
            "AND ot.typeId = re.typeId";
    private static final String selectObservations = "SELECT ot.name, o.value, o.recordedTime, o.ObservationTime from Observation o, ObservationType ot where o.patientId = ? AND ot.typeId = o.typeId";
    private static final String addRecommendation = "INSERT INTO Recommendation VALUES (RecommendationIDSequence.nextval, ?, ?, ?, ?)";
    private static final String addObservation = "INSERT INTO Observation VALUES (ObservationIDSequence.nextval, ?, ?, ?, ?, ?)";
    private static final String selectObservationType = "SELECT * FROM ObservationType";
    protected Scanner scanner;

    public PatientView(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void showActions() {
        System.out.println("");
        System.out.println("Menu :");
        System.out.println("1. Profile");
        System.out.println("2. Diagnoses");
        System.out.println("3. Health Indicators");
        System.out.println("4. Alerts");
        System.out.println("5. Health Supporters");
    }

    @Override
    public void performAction(Integer action, Integer personID, Connection connection) {
        switch(action)
        {
            case 1: showProfile(personID, connection);
                    if(askToEdit())
                        editProfile(personID, connection);
                    break;
            case 2: showDiagnoses(personID, connection);
                    switch(askToEditOrAdd("Disease "))
                    {
                        case 1: addNewDiagnoses(personID, connection);
                                break;
                        case 2: deleteDiagnoses(personID, connection);
                                break;
                    }
                    break;
            case 3: showHealthIndicators(personID, connection);
                    switch(askHealthIndicatorSubMenuChoice())
                    {
                        case 1: addNewObservation(personID, connection);
                                break;
                        case 2: addRecommendation(personID, connection);
                                break;
                        case 3: showPastObservations(personID, connection);
                                break;
                    }
                    break;
            case 4: checkFrequencyBasedAlerts(personID, connection);
                    showAlerts(personID, connection);
                    break;
            case 5: showExistingHealthSupporters(personID, connection);
                    switch (askToEditOrAdd("Health Supporters "))
                    {
                        case 1: addNewHealthSupporter(personID, connection);
                                break;
                        case 2: editHealthSupporter(personID, connection);
                                break;
                    }
        }
    }

    private void showProfile(Integer personID, Connection connection) {
        try{
            PreparedStatement ps = connection.prepareStatement(selectPatient);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = ps.getMetaData();
            if(rs.next()) {
                System.out.println("");
                System.out.println("Profile - ");
                System.out.println("Unique Id: " + rs.getInt(1));
                System.out.println("Name: " + rs.getString(3) + " " + rs.getString(4));
                System.out.println("Gender: " + rs.getString(6));
                System.out.println("Address: " + rs.getString(5));
                System.out.println("Date of Birth: " + rs.getDate(2));
                ArrayList<Disease> arrayList = getMyDiseases(personID, connection);
                System.out.print("Status: ");
                if(arrayList.size() == 0)
                    System.out.println("Well Patient");
                else
                    System.out.println("Sick Patient");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean askToEdit() {
        System.out.println("");
        System.out.print("Do you want to edit this information (y/n): ");
        Character character = scanner.next().charAt(0);
        return character.equals('y');
    }

    private void editProfile(Integer personID, Connection connection) {
        int choice = 0;
        while (choice < 1 || choice > 5) {
            System.out.println("(1: First Name, 2: Last Name, 3: Gender, 4: Address, 5: Date of Birth)");
            System.out.print("Select index of field you want to edit: ");
            choice = scanner.nextInt();
        }
        try {
            PreparedStatement ps = null;
            switch (choice) {
                case 1: System.out.print("Enter new first name: ");
                        ps = connection.prepareStatement(updatePatient1 + "firstName" + updatePatient2);
                        ps.setString(1, scanner.next());
                        break;
                case 2: System.out.print("Enter new last name: ");
                        ps = connection.prepareStatement(updatePatient1 + "lastName" + updatePatient2);
                        ps.setString(1, scanner.next());
                        break;
                case 3: System.out.print("Enter new gender: ");
                        ps = connection.prepareStatement(updatePatient1 + "gender" + updatePatient2);
                        ps.setString(1, String.valueOf(scanner.next().charAt(0)));
                        break;
                case 4: System.out.print("Enter new address: ");
                        ps = connection.prepareStatement(updatePatient1 + "address" + updatePatient2);
                        ps.setString(1, scanner.next());
                        break;
                case 5:
                    System.out.print("Enter new date of birth(MM/dd/yyyy): ");
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        ps = connection.prepareStatement(updatePatient1 + "dateOfBirth" + updatePatient2);
                        ps.setDate(1, new java.sql.Date(new Date(df.parse(scanner.next()).getTime()).getTime()));
                        break;
            }
            ps.setInt(2, personID);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Profile Updated!");
            showProfile(personID, connection);
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Disease> getMyDiseases(Integer personID, Connection connection) {
        ArrayList<Disease> arrayList = new ArrayList<>();
        try{
            PreparedStatement ps = connection.prepareStatement(selectDiagnoses);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int diseaseId = rs.getInt(1);
                String disease = rs.getString(2);
                Disease d = new Disease(diseaseId, disease);
                arrayList.add(d);
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private void showDiagnoses(Integer personID, Connection connection) {
        ArrayList<Disease> arrayList = getMyDiseases(personID, connection);
        System.out.println("");
        if(arrayList.size() == 0) {
            System.out.println("No disease found!");
        }
        else {
            System.out.println("Current diseases - ");
            for(Disease i:arrayList) {
                System.out.println(i.getDiseaseName());
            }
        }
    }

    private int askToEditOrAdd(String s) {
        System.out.println("");
        System.out.println(s + "Sub Menu: ");
        System.out.println("1. Add new");
        System.out.println("2. Edit");
        System.out.println("3. Return");
        System.out.print("Enter your choice : ");
        return scanner.nextInt();
    }

    private ArrayList<Disease> getAllDiseases(Connection connection) {
        ArrayList<Disease> arrayList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(getAllDiseases);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                arrayList.add(new Disease(rs.getInt(1), rs.getString(2)));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private void addNewDiagnoses(Integer personID, Connection connection) {
        ArrayList<Disease> allDiseases = getAllDiseases(connection);
        System.out.println("");
        System.out.print("(");
        for(Disease i:allDiseases) {
            System.out.print(i.getDiseaseID() + ":" + i.getDiseaseName() + " ");
        }
        System.out.println(")");
        System.out.print("Enter the disease ID : ");
        int diseaseID = scanner.nextInt();
        System.out.print("Enter the disease date : ");
        String sickDate = scanner.next();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date sicknessDate = null;
        try {
            sicknessDate = new Date(dateFormat.parse(sickDate).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try{
            PreparedStatement ps = connection.prepareStatement(addDiagnoses);
            ps.setInt(1, personID);
            ps.setInt(2, diseaseID);
            ps.setDate(3, sicknessDate);
            ps.executeUpdate();
            connection.commit();
            ps.close();

            System.out.println("Disease Added!");
            showDiagnoses(personID, connection);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("No such disease !");
        } catch (SQLException e) {
            System.out.println("Must have  health supporter before adding disease.");
        }

    }

    private void deleteDiagnoses(Integer personID, Connection connection) {
        ArrayList<Disease> allDiseases = getMyDiseases(personID, connection);
        if(allDiseases.size() == 0) {
            System.out.println("No disease currently!");
            return;
        }
        System.out.println("");
        System.out.print("(");
        for(Disease i:allDiseases) {
            System.out.print(i.getDiseaseID() + ":" + i.getDiseaseName() + " ");
        }
        System.out.println(")");
        int diseaseID = 0;
        boolean diseaseFound = false;
        while(!diseaseFound) {
            System.out.print("Enter the disease ID you wish to delete: ");
            diseaseID = scanner.nextInt();
            for (int i = 0; i < allDiseases.size(); i++) {
                if (diseaseID == allDiseases.get(i).getDiseaseID()) {
                    diseaseFound = true;
                    break;
                }
            }
        }
        try {
            PreparedStatement ps = connection.prepareStatement(deleteDiagnoses);
            ps.setInt(1, personID);
            ps.setInt(2, diseaseID);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Disease Deleted!");
            showDiagnoses(personID, connection);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Recommendation> getHealthIndicators(Integer personID, Connection connection) {
        ArrayList<Recommendation> arrayList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(selectRecommendations);
            ps.setInt(1, personID);
            ps.setInt(2, personID);
            ps.setInt(3, personID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Recommendation r = new Recommendation();
                ObservationType ot = new ObservationType();
                ot.setObservationTypeID(rs.getInt(1));
                ot.setObservationTypeName(rs.getString(2));
                ot.setDescription(rs.getString(3));
                r.setObservationType(ot);
                r.setFrequency(rs.getInt(4));
                r.setLowerValue(rs.getInt(5));
                r.setHigherValue(rs.getInt(6));
                r.setRecommendationID(rs.getInt(7));
                arrayList.add(r);
            }
            ps.close();
            return arrayList;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showHealthIndicators(Integer personID, Connection connection) {
        ArrayList<Recommendation> rs = getHealthIndicators(personID, connection);
        System.out.println("");
        if(rs.size() == 0) {
            System.out.println("No recommendations found!");
        }
        else {
            System.out.println("Current recommendations: ");
            for(Recommendation i:rs)
            {
                System.out.println(i.getObservationType().getObservationTypeName() + "- Freq: " + i.getFrequency() + " Low: " + i.getLowerValue() + " High: " + i.getHigherValue());
            }
        }
    }

    private int askHealthIndicatorSubMenuChoice() {
        System.out.println("");
        System.out.println("Health Indicator SubMenu: ");
        System.out.println("1. Add new Observation");
        System.out.println("2. Add new Recommendation");
        System.out.println("3. Show Past Observations");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    private void showObservationTypes(Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement(selectObservationType);
            ResultSet rs = ps.executeQuery();
            System.out.println("Type of Observation : ");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ": " + rs.getString(2) + " - " + rs.getString(3) + "(" + rs.getString(4) + ")");
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNewObservation(Integer personID, Connection connection) {
        System.out.println("");
        showObservationTypes(connection);
        System.out.print("Enter typeId: ");
        int type = scanner.nextInt();
        System.out.print("Enter recorded time (MM/dd/yyyy): ");
        String rTime = scanner.next();
        Date rdate, odate;
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            rdate = new java.sql.Date(df.parse(rTime).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date !!");
            return;
        }
        System.out.print("Enter observed time (MM/dd/yyyy): ");
        String oTime = scanner.next();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            odate = new java.sql.Date(df.parse(rTime).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date !!");
            return;
        }
        System.out.print("Enter value: ");
        int value = scanner.nextInt();
        try {
            PreparedStatement ps = connection.prepareStatement(addObservation);
            ps.setInt(1, value);
            ps.setDate(2, rdate);
            ps.setDate(3, odate);
            ps.setInt(4, type);
            ps.setInt(5, personID);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Observation Added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRecommendation(Integer personID, Connection connection) {
        System.out.println("");
        System.out.print("Enter frequency: ");
        int freq = scanner.nextInt();
        System.out.print("Enter low value: ");
        int low = scanner.nextInt();
        System.out.print("Enter upper value: ");
        int upper = scanner.nextInt();
        System.out.print("Enter type of observation: ");
        int type = scanner.nextInt();
        try {
            PreparedStatement ps = connection.prepareStatement(addRecommendation);
            ps.setInt(1, freq);
            ps.setInt(2, low);
            ps.setInt(3, upper);
            ps.setInt(4, type);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Recommendation Added!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showPastObservations(Integer personID, Connection connection) {
        System.out.println("");
        try {
            PreparedStatement ps = connection.prepareStatement(selectObservations);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                System.out.println("No past observations");
                return;
            }
            System.out.println("Past observations- ");
            do {
                System.out.println(rs.getString(1) + ": " + rs.getString(2) + " RTime: "+ rs.getString(3) + " OTime: " + rs.getString(4));
            } while (rs.next());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void checkFrequencyBasedAlerts(Integer personID, Connection connection) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        java.util.Date date = new java.util.Date();

        ArrayList<Recommendation> rs = getHealthIndicators(personID, connection);
        try {
            for(Recommendation i:rs)
            {
                Integer freq = i.getFrequency();
                Integer typeId = i.getObservationType().getObservationTypeID();
                if(freq != 0)
                {
                    PreparedStatement ps = connection.prepareStatement(checkForObservation);
                    ps.setInt(1, typeId);
                    ps.setInt(2, personID);
                    ps.setInt(3, typeId);
                    ps.setInt(4, personID);
                    ps.setDate(5, new java.sql.Date(date.getTime()));
                    ps.setInt(6, freq);
                    ResultSet rsOid = ps.executeQuery();
                    if(rsOid.next()) {
                        Integer oid = rsOid.getInt(1);
                        try {
                            PreparedStatement psInsert = connection.prepareStatement(insertAlert);
                            psInsert.setString(1, "low-activity");
                            psInsert.setInt(2, oid);
                            psInsert.setInt(3, personID);
                            psInsert.setInt(4, i.getRecommendationID());
                            psInsert.executeUpdate();
                            connection.commit();
                            psInsert.close();
                            //debug log
                            //System.out.println("Added freq alert for " + personID + " with oid - " + oid + " for recommendation: " + i.getRecommendationID());
                            break;
                        }
                        catch (SQLIntegrityConstraintViolationException e) {
                            // nothing to do here
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected boolean showAlerts(Integer personID, Connection connection) {
        System.out.println("");
        boolean hasAlerts = false;
        try {
            PreparedStatement ps = connection.prepareStatement(selectAlerts);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();

            if(!rs.next()) {
                System.out.println("No alerts at this time!");
            }
            else {
                hasAlerts = true;
                do {
                    System.out.print("Alert id: " + rs.getInt(5) + " Alert type: " + rs.getString(1) + " - " + rs.getString(2));
                    if ("low-activity".equals(rs.getString(1)))
                        System.out.println(" Last observation at " + rs.getString(4));
                    else
                        System.out.println(" Value - " + rs.getString(3));
                } while (rs.next());
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return hasAlerts;
    }

    private void showExistingHealthSupporters(Integer personID, Connection connection) {
        System.out.println("");
        try {
            PreparedStatement ps = connection.prepareStatement(selectHealthSupporters);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = ps.getMetaData();
            while(rs.next()) {
                String contactInfo = rs.getString(1);
                int id = rs.getInt(2);
                String doa = rs.getString(3);
                String isPrimary = rs.getInt(4) ==  1? "Primary" : "Secondary";
                System.out.println(id + ": " + isPrimary + " health supporter since " + doa + " contactInfo: " + contactInfo);
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNewHealthSupporter(Integer personID, Connection connection) {
        System.out.println("");
        System.out.print("Enter health supporter id: ");
        int hsId = scanner.nextInt();
        System.out.print("Enter DOA (MM/DD/YYYY): ");
        String doa = scanner.next();
        Date date;
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            date = new java.sql.Date(df.parse(doa).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date !!");
            return;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(addHealthSupporter);
            ps.setInt(1, personID);
            ps.setInt(2, hsId);
            ps.setDate(3, date);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Health Supporter Added!");
        }
        catch (SQLException e) {
            System.out.println("Cannot add more than 2 health supporters.");
        }
    }

    private void editHealthSupporter(Integer personID, Connection connection) {
        System.out.println("");
        System.out.print("Enter health supporter id you wish to edit: ");
        int hsId = scanner.nextInt();
        System.out.print("Enter new DOA (MM/DD/YYYY): ");
        String doa = scanner.next();
        Date date;
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            date = new java.sql.Date(df.parse(doa).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date !!");
            return;
        }
        try {
            PreparedStatement ps = connection.prepareStatement(editHealthSupporter);
            ps.setDate(1, date);
            ps.setInt(2, personID);
            ps.setInt(3, hsId);
            ps.executeUpdate();
            connection.commit();
            ps.close();
            System.out.println("Health Supporter Updated!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}