package com.quickbase.devint;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yokin on 4/20/2024.
 */
public class ListCombinator {

    /**
     * Merges two lists of population, if duplicate is encountered, use the entry from the database.
     * Uses nested loops, if size of database entries is n and of API is m, the complexity is O(n*m).
     * @param populationFromDatabase list of population per country extracted from the database
     * @param populationFromAPI list of population per country extracted from the API
     * @return list of merged population data
     */
    public static List<Pair<String, Integer>> mergePopulationUsingLoops(List<Pair<String, Integer>> populationFromDatabase,
                                                                  List<Pair<String, Integer>> populationFromAPI) {
        List<Pair<String, Integer>> mergedPopulation = new ArrayList<>();

        // Fill the merged list with entries from the API, also changing the name of United States of America to U.S.A.
        for (Pair<String, Integer> apiEntry : populationFromAPI) {
            String countryName = apiEntry.getLeft();
            if ("United States of America".equals(countryName)) {
                countryName = "U.S.A.";
            }
            mergedPopulation.add(new ImmutablePair<>(countryName, apiEntry.getRight()));
        }

        int mergedPopulationSize = mergedPopulation.size();
        // Iterate over each entry in the database list
        for (Pair<String, Integer> dbEntry : populationFromDatabase) {
            boolean found = false;

            // Check if the current database entry exists in the merged list
            for (int i = 0; i < mergedPopulationSize; i++) {
                Pair<String, Integer> apiEntry = mergedPopulation.get(i);
                if (apiEntry.getLeft().equals(dbEntry.getLeft())) {
                    // If found, update the merged list with the database value
                    mergedPopulation.set(i, dbEntry);
                    found = true;
                    break;
                }
            }

            // If the database entry was not found in the merged list, add it
            if (!found) {
                mergedPopulation.add(dbEntry);
            }
        }

        return mergedPopulation;
    }

    /**
     * Merges two lists of population, if duplicate is encountered, use the entry from the database.
     * Uses hash maps, if size of database entries is n and of API is m, the complexity is O(n+m).
     * @param populationFromDatabase list of population per country extracted from the database
     * @param populationFromAPI list of population per country extracted from the API
     * @return list of merged population data
     */
    public static List<Pair<String, Integer>> mergePopulationUsingMaps(List<Pair<String, Integer>> populationFromDatabase,
                                                                  List<Pair<String, Integer>> populationFromAPI) {
        Map<String, Integer> populationMap = new HashMap<>();

        // First, add all API data to the map, also changing the name of United States of America to U.S.A.
        for (Pair<String, Integer> entry : populationFromAPI) {
            String countryName = entry.getLeft();
            if ("United States of America".equals(countryName)) {
                countryName = "U.S.A.";
            }
            populationMap.put(countryName, entry.getRight());
        }

        // Add all database data to the map, overwriting API data for duplicate countries
        for (Pair<String, Integer> entry : populationFromDatabase) {
            populationMap.put(entry.getLeft(), entry.getRight()); // This will overwrite any existing entry from the API
        }

        // Convert the map back to a list of pairs for the result
        List<Pair<String, Integer>> mergedPopulationData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : populationMap.entrySet()) {
            mergedPopulationData.add(new ImmutablePair<>(entry.getKey(), entry.getValue()));
        }

        return mergedPopulationData;
    }
}
