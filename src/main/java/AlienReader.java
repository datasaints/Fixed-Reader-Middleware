package main.java;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.alien.enterpriseRFID.reader.*;
import com.alien.enterpriseRFID.tags.Tag;
/**
 * Acts as a wrapper for the AlienClass1Reader class and provides
 * a number of convenience functions for interacting with Alien
 * readers.
 *
 */
public class AlienReader extends AlienClass1Reader implements Runnable {
//   public static int nextReaderNumber = 1;

   public final static int DEFAULT_RF_LEVEL = 300; // Max value for RF level is 315, set in tenthes of dB. Represents power to the antenna(s).
   public final static String DEFAULT_TAG_MASK = "E200 XXXX XXXX XXXX XXXX XXXX";
   private final static String DEFAULT_USERNAME = "alien";
   private final static String DEFAULT_PASSWORD = "password";

   private Database db = new Database(); // Different threads shouldn't share same connection
   private Thread thread = null;
   private ReaderProfile info = null;

   /**
    * Overloaded constructor. Will use default values for user name/password.
    * @param ipAddress
    * @param portNumber
    * @throws UnknownHostException
    * @throws AlienReaderException
    */
   public AlienReader(String ipAddress, int portNumber) throws UnknownHostException, AlienReaderException {
      this(ipAddress, portNumber, DEFAULT_USERNAME, DEFAULT_PASSWORD);
   }

   /**
   * Main constructor. All other constructors will eventually arrive here.
   * @param ipAddress
   * @param portNumber
   * @param username
   * @param password
    * @throws AlienReaderException
    * @throws UnknownHostException
   */
   public AlienReader(String ipAddress, int portNumber, String username, String password) throws UnknownHostException, AlienReaderException {
      super(ipAddress, portNumber);
      initializeReader(username, password);
      info = new ReaderProfile(ipAddress);
   }

   /**
    * This constructor should be used when a NetworkDiscover instance
    * has found a new DiscoveryItem, in which case the .getReader() method
    * of that discovery item can be passed as the argument for this
    * AlienController constructor.
    *
    * @param discoveredReader AlienClass1Reader returned from NetworkDiscover.getReader()
    * @throws AlienReaderException
    * @throws UnknownHostException
    */
   public AlienReader(AlienClass1Reader discoveredReader) throws AlienReaderException, UnknownHostException {
      this(discoveredReader.getIPAddress(),
            discoveredReader.getCommandPort(),
            discoveredReader.getUsername(),
            discoveredReader.getPassword());

      //Possibly add check to see if reader number is
      //if(discoveredReader.getReaderNumber() == 255)
         //reader has default configuration, should call initializeReader().
   }

   /**
   * Initialize reader settings and verify reader connection
   * @throws AlienReaderException
   * @throws UnknownHostException
   */
   public void initializeReader(String username, String password) throws UnknownHostException, AlienReaderException {
      // Establish user parameters *no longer needed
      this.setUsername(username);
      this.setPassword(password);

      // Establish connection and clear any initial tags read
      this.open();
      this.clearTagList();

      // Set reader identifiers
//      this.setReaderNumber(nextReaderNumber);
//      this.setReaderName("AlienReader" + nextReaderNumber++);

      // Establish behavioral parameters
      this.setAutoMode(AlienClass1Reader.OFF);
      this.setRFLevel(DEFAULT_RF_LEVEL);
//      this.setTagMask(tagMask);
      this.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
      this.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
      this.setTagListMillis(AlienClass1Reader.ON);

   }

   /**
    *
    * @param newUserName
    * @param newPassword
    * @throws AlienReaderException
    */
   public void changeReaderCredentials(String newUserName, String newPassword) throws AlienReaderException {

      //Check to see if reader was initialized
      //Check to see if reader was opened successfully

      this.setReaderUsername(newUserName);
      this.setReaderPassword(newPassword);
   }

   @Override
   public void run() {
      ArrayList<String> tagList;
      /* TODO:
       * while condition should check against a flag/method inside of AlienReaderManager.java to see if it should continue normal operation (aka start stop)
       * This will probably involve looking at AlienReaderManager using myIP as a key to check some running state list.
       * Thread.stop() and the like have been deprecated so this is the proper way to stop the thread/runnable.
       * for now just putting true for development
       */
      while (true) {
         try {
            System.out.println("Reader of " + this.getIPAddress() + " is running.");
            // Stay in smartTransactionMode until a transaction has occurred (someone walked in/out with inventory)
            tagList = detectTransaction();
            System.out.println("Reader of " + this.getIPAddress() + " just completed a transaction. Sanitizing data.");

            System.out.println("Tags from last transaction:" + tagList.toString());
            //check db for previous locations of tags in tagList
            //update accordingly

            //transactionDemoMode();
            Thread.sleep(1000);
         } catch (AlienReaderException e) {
            System.out.println(e);
            break;
         } catch (InterruptedException e) {
            System.out.println(e);
            break;
         } catch (IOException e) {
            System.out.println(e);
            break;
         }
      }

      System.out.println("thread done");
   }

   private ArrayList<String> detectTransaction() throws InterruptedException, AlienReaderException, IOException {
      ArrayList<String> tagList = new ArrayList<String>(); // Final tag list to be returned and/or pushed to DB (should never be a large number)
      Tag[] tags;
      long readNewTagTime, nowTime;
      System.out.println("Entering transaction mode\n");

      // Clear any initial tags to start from a clean state
      this.clearTagList();

      /* TODO: Delete this reference comment
       * If reader is in interactive mode, getTagList triggers a scan as well as report of tags
       * if in automode, just returns current list of tags
       *
       * getTagList returns null if no tags were read
       *
       * Read for tags and sleep if none were read
       */
      while((tags = this.getTagList()) == null)
         Thread.sleep(200); // TODO: test this value. want smallest number or no sleep at all if possible without too much system strain.

      // At this point the reader has read one or more tags. A transaction is occurring. Record time of last new read (now).
      readNewTagTime = System.currentTimeMillis();

      //Add each tag that was just read to tagList
      for (Tag tag : tags)
         tagList.add(tag.toString());

      while((nowTime = System.currentTimeMillis()) - readNewTagTime < 2000) { // TODO: test this time limit. too small/large a number is bad. probably enough time to push a cart through and not read another tag.
         // Attempt to rescan and see if any new tags were read
         try {
            tags = this.getTagList();
         } catch (AlienReaderException e) {
            System.out.println("Error retrieving tag list");
            e.printStackTrace();
         }

         if (tags != null) {
            for (Tag tag : tags) {
               if (!tagList.contains(tag.toString()))
                  tagList.add(tag.toString());
            }
            readNewTagTime = nowTime;
         }
      }
      // Transaction timer has finished, therefore construct initial list for DB transaction
      // TODO: test if internal tag list only keeps unique tags or multiple instances of same tag
      // If unique then we can remove the if statement and go straight to adding the tag string
//      for (Tag tag : tags) {
//         if (!tagList.contains(tag.toString()))
//            tagList.add(tag.toString());
//      }
      return tagList;
   }

   public void transactionDemoMode() throws InterruptedException, AlienReaderException, IOException {


      System.out.println("\nEntering demo transaction mode\n");
      this.clearTagList();

      int count = 0;
      while (count < 3) {
         HashSet<String> tagList = new HashSet<String>();
         Tag[] alienTags;
         long start = System.currentTimeMillis();
         String transactionTime;

         while (System.currentTimeMillis() - start < 3000) {
            try {
               alienTags = this.getTagList();
               if (alienTags != null) {
                  for (Tag tag : alienTags) {
                     if (!tagList.contains(tag.toString()))
                        tagList.add(tag.toString());
                  }
               }
            } catch (AlienReaderException e) {
               System.out.println("Error retrieving tag list");
            }
            this.clearTagList();
         }

         if (tagList.isEmpty())
            continue;
         else
            System.out.println("RFID tag(s) detected");

         for (String tagString : tagList) {
            System.out.println("Pulling information for tag: " + tagString);
            //pull database information for that item
            HashMap<String, String> itemInfo = db.getItemInfoById(tagString);

            //check which way it should be updated
            if (itemInfo.get("CheckOut") == null || itemInfo.get("CheckOut").isEmpty()) {
               System.out.println("Checking the item out");
               transactionTime = "'"+(new Date(System.currentTimeMillis())).toString()
                     + " " + (new Time(System.currentTimeMillis())).toString()+"'";
               System.out.printf("\tTag: %s, time: %s\n", tagString, transactionTime);
               // db.updateItemFieldById(tagString, "CheckOut", transactionTime);
               // db.updateItemFieldById(tagString, "CheckIn", "NULL");
            }
            else {
               System.out.println("Checking the item in");
               transactionTime = "'"+(new Date(System.currentTimeMillis())).toString()
                     + " " + (new Time(System.currentTimeMillis())).toString()+"'";
               System.out.printf("\tTag: %s, time: %s\n", tagString, transactionTime);
               // db.updateItemFieldById(tagString, "CheckIn", transactionTime);
               // db.updateItemFieldById(tagString, "CheckOut", "NULL");
            }
         }
         count++;
      }

   }

   /* Why do we need setThread and getThread?
    * Thread management should be done by AlienReaderManager
    * otherwise what is the point of it?
    */
   public void setThread(Thread thread) {
      this.thread = thread;
   }

   public Thread getThread() {
      return this.thread;
   }

   public void setProfile(ReaderProfile info) {
	   this.info = info;
   }

   public ReaderProfile getProfile() {
	   return this.info;
   }

   public Database getDatabase() {
	   return this.db;
   }
}
