package com.grandia.gui;

import java.awt.Button;
import java.awt.Choice;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.grandia.file.ITGETFileEditor;

public class GrandiaITGETFileEditor {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GrandiaITGETFileEditor window = new GrandiaITGETFileEditor();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GrandiaITGETFileEditor() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 667, 377);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmExit = new JMenuItem("Quit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFile.add(mntmExit);
        
        frame.getContentPane().setLayout(null);
        
        JLabel lblSvldFileEditor = new JLabel("ITGET File Editor");
        lblSvldFileEditor.setFont(new Font("Arial", Font.BOLD, 20));
        lblSvldFileEditor.setBounds(10, 11, 235, 39);
        frame.getContentPane().add(lblSvldFileEditor);
        
        Label label = new Label("Input File Directory");
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setBounds(20, 56, 105, 22);
        frame.getContentPane().add(label);
        
        TextField parseInputFileDirField = new TextField();
        parseInputFileDirField.setBounds(131, 56, 299, 22);
        frame.getContentPane().add(parseInputFileDirField);
        
        Button parseInputDirSearchButton = new Button("...");
        parseInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        
        parseInputDirSearchButton.setBounds(436, 56, 22, 22);
        frame.getContentPane().add(parseInputDirSearchButton);
        
        TextField parseOutputFileDirField = new TextField();
        parseOutputFileDirField.setBounds(131, 84, 299, 22);
        frame.getContentPane().add(parseOutputFileDirField);
        
        Button parseOutputDirSearchButton = new Button("...");
        
        parseOutputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseOutputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parseOutputDirSearchButton.setBounds(436, 84, 22, 22);
        frame.getContentPane().add(parseOutputDirSearchButton);
        
        Label label_1 = new Label("Output File Directory");
        label_1.setFont(new Font("Arial", Font.PLAIN, 11));
        label_1.setBounds(20, 84, 105, 22);
        frame.getContentPane().add(label_1);
        
        JButton btnInitialize = new JButton("Initialize");
        btnInitialize.setFont(new Font("Arial", Font.BOLD, 16));
        btnInitialize.setBounds(464, 55, 169, 51);
        frame.getContentPane().add(btnInitialize);
        
        ITGETFileEditor editor = new ITGETFileEditor();
               
        
        btnInitialize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to parse files...");
                btnInitialize.setEnabled(false);
                editor.inputFilePath(parseInputFileDirField.getText())
                .outputFilePath(parseOutputFileDirField.getText() + "\\");
                editor.init();
                btnInitialize.setEnabled(true);
            }
        });
        
        Choice choice = new Choice();
        choice.setFont(null);
        choice.setBounds(131, 155, 299, 20);
        choice.add("8x16 AUTO");
        choice.add("8x16 MANUAL");
        choice.add("8x16 USE");
        choice.add("8x16 DISCARD ITEM");
        choice.add("8x16 DISCARD INVN");
        choice.add("8x16 FOUND ITEMS");
        choice.add("8x16 ACQUIRED");
        choice.add("8x16 GIVE ITEM TO");
        choice.add("NAMES");
        
        frame.getContentPane().add(choice);
        
        JLabel lblValueToChange = new JLabel("Value to Change");
        lblValueToChange.setBounds(20, 155, 105, 20);
        frame.getContentPane().add(lblValueToChange);
        
        JLabel lblNewValuein = new JLabel("New Value (in Hex)");
        lblNewValuein.setBounds(10, 186, 115, 14);
        frame.getContentPane().add(lblNewValuein);
        
        TextField newValueField = new TextField();
        newValueField.setBounds(131, 181, 299, 22);
        frame.getContentPane().add(newValueField);
        
        JButton btnReplace = new JButton("Replace");
        btnReplace.setFont(new Font("Arial", Font.BOLD, 16));
        btnReplace.setBounds(464, 154, 169, 51);
        frame.getContentPane().add(btnReplace);
        
        btnReplace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to replace values and update offsets...");
                btnReplace.setEnabled(false);
                editor.replaceFieldValue(choice.getSelectedItem(), newValueField.getText());
                btnReplace.setEnabled(true);
            }
        });
        
        JButton save = new JButton("Save Changes");
        save.setFont(new Font("Arial", Font.BOLD, 16));
        save.setBounds(357, 233, 276, 51);
        frame.getContentPane().add(save);
        
        JButton btnAutoReplace = new JButton("Auto Replace");
        btnAutoReplace.setFont(new Font("Arial", Font.BOLD, 16));
        btnAutoReplace.setBounds(10, 233, 276, 51);
        frame.getContentPane().add(btnAutoReplace);
        
        btnAutoReplace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to auto replace values...");
                btnAutoReplace.setEnabled(false);
                editor.autoReplace();
                btnAutoReplace.setEnabled(true);
            }
        });
        
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to save file...");
                save.setEnabled(false);
                editor.writeToFile();
                save.setEnabled(true);
            }
        });
    }

}
