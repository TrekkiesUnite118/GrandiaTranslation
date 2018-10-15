package com.grandia.gui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.grandia.file.ScriptReconstructor;

import java.awt.Label;
import java.awt.Font;
import java.awt.TextField;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * 
 * This is the GUI for the Script Reconstructor. 
 * 
 * It is mostly generated code from Eclipse's WindowBuilder Plugin.
 * 
 * @author TrekkiesUnite118
 *
 */
public class GrandiaScriptReconstructor {

	private JFrame frmGrandiaScriptReconstructor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrandiaScriptReconstructor window = new GrandiaScriptReconstructor();
					window.frmGrandiaScriptReconstructor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GrandiaScriptReconstructor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmGrandiaScriptReconstructor = new JFrame();
		frmGrandiaScriptReconstructor.setTitle("Grandia Script Reconstructor");
		frmGrandiaScriptReconstructor.setBounds(100, 100, 450, 335);
		frmGrandiaScriptReconstructor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmGrandiaScriptReconstructor.setJMenuBar(menuBar);
		
		JMenu mnFileMenu = new JMenu("File");
		menuBar.add(mnFileMenu);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFileMenu.add(mntmExit);
		frmGrandiaScriptReconstructor.getContentPane().setLayout(null);
		
		Label inputScriptHeaderFileLabel = new Label("Input Script Header File");
		inputScriptHeaderFileLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		inputScriptHeaderFileLabel.setBounds(10, 10, 142, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptHeaderFileLabel);
		
		TextField inputScriptHeaderFileField = new TextField();
		inputScriptHeaderFileField.setBounds(10, 32, 355, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptHeaderFileField);
		
		Button inputScriptHeaderFileButton = new Button("...");
		inputScriptHeaderFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       inputScriptHeaderFileField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		inputScriptHeaderFileButton.setBounds(371, 32, 33, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptHeaderFileButton);
		
		Label inputScriptPiecesLabel = new Label("Input Script Pieces Directory");
		inputScriptPiecesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		inputScriptPiecesLabel.setBounds(10, 60, 248, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptPiecesLabel);
		
		TextField inputScriptPiecesDirField = new TextField();
		inputScriptPiecesDirField.setBounds(10, 88, 355, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptPiecesDirField);
		
		Button inputScriptPiecesDirButton = new Button("...");
		inputScriptPiecesDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       inputScriptPiecesDirField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		inputScriptPiecesDirButton.setBounds(371, 88, 33, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(inputScriptPiecesDirButton);
		
		TextField outputFileDirField = new TextField();
		outputFileDirField.setBounds(10, 144, 355, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(outputFileDirField);
		
		Label outputFileDirLabel = new Label("Output File Directory");
		outputFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		outputFileDirLabel.setBounds(10, 116, 248, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(outputFileDirLabel);
		
		Button outputFileDirButton = new Button("...");
		outputFileDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       outputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
			    }
			}
		});
		outputFileDirButton.setBounds(371, 144, 33, 22);
		frmGrandiaScriptReconstructor.getContentPane().add(outputFileDirButton);
		
		Button reconstructButton = new Button("Reconstruct");
		reconstructButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScriptReconstructor reconstructor = new ScriptReconstructor()
						.inputScriptFilePath(inputScriptPiecesDirField.getText())
						.inputScriptHeaderFilePath(inputScriptHeaderFileField.getText())
						.outputFilePath(outputFileDirField.getText() + "\\");
				
				reconstructButton.setEnabled(false);
				reconstructor.reconstruct();
				reconstructButton.setEnabled(true);
			}
		});
		reconstructButton.setFont(new Font("Arial", Font.PLAIN, 28));
		reconstructButton.setBounds(10, 196, 394, 59);
		frmGrandiaScriptReconstructor.getContentPane().add(reconstructButton);
	}
}
