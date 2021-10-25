package com.coltla.coltlachat.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {
	
	private DatagramSocket socket;
	private int port;
	
	private boolean running = false;
	private Thread runServer, manage, send, receive;	
	
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		runServer = new Thread(this, "Server");
		runServer.start();
	}

	@Override
	public void run() {
		running = true;
		manageClients();
		receive();
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					// Managing
					
				}
			}
		};
		
		manage.start();
	}

	private void receive() {
		receive = new Thread("Recevive") {
			public void run() {
				while (running) {
					// Receiving
					
				}
			}
		};
		
		receive.start();
	}	
}
