package com.grandia.gui;

import java.awt.Button;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;

import javax.swing.JTextField;

import com.grandia.file.ScriptParser;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * 
 * This is the GUI for the Script Parser. 
 * 
 * It is mostly generated code from Eclipse's WindowBuilder Plugin.
 * 
 * @author TrekkiesUnite118
 *
 */
public class GrandiaScriptParser {

	private JFrame frmGrandiaScriptParser;
	private TextField inputScriptHeaderField;
	private TextField inputScriptField;
	private TextField outputDirField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrandiaScriptParser window = new GrandiaScriptParser();
					window.frmGrandiaScriptParser.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GrandiaScriptParser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGrandiaScriptParser = new JFrame();
		frmGrandiaScriptParser.setTitle("Grandia Script Parser");
		frmGrandiaScriptParser.getContentPane().setFont(new Font("Arial", Font.PLAIN, 11));
		frmGrandiaScriptParser.setBounds(100, 100, 400, 300);
		frmGrandiaScriptParser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmGrandiaScriptParser.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		frmGrandiaScriptParser.getContentPane().setLayout(null);
		
		Label lblInputScriptHeader = new Label("Input Script Header File");
		lblInputScriptHeader.setFont(new Font("Arial", Font.BOLD, 12));
		lblInputScriptHeader.setBounds(10, 11, 172, 22);
		frmGrandiaScriptParser.getContentPane().add(lblInputScriptHeader);
		
		inputScriptHeaderField = new TextField();
		inputScriptHeaderField.setBounds(10, 35, 335, 20);
		frmGrandiaScriptParser.getContentPane().add(inputScriptHeaderField);
		inputScriptHeaderField.setColumns(10);
		
		Button inputScriptHeaderButton = new Button("...");
		inputScriptHeaderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       inputScriptHeaderField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		inputScriptHeaderButton.setBounds(345, 32, 32, 23);
		frmGrandiaScriptParser.getContentPane().add(inputScriptHeaderButton);
		
		Label lblInputScriptFile = new Label("Input Script File");
		lblInputScriptFile.setFont(new Font("Arial", Font.BOLD, 12));
		lblInputScriptFile.setBounds(10, 66, 172, 22);
		frmGrandiaScriptParser.getContentPane().add(lblInputScriptFile);
		
		inputScriptField = new TextField();
		inputScriptField.setColumns(10);
		inputScriptField.setBounds(10, 91, 335, 20);
		frmGrandiaScriptParser.getContentPane().add(inputScriptField);
		
		Button inputScriptButton = new Button("...");
		inputScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       inputScriptField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		inputScriptButton.setBounds(345, 88, 32, 23);
		frmGrandiaScriptParser.getContentPane().add(inputScriptButton);
		
		Label lblOutputFileDirectory = new Label("Output File Directory");
		lblOutputFileDirectory.setFont(new Font("Arial", Font.BOLD, 12));
		lblOutputFileDirectory.setBounds(10, 117, 172, 22);
		frmGrandiaScriptParser.getContentPane().add(lblOutputFileDirectory);
		
		outputDirField = new TextField();
		outputDirField.setColumns(10);
		outputDirField.setBounds(10, 143, 335, 20);
		frmGrandiaScriptParser.getContentPane().add(outputDirField);
		
		Button outputDirButton = new Button("...");
		outputDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       outputDirField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		outputDirButton.setBounds(345, 140, 32, 23);
		frmGrandiaScriptParser.getContentPane().add(outputDirButton);
		
		Button parseButton = new Button("Parse");
		parseButton.setFont(new Font("Arial", Font.BOLD, 28));
		parseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScriptParser parser = new ScriptParser()
						.inputScriptFilePath(inputScriptField.getText())
						.inputScriptHeaderFilePath(inputScriptHeaderField.getText())
						.outputFilePath(outputDirField.getText());
				
				parseButton.setEnabled(false);
				parser.parse();
				parseButton.setEnabled(true);
				
			}
		});
		parseButton.setBounds(20, 174, 335, 56);
		frmGrandiaScriptParser.getContentPane().add(parseButton);
	}
}
