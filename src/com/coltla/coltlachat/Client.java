package com.coltla.coltlachat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private String name, address;
	private int port;
	

	public Client(String name, String address, int port) {
		setTitle("Coltla Chat Client");
		this.name = name;
		this.address = address;
		this.port = port;
		
		createWindow();
	}
	
	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{35, 835, 10};
		gbl_contentPane.rowHeights = new int[]{75, 450, 40};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JTextArea textAreaHistory = new JTextArea();
		GridBagConstraints gbc_textAreaHistory = new GridBagConstraints();
		gbc_textAreaHistory.fill = GridBagConstraints.BOTH;
		gbc_textAreaHistory.gridx = 1;
		gbc_textAreaHistory.gridy = 1;
		contentPane.add(textAreaHistory, gbc_textAreaHistory);

		setVisible(true);
	}
}
