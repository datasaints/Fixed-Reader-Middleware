package main.java;


import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.ModelAndView;
import static spark.Spark.get;


public class Driver {
   public static AlienReaderManager arManager = new AlienReaderManager();
   public static ArrayList<ReaderProfile> dbProfiles = new ArrayList ArrayList<ReaderProfile>;
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
	   
	   while (i < profiles.size()) {
		   ReaderProfile dbReader = dbProfiles.get(i);
		   ReaderProfile curReader = (arManager.getReaderByIP(dbReader.IP)).info;
		   
		   if (Integer.parseInt(dbReader.frequency) != Integer.parseInt(curReader.frequency)) {
			   curReader.setFrequency(dbReader.frequency);
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
         mainDatabase = reader.db;
         res.status(200);

         Thread thread = new Thread(reader);
         reader.setThread(thread);
         thread.start();

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