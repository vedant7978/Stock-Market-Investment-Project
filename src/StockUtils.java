import java.util.*;
/**
 * Utility class for sorting a map by its values.
 */
public class StockUtils {
    // Method to sort a map by values
    /**
     * Sorts a map by its values either in ascending or descending order.
     *
     * @param <K>       The type of keys in the map.
     * @param <V>       The type of values in the map.
     * @param map       The map to be sorted.
     * @param ascending If true, sorts the map in ascending order; otherwise, sorts in descending order.
     * @return A list of map entries sorted by their values.
     */
    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortMapByValue(Map<K, V> map, boolean ascending) {
        // Convert map entries to a list
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        // Sort the list based on values
        Collections.sort(list, (o1, o2) -> {
            if (ascending) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return list;
    }
}
