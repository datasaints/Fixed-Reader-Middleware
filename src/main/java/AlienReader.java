package main.java;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONObject;

import com.alien.enterpriseRFID.reader.*;
import com.alien.enterpriseRFID.tags.*;
import com.alien.enterpriseRFID.notify.Message;
import com.alien.enterpriseRFID.notify.MessageListener;
import com.alien.enterpriseRFID.notify.MessageListenerService;


/**
 * Acts as a wrapper for the AlienClass1Reader class and provides
 * a number of convenience functions for interacting with Alien
 * readers.
 *
 */
public class AlienReader extends AlienClass1Reader implements Runnable, MessageListener, TagTableListener {
   //   public static int nextReaderNumber = 1;

   public final static int DEFAULT_RF_LEVEL = 315; // Max value for RF level is 315, set in tenthes of dB. Represents power to the antenna(s).
   public final static String DEFAULT_TAG_MASK = "E200 XXXX XXXX XXXX XXXX XXXX";
   private final static String DEFAULT_USERNAME = "alien";
   private final static String DEFAULT_PASSWORD = "password";

   private Database db = new Database(); // Different threads shouldn't share same connection
   private Thread thread = null;
   private ReaderProfile info = null;
   private TagTable tagTable = new TagTable();
   private MessageListenerService service = new MessageListenerService(4000);
   private String[] locations = {"Production", "Metrology"};

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
      tagTable.setTagTableListener(this);
      tagTable.setPersistTime(1000);
      service.setMessageListener(this);

      try {
         service.startService();
      } catch (IOException e) {
         System.out.println("Error starting MessageListenerService for reader: " + ipAddress);
         e.printStackTrace();
      }
      System.out.println("MessageListenerService has started for reader: " + ipAddress);
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




      // Set up TagStream.
      // Use this host's IPAddress, and the port number that the service is listening on.
      // getHostAddress() may find a wrong (wireless) Ethernet interface, so you may
      System.out.println("tagStreamAddress is: " + InetAddress.getLocalHost().getHostAddress()); //DELETE ME
      // need to substitute your computers IP address manually.
      this.setTagStreamAddress("192.168.2.2", service.getListenerPort());

      // Need to use custom format to get speed.
      // We need at least the EPC, read time in milliseconds, and the speed
      String customFormatStr = "Tag:${TAGID}, Last:${MSEC2}, Speed:${SPEED}";
      this.setTagStreamFormat(AlienClass1Reader.CUSTOM_FORMAT);
      this.setTagStreamCustomFormat(customFormatStr);
      // Tell the static TagUtil class about the custom format, so it can decode the streamed data.
      TagUtil.setCustomFormatString(customFormatStr);
      // Tell the MessageListenerService that the data has a custom format.
      service.setIsCustomTagList(true);
      this.setTagStreamMode(AlienClass1Reader.ON);

      // Set reader identifiers
      //      this.setReaderNumber(nextReaderNumber);
      //      this.setReaderName("AlienReader" + nextReaderNumber++);

      // Establish behavioral parameters
      //this.setAutoMode(AlienClass1Reader.OFF);
      this.autoModeReset();
      this.setAutoMode(AlienClass1Reader.ON);
      this.setRFLevel(DEFAULT_RF_LEVEL);
      //      this.setTagMask(tagMask);
      //      this.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
      //      this.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
      //      this.setTagListMillis(AlienClass1Reader.ON);
//      this.close();
      System.out.println("Initialized reader");
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
      ArrayList<Tag> tagList;
      /* TODO:
       * while condition should check against a flag/method inside of AlienReaderManager.java to see if it should continue normal operation (aka start stop)
       * This will probably involve looking at AlienReaderManager using myIP as a key to check some running state list.
       * Thread.stop() and the like have been deprecated so this is the proper way to stop the thread/runnable.
       * for now just putting true for development
       */
      try {
         System.out.println("Reader thread of " + this.getIPAddress() + " just started running.");
         while (true) {
               // Stay in smartTransactionMode until a transaction has occurred (someone walked in/out with inventory)
               //tagList = detectTransaction();
               //System.out.println("Reader of " + this.getIPAddress() + " just completed a transaction. Sanitizing data.");

               //System.out.println("Tags from last transaction:" + tagList.toString());
               //check db for previous locations of tags in tagList
               //update accordingly
               //updateTagLocations(tagList);

               // Associate transaction with an employee, if any
               //matchEmployee();

               this.tagTable.removeOldTags();

               Thread.sleep(1000);
         }

      } catch (AlienReaderException e) {
         System.out.println(e);
      } catch (InterruptedException e) {
         System.out.println(e);
      } catch (Exception e) {
         System.out.println(e);
      }
      System.out.println("thread done");
   }

   private void matchEmployee() {
      String employeeName = "Unknown";
      Tag employeeTag = null;

      // TODO: Decide how frequently we want to pull the employee information , for now, check against every tag on every transaction

      for (Tag tag : this.tagTable.getTagList()) {
         /* if (db.employeeTable.contains(tag.getID())) {
          *    employeeName =
          *    break;
          * }
          */
      }

      // assign employee name to each tag
      for (Tag tag : this.tagTable.getTagList()) {
         if (!tag.equals(employeeTag)) {
            //Assign the employee name to the employee field of inventory
            this.db.updateItemFieldById(tag.getTagID(), "Employee", employeeName);
         }
      }
      //TODO: Describe alternative method of keeping employee
   }
   private void updateTagLocations(Tag tag) throws Exception {
      HashMap<String, String> itemInfo;
      String newLocation, resp = "DIDNT UPDATE";

      System.out.println("Tag: " + tag.getTagID() + ", Speed: " + tag.getSmoothSpeed() + ", Position: " + tag.getSmoothPosition()
            + ", Direction: " + tag.getDirection());

      //Depending on that previous location AND depending on what access point we are monitoring, update the location
      // If new assigned location == location this inventory belongs to, mark as checked in


      JSONObject jsonItemObject = Services.findItemByID(tag.getTagID());
      Item item = new Item(jsonItemObject);

      if(tag.getSmoothSpeed() < 0) {
         //METROLOGY (INSIDE)
         item.setLocation(this.locations[0]);
         resp = Services.updateItem(item);
      }
      if(tag.getSmoothSpeed() > 0) {
         //PRODUCTION (OUTSIDE)
         item.setLocation(this.locations[1]);
         resp = Services.updateItem(item);
      }
      else {
         System.out.println("Hit default case in updateTagLocation");
      }

      System.out.println("Item: " + jsonItemObject.toJSONString() + "\nResp: " + resp);
   }

   private void updateTagLocations(ArrayList<Tag> tagList) throws Exception {
      HashMap<String, String> itemInfo;
      String newLocation;

      for (Tag tag : tagTable.getTagList()) {
         //         itemInfo = db.getItemInfoById(tag.toString());
         //         prevLocation = itemInfo.get("Location");

         System.out.println("Tag: " + tag.getTagID() + ", Speed: " + tag.getSmoothSpeed() + ", Position: " + tag.getSmoothPosition()
               + ", Direction: " + tag.getDirection());

         //Depending on that previous location AND depending on what access point we are monitoring, update the location
         // If new assigned location == location this inventory belongs to, mark as checked in
         switch (tag.getDirection()) {
            case Tag.DIR_AWAY:
               newLocation = this.locations[0];
               break;
            case Tag.DIR_TOWARD:
               newLocation = this.locations[1];
               break;
            default:
               System.out.println("Hit default case in updateTagLocation");
               itemInfo = db.getItemInfoById(tag.getTagID());
               newLocation = itemInfo.get("Location");
               break;
         }

         //db.updateItemFieldById(tag.getTagID(), "Location", newLocation);

         JSONObject jsonItemObject = Services.findItemByID(tag.getTagID());
         Item item = new Item(jsonItemObject);
         item.setLocation(newLocation);
         String resp = Services.updateItem(item);
         System.out.println("Item: " + jsonItemObject.toJSONString() + "\nResp: " + resp);
      }
   }

   private ArrayList<Tag> detectTransaction() throws InterruptedException, AlienReaderException, IOException {
      ArrayList<Tag> tagList = new ArrayList<Tag>(); // Final tag list to be returned and/or pushed to DB (should never be a large number)
      Tag[] tags;
      long readNewTagTime, nowTime;
      System.out.println("Entering transaction mode\n");

      /* TODO: new transaction logic
       * Maintain an array list of TAG objects, not strings
       *     OR use the TagTable class and/or tagstreaming etc (see java examples in documentation) to allow for speed/direction recording.
       * for every getTagList call, add new tags, replace old references if already in the arraylist
       * at the end of the transaction, use the last known location in the db along with the tag reference's getDirection/getSpeed to update the location
       * for each reader, use a String[2] to tell which locations this access point is separating
       * add a field in the database for "belongs in", if new location == "belongs in" then update item info to "checked in"
       */

      // Clear any initial tags to start from a clean state
      this.clearTagList();
      tagTable.clearTagList();

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
      for (Tag tag : tags) {
         tagList.add(tag);
         tagTable.addTag(tag);
      }

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
               tagTable.addTag(tag);
               for (Tag t : tagList)
                  if (t.getTagID().equals(tag.getTagID()))
                     tagList.remove(tag);
               tagList.add(tag);
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

      // TODO: Remove tagList logic (it's been replaced by tagTable)
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

   public void setLocations(String[] inputLocations) {
      if (inputLocations.length != 2)
         throw new IllegalArgumentException("String array should be of length 2.");

      /*
       * TODO: Possibly check that input string array elements match known locations via database query
       * eg:
       * locationList = db.getLocations; // A method either we implement or database team provides as http function
       * if (!locationList.contains(inputLocations[0]) || !locationList.contains(inputLocations[1])
       *    throw new IllegalArgumentException("String array elements do not match known locations");
       */

      this.locations = Arrays.copyOf(inputLocations, 2);
   }

   public String[] getLocations() {
      return this.locations;
   }

   /**
    * Implements the TagTableListener interface.
    *
    * When a TagTable is updated, it tells its TagTableListener via these methods.
    * We let the TagTable tell us about new tag reads, so that we get access to the
    * smoothed speed and distance values without having to manually look up in the
    * TagTable after   tags.T
    */
   @Override
   public void tagAdded(Tag tag) {
      System.out.println("NEW Tag: " + tag.getTagID() + ", v0=" + tag.getSpeed() + ", d0=" + tag.getDirection());
   }
   @Override
   public void tagRenewed(Tag tag) {
      System.out.println(tag.getTagID() + ", v=" + tag.getSmoothSpeed() + ", d=" + tag.getDirection());
   }
   @Override
   public void tagRemoved(Tag tag) {
      System.out.println("Tag REMOVED: " + tag.getTagID() + ", v0=" + tag.getSpeed() + ", d0=" + tag.getDirection());

      try {
         updateTagLocations(tag);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }


   /**
    * Implements the MessageListener interface.
    *
    * Data from the reader is captured by a MessageListenerService, which then
    * tells its MessageListeners via this method.
    */
   @Override
   public void messageReceived(Message message){
      //System.out.println("message received");
      for (int i=0; i < message.getTagCount(); i++) {
         Tag tag = message.getTag(i);

         // TagTable will automatically merge new information about an existing
         // tag, including calculating a smoothed speed and distance update.
         tagTable.addTag(tag);

         // After this merge is done, the TagTable will notify us with the final
         // data via the TagTableListener interface (tagAdded, tagRenewed, etc.).
      }
   }

}