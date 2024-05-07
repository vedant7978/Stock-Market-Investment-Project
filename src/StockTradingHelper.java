import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockTradingHelper {
    /**
     * Retrieves the stock ID from the database based on the given stock symbol.
     *
     * @param stockSymbol The symbol of the stock to retrieve the ID for.
     * @param connect     The database connection.
     * @return            The ID of the stock if found, otherwise -1.
     */
    public static int getStockIdFromSymbol(String stockSymbol, Connection connect) {
        int stockId = -1; // Initialize with a default value

        try {
            String sql = "SELECT stockID FROM stocks WHERE stockSymbol = ?";
            PreparedStatement pstmt = connect.prepareStatement(sql);
            pstmt.setString(1, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                stockId = rs.getInt("stockID");
            }
        } catch (SQLException e) {
            System.out.println("Database access error: " + e.getMessage());
        }

        return stockId;
    }
    /**
     * Checks whether the specified account has the reinvestment preference set.
     *
     * @param accountId The ID of the account to check.
     * @param connect   The database connection.
     * @return          True if the account has the reinvestment preference set, otherwise false.
     */
    static boolean shouldReinvest(int accountId, Connection connect) {
        boolean reinvest = false;
        String sql = "SELECT reinvest FROM Accounts WHERE accountID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                reinvest = rs.getBoolean("reinvest");
            }
        } catch (SQLException e) {
            System.out.println("Error checking reinvestment preference: " + e.getMessage());
        }
        return reinvest;
    }
    /**
     * Retrieves the total number of shares owned by the firm for a given stock symbol.
     *
     * @param stockSymbol The symbol of the stock for which to retrieve the firm shares.
     * @param connect     The database connection.
     * @return            The total number of shares owned by the firm for the specified stock symbol.
     */
    private static int getFirmShares(String stockSymbol,Connection connect) {
        int shares = 0;

        String sql = "SELECT SUM(sharesOwned) as totalShares FROM AccountStocks WHERE stockSymbol = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                shares = rs.getInt("totalShares");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching firm shares: " + e.getMessage());
            // Handle exception (e.g., log the error or rethrow as a runtime exception)
        }

        return shares;
    }
    /**
     * Retrieves the number of shares owned for a given stock symbol from the Dividends table.
     *
     * @param stockSymbol The symbol of the stock for which to retrieve the number of shares owned.
     * @param connect     The database connection.
     * @return            The number of shares owned for the specified stock symbol.
     */
    static double getSharesOwned(String stockSymbol, Connection connect) {
        int stockId = getStockIdFromSymbol(stockSymbol, connect);
        String selectSql = "SELECT shareOwned FROM Dividends WHERE stockId = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(selectSql)) {
            pstmt.setInt(1, stockId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("shareOwned");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving sharesOwned: " + e.getMessage());
        }
        // Return a default value if no record found
        return 0.0;
    }
    /**
     * Retrieves a list of all sector names from the 'sectors' table in the database.
     *
     * @param connect The database connection.
     * @return        A list containing all sector names.
     * @throws SQLException If a database access error occurs.
     */
    static List<String> getAllSectorNames(Connection connect) throws SQLException {
        List<String> sectorNames = new ArrayList<>();

        // Query to fetch all sector names from the 'sectors' table
        String query = "SELECT name FROM sectors";

        try (PreparedStatement pstmt = connect.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String sectorName = rs.getString("name");
                sectorNames.add(sectorName);
            }
        }

        return sectorNames;
    }
    /**
     * Checks if a stock with the given stock symbol exists in the stocks table.
     *
     * @param stockSymbol The symbol of the stock to check.
     * @param connect     The database connection.
     * @return            True if the stock exists, false otherwise.
     */
    static boolean stockExists(String stockSymbol, Connection connect)  {
        // Query to check if the stockSymbol exists in the stocks table
        String query = "SELECT COUNT(*) AS count FROM stocks WHERE stockSymbol = ?";

        try (PreparedStatement pstmt = connect.prepareStatement(query)) {
            pstmt.setString(1, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
    /**
     * Retrieves the profile weights for a given profile type from the database.
     *
     * @param profileType The type of profile for which to retrieve weights.
     * @param connect     The database connection.
     * @return            A map containing sector names as keys and corresponding weights as values.
     */
    static Map<String, Integer> getProfileWeights(String profileType, Connection connect) {
        Map<String, Integer> profileWeights = new HashMap<>();
        try {
            String fetchProfileSql = "SELECT s.name, ps.percentage FROM ProfileSectors ps JOIN sectors s ON ps.sectorID = s.sectorID JOIN Profiles p ON ps.profileID = p.profileID WHERE p.profileName = ?";
            PreparedStatement pstmt = connect.prepareStatement(fetchProfileSql);
            pstmt.setString(1, profileType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sectorName = rs.getString("name");
                int percentage = rs.getInt("percentage");
                profileWeights.put(sectorName, percentage);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching profile weights: " + e.getMessage());
        }
        return profileWeights;
    }

    public static int getNumberOfSectors(Connection connection) throws SQLException {
        int numberOfSectors = 0;
        String query = "SELECT COUNT(*) AS numSectors FROM sectors";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                numberOfSectors = resultSet.getInt("numSectors");
            }
        }

        return numberOfSectors;
    }

}
