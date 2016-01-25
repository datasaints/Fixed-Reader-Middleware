

import java.net.UnknownHostException;

import com.alien.*;
import com.alien.enterpriseRFID.reader.*;

public class AlienController {
   private AlienClass1Reader reader;

   private String ipAddress;
   private int portNumber;
   private String username;
   private String password;

   private int RF_LEVEL = 300; // 167;
   private String tagMask = "E200 XXXX XXXX XXXX XXXX XXXX";

   /**
   * Constructor
   * @param ipAddress
   * @param portNumber
   * @param username
   * @param password
   * @throws UnknownHostException
   * @throws AlienReaderException
   */
   public AlienController(String ipAddress, int portNumber, String username, String password) {
      this.ipAddress = ipAddress;
      this.portNumber = portNumber;
      this.username = username;
      this.password = password;
   }

   /**
   * Initialize reader settings and verify reader connection
   * @throws AlienReaderException
   * @throws UnknownHostException
   */
   public void initializeReader() throws UnknownHostException, AlienReaderException {
   // Establish user parameters
      reader = new AlienClass1Reader(ipAddress, portNumber);
      reader.setUsername(username);
      reader.setPassword(password);

   // Establish connection and clear any initial tags read
      reader.open();
      reader.clearTagList();

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
   

}
