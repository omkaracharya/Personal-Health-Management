package com.ncsu.dbms.project.healthsystem.data;

import com.ncsu.dbms.project.healthsystem.entities.HealthSupporter;
import com.ncsu.dbms.project.healthsystem.entities.Patient;
import com.ncsu.dbms.project.healthsystem.entities.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by watve on 10/13/2016.
 */
public class Authentication {

    private static final String checkUser = "select personID from Person where userName = ? and password = ?";
    private static final String checkPatient = "select patientID from Patient where patientID = ?";
    private static final String checkHealthSupporter = "select healthSupporterID from HealthSupporter where healthSupporterID = ?";
    private String userName;
    private String password;

    public Authentication() {
    }

    public Authentication(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Integer login(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkUser);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Integer personID = resultSet.getInt(1);
            resultSet.close();
            preparedStatement.close();
            return personID;
        } catch (SQLException e) {
            System.out.println("Login Incorrect.");
            return null;
        }
    }

    public void authenticate(Scanner scanner) {
        System.out.print("Please enter username : ");
        userName = scanner.next();
        System.out.print("Please enter password : ");
        password = scanner.next();
    }

    public UserType getType(Integer userID, Connection connection) {
        Boolean isPatient = false;
        Boolean isHealthSupporter = false;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(checkPatient);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            isPatient = resultSet.next();
            resultSet.close();
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(checkHealthSupporter);
            preparedStatement.setInt(1, userID);
            resultSet = preparedStatement.executeQuery();
            isHealthSupporter = resultSet.next();
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (isPatient && isHealthSupporter)
            return UserType.BOTH;
        else if (isPatient)
            return UserType.PATIENT;
        else if (isHealthSupporter)
            return UserType.HEALTH_SUPPORTER;
        else
            return UserType.NONE;
    }

    public Person signup() {
        return new Person(userName, password);
    }

    public Person assignAccountType(Integer userID, Scanner scanner) {
        Person person = null;
        while (null == person) {
            System.out.println("Account type : ");
            System.out.println("1) Patient ");
            System.out.println("2) Health Supporter ");
            System.out.print("Enter your choice : ");
            Integer type = scanner.nextInt();
            if (1 == type) {
                person = new Patient();
            } else if (2 == type) {
                person = new HealthSupporter();
            } else {
                System.out.println("Incorrect choice !! ");
            }
        }
        person.setPersonID(userID);
        person.create(scanner);
        return person;
    }
}
