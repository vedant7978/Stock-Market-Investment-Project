import java.sql.*;

public class FirmDividendManager {
    /**
     * Retrieves the fractional shares of the firm for a given stock symbol from the 'AccountFractionalShares' table in the database.
     *
     * @param stockSymbol The symbol of the stock for which to retrieve fractional shares.
     * @param connect     The database connection.
     * @return            The fractional shares of the firm for the specified stock symbol.
     */
    static double getFirmFractionalShares(String stockSymbol, Connection connect) {

        double fractionalShares = 0.0;
        String sql = "SELECT fractionalShares FROM AccountFractionalShares WHERE stockSymbol = ?";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS AccountFractionalShares (stockSymbol VARCHAR(50),fractionalShares DECIMAL(10,4),PRIMARY KEY (stockSymbol),FOREIGN KEY (stockSymbol) REFERENCES stocks(stockSymbol));");
            try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
                pstmt.setString(1, stockSymbol);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    fractionalShares = rs.getDouble("fractionalShares");
                }
            } catch (SQLException e) {
                System.out.println("Error fetching firm fractional shares: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return fractionalShares;
    }
    /**
     * Updates the fractional shares of the firm for a given stock symbol in the 'AccountFractionalShares' table in the database.
     *
     * @param stockSymbol  The symbol of the stock for which to update fractional shares.
     * @param sharesChange The change in fractional shares to apply.
     * @param connect      The database connection.
     */
    private static void updateFirmFractionalShares(String stockSymbol, double sharesChange, Connection connect) {
        // Attempt to update an existing record
//        String updateSql = "UPDATE FirmFractionalShares SET fractionalShares = fractionalShares + ? WHERE stockSymbol = ?";
        String updateSql = "UPDATE FirmFractionalShares SET fractionalShares = ? WHERE stockSymbol = ?";
        String insertSql = "INSERT INTO FirmFractionalShares (stockSymbol, fractionalShares) VALUES (?, ?) ON DUPLICATE KEY UPDATE fractionalShares = VALUES(fractionalShares)";

        try (PreparedStatement pstmt = connect.prepareStatement(updateSql)) {
            pstmt.setDouble(1, sharesChange);
            pstmt.setString(2, stockSymbol);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // No existing record, so insert a new one
                try (PreparedStatement insertStmt = connect.prepareStatement(insertSql)) {
                    insertStmt.setString(1, stockSymbol);
                    insertStmt.setDouble(2, sharesChange);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating firm fractional shares: " + e.getMessage());
        }
    }
       /**
     * Updates the fractional shares of an account for a given stock symbol in the 'Dividends' table in the database.
     *
     * @param stockSymbol  The symbol of the stock for which to update fractional shares.
     * @param sharesChange The change in fractional shares to apply.
     * @param connect      The database connection.
     * @return             The difference between the requested shares change and the actual applied change.
     */
    static int updateAccountFractionalShares(String stockSymbol, double sharesChange, Connection connect) {
        int stockId = StockTradingHelper.getStockIdFromSymbol(stockSymbol,connect);
        String updateSql = "UPDATE Dividends SET shareOwned = ? WHERE stockId = ?";
        String insertSql = "INSERT INTO Dividends (stockId, shareOwned) VALUES (?, ?) ON DUPLICATE KEY UPDATE shareOwned = VALUES(shareOwned)";
        try(Statement statement = connect.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS Dividends (firm_id INT AUTO_INCREMENT PRIMARY KEY,  stockId INT,shareOwned DECIMAL(10, 4),  FOREIGN KEY (stockId) REFERENCES stocks(stockID));");
            double sharesFractionalInTheFirm = StockTradingHelper.getSharesOwned(stockSymbol,connect);
            if (sharesFractionalInTheFirm > sharesChange){
                try (PreparedStatement pstmt = connect.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, sharesChange);
                    pstmt.setInt(2, stockId);
//                pstmt.setString(3, stockSymbol);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        // No existing record, so insert a new one
                        try (PreparedStatement insertStmt = connect.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, stockId);
                            insertStmt.setDouble(2, sharesFractionalInTheFirm-sharesChange);
//                        insertStmt.setDouble(3, sharesChange);
                            insertStmt.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error updating account fractional shares: " + e.getMessage());
                }
                return 0;
            }else {
                int roofDifference = (int) Math.ceil(sharesChange-sharesFractionalInTheFirm);
                try (PreparedStatement pstmt = connect.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, (sharesFractionalInTheFirm + roofDifference) - sharesChange);
                    pstmt.setInt(2, stockId);
//                pstmt.setString(3, stockSymbol);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows == 0) {
                        // No existing record, so insert a new one
                        try (PreparedStatement insertStmt = connect.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, stockId);
                            insertStmt.setDouble(2,(sharesFractionalInTheFirm + roofDifference) - sharesChange );
//                        insertStmt.setDouble(3, sharesChange);
                            insertStmt.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error updating account fractional shares: " + e.getMessage());
                }
                return roofDifference;
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
