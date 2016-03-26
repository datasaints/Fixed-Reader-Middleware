import java.util.HashMap;

import com.alien.enterpriseRFID.discovery.AlienDiscoveryUnknownReaderException;

/**
 * Class to contain the information from the fields of the database
 * reader profile table.
 *
 * @author Bobby
 *
 */
public class ReaderProfile {
   //public class variables will be the fields of the reader profile table

   /**
    * Main constructor, will check the database for a reader profile for
    * the reader with given name.
    *
    * @param readerName Name of the reader to search the database for
    * @throws AlienDiscoveryUnknownReaderException If reader profile does not exist in the database
    */
   public ReaderProfile(String readerName) throws AlienDiscoveryUnknownReaderException {
      try {
         //Query database reader profile table for reader by name of 'readerName'
      } catch (Exception e) {
         e.printStackTrace();
         throw new AlienDiscoveryUnknownReaderException();
      }
   }

   /**
    * Constructor for adding a new reader that does not have an existing
    * profile to the database reader profiles table.
    *
    * @param values
    */
   public ReaderProfile(HashMap<String, String> values) {

   }
}
