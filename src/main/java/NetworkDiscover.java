package main.java;

import java.net.UnknownHostException;

import com.alien.enterpriseRFID.discovery.AlienDiscoverySocketException;
import com.alien.enterpriseRFID.discovery.AlienDiscoveryUnknownReaderException;
import com.alien.enterpriseRFID.discovery.DiscoveryItem;
import com.alien.enterpriseRFID.discovery.DiscoveryListener;
import com.alien.enterpriseRFID.discovery.NetworkDiscoveryListenerService;
import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;

/**
 * This service class is meant to act as a listener for the network heartbeats
 * that newly connected readers on the network will send out at regular intervals.
 * After receiving a heartbeat, this class adds the reader to the AlienReaderManager
 * class and updates any settings if available from the reader profile section of the
 * database. The discovery service notifies this application when a reader is
 * discovered, seen again, or lost.
 *
 */
class NetworkDiscover implements DiscoveryListener {
   public NetworkDiscoveryListenerService service;

   /**
    * Default constructor beginning the service that listens for
    * AlienReader network heart beats.
    *
    */
   public NetworkDiscover() {
      this.service = new NetworkDiscoveryListenerService();
      service.setDiscoveryListener(this);
      try {
         service.startService();
      } catch (AlienDiscoverySocketException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * A new reader has been discovered to the network.
    * This method implements the DiscoveryListener interface.
    *
    * @param discoveryItem details of the newly-discovered reader
    */
   @Override
   public void readerAdded(DiscoveryItem discoveryItem){
      System.out.println("Reader Added:\n" + discoveryItem.toString());


      //Make sure that you use the AlienReader class instead of the AlienClass1Reader,
      //DONT use the DiscoveryItem.getReader method unless it is for constructing a AlienController

      //Check to see if there is a reader profile in the database
         //Perhaps use the discoveryItem.getReaderName()
      //If so, set those configurations to the reader

      //Otherwise use a set of default configurations, then notify webApp

      //Finally, add the reader to the AlienReaderManager
      try {
         AlienClass1Reader reader = discoveryItem.getReader();
         Driver.arManager.addReader(reader.getIPAddress(), new AlienReader(reader));
      } catch (AlienDiscoveryUnknownReaderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (AlienReaderException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (UnknownHostException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   /**
    * A known reader has been seen again.
    * This method implements the DiscoveryListener interface.
    *
    * @param discoveryItem details of the renewed reader
    */
   @Override
   public void readerRenewed(DiscoveryItem discoveryItem) {
      //May not even need to use this method.

      //Possible use of method could be to update renewal time to database every 1/5/10 minutes?
      System.out.println("Reader Renewed:\n" + discoveryItem.toString());
   }


   /**
    * A reader has been removed from the network and is no longer available.
    * This method implements the DiscoveryListener interface.
    *
    * @param discoveryItem details of the removed reader
    */
   @Override
   public void readerRemoved(DiscoveryItem discoveryItem) {
      System.out.println("Reader Removed:\n" + discoveryItem.toString());

      //Should update database reader profile connected field to 'Disconnected'
   }




}
