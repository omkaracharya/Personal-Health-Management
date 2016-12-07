package com.ncsu.dbms.project.healthsystem.controller;

import com.ncsu.dbms.project.healthsystem.entities.HealthSupporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by watve on 10/13/2016.
 */
public class HealthSupporterView implements View {
	private static final String getPatients = "SELECT P.patientid, P.firstname, P.lastname "
			+ "FROM Patient P, Supports S WHERE P.patientid = S.patientid AND S.healthsupporterid=? "
			+ "AND S.dateOfAuthorization < SYSDATE "
			+ "ORDER BY P.patientid ";
	private static final String getSupporter = "SELECT * FROM HealthSupporter WHERE healthsupporterid=?";
	private static final String updateSupporter = "UPDATE HealthSupporter Set ContactInfo = ? WHERE healthsupporterid = ?";
	
	private Scanner scanner;

	public HealthSupporterView(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public void showActions() {
    	System.out.println("Menu:");
    	System.out.println("1. Profile");
    	System.out.println("2. Manage patient");
    }

    @Override
    public void performAction(Integer action, Integer personID, Connection connection) {
    	switch(action) {
    		case 1:
    			showProfile(personID, connection);
    			if ( askToEdit() )
    				editProfile(personID, connection);
    			break;
    		case 2:
    			showPatients(personID, connection);
    			break;
    		default:
    			System.out.println("Invalid command");
    			return;
    	}
    }
    
    private void showProfile(int personID, Connection connection) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		ps = connection.prepareStatement(getSupporter);
    		ps.setInt(1, personID);
    		rs = ps.executeQuery();
    		if ( ! rs.next() ) {
    			throw new IllegalArgumentException("invalid health supporter: " + personID);
    		}
    		System.out.println("Id: " + rs.getString("HealthSupporterId"));
    		System.out.println("Contact info: " + rs.getString("ContactInfo"));
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} finally {
    		if ( ps != null ) {
    			try {
    				ps.close();
    			} catch (SQLException e) {
    				//ignored
    			}
    		}
    		if ( rs != null ) {
    			try {
					rs.close();
				} catch (SQLException e) {
					// ignored
				}
    		}
    	}
    }
    
    private void editProfile(int personID, Connection connection) {
    	HealthSupporter updated = new HealthSupporter();
    	updated.create(scanner);
    	PreparedStatement ps = null;
    	try {
    		ps = connection.prepareStatement(updateSupporter);
    		ps.setString(1, updated.getContactInfo());
    		ps.setInt(2,personID);
    		ps.executeUpdate();
    		connection.commit();
    		System.out.println("Profile updated.");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} finally {
    		if ( ps != null ) {
    			try {
    				ps.close();
    			} catch (SQLException e) {
    				//ignored
    			}
    		}
    	}
    }
    
    private void showPatients(int personID, Connection connection) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		ps = connection.prepareStatement(getPatients);
    		ps.setInt(1, personID);
    		rs = ps.executeQuery();
    		List<Integer> ids = new LinkedList<Integer>();
			boolean hasPatients = false;
			while ( rs.next() ) {
				hasPatients = true;
				ids.add(rs.getInt("patientid"));
    			System.out.println(rs.getInt("patientid") + "\t" 
    						+ rs.getString("lastname") + ", " + rs.getString("firstname"));
    		}
			if (hasPatients) {
				System.out.println("Which patient would you like to edit(id)? or (q) to quit");
				if (scanner.hasNextInt()) {
					int id = scanner.nextInt();
					if (!ids.contains(id)) {
						System.out.println("You are not authorized to manage this patient.");
						return;
					}
					managePatient(id, connection);
				} else if (scanner.next().toLowerCase().charAt(0) == 'q') {
					return;
				} else {
					System.out.println("invlalid id");
					return;
				}
			} else {
				System.out.println("You don't have any patients yet !!");
			}
		} catch (SQLException e) {
    		e.printStackTrace();
    	} finally {
    		if ( ps != null ) {
    			try {
    				ps.close();
    			} catch (SQLException e) {
    				//ignored
    			}
    		}
    		if ( rs != null ) {
    			try {
					rs.close();
				} catch (SQLException e) {
					// ignored
				}
    		}
    	}
    }
    
    private boolean askToEdit() {
        System.out.print("Do you want to edit this information(y/n): ");
        Character character = scanner.next().charAt(0);
        return Character.toLowerCase(character) == 'y';	
    }
    
    private void managePatient(int patientId, Connection connection) {
		View view = new HealthSupporterPatientView(scanner);
		while (true) {
            view.showActions();
            System.out.print("Enter your choice : ");
            Integer action = scanner.nextInt();
            view.performAction(action, patientId, connection);
            System.out.print("Do you want to continue with this patient? (y/n) : ");
            Character character = scanner.next().charAt(0);
            if (!character.equals('y'))
                break;
        }
    }
}
