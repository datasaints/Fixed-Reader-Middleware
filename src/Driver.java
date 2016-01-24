import com.alien.enterpriseRFID.discovery.*;
import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Driver {
   ArrayList<AlienClass1Reader> readerList = new ArrayList<AlienClass1Reader>();
   static Thread networkDiscover;

   public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      AlienClass1Reader reader = null;
      int choice;
      
      Database db = new Database();

      while ((choice = printMainMenu(scan)) != 4) {
         switch (choice) {
            case 1:  //reader = discoverReader();
                     break;
            case 2:  getTagsAndUpdateDatabase(reader, db);
                     break;
            case 3:  db.getRecentItems();
                     break;
            case 4:  System.exit(0);
            default: System.out.println("Invalid Option");
         }
      }

      System.out.println("Exiting...");
   }


   public static int printMainMenu(Scanner scan) {
      System.out.println();
      System.out.println("============================");
      System.out.println("|           MENU           |");
      System.out.println("============================");
      System.out.println("|1. Discover Reader        |");
      System.out.println("|2. Stream Tag Data        |");
      System.out.println("|3. Show Database          |");
      System.out.println("|4. Exit                   |");
      System.out.println("============================");
      System.out.print("Select an option: ");

      return scan.nextInt();
   }
   
   
   // Get unique list of tags after X number of trials and add to database
   // This will not update previous records of database, just add new entries
   public static void getTagsAndUpdateDatabase(AlienClass1Reader reader, Database db) {
      System.out.println("\nGetting tags within the area\n");
      int trials = 5;
      
      HashSet<String> tagList = new HashSet<String>();
      
      for (int idx = 0; idx < trials; idx++) {
         Tag[] alienTags;
         try {
            alienTags = reader.getTagList();
            if (alienTags != null) {
               for (Tag tag : alienTags) {
                  tagList.add(tag.toString());
               }
            }
         } catch (AlienReaderException e) {
            System.out.println("Error retrieving tag list");
            System.exit(-1);
         }
      }
      
      db.addTagsToDatabase(tagList);
   }

}
