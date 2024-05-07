import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShareManager {
    /**
     * Retrieves the current number of shares owned for a given account and stock symbol.
     *
     * @param accountID   The ID of the account.
     * @param stockSymbol The symbol of the stock.
     * @param connect     The database connection.
     * @return            The current number of shares owned.
     */
    static double getCurrentSharesOwned(int accountID, String stockSymbol, Connection connect) {
        double sharesOwned = 0; // Default shares owned is 0 if the stock is not found

        String sql = "SELECT sharesOwned FROM AccountStocks WHERE accountID = ? AND stockSymbol = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            pstmt.setString(2, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            // If the stock exists, update the sharesOwned from the query result
            if (rs.next()) {
                sharesOwned = rs.getDouble("sharesOwned");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching shares owned: " + e.getMessage());
            // Handle exception
        }

        return sharesOwned;
    }
    /**
     * Retrieves the current average cost basis (ACB) for a given account and stock symbol.
     *
     * @param accountID   The ID of the account.
     * @param stockSymbol The symbol of the stock.
     * @param connect     The database connection.
     * @return            The current average cost basis (ACB).
     */
    static double getCurrentACB(int accountID, String stockSymbol, Connection connect) {
        double acb = 0.0; // Default ACB is 0 if the stock is not found

        String sql = "SELECT acb FROM AccountStocks WHERE accountID = ? AND stockSymbol = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            pstmt.setString(2, stockSymbol);
            ResultSet rs = pstmt.executeQuery();

            // If the stock exists, update the ACB from the query result
            if (rs.next()) {
                acb = rs.getDouble("acb");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching ACB: " + e.getMessage());
            // Handle exception (e.g., log the error or rethrow as a runtime exception)
        }

        return acb;
    }
    /**
     * Checks if an account has sufficient cash to perform a transaction.
     *
     * @param accountID      The ID of the account.
     * @param amountRequired The required amount of cash.
     * @param connect        The database connection.
     * @return               True if the account has sufficient cash, otherwise false.
     */
    static boolean hasSufficientCash(int accountID, double amountRequired, Connection connect) {
        String sql = "SELECT cashBalance FROM Accounts WHERE accountID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double cashBalance = rs.getDouble("cashBalance");
                return cashBalance >= amountRequired;
            }
        } catch (SQLException e) {
            System.out.println("Error checking cash balance: " + e.getMessage());
        }
        return false;
    }
    /**
     * Updates the cash balance for a given account by a specified amount.
     *
     * @param accountID The ID of the account.
     * @param amount    The amount to update the cash balance by.
     * @param connect   The database connection.
     */
    static void updateCashBalance(int accountID, double amount, Connection connect) {

        String sql = "UPDATE Accounts SET cashBalance = cashBalance + ? WHERE accountID = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating cash balance: " + e.getMessage());
        }
    }
    /**
     * Checks if an account has sufficient shares of a given stock to perform a transaction.
     *
     * @param accountID      The ID of the account.
     * @param stockSymbol    The symbol of the stock.
     * @param sharesRequested The number of shares requested.
     * @param connect        The database connection.
     * @return               True if the account has sufficient shares, otherwise false.
     */
    static boolean hasSufficientShares(int accountID, String stockSymbol, int sharesRequested, Connection connect) {
        String sql = "SELECT sharesOwned FROM AccountStocks WHERE accountID = ? AND stockSymbol = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setInt(1, accountID);
            pstmt.setString(2, stockSymbol);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int sharesOwned = rs.getInt("sharesOwned");
                sharesRequested = -sharesRequested;
                if (sharesOwned >= sharesRequested){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking shares: " + e.getMessage());
        }
        return false;
    }
    /**
     * Retrieves the current share price of a given stock.
     *
     * @param stockSymbol The symbol of the stock.
     * @param connect     The database connection.
     * @return            The current share price.
     */
    static double getCurrentSharePrice(String stockSymbol, Connection connect) {
        String sql = "SELECT currentPrice FROM stocks WHERE stockSymbol = ?;";
        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
            pstmt.setString(1, stockSymbol);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("currentPrice");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching stock price: " + e.getMessage());
        }
        return 1.0; // Default price
    }
}
