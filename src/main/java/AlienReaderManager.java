package main.java;

import java.util.HashMap;


// There should only ever be a single instance of AlienReaderManager
public class AlienReaderManager {

   // Map of all alien readers, key by IP address, value of AlienReader
   private HashMap<String, AlienReader> readerMap = new HashMap<String, AlienReader>();
   private NetworkDiscover networkDiscover = null; // = new NetworkDiscover();

   public AlienReaderManager() {

   }

   // Get reader map
   public HashMap<String, AlienReader> getReaderMap() {
      return this.readerMap;
   }

   // Retrieve specific reader by IP address
   public AlienReader getReaderByIP(String ipAddress) {
      return this.readerMap.get(ipAddress);
   }

   // Add new reader
   /* Adding new readers will eventually be done in NetworkDiscover
    * where it will call AlienReaderManager
    */
   public void addReader(String ipAddress, AlienReader reader) {
      this.readerMap.put(ipAddress, reader);

   }

   // Remove current reader, returns true if removed
   public boolean removeReaderByIP(String ipAddress) {
      AlienReader temp = this.readerMap.remove(ipAddress);
      return temp == null ? true : false;
   }
}
