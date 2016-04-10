package main.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


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

   public ArrayList<ReaderProfile> getReaderProfiles() {
	  ResultSet set = null;
	  ArrayList<ReaderProfile> profiles = new ArrayList<ReaderProfile>();
	  
	  try {
	         statement = connection.createStatement();
	         String sql = "SELECT * FROM ReaderProfiles";
	         set = statement.executeQuery(sql);
	      } catch (SQLException e1) {
	         System.out.println("Query execution error");
	         System.exit(-1);
	      }
	  
	  try {
	         while (set.next()) {
	        	 ReaderProfile entry = new ReaderProfile(set.getString("ID"), set.getString("Frequency"), set.getString("IP")); 
	        	 profiles.add(entry);
	         }

	         set.close();
	      } catch (SQLException e) {
	         System.out.println("ResultSet iteration error");
	         System.exit(-1);
	      }
	  
	  return profiles;
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
            System.out.printf("Equipment #%d\n", idx++);
            System.out.printf("\tItemID:     %s\n", set.getString("ItemID"));
            System.out.printf("\tEmployeeID: %s\n", set.getString("EmployeeID"));
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

/*      // delete, testing purposes
      System.out.println("\t\tHacky inserting");
      HashSet<String> list = new HashSet<String>();
      list.add("aaaaa");
      list.add("bbbbb");
      list.add("ccccc");
      addTagsToDatabase(list);*/
   }

   public void addTagsToDatabase(HashSet<String> tagList) {
      String sql;

      if (tagList == null || tagList.isEmpty()) {
         System.out.println("tag list is empty.");
         return;
      }

      for (String tag : tagList) {
         try {
            System.out.printf("Inserting tag: %s\n", tag);
            statement = connection.createStatement();
            String checkin = (new Date(System.currentTimeMillis())).toString()
                  + " " + (new Time(System.currentTimeMillis())).toString();
            String checkout = (new Date(System.currentTimeMillis())).toString()
                  + " " + (new Time(System.currentTimeMillis())).toString();
            String lastcali = (new Date(System.currentTimeMillis())).toString()
                  + " " + (new Time(System.currentTimeMillis())).toString();

            sql = "INSERT INTO Equipment VALUES ('" + tag;
            sql += "', '000', 'item_name', ";
            sql += "'" + checkin + "', ";
            sql += "'" + checkout + "', ";
            sql += "'" + lastcali + "')";

            System.out.printf("query:\t%s\n", sql);
            statement.executeUpdate(sql);
         } catch (SQLException e) {
            System.out.println("Error adding tag, possibly duplicate");
         }
      }
      System.out.println("Inserted\n");
   }

   public HashMap<String, String> getItemInfoById(String tagID) {
      ResultSet set = null;
      HashMap<String, String> itemInfo = new HashMap<String, String>();

      try {
         statement = connection.createStatement();
         String sql = "SELECT * FROM DSaints.Equipment where ItemID = \"" + tagID + "\";";
         set = statement.executeQuery(sql);
      } catch (SQLException e1) {
         System.out.println("getItemInfoByID: query execution error");
         e1.printStackTrace();
         System.exit(-1);
      }

      try {
         set.next();
         itemInfo.put("ItemID", set.getString("ItemID"));
         itemInfo.put("EmployeeID", set.getString("EmployeeID"));
         itemInfo.put("ItemName", set.getString("ItemName"));
         itemInfo.put("CheckIn", set.getString("CheckIn"));
         itemInfo.put("CheckOut", set.getString("CheckOut"));
         itemInfo.put("LastCalibrated", set.getString("LastCalibrated"));
         set.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }

      return itemInfo;
   }

   public void updateItemFieldById(String tagID, String field, String value) {
      //field should only ever be ItemID, EmployeeID, ItemName, CheckIn, CheckOut, or LastCalibrated
      //if field is CheckIn, CheckOut, or LastCalibrated, then format for value should be:
      //[yyyy-mm-dd hh:mm:ss]
      String sql = "UPDATE Equipment SET " + field + " = " + value + " WHERE ItemID = \"" + tagID + "\";";
      //Example sql query: [UPDATE Equipment SET CheckOut = '2009-11-01 13:23:00' WHERE ItemID = "12"]

      try {
         System.out.printf("Updating info for tag: %s\n", tagID);
         statement = connection.createStatement();

         System.out.printf("query:\t%s\n", sql);
         statement.executeUpdate(sql);
      } catch (SQLException e) {
         System.out.println("Error adding tag");
         e.printStackTrace();
      }
      System.out.println("Finished updating");
   }

   public ResultSet getReaderProfile(String readerName) {
      ResultSet set = null;

      //Query database for reader profile by the identifier readerName

      //assign set to ResultSet from that query, if none exists return null
      return set;
   }
}