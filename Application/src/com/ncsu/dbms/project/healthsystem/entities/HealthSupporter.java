package com.ncsu.dbms.project.healthsystem.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by watve on 10/12/2016.
 */

public class HealthSupporter extends Person {

    private static final String insertQuery = "insert into HealthSupporter values(?,?)";
    private String contactInfo;

    public HealthSupporter() {
    }

    public HealthSupporter(Integer healthSupporterID, String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public void create(Scanner scanner) {
        System.out.print("Enter contactInfo: ");
        contactInfo = scanner.next();
    }

    @Override
    public Integer insert(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, getPersonID());
            preparedStatement.setString(2, contactInfo);
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return getPersonID();
    }
}
