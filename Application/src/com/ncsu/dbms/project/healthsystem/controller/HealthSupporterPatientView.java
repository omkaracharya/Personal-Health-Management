package com.ncsu.dbms.project.healthsystem.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HealthSupporterPatientView extends PatientView {
	private static final String clearAlert = "UPDATE Alert SET isActive=0 WHERE alertID=?";
	private static final String checkAlert = "SELECT * FROM Alert WHERE patientID = ? AND alertID = ?";
    private static final String selectAlerts = "SELECT a.alertType, ot.name, o.value, o.recordedTime, a.alertID from Alert a, Observation o, ObservationType ot where a.patientID = ? AND a.isActive = 1 AND a.observationId = o.oid AND ot.typeId = o.typeId";

	public HealthSupporterPatientView(Scanner scanner) {
		super(scanner);
	}

	@Override
	public void showActions() {
        System.out.println("Menu :");
        System.out.println("1. Profile");
        System.out.println("2. Diagnoses");
        System.out.println("3. Health Indicators");
        System.out.println("4. Alerts");
	}

	@Override
	public void performAction(Integer action, Integer personID, Connection connection) {
		if (action == 5) {
			return;
		} else if (action == 4) {
			super.checkFrequencyBasedAlerts(personID, connection);
			if (showAlerts(personID, connection))
				clearAlerts(personID, connection);
		} else {
			super.performAction(action, personID, connection);
		}
	}

    /*private void showAlerts(Integer personID, Connection connection) {
		System.out.println("");
        try {
            PreparedStatement ps = connection.prepareStatement(selectAlerts);
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()) {
                System.out.println("No alerts at this time!");
            }
            else {
                 do{
                	System.out.print(rs.getString("alertID") + "\t");
                    System.out.print("Alert type: " + rs.getString(1) + " - " + rs.getString(2));
					if (rs.getString(1) == "low-activity")
						System.out.println(" Last observation at " + rs.getString(4));
					else
						System.out.println(" Value - " + rs.getString(3));
                }while (rs.next());
            }
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
	
	private void clearAlerts(int patientId, Connection connection) {
		System.out.print("Would you like to clear an alert? (y/n) : ");
		if ( scanner.next().toLowerCase().charAt(0) == 'y' ) {
			System.out.print("Which alert would you like to clear? (id) : ");
			if ( ! scanner.hasNextInt() ) {
				System.out.println("Invalid alert id");
				return;
			}
			int id = scanner.nextInt();
			clearAlert(patientId, id, connection);
		}
	}

	private void clearAlert(int patientId, int alertId, Connection connection) {
		if ( !alertBelongsToPatient(patientId, alertId, connection) ) {
			System.out.println("Invalid alert id");
			return;
		}
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(clearAlert);
			ps.setInt(1, alertId);
			ps.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if ( ps != null ) {
				try {
					ps.close();
				} catch (SQLException e) {
					// ignored
				}
			}
		}
	}

	private boolean alertBelongsToPatient(int patientId, int alertId, Connection connection) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try {
    		ps = connection.prepareStatement(checkAlert);
			ps.setInt(1, patientId);
			ps.setInt(2, alertId);
			rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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

}
