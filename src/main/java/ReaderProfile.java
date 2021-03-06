package main.java;

import java.util.HashMap;

/**
 * Class to contain the information from the fields of the database
 * reader profile table.
 */
public class ReaderProfile {

  //public class variables will be the fields of the reader profile table
	private String ID = null;
	private String frequency = null;
	private String IP = null;

   /**
    * Main constructor, will check the database for a reader profile for
    * the reader with given name.
    *
    * @param readerName Name of the reader to search the database for
    * @throws AlienDiscoveryUnknownReaderException If reader profile does not exist in the database
    */
	/*
   public ReaderProfile(String readerName) throws AlienDiscoveryUnknownReaderException {
      try {
         //Query database reader profile table for reader by name of 'readerName'
      } catch (Exception e) {
         e.printStackTrace();
         throw new AlienDiscoveryUnknownReaderException();
      }
   }
   */

   /**
    * Constructor for adding a new reader that does not have an existing
    * profile to the database reader profiles table.
    *
    * @param values
    */
   public ReaderProfile(HashMap<String, String> values) {

   }

   /**
    * Constructor for making a profile according to a database
    * reader profile entry.
    *
    * @param values
    */
   public ReaderProfile(String ID, String frequency, String IP) {
	   this.ID = ID;
	   this.frequency = frequency;
	   this.IP = IP;
   }

   /**
    * Constructor for making a profile according to an IP address
    * @param  IP the string representation of an IP address
    */
   public ReaderProfile(String IP) {
	   this.IP = IP;
   }

   // Setters

   public void setFrequency(String freq) {
	   this.frequency = freq;
   }

   // Getters

   public String getFrequency() {
	   return this.frequency;
   }

   public String getIP() {
	   return this.IP;
   }

   public String getID() {
	   return this.ID;
   }
}
