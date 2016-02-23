package me.nallen.divisionselector.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TcpThread extends Thread implements DataListener {
	private static final char FIELD_SEPARATOR = ((char) 28);
	private static final char ITEM_SEPARATOR = ((char) 29);
	
    private Socket socket = null;
    private BufferedReader in = null;
    private BufferedWriter out = null;
    
	public TcpThread(Socket socket) {
		super("Division Selector TCP Thread");
		this.socket = socket;
	    
	    DivisionSelectorServer.divisionData.addListener(this);
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
	
	private boolean sendCommand(MessageType type) {
        return sendCommand(type, null);
    }

    private boolean sendCommand(MessageType type, String[] fields) {
        String str = "" + type.getValue() + FIELD_SEPARATOR;
        if(fields != null) {
            for(String field : fields) {
                str += field + FIELD_SEPARATOR;
            }
        }
        str = str.substring(0, str.length()-1);
        return sendMessage(str);
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
		    				parts = str.split("" + FIELD_SEPARATOR, -1);
	    					MessageType type = MessageType.fromInt(Integer.parseInt(parts[0]));
	    					
		    				if(type == MessageType.ASSIGN_TEAM) {
		    					if(parts.length == 3) {
		    						DivisionSelectorServer.divisionData.assignDivisionForTeam(parts[1], parts[2]);
		    					}
		    				}
		    				else if(type == MessageType.UNASSIGN_TEAM) {
		    					if(parts.length == 2) {
		    						DivisionSelectorServer.divisionData.removeDivisionForTeam(parts[1]);
		    					}
		    				}
		    				else if(type == MessageType.RANDOMISE_TEAMS) {
	    						DivisionSelectorServer.divisionData.randomiseRemainingTeams();
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

	@Override
	public void update(MessageType type, String[] params) {
		sendCommand(type, params);
	}
}