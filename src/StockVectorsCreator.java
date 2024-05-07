import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
/**
 * Utility class for creating vectors representing stocks held by each account.
 */
public class StockVectorsCreator {
        // Method to create vectors for stocks held by each account
    /**
     * Creates vectors representing stocks held by each account.
     *
     * @param connection The database connection.
     * @return A map where each key represents an account ID and the corresponding value is a map of stock symbols to shares owned.
     * @throws RuntimeException If an error occurs while initializing or creating the stock vectors.
     */
        public static Map<Integer, Map<String, Double>> createStockVectors(Connection connection)  {
            Map<Integer, Map<String, Double>> stockVectors = new HashMap<>();
            // Initialize stock vectors for each account with zeros
            try {
                initializeStockVectors(connection, stockVectors);
            } catch (SQLException e) {
                throw new RuntimeException("Error initializing stock vectors", e);
            }
            // SQL query to fetch data about stocks held by each account
            String sqlQuery = "SELECT astocks.accountID, s.stockSymbol, s.companyName, s.sectorID, sec.name AS sectorName, astocks.sharesOwned " +
                    "FROM AccountStocks AS astocks  " +
                    "JOIN stocks AS s ON astocks.stockSymbol = s.stockSymbol  " +
                    "JOIN sectors AS sec ON s.sectorID = sec.sectorID;";

            try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
                ResultSet rs = pstmt.executeQuery();

                // Iterate through the result set and construct vectors for each account
                while (rs.next()) {
                    int accountId = rs.getInt("accountID");
                    String stockSymbol = rs.getString("stockSymbol");
                    double sharesOwned = rs.getDouble("sharesOwned");

                    // Create or update the stock vector for the current account
                    if (!stockVectors.containsKey(accountId)) {
                        stockVectors.put(accountId, new HashMap<>());
                    }
                    stockVectors.get(accountId).put(stockSymbol, sharesOwned);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println(stockVectors);

            return stockVectors;
        }
    // Helper method to initialize stock vectors with zeros for all stocks
    /**
     * Initializes stock vectors with zeros for all stocks and accounts.
     *
     * @param connection   The database connection.
     * @param stockVectors The map to store the initialized stock vectors.
     * @throws SQLException If an SQL error occurs during initialization.
     */
    private static void initializeStockVectors(Connection connection, Map<Integer, Map<String, Double>> stockVectors) throws SQLException {
        String sqlQuery = "SELECT DISTINCT accountID FROM AccountStocks;";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int accountId = rs.getInt("accountID");
                stockVectors.put(accountId, new HashMap<>());
            }
        }

        // Get all distinct stock symbols
        sqlQuery = "SELECT DISTINCT stockSymbol FROM stocks;";
        try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String stockSymbol = rs.getString("stockSymbol");
                // Set initial share owned as 0 for each stock in each account
                for (Map<String, Double> vector : stockVectors.values()) {
                    vector.put(stockSymbol, 0.0);
                }
            }
        }
    }
}
