package com.coltla.coltlachat;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final int PACKET_SIZE = 1024;

	private JPanel contentPane;
	
	private String name, address;
	private int port;
	private JTextField chatMessage;
	private JTextArea history;
	
	private DefaultCaret caret;
	
	private DatagramSocket socket;
	private InetAddress ip;
	
	private Thread send;

	public Client(String name, String address, int port) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 

		setTitle("Coltla Chat Client");
		this.name = name;
		this.address = address;
		this.port = port;

		boolean connect = openConnection(address, port);
		if (!connect) {
			String message = "Connection failed";
			System.err.println(message);
			console(message);
			return;
		}
		
		createWindow();
		console("Attempting a connection to " + this.address + ":" + this.port + " for user " + this.name);
	}
	
	private boolean openConnection(String address, int port) {
		try {
			socket = new DatagramSocket(port);
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private String receive() {
		byte[] data = new byte[PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String message = new String(packet.getData());
		return message;
	}
	
	private void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		send.start();
	}
	
	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{50, 790, 30, 10};
		gbl_contentPane.rowHeights = new int[]{75, 450, 40};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		contentPane.add(scroll, scrollConstraints);
		
		chatMessage = new JTextField();
		chatMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					writeMessage();
				}
			}
		});
		chatMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		GridBagConstraints gbc_textMessage = new GridBagConstraints();
		gbc_textMessage.insets = new Insets(0, 0, 0, 5);
		gbc_textMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_textMessage.gridx = 0;
		gbc_textMessage.gridy = 2;
		gbc_textMessage.gridwidth = 2;
		contentPane.add(chatMessage, gbc_textMessage);
		chatMessage.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeMessage();
			}
		});
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 16));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 2;
		contentPane.add(btnNewButton, gbc_btnNewButton);

		setVisible(true);
		chatMessage.requestFocusInWindow();
	}
	
	private void writeMessage() {
		String message = chatMessage.getText();
		if (message.equals("")) return;
		
		message = this.name + " =>\t" + message;
		console(message);
		chatMessage.setText("");
		chatMessage.requestFocusInWindow();
	}
	
	public void console(String message) {
		history.append(message + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
	}
}
