package main.java;

import java.util.HashMap;

/**
 * This service class is meant to act as a listener for the network
 * heartbeats that unconnected readers on the network will send out
 * at regular intervals. After receiving a heartbeat, this class adds
 * the reader to the Alien manager class and updates any settings if
 * available from the reader profile section of the database. There
 * should only ever be a single instance of AlienReaderManager.
 *
 */
public class AlienReaderManager {

   // Map of all alien readers. Key: IP address, value: AlienReader reference
   private HashMap<String, AlienReader> readerMap = null;
   private NetworkDiscover networkDiscover = null;

   /**
    * Default constructor. Initializes instance variables.
    */
   public AlienReaderManager() {
      readerMap = new HashMap<String, AlienReader>();
      networkDiscover = new NetworkDiscover();
   }

   /**
    * Returns this manager's IP to AlienReader hashmap.
    * @return This manager's hashmap.
    */
   public HashMap<String, AlienReader> getReaderMap() {
      return this.readerMap;
   }

   /**
    * Retrieve specific reader reference by IP address.
    * @param ipAddress The local network IP address of the desired reader.
    * @return The AlienReader reference associated with the given IP.
    */
   public AlienReader getReaderByIP(String ipAddress) {
      return this.readerMap.get(ipAddress);
   }

   /**
    * Add new reader to this manager.
    * @param ipAddress
    * @param reader
    */
   public void addReader(String ipAddress, AlienReader reader) {
      /* Adding new readers will eventually be done in NetworkDiscover
       * where it will call this method
       */
      this.readerMap.put(ipAddress, reader);

   }

   /**
    * Removes a reader, returns true if removed.
    * @param ipAddress The local IP address of the reader to be removed.
    * @return True if reader was successfully removed, otherwise false.
    */
   public boolean removeReaderByIP(String ipAddress) {
      return this.readerMap.remove(ipAddress) == null ? true : false;
   }
}
