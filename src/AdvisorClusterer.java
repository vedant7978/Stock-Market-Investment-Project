import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AdvisorClusterer {
    /**
     * Calculates the sector differences for each account based on the current prices of stocks and shares owned.
     *
     * @param connect the database connection
     * @return a map where each key represents an account ID and the corresponding value is a map of sector IDs to sector values
     * @throws SQLException if there is an error accessing the database
     */
    static Map<Integer, Map<String, Double>> calculateSectorDifferences(Connection connect) throws SQLException {
        Map<Integer, Map<String, Double>> sectorDifferences = new HashMap<>();

        try (PreparedStatement statement = connect.prepareStatement(
                "SELECT astocks.accountID, sec.sectorID, SUM(s.currentPrice * astocks.sharesOwned) AS sectorValue \n" +
                        "FROM AccountStocks AS astocks \n" +
                        "JOIN stocks AS s ON astocks.stockSymbol = s.stockSymbol\n" +
                        "JOIN sectors AS sec ON s.sectorID = sec.sectorID\n" +
                        "GROUP BY astocks.accountID, sec.sectorID;")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int accountID = resultSet.getInt("accountID");
                int sectorID = resultSet.getInt("sectorID");
                double sectorValue = resultSet.getDouble("sectorValue");

                sectorDifferences.putIfAbsent(accountID, new HashMap<>());
                sectorDifferences.get(accountID).put(String.valueOf(sectorID), sectorValue);
            }
        }

        return sectorDifferences;
    }
    /**
     * Initializes cluster representatives with random values for each sector.
     *
     * @param maxGroups the maximum number of clusters
     * @param connect the database connection
     * @return a list of maps where each map represents a cluster with sector IDs as keys and random values as sector weights
     * @throws SQLException if there is an error accessing the database
     */
    static List<Map<String, Double>> initializeClusters(int maxGroups, Connection connect) throws SQLException {
        List<Map<String, Double>> clusterRepresentatives = new ArrayList<>();
        Random random = new Random();
        int numSectors = StockTradingHelper.getNumberOfSectors(connect);

        // Initialize k cluster representatives with random values
        for (int i = 0; i < maxGroups; i++) {
            Map<String, Double> cluster = new HashMap<>();
            // Assuming sectorIDs are known
            for (int sectorID = 1; sectorID <= numSectors; sectorID++) {
                double randomValue = random.nextDouble() * 100; // Assuming percentage values
                cluster.put(String.valueOf(sectorID), randomValue);
            }
            clusterRepresentatives.add(cluster);
        }

        return clusterRepresentatives;
    }
    /**
     * Assigns each account to the closest cluster representative based on cosine similarity.
     *
     * @param sectorDifferences a map containing sector differences for each account
     * @param clusterRepresentatives a list of maps representing cluster representatives
     * @return a map containing account IDs as keys and their corresponding cluster assignments as values
     */
    static Map<Integer, Integer> assignToClusters(Map<Integer, Map<String, Double>> sectorDifferences,
                                                  List<Map<String, Double>> clusterRepresentatives) {
        Map<Integer, Integer> clusterAssignments = new HashMap<>();

        for (Map.Entry<Integer, Map<String, Double>> entry : sectorDifferences.entrySet()) {
            int accountID = entry.getKey();
            Map<String, Double> sectorDifference = entry.getValue();

            double minDistance = Double.MAX_VALUE;
            int closestClusterIndex = -1;

            for (int i = 0; i < clusterRepresentatives.size(); i++) {
                double distance = CosineSimilarityCalculator.calculateCosineSimilarity(sectorDifference, clusterRepresentatives.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestClusterIndex = i;
                }
            }

            clusterAssignments.put(accountID, closestClusterIndex);
        }

        return clusterAssignments;
    }
    /**
     * Updates cluster representatives by recalculating them as the average of vectors associated with each cluster.
     *
     * @param sectorDifferences a map containing sector differences for each account
     * @param clusterAssignments a map containing account IDs as keys and their corresponding cluster assignments as values
     * @param clusterRepresentatives a list of maps representing cluster representatives
     */
    static void updateClusterRepresentatives(Map<Integer, Map<String, Double>> sectorDifferences,
                                             Map<Integer, Integer> clusterAssignments,
                                             List<Map<String, Double>> clusterRepresentatives) {
        Map<Integer, Map<String, Double>> clusterSums = new HashMap<>();
        Map<Integer, Integer> clusterCounts = new HashMap<>();

        // Calculate sums of vectors associated with each cluster
        for (Map.Entry<Integer, Map<String, Double>> entry : sectorDifferences.entrySet()) {
            int accountID = entry.getKey();
            int clusterIndex = clusterAssignments.get(accountID);
            Map<String, Double> sectorDifference = entry.getValue();

            clusterSums.putIfAbsent(clusterIndex, new HashMap<>());
            for (Map.Entry<String, Double> sectorEntry : sectorDifference.entrySet()) {
                String sectorID = sectorEntry.getKey();
                double value = sectorEntry.getValue();

                clusterSums.get(clusterIndex).merge(sectorID, value, Double::sum);
            }
            clusterCounts.merge(clusterIndex, 1, Integer::sum);
        }

        // Recalculate cluster representatives as the average of vectors associated with the cluster
        for (int i = 0; i < clusterRepresentatives.size(); i++) {
            Map<String, Double> clusterSum = clusterSums.getOrDefault(i, new HashMap<>());
            int count = clusterCounts.getOrDefault(i, 1);

            Map<String, Double> newClusterRepresentative = new HashMap<>();
            for (Map.Entry<String, Double> sumEntry : clusterSum.entrySet()) {
                String sectorID = sumEntry.getKey();
                double sum = sumEntry.getValue();
                newClusterRepresentative.put(sectorID, sum / count);
            }

            clusterRepresentatives.set(i, newClusterRepresentative);
        }
    }
    /**
     * Calculates the maximum distance between each account's sector difference vector and its assigned cluster representative.
     *
     * @param sectorDifferences a map containing sector differences for each account
     * @param clusterAssignments a map containing account IDs as keys and their corresponding cluster assignments as values
     * @param clusterRepresentatives a list of maps representing cluster representatives
     * @return the maximum distance between any account and its assigned cluster representative
     */
    static double calculateMaxDistance(Map<Integer, Map<String, Double>> sectorDifferences,
                                       Map<Integer, Integer> clusterAssignments,
                                       List<Map<String, Double>> clusterRepresentatives) {
        double maxDistance = Double.MIN_VALUE;

        for (Map.Entry<Integer, Map<String, Double>> entry : sectorDifferences.entrySet()) {
            int accountID = entry.getKey();
            Map<String, Double> sectorDifference = entry.getValue();
            int clusterIndex = clusterAssignments.get(accountID);
            Map<String, Double> clusterRepresentative = clusterRepresentatives.get(clusterIndex);

            double distance = CosineSimilarityCalculator.calculateCosineSimilarity(sectorDifference, clusterRepresentative);
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }

        return maxDistance;
    }
    /**
     * Converts cluster assignments to advisor groups, where each set represents a group of advisors with similar preferences.
     *
     * @param clusterAssignments a map containing account IDs as keys and their corresponding cluster assignments as values
     * @return a set of sets representing advisor groups, where each inner set contains account IDs of advisors in the same group
     */
    static Set<Set<Integer>> convertToAdvisorGroups(Map<Integer, Integer> clusterAssignments) {
        Map<Integer, Set<Integer>> groups = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : clusterAssignments.entrySet()) {
            int accountID = entry.getKey();
            int clusterIndex = entry.getValue();

            groups.putIfAbsent(clusterIndex, new HashSet<>());
            groups.get(clusterIndex).add(accountID);
        }

        return new HashSet<>(groups.values());
    }
}
