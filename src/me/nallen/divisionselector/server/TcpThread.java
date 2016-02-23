package me.nallen.divisionselector.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpThread extends Thread {
	private static final char FIELD_SEPARATOR = ((char) 28);
	private static final char ITEM_SEPARATOR = ((char) 29);
	
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
    public enum MessageType {
		CLEAR_ALL(0),
		CLEAR_TEAMS(1),
		CLEAR_DIVISIONS(2),
    	
    	ADD_TEAM(3),
		ASSIGN_TEAM(4),
		UNASSIGN_TEAM(5),
		
		ADD_DIVISION(6),
		REMOVE_DIVISION(7);
		
		private final int id;
		MessageType(int id) { this.id = id; }
		public int getValue() { return id; }
		public static MessageType fromInt(int id) {
			MessageType[] values = MessageType.values();
            for(int i=0; i<values.length; i++) {
                if(values[i].getValue() == id)
                    return values[i];
            }
            return null;
		}
	}
    
	public TcpThread(Socket socket) {
		super("Division Selector TCP Thread");
		this.socket = socket;
	}
	
	private boolean sendMessage(String paramString) {
		if (out != null) {
			try {
				out.write(paramString + '\n');
				out.flush();
				return true;
			} catch (Exception e) {}
		}
		return false;
	}
	
	public void run() {
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    
		    // Check that they can use it
		    
		    if(out != null) {
		    	sendMessage("1");
		    	sendInit();
		    	
		    	// Loop for messages from the client
		    	while(true) {
		    		String[] parts;
		    		try {
		    			String str = in.readLine();
		    			
		    			if(str != null) {
		    				parts = str.split("" + ((char)29), -1);
	    					MessageType type = MessageType.fromInt(Integer.parseInt(parts[0]));
	    					
		    				if(parts.length == 2) {
		    					
		    				}
		    			}
		    			else {
		    				break;
		    			}
		    		
						Thread.sleep(10);
		    		} catch (IOException | InterruptedException e) {
						break;
					}
		    	}
		    }
		    else {
		    	sendMessage("0");
		    }
		    
		    out.close();
		    in.close();
		    socket.close();
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void sendInit() {
		String data = "";
		
		boolean remove = false;
		for(String division : DivisionSelectorServer.divisionData.getAllDivisions()) {
			remove = true;
			data = data + division + FIELD_SEPARATOR;
		}
		if(remove)
			data = data.substring(0, data.length()-1);
		
		data = data + ITEM_SEPARATOR;
		
		remove = false;
		for(Team team : DivisionSelectorServer.divisionData.getAllTeams()) {
			remove = true;
			data = data + team.number + FIELD_SEPARATOR;
		}
		if(remove)
			data = data.substring(0, data.length()-1);
		
		data = data + ITEM_SEPARATOR;
		
		remove = false;
		for(String division : DivisionSelectorServer.divisionData.getAllDivisions()) {
			for(String team : DivisionSelectorServer.divisionData.getTeamsForDivision(division)) {
				remove = true;
				data = data + team + FIELD_SEPARATOR + division + FIELD_SEPARATOR;
			}
		}
		if(remove)
			data = data.substring(0, data.length()-1);
		
		sendMessage(data);
	}
}