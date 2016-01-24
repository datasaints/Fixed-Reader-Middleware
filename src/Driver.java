import com.alien.enterpriseRFID.discovery.*;
import com.alien.enterpriseRFID.reader.AlienClass1Reader;

import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
   ArrayList<AlienClass1Reader> readerList = new ArrayList<AlienClass1Reader>();
   static Thread networkDiscover;

   public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      AlienClass1Reader reader;
      int choice;

      while ((choice = printMainMenu(scan)) != 4) {
         switch (choice) {
            case 0:  reader = discoverReader();
                     break;
            case 1:  streamTags();
                     break;
            case 2:  printDatabase();
                     break;
            default: System.out.println("Invalid Option");
         }
      }

      System.out.println("Exiting...");
   }


   private static int printMainMenu(Scanner scan) {
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

   public static AlienClass1Reader discoverReader() {
      if (networkDiscover == null) {
         networkDiscover = new NetworkDiscover()
      }
         
         
      
      return null;
   }
}
