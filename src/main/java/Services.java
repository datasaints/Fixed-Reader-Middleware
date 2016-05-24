package main.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class Services {

   private static final String ENDPOINT_FINDITEM = "http://datasaints-env.us-west-1.elasticbeanstalk.com/findItemByID/";
   private static final String ENDPOINT_UPDATEITEM = "http://datasaints-env.us-west-1.elasticbeanstalk.com/updateItem";
   private static final String USER_AGENT = "Mozilla/5.0";

   // HTTP GET - findItemByID
   public static JSONObject findItemByID(String itemID) throws Exception {
      URL obj = new URL(ENDPOINT_FINDITEM + itemID);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // optional default is GET
      con.setRequestMethod("GET");

      //add request header
      con.setRequestProperty("User-Agent", USER_AGENT);

      int responseCode = con.getResponseCode();
      System.out.println("\nSending 'GET' request to URL : " + ENDPOINT_FINDITEM);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
         response.append(inputLine);
      }
      in.close();

      JSONParser parser = new JSONParser();
      JSONObject jObj = (JSONObject) parser.parse(response.toString());


      //print result
      System.out.println(jObj.toJSONString());

      return jObj;
   }

   // HTTP POST - updateItem
   public static String updateItem(Item item) throws Exception {
      String payload = item.toJSONString();

      URL url = new URL(ENDPOINT_UPDATEITEM);
      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", USER_AGENT);
      connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoInput(true);
      connection.setDoOutput(true);

      connection.connect();

      OutputStream os = connection.getOutputStream();
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
      pw.write(payload);
      pw.close();

      InputStream is = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line = null;
      StringBuffer sb = new StringBuffer();
      while ((line = reader.readLine()) != null) {
          sb.append(line);
      }
      is.close();
      String response = sb.toString();

//      System.out.println("Response: " + response);
      return response;
   }
}
