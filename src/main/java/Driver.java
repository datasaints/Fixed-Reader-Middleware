package main.java;


import java.net.UnknownHostException;
import java.util.ArrayList;

import com.alien.enterpriseRFID.reader.AlienReaderException;

import static spark.Spark.*;
import main.java.Services;
import main.java.Item.Status;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Main Driver class for the Middleware Server
 */
public class Driver {
   // Reader manager that contains all instances of readers
   public static AlienReaderManager arManager = new AlienReaderManager();

   // List of all database profiles
   public static ArrayList<ReaderProfile> dbProfiles = new ArrayList<ReaderProfile>();

   // Main Database reference
   public static Database mainDatabase = null;

   /**
    * Main
    */
   public static void main(String[] args) throws UnknownHostException, AlienReaderException, InterruptedException {
      // port(Integer.valueOf(System.getenv("PORT")));

      // Sets localhost port to 3000
      port(3000);

      // Initialize all routes
      initializeRoutes();

      System.out.println("Exiting main...");
   }

   /**
    * Sends a command to a specified reader
    * @param  reader               the desired reader
    * @param  cmd                  the desired command
    * @throws AlienReaderException if the command fails to execute on the reader
    */
   private static void sendReaderCommand(AlienReader reader, String cmd) throws AlienReaderException {
      // Verify valid reader
      if (reader == null) {
         System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
                            " call from the main menu first.");
         return;
      }

      reader.open();
      reader.doReaderCommand(cmd);
      reader.close();
   }

   /**
    * Retrieves and sets the list of readers currently handled by the reader
    */
   private static void getUpdatedProfiles() {
	   dbProfiles = mainDatabase.getReaderProfiles();
   }

   /**
    * Initializes all the URL routes of the middleware server
    * http://sparkjava.com/documentation.html
    */
   private static void initializeRoutes() {
      // POST addReader - Add a new reader and immediately runs a new thread for it
      post("/addReader", (req, res) -> {
         String ip = req.queryParams("ip");
         int port = Integer.valueOf(req.queryParams("port"));
         String username = req.queryParams("username");
         String password = req.queryParams("password");

         AlienReader reader = null;
         try {
            reader = new AlienReader(ip, port, username, password);
         }
         catch (Exception e) {
            res.status(400);
            return "Error: " + e.toString();
         }

         arManager.addReader(reader.getIPAddress(), reader);
         mainDatabase = reader.getDatabase();
         res.status(200);

         Thread thread = new Thread(reader);
         reader.setThread(thread); // Why is this necessary?
         thread.start();

         return "succesfully discovered and added reader";
      });

      // GET testPOST, used for testing purposes
      get("/testPOST", (req, res) -> {
         Item testItem = new Item();
         testItem.setId("7");
         testItem.setOwner("Production");
         testItem.setSerial(145781);
         testItem.setItemName("METItem7");
         testItem.setLocation("Metrology");
         testItem.setStatus(Status.CHECKED_IN);
         testItem.setLastCalibrated(new Date(2016, 4, 8));
         testItem.setCheckTime(new Timestamp(System.nanoTime()));

         return Services.updateItem(testItem);
      });
   }
}