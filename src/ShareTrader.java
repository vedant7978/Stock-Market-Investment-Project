import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShareTrader {
//    static String propertyFilename = "G:/SDC_Project/vedant/src/sample.prop";
//    static DBConfig config = new DBConfig(propertyFilename);
//    public static Connection connect = DBConnection.getConnection(config.getDbUrl(), config.getUsername(), config.getPassword());
    /**
     * Executes a transaction to buy shares for a given account. This method updates the account's stock ownership, average cost basis (ACB), and cash balance accordingly.
     *
     * @param accountID    The ID of the account making the purchase.
     * @param stockSymbol  The symbol of the stock to be purchased.
     * @param sharesToBuy  The number of shares to buy.
     * @param sharePrice   The price per share.
     * @param connect      The database connection.
     */
    static void buyShares(int accountID, String stockSymbol, double sharesToBuy, double sharePrice, Connection connect) {

        double totalCost = sharesToBuy * sharePrice;

        // Begin transaction
        try {
            connect.setAutoCommit(false);

            if (!ShareManager.hasSufficientCash(accountID, totalCost, connect)) {
                System.out.println("Insufficient funds.");
                return;
            }

            // Fetch the current sharesOwned
            double currentSharesOwned = ShareManager.getCurrentSharesOwned(accountID, stockSymbol, connect);
            double currentACB = (currentSharesOwned > 0) ? ShareManager.getCurrentACB(accountID, stockSymbol, connect) : 0;

            // Calculate the new total cost (for all shares) and new ACB
            double newTotalCost = currentACB * currentSharesOwned + totalCost;
            double newSharesOwned = currentSharesOwned + sharesToBuy;
            double newACB = newTotalCost / newSharesOwned; // This now properly accounts for the first purchase

            // Update or insert the shares owned and ACB
            String updateSharesSQL = "INSERT INTO AccountStocks (accountID, stockSymbol, sharesOwned, acb) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE sharesOwned = VALUES(sharesOwned), acb = ?";
            try (PreparedStatement pstmt = connect.prepareStatement(updateSharesSQL)) {
                pstmt.setInt(1, accountID);
                pstmt.setString(2, stockSymbol);
                pstmt.setDouble(3, newSharesOwned);
                pstmt.setDouble(4, newACB);
                pstmt.setDouble(5, newACB); // For ON DUPLICATE KEY UPDATE
                pstmt.executeUpdate();
            }

            // Update cash balance
            ShareManager.updateCashBalance(accountID, -totalCost, connect);

            connect.commit();

        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
            try {
                connect.rollback();
            } catch (SQLException se) {
                System.out.println("Rollback failed: " + se.getMessage());
            }
        } finally {
            try {
                connect.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }
    /**
     * Executes a transaction to sell shares for a given account. This method updates the account's stock ownership and cash balance accordingly.
     *
     * @param accountID    The ID of the account making the sale.
     * @param stockSymbol  The symbol of the stock to be sold.
     * @param sharesToSell The number of shares to sell.
     * @param sharePrice   The price per share.
     * @param connect      The database connection.
     */
    static void sellShares(int accountID, String stockSymbol, int sharesToSell, double sharePrice, Connection connect) {
        // Calculate total sale value
        double totalSaleValue = sharesToSell * sharePrice;

        // Start a transaction
        try {
            connect.setAutoCommit(false); // Disable auto-commit for transactional integrity

            // Check if the account has enough shares to sell
            if (!ShareManager.hasSufficientShares(accountID, stockSymbol, sharesToSell, connect)) {
                System.out.println("Insufficient shares to sell.");
                connect.rollback(); // Rollback the transaction
                return;
            }
            // Fetch the current sharesOwned
            double currentSharesOwned = ShareManager.getCurrentSharesOwned(accountID, stockSymbol, connect);
            double currentACB = (currentSharesOwned > 0) ? ShareManager.getCurrentACB(accountID, stockSymbol, connect) : 0;

//            // Calculate the new total cost (for all shares) and new ACB
//            double newTotalCost = currentACB * currentSharesOwned + totalSaleValue;
//            double newSharesOwned = currentSharesOwned + sharesToSell;
//            double newACB = 0;
//            if (newSharesOwned > 0){
//                 newACB = newTotalCost / newSharesOwned; // This now properly accounts for the first purchase
//
//            }else{
//                 newACB = 0;
//            }


            // Update the shares owned by subtracting the sold shares
            String updateSharesSQL = "UPDATE AccountStocks SET sharesOwned = sharesOwned + ?, acb = ? WHERE accountID = ? AND stockSymbol = ?;";
            try (PreparedStatement pstmt = connect.prepareStatement(updateSharesSQL)) {
                pstmt.setInt(1, sharesToSell);
                pstmt.setDouble(2, currentACB);
                pstmt.setInt(3, accountID);
                pstmt.setString(4, stockSymbol);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    // If no rows were updated, it means the stockSymbol/accountID combination does not exist
                    throw new SQLException("No shares updated, possibly due to an invalid stock symbol or account ID.");
                }
            }

            // Update the cash balance by adding the sale value
            ShareManager.updateCashBalance(accountID, -totalSaleValue, connect);

            connect.commit(); // Commit the transaction
            System.out.println("Sold " + sharesToSell + " shares of " + stockSymbol + " for account ID " + accountID);
        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
            try {
                connect.rollback(); // Attempt to rollback the transaction on error
            } catch (SQLException se) {
                System.out.println("Failed to rollback transaction: " + se.getMessage());
            }
        } finally {
            try {
                connect.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                System.out.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }
}
