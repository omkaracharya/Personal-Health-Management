package com.ncsu.dbms.project.healthsystem.entities;

import java.sql.*;
import java.util.Scanner;

/**
 * Created by watve on 10/12/2016.
 */

public class Person {
    private static final String insertQuery = "insert into Person values(PersonIDSequence.nextval,?,?)";
    private static final String getSequenceValue = "select PersonIDSequence.currval from dual";
    private Integer personID;
    private String userName;
    private String password;


    public Person() {
    }

    public Person(String userName, String password) {
        this.userName = userName;
        this.password = password;

    }

    public Integer getPersonID() {
        return personID;
    }

    public void setPersonID(Integer personID) {
        this.personID = personID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void create(Scanner scanner) {
        System.out.print("Please enter username : ");
        userName = scanner.next();
        System.out.print("Please enter password : ");
        password = scanner.next();
    }

    public Integer insert(Connection connection) {
        // Query for inserting Person and return auto incremented id
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Username already exists !!");
            return null;
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(getSequenceValue);
            resultSet.next();
            Integer ID = resultSet.getInt(1);
            resultSet.close();
            statement.close();
            connection.commit();
            return ID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
