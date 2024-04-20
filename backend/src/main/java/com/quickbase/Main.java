package com.quickbase;

import com.quickbase.devint.*;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.util.List;

/**
 * The main method of the executable JAR generated from this repository. This is to let you
 * execute something from the command-line or IDE for the purposes of demonstration, but you can choose
 * to demonstrate in a different way (e.g. if you're using a framework)
 */
public class Main {
    public static void main( String args[] ) {
        System.out.println("Starting.");
        System.out.print("Getting DB Connection...");

        DBManager dbm = new DBManagerImpl();
        Connection c = dbm.getConnection();
        if (null == c ) {
            System.out.println("failed.");
            System.exit(1);
        }

        // Print database tables and their column names
        dbm.printDatabaseMetadata();

        // Retrieve the total population data by country from the database
        List<Pair<String, Integer>> populationFromDatabase = dbm.getTotalPopulationByCountry();
        System.out.println("Data from the database:");
        System.out.println(populationFromDatabase);

        // Get population data by country from the API
        IStatService concreteStatService = new ConcreteStatService();
        List<Pair<String, Integer>> populationFromAPI = concreteStatService.GetCountryPopulations();
        System.out.println("Data from the API:");
        System.out.println(populationFromAPI);

        // Merge population data using a loop-based approach
        List<Pair<String, Integer>> mergedPopulationUsingLists = ListCombinator.mergePopulationUsingLoops(populationFromDatabase,
                populationFromAPI);
        System.out.println("Merged data using first method:");
        System.out.println(mergedPopulationUsingLists);

        // Merge population data using a hashmap-based approach
        List<Pair<String, Integer>> mergedPopulationUsingMaps = ListCombinator.mergePopulationUsingMaps(populationFromDatabase,
                populationFromAPI);
        System.out.println("Merged data using second method:");
        System.out.println(mergedPopulationUsingMaps);
    }
}