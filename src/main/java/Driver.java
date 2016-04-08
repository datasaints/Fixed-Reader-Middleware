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


   public static void main(String[] args) throws UnknownHostException, AlienReaderException, InterruptedException {
      // port(Integer.valueOf(System.getenv("PORT")));
      port(3000);

      initializeRoutes();

      System.out.println("Exiting main...");
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