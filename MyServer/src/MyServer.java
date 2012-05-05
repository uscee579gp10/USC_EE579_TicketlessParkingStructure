import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


/**
 * ROWS : represents number of parking lots we take care of
 * lightThresh : a value set to distinguish between "Lot Available" and "Lot Occupied"
 * sigStableThresh : a time factor that avoid short term signal variation
 * status : indicate parking lot status
 * currentState : contains short term signal based state
 */

public class MyServer extends Thread {
   private final static int ROWS = 5;
   private final static int COLUMNS = 2;
   private final static int lightThresh = 100;
   private final static int sigStableThresh = 15;
   private final static int rsvpTimeOut = 50;
   private static int[][] rsvpBitMap;
   private static int[] signalFromMote;
   private static long[][] status;
   private static int[] currentState;
   private static int[] timerOn;
   private OutputStream out = null;
   private DataInputStream in = null;
   private ServerSocket serverSocket = null;
   private Socket clientSocket = null;
   private int serverPort = 9777;
   private static HashMap<String, String> users;
   private static HashMap<String, String> receipts;
   private static String[] currentHolder;

   public MyServer() throws IOException {
       initializeServer(serverPort);
       rsvpBitMap = new int[ROWS][COLUMNS];
       signalFromMote = new int[ROWS];
       status = new long[ROWS][COLUMNS + 1];
       currentState = new int[ROWS];
       timerOn = new int[ROWS];
       users = new HashMap<String, String>();
       currentHolder = new String[ROWS];
       receipts=new HashMap<String, String>();
       //rsvpBitMap[0][0]=1;
       //rsvpBitMap[3][0]=1;

   }

   public MyServer(int serverPort) throws IOException {
       this.serverPort = serverPort;
       initializeServer(serverPort);
       rsvpBitMap = new int[ROWS][COLUMNS];
       signalFromMote = new int[ROWS];
       status = new long[ROWS][COLUMNS + 1];
       currentState = new int[ROWS];
       timerOn = new int[ROWS];
       users = new HashMap<String, String>();
       currentHolder = new String[ROWS];
       receipts=new HashMap<String, String>();
       //rsvpBitMap[0][0]=1;
       //rsvpBitMap[3][0]=1;
   }

   //initialize server
   private void initializeServer(int port) throws IOException {
       //TODO: Set up the server to open on the serverPort
       //ServerSocket serverSocket = null;
       try {
           this.serverSocket = new ServerSocket(port);
       } catch (IOException e) {
           System.err.println("Could not listen on port: " + port + ".");
           System.exit(1);
       }
       System.out.println();
       System.out.println("==========Ticketless Parking Structure Server System==========");
       System.out.println();
       System.out.println("Server Start to Listen on port: " + port);
       //Socket clientSocket = null;
   }

   public void getConnect() {
       try {
           this.clientSocket = this.serverSocket.accept();
           new ClientHandler(clientSocket).start();
       } catch (IOException e) {
           System.err.println("ERROR - Accept failed.");
           System.exit(1);
       }
   }

   //find a lot for client return -1 if full; otherwise return the lot number(corresponing to the mote with the same id)
   public static int rsvp() {
       //rsvpBitMapLock.lock();
       for (int i = 0; i < ROWS; i++) {
           if (rsvpBitMap[i][0] == 0) {
               rsvpBitMap[i][0] = 1;
               timerOn[i] = 1;
               //rsvpBitMapLock.unlock();
               return i;
           }
       }
       //rsvpBitMapLock.unlock();
       return -1;
   }
   
   //Add user info when a user register
   public static boolean addUsers(String username, String password) {
       if (users.containsKey(username)) {
           return false;
       }
       users.put(username, password);
       return true;
   }

   //Check whether the user has already existed
   public static boolean checkUsers(String username, String password) {
       if (!users.containsKey(username)) {
           return false;
       }
       return (password.equals(users.get(username))) ? true : false;
   }

   //assign the user to a lot
   public static void enterIntoSpace(int lotNumber, String name) {
       currentHolder[lotNumber] = name;
   }

   //find the lot number given a user name
   public static int checkLotNumber(String name) {
       for (int i = 0; i < COLUMNS; i++) {
           if (currentHolder[i] != null && currentHolder[i].equals(name)) {
               return i;
           }
       }
       return -1;
   }
   
   //update the info when a customer leave
   public static void leaveSpace(int lotNumber) {
       currentHolder[lotNumber] = null;
   }

   //prepare the parking receipt to a end user when a user leaves
   public static String getReceipt(String username){
	  if(receipts.containsKey(username)){
		  String receipt = receipts.get(username);
		  receipts.remove(username);
		  return receipt;
	  }else{
		  return "Still parking!";
	  }
   }
   
   //main server process 
   public void run() {
		long startTime1 = 0;
		long endTime1 = 0;
		long workTime1 = 0;
		int[] signalFromMote_copy = new int[ROWS];
		Oscilloscope test = new Oscilloscope();
		File inFile = new File("/opt/tinyos-2.1.1/apps/Oscilloscope/java/temp1.txt");
		String sigStr="";
		while(true){
			startTime1=new Date().getTime();
			try {
				Scanner in = new Scanner(inFile);
				sigStr=in.nextLine();
				Scanner sc = new Scanner(sigStr);
			    for(int k=0;k<5;k++){
			    	signalFromMote[k]= sc.nextInt();
			    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

           for (int i = 0; i < ROWS; i++) {
               currentState[i] = (signalFromMote[i] < lightThresh) ? 1 : 0;
           }
           for (int i = 0; i < ROWS; i++) {
               if (currentState[i] != status[i][0]) {
                   //short term state changes
                   status[i][1]=status[i][1]+1;
                   if (status[i][1] >= sigStableThresh) {
                       if (status[i][0] == 1) { //1->0
                           //car just leave the lot => update rsvpBitMap to be available
                           rsvpBitMap[i][0] = 0;
                           rsvpBitMap[i][1] = 0;
                           Date myTimer2 = new Date();
                           DateFormat df1 = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                           String leaveTime = df1.format(myTimer2);
                           String enterTime = df1.format(status[i][2]);
                           long diffInSeconds = (myTimer2.getTime()-status[i][2]) / 1000;
                           // calculate the parking duration
                           long diff[] = new long[] { 0, 0, 0, 0 };
                           /* sec */diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
                           /* min */diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
                           /* hours */diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
                           /* days */diff[0] = (diffInSeconds = (diffInSeconds / 24));
                           String duration = String.format(
                                   "%d day%s, %d hr%s, %d min%s, %d sec%s ",
                                   diff[0],
                                   diff[0] > 1 ? "s" : "",
                                   diff[1],
                                   diff[1] > 1 ? "s" : "",
                                   diff[2],
                                   diff[2] > 1 ? "s" : "",
                                   diff[3],
                                   diff[3] > 1 ? "s" : "");
                           
                           String receipt = "Car of spot: " + i + " left"
                                   + "; Name:" + currentHolder[i]
                                   + "; Enter Time:" + enterTime
                                   + "; Leaving Time:" + leaveTime
                                   + "; Total Time:" + duration;
                           receipts.put(currentHolder[i], receipt);
                           System.out.println("Sys Msg - " +receipt);
                           status[i][0] = 0;
                           status[i][1] = 0;
                           status[i][2] = 0;

                       } else if (status[i][0] == 0) { //0->1
                           if (rsvpBitMap[i][0] == 0) {
                               System.out.println("Sys ALERT - Please let parking enforcement go to spot: " + i);
                           } else {
                               rsvpBitMap[i][1] = 0;
                               status[i][0] = 1;
                               status[i][1] = 0;
                               Date myTimer3 = new Date();
                               status[i][2] = myTimer3.getTime();
                               DateFormat df1 = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                               String enterTime = df1.format(myTimer3.getTime());
                               String receipt = "A car entered: " + i + " spot"
                                       + "; Name:" + currentHolder[i]
                                       + "; Enter Time:" + enterTime;
                               System.out.println("Sys Msg - "+receipt);
                           }
                       }                   
                   }
               } else { // 1->1 ; 0->0
                   status[i][1] = 0;
               }
               //rsvp Timer
           }
           for (int i = 0; i < ROWS; i++) {
               if (rsvpBitMap[i][0] == 1) {
                   rsvpBitMap[i][1]++;
                   if (rsvpBitMap[i][1] >= rsvpTimeOut  && status[i][0] == 0) {
                       //RSVP TimeOut => Cancel RSVP
                       System.out.println("Sys Msg - Reservation of spot" + i + " has been cacelled, name: " + currentHolder[i]);
                       leaveSpace(i);
                       rsvpBitMap[i][0] = 0;
                       rsvpBitMap[i][1] = 0;
                   }
               }
           }
           workTime1 = new Date().getTime() - startTime1;
           if (workTime1 < 1000) {
               try {
                   Thread.currentThread().sleep(1000 - workTime1);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
   }
}


