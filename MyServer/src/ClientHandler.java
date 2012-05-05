import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Implementation of a multiuser socket,
 * each ClientHandler creates to run for a child socket 
 */
public class ClientHandler extends Thread {
   
	protected Socket incomming;
   
	public ClientHandler(Socket incomming) {
		this.incomming = incomming;
	}

	@Override
	/**
	 * Read User Msg from socket, handle it and send the response back if necessary
	 * Also, print Sys Msg on the server side 
	 */
	public void run() {
		System.out.println("Sys Msg - One User Connection Accept Succeed.");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomming.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(incomming.getOutputStream()));

			while (true) {
				String[] command = in.readLine().split("%");
				if (command[0].equals("0") && command.length == 3) {
					boolean msg = MyServer.addUsers(command[1], command[2]);
					if (msg == false) {
						System.out.println("Sys Msg - Registeration Fail: "+ "User " +command[1]+ " already exist");
						out.println("User already exist!");
						out.flush();
					} else {
						System.out.println("Sys Msg - Registeration Succeed: "+ "User " +command[1]+ " registered with PWD "+command[2]);
						out.println("Registeration complete!");
						out.flush();
					}
				}else if (command[0].equals("1") && command.length == 3) {
					boolean msg = MyServer.checkUsers(command[1], command[2]);
					if (msg == false) {
						System.out.println("Sys Msg - Login Fail: User " +command[1]+ " with PWD "+command[2]);
						out.println("Login Fail");
						out.flush();
					} else {
						System.out.println("Sys Msg - Login Success: User " +command[1]+ " with PWD "+command[2]);
						out.println("Login Success!");
						out.flush();
					}
				}else if(command[0].equals("2") && command.length == 2 ){
					System.out.println("Sys Msg - RSVP: Receive RSVP From User "+ command[1]);
					int spaceNumber = MyServer.rsvp();
					if (spaceNumber == -1){
						System.out.println("Sys Alert - Parking Structure is Full");
						out.println("Parking Lot is full!");
						out.flush();
					}
					else{
						MyServer.enterIntoSpace(spaceNumber, command[1]);
						System.out.println("Sys Msg - Assign User "+ command[1] +" with space "+spaceNumber);
						out.println(command[1]+"%"+spaceNumber);
						out.flush();
					}
				}else if(command[0].equals("3") && command.length == 2 ){		   
					String receipt = MyServer.getReceipt(command[1]);
					System.out.println("Sys Msg - Send Receipt to User "+ command[1]);
					out.println(receipt);
					out.flush();
				}else{
					out.println("ERROR: Input ERROR");
					out.flush();
				}
			}
		} 
		catch (Exception e) {
		}
	}
}

