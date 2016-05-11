package main.java;


import java.net.UnknownHostException;
import java.util.ArrayList;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import static spark.Spark.*;


public class Driver {
   public static AlienReaderManager arManager = new AlienReaderManager();
   public static ArrayList<ReaderProfile> dbProfiles = new ArrayList<ReaderProfile>();
   public static Database mainDatabase = null;

   public static void main(String[] args) throws UnknownHostException, AlienReaderException, InterruptedException {
      // port(Integer.valueOf(System.getenv("PORT")));
      port(3000);

      initializeRoutes();

      System.out.println("Exiting main...");
   }

   private static void sendReaderCommand(AlienReader reader, String cmd) throws AlienReaderException {
	      if (reader == null) {
	         System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
	                            " call from the main menu first.");
	         return;
	      }

	      reader.open();
	      reader.doReaderCommand(cmd);
	      reader.close();
   }

   private static void getUpdatedProfiles() {
	   dbProfiles = mainDatabase.getReaderProfiles();
   }

   /*
    * Right now the only field to compare is frequency
    * we need to figure out what else we need in the profiles
    */
   private static void updateReaders() {
	   int i = 0;

	   while (i < dbProfiles.size()) {
		   ReaderProfile dbReader = dbProfiles.get(i);
		   ReaderProfile curReader = arManager.getReaderByIP(dbReader.getIP()).getProfile();

		   if (Integer.parseInt(dbReader.getFrequency()) != Integer.parseInt(curReader.getFrequency())) {
			   curReader.setFrequency(dbReader.getFrequency());
			   //sendReaderCommand(arManager.getReaderByIP(dbReader.IP), "set frequency to " + dbReader.frequency);
		   }
		   i++;
	   }
   }

   private static void initializeRoutes() {
      // http://sparkjava.com/documentation.html
      get("/hello", (req, res) -> "Hello World");

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
         //thread.start();

         return "succesfully discovered and added reader";
      });

      post("/newReader", (req, res) -> {
         String ip = req.queryParams("ip");
         String name = req.queryParams("name");
         return "";
      });

      put("/updateReaders", (req, res) -> {
         return "";
      });
   }

}