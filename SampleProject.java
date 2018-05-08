/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sampleproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author ShadowWing12
 */
public class SampleProject {

    //  Database credentials
    static String USER;
    static String PASS;
    static String DBNAME;
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are
    //strings, but that won't always be the case.
    static final String displayFormat="%-20s%-20s%-20s%-20s\n";
// JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://cecs-db01.coe.csulb.edu:3306/";
//            + "testdb;user=";
/**
 * Takes the input string and outputs "N/A" if the string is empty or null.
 * @param input The string to be mapped.
 * @return  Either the input string or "N/A" as appropriate.
 */
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }
    public static void main(String[] args) {
        //Prompt the user for the database name, and the credentials.
        //If your database has no credentials, you can update this code to
        //remove that from the connection string.
        Scanner in = new Scanner(System.in);
        //System.out.print("Name of the database (not the user account): ");
        DBNAME = "cecs323sec5g1";//in.nextLine();
        //System.out.print("Database user name: ");
        USER = "cecs323sec5n31";//in.nextLine();
        //System.out.print("Database password: ");
        PASS = "yaeri7";//in.nextLine();
        
        //Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME + "?user="+ USER + "&password=" + PASS;
        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT VIN, make, model, yr FROM cecs323sec5g1.Cars";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            System.out.printf(displayFormat, "VIN", "Make", "Model", "Year");
            while (rs.next()) {
                //Retrieve by column name
                String vin = rs.getString("VIN");
                String make = rs.getString("make");
                String model = rs.getString("model");
                String yr = rs.getString("yr");

                //Display values
                System.out.printf(displayFormat,
                        dispNull(vin), dispNull(make), dispNull(model), dispNull(yr));
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main
    
}//end FirstExample}
