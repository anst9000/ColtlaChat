package com.coltla.coltlachat;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textName;
	private JTextField textIPAddress;
	private JTextField textPort;
	private JButton btnLogin;

	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textName = new JTextField();
		textName.setFont(new Font("Arial", Font.PLAIN, 14));
		textName.setBounds(60, 41, 165, 30);
		contentPane.add(textName);
		textName.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Arial", Font.PLAIN, 16));
		lblName.setBounds(116, 20, 53, 17);
		contentPane.add(lblName);
		
		JLabel lblIPAddress = new JLabel("IP Address");
		lblIPAddress.setFont(new Font("Arial", Font.PLAIN, 16));
		lblIPAddress.setBounds(99, 88, 88, 17);
		contentPane.add(lblIPAddress);
		
		textIPAddress = new JTextField();
		textIPAddress.setFont(new Font("Arial", Font.PLAIN, 14));
		textIPAddress.setColumns(10);
		textIPAddress.setBounds(60, 110, 165, 30);
		contentPane.add(textIPAddress);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setFont(new Font("Arial", Font.PLAIN, 16));
		lblPort.setBounds(123, 170, 39, 17);
		contentPane.add(lblPort);
		
		textPort = new JTextField();
		textPort.setFont(new Font("Arial", Font.PLAIN, 14));
		textPort.setColumns(10);
		textPort.setBounds(60, 191, 165, 30);
		contentPane.add(textPort);
		
		JLabel lblExampleIPAddress = new JLabel("eg. 192.168.0.2");
		lblExampleIPAddress.setFont(new Font("Arial", Font.PLAIN, 14));
		lblExampleIPAddress.setBounds(87, 141, 112, 17);
		contentPane.add(lblExampleIPAddress);
		
		JLabel lblExamplePort = new JLabel("eg. 8192");
		lblExamplePort.setFont(new Font("Arial", Font.PLAIN, 14));
		lblExamplePort.setBounds(109, 221, 68, 17);
		contentPane.add(lblExamplePort);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textName.getText();
				String address = textIPAddress.getText();
				int port = Integer.parseInt(textPort.getText());
				
				login(name, address, port);
			}
		});
		btnLogin.setFont(new Font("Arial", Font.PLAIN, 16));
		btnLogin.setBounds(76, 272, 133, 30);
		contentPane.add(btnLogin);
	}
	
	private void login(String name, String address, int port) {
		dispose();
		new Client(name, address, port);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
