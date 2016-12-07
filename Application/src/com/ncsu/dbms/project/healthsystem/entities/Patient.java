package com.ncsu.dbms.project.healthsystem.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by watve on 10/12/2016.
 */
public class Patient extends Person {

    private static final String insertQuery = "insert into Patient values(?,?,?,?,?,?)";
    private Date dateOfBirth;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;

    public Patient() {
    }

    public Patient(Integer _pid, Date _dob, String _firstName, String _lastName, String _address, String _gender) {
        dateOfBirth = _dob;
        firstName = _firstName;
        lastName = _lastName;
        address = _address;
        gender = _gender;
        setPersonID(_pid);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public void create(Scanner scanner) {
        System.out.print("Enter first name : ");
        firstName = scanner.next();
        System.out.print("Enter last name : ");
        lastName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter address : ");
        address = scanner.nextLine();
        System.out.print("Enter gender : ");
        gender = scanner.next();
        while (null == dateOfBirth)
        try {
            System.out.print("Enter date of birth (MM/dd/yyyy) : ");
            String dob = scanner.next();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            dateOfBirth = df.parse(dob);
        } catch (ParseException e) {
            System.out.println("Invalid date !!");
        }
    }

    @Override
    public Integer insert(Connection connection) {
        //Query for inserting patient
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, getPersonID());
            preparedStatement.setDate(2, new java.sql.Date(dateOfBirth.getTime()));
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, String.valueOf(gender.charAt(0)));
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return getPersonID();
    }
}
