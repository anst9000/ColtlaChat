package com.coltla.coltlachat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();

	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	private final int MAX_ATTEMPTS = 5;

	private boolean raw = false;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		Scanner scanner = new Scanner(System.in);
		String argument = "";

		while (running) {
			String command = scanner.nextLine();

			if (!command.startsWith("/")) {
				sendToAll("/m/Server: " + command + "/e/");
				continue;
			}
			
			command = command.substring(1);

			if (command.startsWith("kick")) {
				argument = command.split(" ")[1];	
				command = command.split(" ")[0];
			}
			
			switch (command) {
			case "raw":
				switchRawMode();
				break;
			case "clients":
				printAllClients();
				break;
			case "kick":
				kickClient(argument);
				break;
			case "help":
				printHelp();
				break;
			case "quit":
				quit();
				break;
			default:
				System.out.println("Unknown command.");
				printHelp();
				break;
			}
		}

		scanner.close();
	}

	private void kickClient(String user) {
		System.out.println("in kickClient");
//		String name = text.split(" ")[1];
		int id = -1;
		boolean number = true;

		try {
			id = Integer.parseInt(user);
		} catch (NumberFormatException e) {
			number = false;
		}

		if (number) {
			boolean exists = false;

			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getID() == id) {
					exists = true;
					break;
				}
			}

			if (exists) disconnect(id, true);
			else System.out.println("Client " + id + " doesn't exist! Check ID number.");
		} else {
			for (int i = 0; i < clients.size(); i++) {
				ServerClient c = clients.get(i);

				if (user.equals(c.name)) {
					disconnect(c.getID(), true);
					break;
				}
			}
		}				
	}

	private void switchRawMode() {
		if (raw) System.out.println("Raw mode off.");
		else System.out.println("Raw mode on.");

		raw = !raw;				
	}

	private void printAllClients() {
		System.out.println("Clients:");
		System.out.println("========");

		for (int i = 0; i < clients.size(); i++) {
			ServerClient c = clients.get(i);
			System.out.println(c.name + "(" + c.getID() + "): " + c.address.toString() + ":" + c.port);
		}

		System.out.println("========");				
	}

	private void printHelp() {
		System.out.println(helpText());
	}
	
	private String helpText() {
		String newLine = System.getProperty("line.separator");

		return new StringBuilder()
            .append("Here is a list of all available commands:").append(newLine)
            .append("=========================================").append(newLine)
            .append("/raw              - enables raw mode.").append(newLine)
            .append("/clients          - shows all connected clients.").append(newLine)
            .append("/kick [user ID]   - kicks a user.").append(newLine)
            .append("/kick [username]  - kicks a user.").append(newLine)
			.append("/help             - shows this help message.").append(newLine)
			.append("/quit             - shuts down the server.").append(newLine)
            .toString();
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(Integer.valueOf(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}

	private void sendStatus() {
		if (clients.size() <= 0) return;
		String users = "/u/";
		for (int i = 0; i < clients.size() - 1; i++) {
			users += clients.get(i).name + "/n/";
		}
		users += clients.get(clients.size() - 1).name + "/e/";
		sendToAll(users);
	}

	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (SocketException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}

	private void sendToAll(String message) {
		if (message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(message);
		}

		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}

	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	private void send(String message, InetAddress address, int port) {
		message += "/e/";
		send(message.getBytes(), address, port);
	}

	private void process(DatagramPacket packet) {
		String info = new String(packet.getData());
		String infoPrefix = info.substring(0, 3);

		if (raw) System.out.println(info);
		
		switch (infoPrefix) {
		case "/c/":
			sendConnectedInfo(info, packet);
			break;
		case "/m/":
			sendToAll(info);
			break;
		case "/d/":
			String idString = info.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(idString), true);
			break;
		case "/i/":
			clientResponse.add(Integer.parseInt(info.split("/i/|/e/")[1]));
			break;
		default:
			System.out.println(info);
			break;
		}
	}

	private void sendConnectedInfo(String info, DatagramPacket packet) {
		// UUID id = UUID.randomUUID();
		int id = UniqueIdentifier.getIdentifier();
		String name = info.split("/c/|/e/")[1];
		System.out.println(name + "(" + id + ") connected!");
		clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
		String ID = "/c/" + id;
		send(ID, packet.getAddress(), packet.getPort());
	}

	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}

		running = false;
		socket.close();
	}

	private void disconnect(int id, boolean status) {
		ServerClient c = null;
		boolean existed = false;

		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				existed = true;
				break;
			}
		}

		if (!existed) return;

		if (status) System.out.println(c + " disconnected.");
		else System.out.println(c + " timed out.");
	}

}