package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Patients {
    private Connection connection;
    private Scanner scanner;

    public Patients(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatients() {
        try {
            System.out.print("Enter the name of Patient: ");
            String name = scanner.next();

            System.out.print("Enter patient age: ");
            int age = scanner.nextInt();


            System.out.print("Enter the patient gender: ");
            String gender = scanner.next();

            String query = "INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient Added Successfully");
            } else {
                System.out.println("Failed to add Patient");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input format. Please enter valid data.");
            scanner.next(); // Consume invalid input
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void viewPatients() {
        String query = "SELECT * FROM patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients:");
            System.out.println("+--------------+--------------+--------------+");
            System.out.println("| Patients_id  |    Name      |    Age       |");
            System.out.println("+--------------+--------------+--------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println(String.format("|%-15s|%-15s|%-15s|\n", id, name, age));
                System.out.println("+--------------+--------------+--------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getPatientsById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
