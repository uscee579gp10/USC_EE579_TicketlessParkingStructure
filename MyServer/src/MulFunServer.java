import java.io.IOException;

public class MulFunServer {
	/**
	 * Two Functionalities:
	 * 1. Process the info got from Tmotes
	 * 2. Create a server socket to get connection from users
	 */
	public static void main(String[] args) {
		try {
			MyServer me = new MyServer();
			me.start();
			while(true){
				//start server socket
				me.getConnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
