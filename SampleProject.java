/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.*;

/**
 *
 * @author ShadowWing12
 */
public class DBMenu {

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
    static String displayFormat="%-20s%-20s%-20s%-20s\n";
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
        System.out.print("Database user name: ");
        USER = in.nextLine();
        System.out.print("Database password: ");
        PASS = in.nextLine();
        
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
            int menuChoice = 0;
            while(menuChoice != 16){
                System.out.println("Please make a selection: ");
                System.out.println("1. All customers who have not made a purchase within the last month.");
                System.out.println("2. Customers who haven't purchased a vehicle of a specified color in 3 to 5 years.");
                System.out.println("3. Customers who have purchases a vehicle of a specific color in 3 to 5 years.");
                System.out.println("4. Frequent customers who make a purchase every 2 or so years.");
                System.out.println("5. Employees with unused vacation time.");
                System.out.println("6. Pay rates of all technicians possessing certificates.");
                System.out.println("7. Top three salespeople with highest number of sales in past 30 days.");
                System.out.println("8. Top three salespeople with highest gross sales in past 30 days.");
                System.out.println("9. Top three salespeople with most number of repeated sales to same customers.");
                System.out.println("10. Top five most popular car models in past 3 years.");
                System.out.println("11. All electric cars sold within last year.");
                System.out.println("12. All non-fossil fuel cars sold within last year.");
                System.out.println("13. Month with the highest number of convertible cars sold.");
                System.out.println("14. List of cars of a certain make and model.");
                System.out.println("15. List of cars of a certain make, model, and color.");
                System.out.println("16. Quit");
                while(menuChoice < 1 || menuChoice > 16){
                    while(!in.hasNextInt()){
                        in.next();
                        System.out.print("Please enter a number: ");
                    }
                    menuChoice = in.nextInt();
                    if(menuChoice > 0 && menuChoice < 17){
                        break;
                    }
                    System.out.print("Please enter a valid option: ");
                }
                if(menuChoice != 16){
                    if(menuChoice == 1){
                        displayFormat="%-20s%-20s%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(cus.last_name, ', ', cus.first_name, ' ', cus.middle_name) AS customer_name,cus.address, cus.zipcode,sale.date_of_purchase,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Home') AS home_phone,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Cell') AS cell_phone FROM cecs323sec5g1.Customers AS cus LEFT OUTER JOIN cecs323sec5g1.Sales AS sale ON sale.customer_id = cus.customer_id WHERE cus.customer_id NOT IN(SELECT customer_id FROM cecs323sec5g1.Sales AS sale WHERE sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 MONTH))";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Customer Name", "Address", "Zip Code", "Date of Purchase", "Cell Phone");
                        while (rs.next()) {
                            String cn = rs.getString("customer_name");
                            String addr = rs.getString("address");
                            String zip = rs.getString("zipcode");
                            String date = rs.getString("date_of_purchase");
                            String phone = rs.getString("cell_phone");
                            System.out.printf(displayFormat,
                            dispNull(cn), dispNull(addr), dispNull(zip), dispNull(date), dispNull(phone));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 2){
                        displayFormat="%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n";
                        System.out.print("Please enter the color: ");
                        String color = in.next();
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(cus.last_name, ', ', cus.first_name, ' ', cus.middle_name) AS customer_name,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Home') AS home_phone,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Cell') AS cell_phone,sale.date_of_purchase,car.make, car.model, car.yr FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Customers AS cus ON sale.customer_id = cus.customer_id INNER JOIN cecs323sec5g1.Cars AS car ON sale.VIN = car.VIN WHERE (car.color = '" + color + "') AND sale.date_of_purchase BETWEEN DATE_ADD(NOW(), INTERVAL -5 YEAR) AND DATE_ADD(NOW(), INTERVAL -3 YEAR) ORDER BY sale.date_of_purchase DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Customer Name", "Home Phone", "Cell Phone", "Date of Purchase", "Make", "Model", "Year");
                        while (rs.next()) {
                            String cn = rs.getString("customer_name");
                            String home = rs.getString("home_phone");
                            String cell = rs.getString("cell_phone");
                            String date = rs.getString("date_of_purchase");
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            System.out.printf(displayFormat,
                            dispNull(cn), dispNull(home), dispNull(cell), dispNull(date), dispNull(make), dispNull(model), dispNull(yr));
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 3){
                        displayFormat="%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n";
                        System.out.print("Please enter the color: ");
                        String color = in.next();
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(cus.last_name, ', ', cus.first_name, ' ', cus.middle_name) AS customer_name,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Home') AS home_phone,(SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Cell') AS cell_phone, sale.date_of_purchase, car.make, car.model, car.yr FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Customers AS cus ON sale.customer_id = cus.customer_id INNER JOIN cecs323sec5g1.Cars AS car ON sale.VIN = car.VIN WHERE (car.color = '" + color + "') AND sale.date_of_purchase BETWEEN DATE_ADD(NOW(), INTERVAL -5 YEAR) AND DATE_ADD(NOW(), INTERVAL -3 YEAR) ORDER BY sale.date_of_purchase DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Customer Name", "Home Phone", "Cell Phone", "Date of Purchase", "Make", "Model", "Year");
                        while (rs.next()) {
                            String cn = rs.getString("customer_name");
                            String home = rs.getString("home_phone");
                            String cell = rs.getString("cell_phone");
                            String date = rs.getString("date_of_purchase");
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            System.out.printf(displayFormat,
                            dispNull(cn), dispNull(home), dispNull(cell), dispNull(date), dispNull(make), dispNull(model), dispNull(yr));
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 4){
                        displayFormat="%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(cus.last_name, ', ', cus.first_name, ' ', cus.middle_name) AS customer_name, (SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Home') AS home_phone, (SELECT phone FROM cecs323sec5g1.CPhoneNumbers WHERE customer_id = cus.customer_id AND phone_type = 'Cell') AS cell_phone, sale.date_of_purchase, car.make, car.model, car.yr FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Customers AS cus ON sale.customer_id = cus.customer_id INNER JOIN cecs323sec5g1.Cars AS car ON sale.VIN = car.VIN WHERE sale.date_of_purchase BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL -2 YEAR) AND cus.customer_id IN (SELECT customer_id FROM cecs323sec5g1.Sales AS sale WHERE sale.date_of_purchase BETWEEN DATE_ADD(NOW(), INTERVAL -2 YEAR) AND DATE_ADD(NOW(), INTERVAL -4 YEAR))ORDER BY sale.date_of_purchase DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Customer Name", "Home Phone", "Cell Phone", "Date of Purchase", "Make", "Model", "Year");
                        while (rs.next()) {
                            String cn = rs.getString("customer_name");
                            String home = rs.getString("home_phone");
                            String cell = rs.getString("cell_phone");
                            String date = rs.getString("date_of_purchase");
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            System.out.printf(displayFormat,
                            dispNull(cn), dispNull(home), dispNull(cell), dispNull(date), dispNull(make), dispNull(model), dispNull(yr));                           
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 5){
                        displayFormat="%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(emp.last_name, ', ', emp.first_name, ' ', emp.middle_name) AS employee_name,ass.department, emp.unused_vacation_days FROM cecs323sec5g1.Employees AS emp LEFT OUTER JOIN cecs323sec5g1.Assignments AS ass ON ass.assignment_id = emp.cAssignment_id WHERE emp.unused_vacation_days > 0";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Employee Name", "Department", "Unused Vacation Days");
                        while (rs.next()) {
                            String en = rs.getString("employee_name");
                            String dep = rs.getString("department");
                            String vac = rs.getString("unused_vacation_days");
                            System.out.printf(displayFormat,
                            dispNull(en), dispNull(dep), dispNull(vac));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 6){
                        displayFormat="%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(emp.last_name, ', ', emp.first_name, ' ', emp.middle_name) AS employee_name,ass.monthly_salary,(SELECT COUNT(pos.detail_id) FROM cecs323sec5g1.PositionDetails AS pos WHERE pos.assignment_id = ass.assignment_id AND pos.detail_category = 'Certification' ) AS number_of_certifications FROM cecs323sec5g1.Employees AS emp LEFT OUTER JOIN cecs323sec5g1.Assignments AS ass ON ass.assignment_id = emp.cAssignment_id WHERE ass.department = 'Service'";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Employee Name", "Monthly Salary", "Position");
                        while (rs.next()) {
                            String en = rs.getString("employee_name");
                            String mo = rs.getString("monthly_salary");
                            String pos = rs.getString("pos");
                            System.out.printf(displayFormat,
                            dispNull(en), dispNull(mo), dispNull(pos));
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 7){
                        displayFormat="%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT CONCAT(emp.last_name,', ', emp.first_name,' ', emp.middle_name) AS employee_name,(SELECT COUNT(sale.sale_id) FROM cecs323sec5g1.Sales AS sale WHERE sale.employee_id = emp.employee_id AND (sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 MONTH))) AS sale_count FROM cecs323sec5g1.Employees AS emp INNER JOIN cecs323sec5g1.Sales AS sale ON sale.employee_id = emp.employee_id WHERE (sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 MONTH)) ORDER BY sale_count DESC LIMIT 3";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Employee Name", "Sale Count");
                        while (rs.next()) {
                            String en = rs.getString("employee_name");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(en), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 8){
                        displayFormat="%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT CONCAT(emp.last_name,', ', emp.first_name,' ', emp.middle_name) AS employee_name,(SELECT IFNULL(SUM(sale.price),0) FROM cecs323sec5g1.Sales AS sale WHERE sale.employee_id = emp.employee_id AND (sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 MONTH))) AS gross_sales FROM cecs323sec5g1.Employees AS emp INNER JOIN cecs323sec5g1.Sales AS sale ON sale.employee_id = emp.employee_id WHERE (sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 MONTH)) ORDER BY gross_sales DESC LIMIT 3";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Employee Name", "Gross Sales");
                        while (rs.next()) {
                            String en = rs.getString("employee_name");
                            String gs = rs.getString("gross_sales");
                            System.out.printf(displayFormat,
                            dispNull(en), dispNull(gs));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 9){
                        displayFormat="%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT CONCAT(emp.last_name,', ', emp.first_name,' ', emp.middle_name) AS employee_name,(SELECT COUNT(sale.customer_id) FROM cecs323sec5g1.Sales AS sale WHERE sale.employee_id = emp.employee_id AND sale.customer_id = s.customer_id) AS sale_count FROM cecs323sec5g1.Sales AS s INNER JOIN cecs323sec5g1.Employees AS emp ON s.employee_id = emp.employee_id WHERE (SELECT COUNT(sale.customer_id) FROM cecs323sec5g1.Sales AS sale WHERE sale.employee_id = emp.employee_id AND sale.customer_id = s.customer_id) >1 ORDER BY sale_count DESC LIMIT 3";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Employee Name", "Sale Count");
                        while (rs.next()) {
                            String en = rs.getString("employee_name");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(en), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 10){
                        displayFormat="%-20s%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT car.make, car.model, car.yr,(SELECT COUNT(sale.sale_id) FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Cars AS c ON c.VIN = sale.VIN WHERE  car.make = c.make AND car.yr = c.yr AND car.model =c.model AND sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -3 YEAR)) AS sale_count FROM cecs323sec5g1.Sales AS s INNER JOIN cecs323sec5g1.Cars AS car ON car.VIN = s.VIN WHERE s.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -3 YEAR) ORDER BY sale_count DESC LIMIT 5";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Make", "Model", "Year", "Sale Count");
                        while (rs.next()) {
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(make), dispNull(model), dispNull(yr), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 11){ //Syntax error
                        displayFormat="%-20s%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT car.make, car.model, car.yr, (SELECT COUNT(sale.sale_id) FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Cars AS c ON c.VIN = sale.VIN WHERE  car.make = c.make AND car.yr = c.yr AND car.model =c.model AND car.fuel_type = c.fuel_type AND sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 YEAR)) AS sale_count FROM cecs323sec5g1.Sales AS s INNER JOIN cecs323sec5g1.Cars AS car ON car.VIN = s.VIN WHERE car.fuel_type IN ('Electric', 'ELECTRIC', 'electric'/**, 'hybrid', 'Hybrid', 'HYBRID'**/) AND s.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 YEAR) ORDER BY sale_count DESC LIMIT 5";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Make", "Model", "Year", "Sale Count");
                        while (rs.next()) {
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(make), dispNull(model), dispNull(yr), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 12){ //syntax error
                        displayFormat="%-20s%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT car.make, car.model, car.yr, (SELECT COUNT(sale.sale_id) FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Cars AS c ON c.VIN = sale.VIN WHERE  car.make = c.make AND car.yr = c.yr AND car.model =c.model AND sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL - 1 YEAR)) AS sale_count FROM cecs323sec5g1.Sales AS s INNER JOIN cecs323sec5g1.Cars AS car ON car.VIN = s.VIN WHERE (car.fuel_type NOT IN ('GAS', 'Gas', 'gas', 'diesel', 'Diesel', 'DIESEL', 'fossil fuel') OR car.fuel_type IS NULL) AND s.date_of_purchase >= DATE_ADD(NOW(), INTERVAL - 1 YEAR) ORDER BY sale_count DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Make", "Model", "Year", "Sale Count");
                        while (rs.next()) {
                            String make = rs.getString("make");
                            String model = rs.getString("model");
                            String yr = rs.getString("yr");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(make), dispNull(model), dispNull(yr), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 13){
                        displayFormat="%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT DATE_FORMAT(s.date_of_purchase, '%M') AS month,(SELECT COUNT(sale.sale_id) FROM cecs323sec5g1.Sales AS sale INNER JOIN cecs323sec5g1.Cars AS c ON c.VIN = sale.VIN WHERE  DATE_FORMAT(s.date_of_purchase, '%M') = DATE_FORMAT(sale.date_of_purchase, '%M') /**AND sale.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 YEAR)**/) AS sale_count FROM cecs323sec5g1.Sales AS s INNER JOIN cecs323sec5g1.Cars AS car ON car.VIN = s.VIN /**WHERE s.date_of_purchase >= DATE_ADD(NOW(), INTERVAL -1 YEAR)**/ ORDER BY sale_count DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Month", "Sale Count");
                        while (rs.next()) {
                            String mo = rs.getString("month");
                            String sc = rs.getString("sale_count");
                            System.out.printf(displayFormat,
                            dispNull(mo), dispNull(sc));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 14){
                        System.out.println("Enter the make: ");
                        String make = in.nextLine();
                        in.next();
                        System.out.println("Enter the model: ");
                        String model = in.nextLine();
                        in.next();
                        displayFormat="%-20s%-20s%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT car.model, car.make, car.yr, car.retail_price AS highest_price, car.invoice_price AS lowest_price, (SELECT COUNT(*) FROM cecs323sec5g1.Cars AS c WHERE  c.VIN NOT IN (SELECT sale.VIN FROM cecs323sec5g1.Sales AS sale) AND c.make = car.make AND c.model = car.model AND c.yr = car.yr AND c.retail_price = car.retail_price AND c.invoice_price = car.invoice_price)AS count FROM cecs323sec5g1.Cars AS car WHERE car.VIN NOT IN (SELECT s.VIN FROM cecs323sec5g1.Sales AS s) AND car.model = '" + model + "'  AND car.make = '" + make + "' ORDER BY make, model, yr DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Highest Price", "Lowest Price", "Count");
                        while (rs.next()) {
                            String hp = rs.getString("highest_price");
                            String lp = rs.getString("lowest_price");
                            String co = rs.getString("count");
                            System.out.printf(displayFormat,
                            dispNull(hp), dispNull(lp), dispNull(co));                            
                        }
                        menuChoice = 0;
                    }
                    else if(menuChoice == 15){
                        System.out.println("Enter the make: ");
                        String make = in.nextLine();
                        in.next();
                        System.out.println("Enter the model: ");
                        String model = in.nextLine();
                        in.next();
                        System.out.println("Enter the color: ");
                        String color = in.nextLine();
                        in.next();
                        displayFormat="%-20s\n";
                        stmt = conn.createStatement();
                        String sql;
                        sql = "SELECT DISTINCT car.model, car.make, car.yr, car.color, car.retail_price AS price FROM cecs323sec5g1.Cars AS car WHERE car.VIN NOT IN (SELECT s.VIN FROM cecs323sec5g1.Sales AS s) AND car.model = '" + model + "' AND car.make = '" + make + "' AND car.color = '" + color + "' ORDER BY yr DESC";
                        ResultSet rs = stmt.executeQuery(sql);
                        System.out.printf(displayFormat, "Price");
                        while (rs.next()) {
                            String pr = rs.getString("price");
                            System.out.printf(displayFormat,
                            dispNull(pr));                            
                        }
                        menuChoice = 0;
                    }
                    else{
                        System.out.println("This shouldn't appear.");
                    }
                }
            }
            //STEP 6: Clean-up environment
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
