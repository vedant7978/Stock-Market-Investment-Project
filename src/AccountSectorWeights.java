import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AccountSectorWeights {
    static Map<Integer, Map<Integer, Double>> getAccountSectorWeights(Connection connection) throws SQLException {
        Map<Integer, Map<Integer, Double>> accountSectorWeights = new HashMap<>();

        String sql = "SELECT a.accountID, ps.sectorID, ps.percentage\n" +
                "FROM Accounts a\n" +
                "JOIN AccountStocks ast ON a.accountID = ast.accountID\n" +
                "JOIN stocks s ON ast.stockSymbol = s.stockSymbol\n" +
                "JOIN sectors st ON s.sectorID = st.sectorID\n" +
                "JOIN ProfileSectors ps ON ps.sectorID = st.sectorID \n" +
                "GROUP BY a.accountID, ps.sectorID;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int accountID = rs.getInt("accountID");
                int sectorID = rs.getInt("sectorID");
                double percentage = rs.getDouble("percentage");

                if (!accountSectorWeights.containsKey(accountID)) {
                    accountSectorWeights.put(accountID, new HashMap<>());
                }
                accountSectorWeights.get(accountID).put(sectorID, percentage);
            }
        }

        return accountSectorWeights;
    }
    static List<Integer> selectInitialClusterRepresentatives(Map<Integer, Map<Integer, Double>> accountSectorWeights, int maxGroups) {
        // Randomly select k accounts as initial cluster representatives
        List<Integer> accountIDs = new ArrayList<>(accountSectorWeights.keySet());
        Collections.shuffle(accountIDs);
        return accountIDs.subList(0, Math.min(maxGroups, accountIDs.size()));
    }

}
