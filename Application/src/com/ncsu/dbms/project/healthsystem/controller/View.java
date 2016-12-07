package com.ncsu.dbms.project.healthsystem.controller;

import java.sql.Connection;

/**
 * Created by watve on 10/13/2016.
 */
public interface View {

    /**
     * Prints the available actions
     */
    void showActions();


    /**
     * Performs selected action on person
     *
     * @param action
     * @param personID
     */
    void performAction(Integer action, Integer personID, Connection connection);
}
