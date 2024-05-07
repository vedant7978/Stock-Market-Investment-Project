import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountCheck {
    /**
     * Checks if a client exists in the database.
     *
     * @param clientId The ID of the client to check.
     * @param connect  The database connection.
     * @return         True if the client exists, false otherwise.
     */
    public static boolean clientExists(int clientId, Connection connect) {
        String checkClientExistsSQL = "SELECT COUNT(*) AS clientCount FROM Clients WHERE clientID = ?";
        try (PreparedStatement pstmtClient = connect.prepareStatement(checkClientExistsSQL)) {
            pstmtClient.setInt(1, clientId);
            ResultSet rsClient = pstmtClient.executeQuery();
            if (rsClient.next()) {
                int clientCount = rsClient.getInt("clientCount");
                return clientCount > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking client existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Checks if an advisor exists in the database.
     *
     * @param financialAdvisor The ID of the advisor to check.
     * @param connect          The database connection.
     * @return                 True if the advisor exists, false otherwise.
     */
    public static boolean advisorExists(int financialAdvisor, Connection connect) {
        String checkAdvisorExistsSQL = "SELECT COUNT(*) AS advisorCount FROM Advisors WHERE advisorID = ?";
        try (PreparedStatement pstmtAdvisor = connect.prepareStatement(checkAdvisorExistsSQL)) {
            pstmtAdvisor.setInt(1, financialAdvisor);
            ResultSet rsAdvisor = pstmtAdvisor.executeQuery();
            if (rsAdvisor.next()) {
                int advisorCount = rsAdvisor.getInt("advisorCount");
                return advisorCount > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking advisor existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Checks if an account exists in the database.
     *
     * @param accountId The ID of the account to check.
     * @param connect   The database connection.
     * @return          True if the account exists, false otherwise.
     */
    static boolean accountExists(int accountId, Connection connect)  {
        String sqlCheckAccount = "SELECT COUNT(*) AS count FROM Accounts WHERE accountID = ?";
        try (PreparedStatement pstmt = connect.prepareStatement(sqlCheckAccount)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking account existence: " + e.getMessage());

        }
        return false;
    }
}
