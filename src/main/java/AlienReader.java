package main.java;

import java.net.UnknownHostException;

import com.alien.enterpriseRFID.reader.*;
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
   private final static String DEFAULT_USERNAME = "alien", 
         DEFAULT_PASSWORD = "password";

   public AlienReader(String ipAddress, int portNumber) throws UnknownHostException, AlienReaderException {
      this(ipAddress, portNumber, DEFAULT_USERNAME, DEFAULT_PASSWORD);
   }

   /**
   * Constructor
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
    */
   public void changeReaderCredentials(String newUserName, String newPassword) {

      //Check to see if reader was initialized
      //Check to see if reader was opened successfully

      try {
         this.setReaderUsername(newUserName);
         this.setReaderPassword(newPassword);
      } catch (AlienReaderException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void run() {
      while (true) {
         try {
            System.out.println("Reader of " + this.getIPAddress() + " is running");
            
            Thread.sleep(1000);
         } catch (AlienReaderException e) {
            e.printStackTrace();
            break;
         } catch (InterruptedException e) {
            e.printStackTrace();
            break;
         }
      }
      
      System.out.println("thread done");
   }

}
