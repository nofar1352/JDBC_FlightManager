import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class provides a simple utility to update the delay status of a specific flight in the database.
 */
public class SimpleFlightUpdater {

    /**
     * Main method that handles user input and updates the flight's delay status.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))) {

            System.out.print("Enter MySQL password: ");
            String password = reader.readLine();

            try (Connection connection =
                         DriverManager.getConnection(url, user, password)) {

                System.out.print("Enter new delay status for flight 'LX354': ");
                String userInput = reader.readLine();

                String sql = "UPDATE Arrivals SET delay_status = ? WHERE flight_number = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    // Using PreparedStatement to prevent SQL Injection
                    // The first question mark (?) is replaced by the value of userInput
                    // The second question mark (?) is replaced by the string constant "LX354"
                    statement.setString(1, userInput);
                    statement.setString(2, "LX354");

                    int rowsAffected = statement.executeUpdate();

                    System.out.println("Number of affected rows: " + rowsAffected);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
