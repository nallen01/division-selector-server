package me.nallen.divisionselector.server;

public class DivisionSelectorServer {
	public static DivisionData divisionData;
	DivisionGui gui;
	SelectorGui selectorGui;
	//TcpServer tcpServer;
	
	public static void main(String[] args) {
		new DivisionSelectorServer();
	}
	
	public DivisionSelectorServer() {
		init();
	}
	
	public void init() {
		divisionData = new DivisionData();
		divisionData.clear();
		
		// Start the TCP Server
		/*tcpServer = new TcpServer();
		tcpServer.run();*/
		
		// Start the GUI
		gui = new DivisionGui();
		
		// Start the Selector GUI
		selectorGui = new SelectorGui();
	}
}
