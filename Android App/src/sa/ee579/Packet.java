package sa.ee579;

import java.io.Serializable;
import java.util.ArrayList;

public class Packet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> packet;
	
	public Packet(){
		packet = new ArrayList<String>();
	}
	public void add(String s){
		packet.add(s);
	}
	public String get(int i){
		return packet.get(i);
	}
	public int size(){
		return packet.size();
	}
}
