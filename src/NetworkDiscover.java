import com.alien.enterpriseRFID.discovery.DiscoveryItem;
import com.alien.enterpriseRFID.discovery.DiscoveryListener;
import com.alien.enterpriseRFID.discovery.NetworkDiscoveryListenerService;

class NetworkDiscover implements DiscoveryListener {

   public NetworkDiscover() throws Exception {
      NetworkDiscoveryListenerService service = new NetworkDiscoveryListenerService();
      service.setDiscoveryListener(this);
      service.startService();

      while (service.isRunning()) {
         Thread.sleep(100);
      }
   }

   public void readerAdded(DiscoveryItem ds) {
      System.out.println("Reader Added: " + ds.toString());

   }

   @Override
   public void readerRemoved(DiscoveryItem ds) {
      System.out.println("Reader Removed: " + ds.toString());
   }

   @Override
   public void readerRenewed(DiscoveryItem ds) {
      System.out.println("Reader Renewed: " + ds.toString());

   }

}
