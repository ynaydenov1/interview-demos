package com.quickbase.devint;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This DBManager implementation provides a connection to the database containing population data.
 *
 * Created by ckeswani on 9/16/15.
 */
public class DBManagerImpl implements DBManager {
    public Connection getConnection() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:resources/data/citystatecountry.db");
            System.out.println("Opened database successfully");

        } catch (ClassNotFoundException cnf) {
            System.out.println("could not load driver");
        } catch (SQLException sqle) {
            System.out.println("sql exception:" + sqle.getStackTrace());
        }
        return c;
    }

    /**
     * Prints metadata about all tables and their columns in the database.
     * This is used for understanding the database schema.
     */
    public void printDatabaseMetadata() {
        try (Connection conn = this.getConnection()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            System.out.println("Listing tables and columns:");

            // Retrieve a description of the tables available in the database
            try (ResultSet rs = dbMetaData.getTables(null, null, "%", new String[] {"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    System.out.println("Table: " + tableName);

                    // Retrieves a description of the columns available in the table
                    try (ResultSet rsColumns = dbMetaData.getColumns(null, null, tableName, "%")) {
                        while (rsColumns.next()) {
                            String columnName = rsColumns.getString("COLUMN_NAME");
                            String columnType = rsColumns.getString("TYPE_NAME");
                            int columnSize = rsColumns.getInt("COLUMN_SIZE");
                            System.out.println("\tColumn Name: " + columnName + ", Type: " + columnType + ", Size: " + columnSize);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
    }

    /**
     * Retrieves the total population for each country by aggregating population data
     * from cities that are joined based on states.
     * @return list of pairs, each containing a country name and its total population
     */
    public List<Pair<String, Integer>> getTotalPopulationByCountry() {
        List<Pair<String, Integer>> countryPopulations = new ArrayList<>();

        // SQL query to sum populations by country
        String query = "SELECT Country.CountryName, SUM(City.Population) AS TotalPopulation\n" +
                "FROM Country\n" +
                "JOIN State ON Country.CountryId = State.CountryId\n" +
                "JOIN City ON State.StateId = City.StateId\n" +
                "GROUP BY Country.CountryName;\n";

        // Connect to the database and execute the query
        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String countryName = rs.getString("CountryName");
                int totalPopulation = rs.getInt("TotalPopulation");
                countryPopulations.add(new ImmutablePair<>(countryName, totalPopulation));
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        return countryPopulations;
    }
}
