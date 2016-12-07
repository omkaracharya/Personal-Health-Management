package com.ncsu.dbms.project.healthsystem.controller;

import com.ncsu.dbms.project.healthsystem.data.Authentication;
import com.ncsu.dbms.project.healthsystem.data.DatabaseConnection;
import com.ncsu.dbms.project.healthsystem.data.UserType;
import com.ncsu.dbms.project.healthsystem.entities.HealthSupporter;
import com.ncsu.dbms.project.healthsystem.entities.Patient;
import com.ncsu.dbms.project.healthsystem.entities.Person;
import com.ncsu.dbms.project.healthsystem.util.ScriptRunner;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by watve on 10/12/2016.
 * <p>
 * The main command line application driver
 */

public class Main {
    private static Scanner scanner;
    private static String[] queries = new String[]{"SELECT count(DISTINCT HEALTHSUPPORTERID)\n" +
            "FROM SUPPORTS\n" +
            "WHERE extract(MONTH FROM DATEOFAUTHORIZATION) = '09' AND extract(YEAR FROM DATEOFAUTHORIZATION) = '2016' AND\n" +
            "      PATIENTID IN (SELECT PATIENTID\n" +
            "                    FROM SICK\n" +
            "                    WHERE DISEASEID = 1)", "SELECT count(DISTINCT PATIENTID)\n" +
            "FROM ALERT\n" +
            "WHERE ALERTTYPE = 'low-activity'", "SELECT\n" +
            "  FIRSTNAME,\n" +
            "  LASTNAME\n" +
            "FROM PATIENT\n" +
            "WHERE PATIENTID IN (SELECT PATIENTID\n" +
            "                    FROM PATIENT INTERSECT SELECT HEALTHSUPPORTERID\n" +
            "                                           FROM HEALTHSUPPORTER)", "SELECT\n" +
            "  FIRSTNAME,\n" +
            "  LASTNAME\n" +
            "FROM PATIENT\n" +
            "WHERE PATIENTID NOT IN (SELECT DISTINCT PATIENTID\n" +
            "                        FROM SICK)", "SELECT COUNT(DISTINCT PATIENTID)\n" +
            "FROM OBSERVATION\n" +
            "WHERE OBSERVATIONTIME <> RECORDEDTIME"};

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(connection, false, false);
        /*try {
            scriptRunner.runScript(new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/sequences.sql"))));
            scriptRunner.runScript(new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/tables.sql"))));
            scriptRunner.runScript(new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/sampledata.sql"))));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        scanner = new Scanner(System.in);
        Authentication authentication = new Authentication();
        while (true) {
            promptSignUpOrLogin();
            Integer choice = scanner.nextInt();
            if (1 == choice) {
                Integer userID = null;
                while (null == userID) {
                    authentication.authenticate(scanner);
                    userID = authentication.login(connection);
                }
                System.out.println("userID = " + userID);
                UserType userType = authentication.getType(userID, connection);
                System.out.println("userType = " + userType);
                if (UserType.PATIENT.equals(userType)) {
                    System.out.print("Do you want to sign up as Health Supporter as well? (y/n): ");
                    Character ch = scanner.next().charAt(0);
                    if ('y' == ch) {
                        HealthSupporter healthSupporter = new HealthSupporter();
                        healthSupporter.setPersonID(userID);
                        healthSupporter.create(scanner);
                        healthSupporter.insert(connection);
                        System.out.println("You are now a health supporter as well !!");
                    }
                } else if (UserType.HEALTH_SUPPORTER.equals(userType)) {
                    System.out.print("Do you want to sign up as Patient as well? (y/n): ");
                    Character ch = scanner.next().charAt(0);
                    if ('y' == ch) {
                        Patient patient = new Patient();
                        patient.setPersonID(userID);
                        patient.create(scanner);
                        patient.insert(connection);
                        System.out.println("You are now a patient as well !!");
                    }
                }
                View view = chooseView(userType);
                while (true) {
                    if (null == view) {
                        System.out.println("No actions to show.");
                        break;
                    }
                    view.showActions();
                    System.out.print("Enter your choice or press '0' to logout: ");
                    Integer action = scanner.nextInt();
                    if (0 == action)
                        break;
                    view.performAction(action, userID, connection);
                }
                System.out.println("Logged out Successfully.");
            } else if (2 == choice) {
                Integer userID = null;
                Person newUser = null;
                while (null == userID) {
                    authentication.authenticate(scanner);
                    newUser = authentication.signup();
                    userID = newUser.insert(connection);
                }
                newUser = authentication.assignAccountType(userID, scanner);
                newUser.insert(connection);
                System.out.println("Signed up Successfully.");
            } else if (3 == choice) {
                int queryID = 1;
                for (String query : queries) {
                    try {
                        System.out.println("Query " + queryID++ + ": \n");
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        ResultSet rs = preparedStatement.executeQuery();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int cols = rsmd.getColumnCount();
                        for (int i = 1; i <= cols; i++) {
                            String name = rsmd.getColumnLabel(i);
                            System.out.print(name + "\t");
                        }
                        System.out.println("");
                        while (rs.next()) {
                            for (int i = 1; i <= cols; i++) {
                                String value = rs.getString(i);
                                System.out.print(value + "\t");
                            }
                            System.out.println("");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("\n");
                }
            } else {
                break;
            }
        }
        databaseConnection.close();
        scanner.close();
    }

    private static void promptSignUpOrLogin() {
        System.out.println("Please choose your option : ");
        System.out.println("1.Login ");
        System.out.println("2.Sign Up ");
        System.out.println("3.Execute queries ");
        System.out.println("4.Exit ");
        System.out.print("Enter your choice : ");
    }

    private static View chooseView(UserType userType) {
        if (UserType.BOTH.equals(userType)) {
            System.out.println("Please choose your view : ");
            System.out.println("1.Patient View ");
            System.out.println("2.Health Supporter View ");
            System.out.print("Enter your choice : ");
            Integer choice = scanner.nextInt();
            if (1 == choice)
                return new PatientView(scanner);
            else if (2 == choice)
                return new HealthSupporterView(scanner);
            else
                return null;
        } else if (UserType.PATIENT.equals(userType)) {
            return new PatientView(scanner);
        } else if (UserType.HEALTH_SUPPORTER.equals(userType)) {
            return new HealthSupporterView(scanner);
        }
        return null;
    }
}
