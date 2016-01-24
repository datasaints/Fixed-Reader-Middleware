import java.sql.*;

import com.mysql.jdbc.Driver;


public class Database {
   // JDBC Driver and URL
   static final String DB_AWS_URL = "datasaintsdbinstance.chsdnjuecf9v.us-west-1.rds.amazonaws.com/";
   static final String DB_NAME = "DSaints";
   static final String DB_URL = "jdbc:mysql://" + DB_AWS_URL + DB_NAME;
   
   // Database Credentials
   static final String USER = "datasaints";
   static final String PASS = "datasaints";
   
   private Connection connection = null;
   private Statement statement = null;
   
   public Database() {
      try {
         System.out.println("Connecting to database...");
         connection = DriverManager.getConnection(DB_URL, USER, PASS);
         
      } catch (SQLException e) {
         System.out.println("Connection failure");
         System.exit(-1);
      }
   }
   
   public void getRecentItems() {
      ResultSet set = null;
      int idx = 1;
      
      try {
         statement = connection.createStatement();
         String sql = "SELECT * FROM Equipment";
         set = statement.executeQuery(sql);
      } catch (SQLException e1) {
         System.out.println("Query execution error");
         System.exit(-1);
      }

      
      try {
         while (set.next()) {
            System.out.printf("Equipment #%d\n", idx);
            System.out.printf("\tItemID:     %d\n", set.getInt("ItemID"));
            System.out.printf("\tEmployeeID: %d\n", set.getInt("EmployeeID"));
            System.out.printf("\tItemName:   %s\n", set.getString("ItemName"));
            System.out.printf("\tCheckIn:    %s\n", set.getString("CheckIn"));
            System.out.printf("\tCheckOut:   %s\n", set.getString("CheckOut"));
            System.out.printf("\tLastCalibrated: %s\n", set.getString("LastCalibrated"));
         }
         
         set.close();
      } catch (SQLException e) {
         System.out.println("ResultSet iteration error");
         System.exit(-1);
      }
   }
   
   

}