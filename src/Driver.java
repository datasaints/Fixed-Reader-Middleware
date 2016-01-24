import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

public class Driver {
   ArrayList<AlienClass1Reader> readerList = new ArrayList<AlienClass1Reader>();
   NetworkDiscover networkDiscover;

   public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      AlienClass1Reader reader = null;
      int choice;
      
      Database db = new Database();

      while ((choice = printMainMenu(scan)) != 0) {
         try {
            switch (choice) {
               case 0:  System.exit(0);
                        break;
               case 1:  reader = discoverReader(scan);
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
      do {
         System.out.println("\nAlien>");
         String line = scan.nextLine();
         if (line.equals("q")) break;
         System.out.println(reader.doReaderCommand(line));
      } while(true);

      System.out.println("\nGoodbye!");
      reader.close();
   }


   private static AlienClass1Reader discoverReader(Scanner scan) {
      AlienClass1Reader reader = new AlienClass1Reader();
      
      System.out.print("Enter the COM number of the serial port: ");
      
      reader.setConnection("COM" + scan.nextInt());
//      System.out.println();
      try {
         reader.open();
         reader.clearTagList();
         
         // Establish behavioral parameters
         reader.setAutoMode(AlienClass1Reader.OFF);
         reader.setRFLevel(AlienController.RF_LEVEL);
         reader.setTagMask(AlienController.tagMask);
         reader.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
         reader.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
         reader.setTagListMillis(AlienClass1Reader.ON);
         
      } catch (AlienReaderException e) {
         e.printStackTrace();
      }
//      } catch (AlienReaderConnectionRefusedException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      } catch (AlienReaderNotValidException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      } catch (AlienReaderTimeoutException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      } catch (AlienReaderConnectionException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//      }
      
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
