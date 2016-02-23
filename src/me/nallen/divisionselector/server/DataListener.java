package me.nallen.divisionselector.server;

public interface DataListener {
	public void update(MessageType type, String[] params);
}