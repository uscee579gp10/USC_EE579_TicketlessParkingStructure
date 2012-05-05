package sa.ee579;
/**
 * The main app, implements SensorEventListener.
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConsiditonTester extends Activity{

	private static final String IP  = "";
	private static final int PORT = 9997;
	
	
	private EditText nameInput;
	private EditText passwordInput;
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private TextView welcome;
	private TextView console;
	
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	private static String command;
	private static String name;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		nameInput = (EditText) findViewById(R.id.nameInput);
		passwordInput = (EditText) findViewById(R.id.passwordInput);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		welcome = (TextView) findViewById(R.id.welcome);
		console = (TextView) findViewById(R.id.console);

		
		button3.setVisibility(View.INVISIBLE);
		button4.setVisibility(View.INVISIBLE);
		console.setVisibility(View.INVISIBLE);


		try {
			socket = new Socket(IP, PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
		} catch (Exception e) {
			Log.e("SocketConn", "Erroe", e);
		} 
		
		

		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			    String inputName = nameInput.getText().toString();
				String password = passwordInput.getText().toString();
				out.println("0%"+inputName+"%"+password);
				String response = "";
				try {
					response = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
					welcome.setText(response);
			}
		});
		
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			    String inputName = nameInput.getText().toString();
				String password = passwordInput.getText().toString();
				out.println("1%"+inputName+"%"+password);
				String response = "";
				try {
					response = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(response.equals("Wrong password!")){
					welcome.setText(response);
				}
				else{
					welcome.setText("Welcome, "+inputName);
					name = inputName;
					
					nameInput.setVisibility(View.INVISIBLE);
					passwordInput.setVisibility(View.INVISIBLE);
					button1.setVisibility(View.INVISIBLE);
					button2.setVisibility(View.INVISIBLE);
					button3.setVisibility(View.VISIBLE);
					console.setVisibility(View.VISIBLE);
					
					
					
				}
			}
		});
		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				out.println("2%"+name);
				String response = "";
				try {
					response = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				console.setText(response);
				if(!response.equals("Parking Lot is full!")){
					button4.setVisibility(View.VISIBLE);
				}
			}
		});
		
		button4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				out.println("3%"+name);
				String response = "";
				try {
					response = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				console.setText(response);
				if(!response.equals("Parking Lot is full!")){
					button4.setVisibility(View.VISIBLE);
				}
			}
		});
	}

}