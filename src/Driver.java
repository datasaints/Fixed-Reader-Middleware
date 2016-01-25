import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

public class Driver {
   ArrayList<AlienClass1Reader> readerList = new ArrayList<AlienClass1Reader>();
   NetworkDiscover networkDiscover;

   public static void main(String[] args) throws UnknownHostException, AlienReaderException, InterruptedException {
      Scanner scan = new Scanner(System.in);
      AlienClass1Reader reader = null;
      int choice;
      
      Database db = new Database();

      while ((choice = printMainMenu(scan)) != 0) {
         try {
            switch (choice) {
               case 0:  System.exit(0);
                        break;
               case 1:  reader = discoverReader(reader, scan);
                        break;
               case 2:  getTagsAndUpdateDatabase(reader, db);
                        break;
               case 3:  db.getRecentItems();
                        break;
               case 4:  sendReaderCommand(reader, scan);
                        break;
               default: System.out.println("Invalid Option");
            }
         } catch (AlienReaderException e) {
            e.printStackTrace();
         }
      }

      System.out.println("Exiting...");
   }

   private static void sendReaderCommand(AlienClass1Reader reader, Scanner scan) throws AlienReaderException {
      if (reader == null) {
         System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
                            " call from the main menu first.");
         return;
      }
      reader.open();
      System.out.println("Entering reader communication mode ('q' to quit)");
      scan.nextLine();
      do {
         System.out.print("\nAlien> ");
         String line = scan.nextLine();
         if (line.equals("q")) break;
         System.out.println(reader.doReaderCommand(line));
      } while(true);

      System.out.println("\nGoodbye!");
      reader.close();
   }

   private static AlienClass1Reader discoverReader(AlienClass1Reader oldReader, Scanner scan) throws AlienReaderException, UnknownHostException {
      AlienController controller;
      AlienClass1Reader reader;
      String ipAddr, username, psswd;
      int portNum;
      
      if (oldReader != null) {
         System.out.println("Reader has already been configured!");
         System.out.println("Reader info: " + oldReader.getInfo());
         return oldReader;
      }
      scan.nextLine();
      System.out.println("Enter the LAN info of the reader");
      System.out.print("IP: ");
      ipAddr = scan.nextLine();
      System.out.print("Port: ");
      portNum = Integer.parseInt(scan.nextLine().trim());
      
      System.out.println("Enter the user credentials (default is alien/password)");
      System.out.print("Username: ");
      username = scan.nextLine();
      System.out.print("Password: ");
      psswd = scan.nextLine();
      
      // Example arguments: 192.168.0.106, 23, alien, password
      controller = new AlienController(ipAddr, portNum, username, psswd);
      controller.initializeReader();
      reader = controller.getReader();
      
//      reader.setConnection("COM" + scan.nextInt());
////      System.out.println();
//      try {
//         reader.open();
//         reader.clearTagList();
//         
//         // Establish behavioral parameters
//         reader.setAutoMode(AlienClass1Reader.OFF);
//         reader.setRFLevel(AlienController.RF_LEVEL);
//         reader.setTagMask(AlienController.tagMask);
//         reader.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
//         reader.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
//         reader.setTagListMillis(AlienClass1Reader.ON);
//         
//      } catch (AlienReaderException e) {
//         e.printStackTrace();
//      }
      System.out.println("Successful connection!");
      System.out.print("View reader info? [y/n]: ");
      if (scan.nextLine().trim().equalsIgnoreCase("y")) {
         System.out.println("Reader info: " + reader.getInfo());
      }

      return reader;
   }

   private static int printMainMenu(Scanner scan) {
      System.out.println();
      System.out.println("============================");
      System.out.println("|           MENU           |");
      System.out.println("============================");
      System.out.println("|1. Connect Reader         |");
      System.out.println("|2. Stream Tag Data        |");
      System.out.println("|3. Show Database          |");
      System.out.println("|4. Send Reader Command    |");
      System.out.println("|0. Exit                   |");
      System.out.println("============================");
      System.out.print("Select an option: ");

      return scan.nextInt();
   }
   
   // Get unique list of tags after X number of trials and add to database
   // This will not update previous records of database, just add new entries
   public static void getTagsAndUpdateDatabase(AlienClass1Reader reader, Database db) throws InterruptedException, AlienReaderException {
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
         }
         reader.clearTagList();
         Thread.sleep(1000);
      }

      db.addTagsToDatabase(tagList);
   }

}
