
import java.sql.*;
import java.util.Scanner;

public class FlightViewer {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/FlightDB";

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            try (Connection connection =
                         DriverManager.getConnection(url, "root", password)) {

                System.out.print("Enter target terminal number (2, 3, 4, 5): ");
                String terminal = scanner.nextLine();

                if (!terminal.equals("2") &&
                        !terminal.equals("3") &&
                        !terminal.equals("4") &&
                        !terminal.equals("5")) {

                    System.out.println("Invalid terminal number.");
                    return;
                }

                // Check actual database column names
                DatabaseMetaData meta = connection.getMetaData();

                System.out.println("\nArrivals columns:");
                try (ResultSet columns = meta.getColumns(
                        connection.getCatalog(), null, "Arrivals", null)) {
                    while (columns.next()) {
                        System.out.println(columns.getString("COLUMN_NAME"));
                    }
                }

                System.out.println("\nFlightRoutes columns:");
                try (ResultSet columns = meta.getColumns(
                        connection.getCatalog(), null, "FlightRoutes", null)) {
                    while (columns.next()) {
                        System.out.println(columns.getString("COLUMN_NAME"));
                    }
                }

                String sql =
                        "SELECT Arrivals.*, FlightRoutes.* " +
                                "FROM Arrivals " +
                                "INNER JOIN FlightRoutes " +
                                "ON Arrivals.flight_number = FlightRoutes.flight_number " +
                                "WHERE Arrivals.terminal = ? " +
                                "ORDER BY Arrivals.scheduled ASC";

                try (PreparedStatement pstmt =
                             connection.prepareStatement(sql)) {

                    pstmt.setString(1, terminal);

                    try (ResultSet rs = pstmt.executeQuery()) {

                        ResultSetMetaData rsMeta = rs.getMetaData();
                        int count = rsMeta.getColumnCount();

                        for (int i = 1; i <= count; i++) {
                            System.out.printf("%-22s",
                                    rsMeta.getColumnLabel(i));
                        }
                        System.out.println();

                        boolean found = false;

                        while (rs.next()) {
                            found = true;

                            for (int i = 1; i <= count; i++) {
                                System.out.printf("%-22s",
                                        rs.getString(i));
                            }
                            System.out.println();
                        }

                        if (!found) {
                            System.out.println(
                                    "No flights found for terminal " + terminal);
                        }
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
