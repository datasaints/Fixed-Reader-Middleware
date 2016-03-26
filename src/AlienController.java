

import java.net.UnknownHostException;

import com.alien.enterpriseRFID.reader.*;
/**
 * Acts as a wrapper for the AlienClass1Reader class and provides
 * a number of convenience functions for interacting with Alien
 * readers.
 *
 */
public class AlienController {
   private AlienClass1Reader reader;

   private String ipAddress;
   private int portNumber;
   private String username;
   private String password;
   public static int nextReaderNumber = 1;

   public final static int RF_LEVEL = 300; // Max value for RF level is 315, set in tenthes of dB. Represents power to the antenna(s).
   public final static String tagMask = "E200 XXXX XXXX XXXX XXXX XXXX";
   private final static String defUserName = "alien", defPassword = "password";

   public AlienController(String ipAddress, int portNumber) {
      this(ipAddress, portNumber, defUserName, defPassword);
   }

   /**
   * Constructor
   * @param ipAddress
   * @param portNumber
   * @param username
   * @param password
   */
   public AlienController(String ipAddress, int portNumber, String username, String password) {
      this.ipAddress = ipAddress;
      this.portNumber = portNumber;
      this.username = username;
      this.password = password;
      reader = new AlienClass1Reader(ipAddress, portNumber);
   }

   /**
    * This constructor should be used when a NetworkDiscover instance
    * has found a new DiscoveryItem, in which case the .getReader() method
    * of that discovery item can be passed as the argument for this
    * AlienController constructor.
    *
    * @param discoveredReader AlienClass1Reader returned from NetworkDiscover.getReader()
    */
   public AlienController(AlienClass1Reader discoveredReader) {
      reader = discoveredReader;
      this.username = defUserName;
      this.password = defPassword;

      //Possibly add check to see if reader number is
      //if(discoveredReader.getReaderNumber() == 255)
         //reader has default configuration, should call initializeReader().
   }

   /**
   * Initialize reader settings and verify reader connection
   * @throws AlienReaderException
   * @throws UnknownHostException
   */
   public void initializeReader() throws UnknownHostException, AlienReaderException {
      // Establish user parameters
      reader.setUsername(username);
      reader.setPassword(password);

      // Establish connection and clear any initial tags read
      reader.open();
      reader.clearTagList();

      // Set reader identifiers
      reader.setReaderNumber(nextReaderNumber);
      reader.setReaderName("AlienReader" + nextReaderNumber++);

      // Establish behavioral parameters
      reader.setAutoMode(AlienClass1Reader.OFF);
      reader.setRFLevel(RF_LEVEL);
//      reader.setTagMask(tagMask);
      reader.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
      reader.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
      reader.setTagListMillis(AlienClass1Reader.ON);
   }

   // Return reader
   public AlienClass1Reader getReader() {
      return this.reader;
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
         reader.setReaderUsername(newUserName);
         reader.setReaderPassword(newPassword);
      } catch (AlienReaderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
