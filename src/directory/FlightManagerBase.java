import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Base Data Access Layer (DAL) utility for the Airport Flight Arrivals Management System.
 * <p>
 * Connects to the normalized {@code FlightDB} MySQL schema and displays a joined live arrivals
 * board combining {@code Arrivals} and {@code FlightRoutes}.
 * </p>
 *
 * @author Braude College of Engineering - Software Engineering Methods
 * @version 2.0 (2026-2027)
 */
public class FlightManagerBase {

    /** JDBC connection URL pointing to FlightDB with public key retrieval and Jerusalem timezone enabled. */
    private static final String URL =
            "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";

    /** Default MySQL administrative user. */
    private static final String USER = "root";

    /**
     * Application entry point. Prompts for the MySQL root password, establishes a JDBC connection
     * using try-with-resources, and renders the joined arrivals board to standard output.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Please enter your MySQL root password: ");
            String password = scanner.nextLine();

            // 1. Establish connection using Try-With-Resources (auto-closes connection)
            try (Connection conn = DriverManager.getConnection(URL, USER, password)) {
                System.out.println("Successfully connected to FlightDB!\n");

                // 2. Fetch and display joined data from FlightRoutes and Arrivals
                displayArrivalsBoard(conn);

            } catch (SQLException e) {
                System.err.println("Database connection failed!");
                System.err.println("Error Code: " + e.getErrorCode());
                System.err.println("Message: " + e.getMessage());
            }
        }
    }

    /**
     * Queries both {@code Arrivals} and {@code FlightRoutes} via an {@code INNER JOIN}
     * and prints a formatted arrivals table to the console.
     *
     * @param conn active JDBC {@link Connection} to {@code FlightDB}
     */
    private static void displayArrivalsBoard(Connection conn) {
        String sql = "SELECT a.id, a.flight_number, r.from_city, a.scheduled, a.delay_status, a.terminal " +
                     "FROM Arrivals a " +
                     "INNER JOIN FlightRoutes r ON a.flight_number = r.flight_number " +
                     "ORDER BY a.id ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println(" --- Flight Arrivals Board ---");
            System.out.printf("%-5s | %-10s | %-15s | %-10s | %-20s | %-10s%n",
                    "ID", "Flight", "From", "Scheduled", "Status", "Terminal");
            System.out.println("--------------------------------------------------------------------------------------");

            int rowCount = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String flightNumber = rs.getString("flight_number");
                String fromCity = rs.getString("from_city");
                String scheduled = rs.getString("scheduled");
                String delayStatus = rs.getString("delay_status");
                String terminal = rs.getString("terminal");

                System.out.printf("%-5d | %-10s | %-15s | %-10s | %-20s | %-10s%n",
                        id, flightNumber, fromCity, scheduled, delayStatus, terminal);
                rowCount++;
            }
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.println("Total flights displayed: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Failed to fetch flight data.");
            System.err.println("Message: " + e.getMessage());
        }
    }
}
