import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
/**
 * Configuration class for database connection details.
 */
public class DBConfig {
    private String username;
    private String password;
    private String dbUrl;
    /**
     * Constructs a new DBConfig instance using the specified property file.
     *
     * @param propertyFilename The path to the property file containing database connection details.
     * @throws RuntimeException If an error occurs while loading the database configuration.
     */
    public DBConfig(String propertyFilename) {
        Properties identity = new Properties();
        try (InputStream stream = new FileInputStream(propertyFilename)) {
            identity.load(stream);
            this.username = identity.getProperty("username");
            this.password = identity.getProperty("password");
            this.dbUrl = identity.getProperty("dbUrl", "jdbc:mysql://db.cs.dal.ca:3306/vedant");
        } catch (Exception e) {
            throw new RuntimeException("Loading database configuration failed", e);
        }
    }
    /**
     * Gets the username for the database connection.
     *
     * @return The database username.
     */
    public String getUsername() {
        return username;
    }
    /**
     * Gets the password for the database connection.
     *
     * @return The database password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * Gets the URL for the database connection.
     *
     * @return The database URL.
     */
    public String getDbUrl() {
        return dbUrl;
    }
}
