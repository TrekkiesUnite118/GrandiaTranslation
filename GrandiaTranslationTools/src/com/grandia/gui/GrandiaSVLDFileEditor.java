package com.grandia.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.grandia.file.MDTFileParser;
import com.grandia.file.SVLDFileEditor;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Button;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Choice;

public class GrandiaSVLDFileEditor {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GrandiaSVLDFileEditor window = new GrandiaSVLDFileEditor();
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
    public GrandiaSVLDFileEditor() {
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
        
        JLabel lblSvldFileEditor = new JLabel("SVLD File Editor");
        lblSvldFileEditor.setFont(new Font("Arial", Font.BOLD, 20));
        lblSvldFileEditor.setBounds(10, 11, 162, 39);
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
        
        SVLDFileEditor editor = new SVLDFileEditor();
               
        
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
        choice.add("TIME");
        choice.add("LOAD");
        choice.add("SAVE");
        choice.add("SYSTEM RAM");
        choice.add("BACKUP RAM");
        choice.add("BACKUP RAM NOT FOUND");
        choice.add("NOT INITIALIZED");
        choice.add("NO SAVE DATA");
        choice.add("AREA");
        choice.add("SELECT A SAVE FILE");
        choice.add("SELECT A SAVE FILE 2");
        choice.add("CANNOT CANCEL (?)");
        choice.add("INSUFFICIENT MEMORY");
        choice.add("THIS DATA CANNOT BE USED");
        choice.add("WRONG DISC NUMBER");
        choice.add("DATA IS CORRUPT");
        choice.add("LOAD?");
        choice.add("LOAD YES");
        choice.add("LOAD /");
        choice.add("LOAD NO");
        choice.add("OVERWRITE");
        choice.add("OVERWRITE YES");
        choice.add("OVERWRITE /");
        choice.add("OVERWRITE NO");
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
        
        JButton save = new JButton("Save Changes");
        save.setFont(new Font("Arial", Font.BOLD, 16));
        save.setBounds(357, 233, 276, 51);
        frame.getContentPane().add(save);
        
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
