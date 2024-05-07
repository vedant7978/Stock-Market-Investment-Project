import java.sql.Connection;
import java.sql.DriverManager;
/**
 * Utility class for establishing a database connection.
 */
public class DBConnection {
    /**
     * Establishes a connection to the database using the provided URL, username, and password.
     *
     * @param url  The URL of the database.
     * @param user The username for authentication.
     * @param pass The password for authentication.
     * @return A Connection object representing the database connection.
     * @throws RuntimeException If there is an error connecting to the database.
     */
    public static Connection getConnection(String url, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
}
