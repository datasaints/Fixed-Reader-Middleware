
   private static void sendReaderCommand(AlienClass1Reader reader, Scanner scan) throws AlienReaderException {
      if (reader == null) {
         System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
                            " call from the main menu first.");
         return;
      }
      reader.open();
      System.out.println("Entering reader communication mode ('q' to quit)");

      do {
         System.out.print("\nAlien> ");
         String line = scan.nextLine();
         if (line.equals("q")) break;
         System.out.println(reader.doReaderCommand(line));
      } while(true);

      System.out.println("\nGoodbye!");
      reader.close();
   }

   private static AlienClass1Reader discoverReader(AlienClass1Reader oldReader, Scanner scan) throws AlienReaderException, UnknownHostException {
      AlienReader controller;
      AlienClass1Reader reader;
      String ipAddr, username, psswd;
      int portNum;

      if (oldReader != null) {
         System.out.println("Reader has already been configured!");
         System.out.println("Reader info: " + oldReader.getInfo());
         return oldReader;
      }
//      else {
//         controller = new AlienController("192.168.0.106", 23, "alien", "password");
//         controller.initializeReader();
//         reader = controller.getReader();
//         return reader;
//      }

      System.out.println("Enter the LAN info of the reader");
      System.out.print("IP: ");
      ipAddr = scan.nextLine();
      System.out.print("Port: ");
      portNum = Integer.parseInt(scan.nextLine().trim());

      System.out.println("Enter the user credentials (default is alien/password)");
      System.out.print("Username: ");
      username = scan.nextLine();
      System.out.print("Password: ");
      psswd = scan.nextLine();

      // Example arguments: 192.168.0.106, 23, alien, password
      controller = new AlienReader(ipAddr, portNum, username, psswd);
      controller.initializeReader(username, psswd);
      reader = controller;

//      reader.setConnection("COM" + scan.nextInt());
////      System.out.println();
//      try {
//         reader.open();
//         reader.clearTagList();
//
//         // Establish behavioral parameters
//         reader.setAutoMode(AlienClass1Reader.OFF);
//         reader.setRFLevel(AlienController.RF_LEVEL);
//         reader.setTagMask(AlienController.tagMask);
//         reader.setTagListFormat(AlienClass1Reader.TEXT_FORMAT);
//         reader.setTagStreamFormat(AlienClass1Reader.TEXT_FORMAT);
//         reader.setTagListMillis(AlienClass1Reader.ON);
//
//      } catch (AlienReaderException e) {
//         e.printStackTrace();
//      }
      System.out.println("Successful connection!");
      System.out.print("View reader info? [y/n]: ");
      if (scan.nextLine().trim().equalsIgnoreCase("y")) {
         System.out.println("Reader info: " + reader.getInfo());
      }

      return reader;
   }

   private static int printMainMenu(Scanner scan) {
      int selection;

      System.out.println();
      System.out.println("============================");
      System.out.println("|           MENU           |");
      System.out.println("============================");
      System.out.println("|1. Connect Reader         |");
      System.out.println("|2. Stream Tag Data        |");
      System.out.println("|3. Show Database          |");
      System.out.println("|4. Send Reader Command    |");
      System.out.println("|0. Exit                   |");
      System.out.println("============================");
      System.out.print("Select an option: ");

      try {

         selection = Integer.parseInt(scan.nextLine().trim());
      } catch (Exception e) {
         selection = 99;
      }
      return selection;
   }


      // Get unique list of tags after X number of trials and add to database
      // This will not update previous records of database, just add new entries
      public static void getTagsAndUpdateDatabase(AlienClass1Reader reader, Database db) throws InterruptedException, AlienReaderException {
         if (reader == null) {
            System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
                               " call from the main menu first.");
            return;
         }

         System.out.println("\nGetting tags within the area\n");
         int trials = 5;
         HashSet<String> tagList = new HashSet<String>();

         reader.open();

         for (int idx = 0; idx < trials; idx++) {
            Tag[] alienTags;
            try {
               alienTags = reader.getTagList();
               if (alienTags != null) {
                     System.out.println("alienTags has " + alienTags.length + " elements on trial " + idx);
                  for (Tag tag : alienTags) {
                     tagList.add(tag.toString());
                  }
               }
               else System.out.println("alienTags was null :(");
            } catch (AlienReaderException e) {
               System.out.println("Error retrieving tag list");
            }
            reader.clearTagList();
            Thread.sleep(1000);
         }

         reader.close();
         if (tagList.isEmpty()) {
            System.out.println("1tag list is empty.");
            return;
         }
         db.addTagsToDatabase(tagList);
      }

      // Get unique list of tags after X number of trials and add to database
      // This will not update previous records of database, just add new entries
      public static void transactionDemoMode(AlienClass1Reader reader, Database db) throws InterruptedException, AlienReaderException, IOException {
         if (reader == null) {
            System.out.println("Reader has not been set. Perform a 'Connect Reader'" +
                               " call from the main menu first.");
            return;
         }

         System.out.println("\nEntering transaction mode\n");

         reader.open();

         int count = 0;
         while (count < 10) {
            HashSet<String> tagList = new HashSet<String>();
            Tag[] alienTags;
            long start = System.currentTimeMillis();
            String transactionTime;

            while (System.currentTimeMillis() - start < 10000) {
               try {
                  alienTags = reader.getTagList();
                  if (alienTags != null) {
                     for (Tag tag : alienTags) {
                        if (!tagList.contains(tag.toString()))
                           tagList.add(tag.toString());
                     }
                  }
               } catch (AlienReaderException e) {
                  System.out.println("Error retrieving tag list");
               }
               reader.clearTagList();
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
                  db.updateItemFieldById(tagString, "CheckOut", transactionTime);
                  db.updateItemFieldById(tagString, "CheckIn", "NULL");
               }
               else {
                  System.out.println("Checking the item in");
                  transactionTime = "'"+(new Date(System.currentTimeMillis())).toString()
                        + " " + (new Time(System.currentTimeMillis())).toString()+"'";
                  db.updateItemFieldById(tagString, "CheckIn", transactionTime);
                  db.updateItemFieldById(tagString, "CheckOut", "NULL");
               }
            }
            count++;
         }

         reader.close();
      }
   }
