import java.util.Map;

public class CosineSimilarityCalculator {
    // Method to calculate cosine similarity between two vectors
    /**
     * Calculates the cosine similarity between two vectors represented as maps of stock symbols and their corresponding values.
     *
     * @param vectorA The first vector represented as a map of stock symbols and their values.
     * @param vectorB The second vector represented as a map of stock symbols and their values.
     * @return The cosine similarity between the two input vectors.
     */
    public static double calculateCosineSimilarity(Map<String, Double> vectorA, Map<String, Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // Calculate dot product and norms
        for (Map.Entry<String, Double> entryA : vectorA.entrySet()) {
            String stockSymbol = entryA.getKey();
            double sharesA = entryA.getValue();
            double sharesB = vectorB.getOrDefault(stockSymbol, 0.0);

            dotProduct += sharesA * sharesB;
            normA += sharesA * sharesA;
            normB += sharesB * sharesB;
        }

        // Calculate norms
        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        // Calculate cosine similarity
        if (normA != 0 && normB != 0) {
            return dotProduct / (normA * normB);
        } else {
            return 0.0; // Return 0 if one of the norms is zero
        }
    }
}