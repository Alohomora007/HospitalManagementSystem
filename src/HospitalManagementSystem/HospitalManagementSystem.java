package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Indo@112233";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Patients patients = new Patients(connection, scanner);
            Doctors doctors = new Doctors(connection, scanner);
            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM!!!");
                System.out.println("\n1. Add Patients\n2. View Patients\n3. View Doctors\n4. Book Appointments\n5. Exit");
                System.out.println("Enter your Choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        patients.addPatients();
                        break;
                    case 2:
                        patients.viewPatients();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patients, doctors, connection, scanner);
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("\nEnter valid Choice!!!\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void bookAppointment(Patients patients, Doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter patient ID: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patients.getPatientsById(patientId) && doctors.getDoctorsById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patients_id, doctors_id, appointments_date) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("\nAppointment Booked!\n");
                    } else {
                        System.out.println("\nFailed to Book Appointment!\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("\nDoctor not available on this date!\n");
            }
        } else {
            System.out.println("\nEither doctor or patient not available!\n");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctors_id = ? AND appointments_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
