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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.grandia.file.SHOPFileEditor;

public class GrandiaSHOPFileEditor {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GrandiaSHOPFileEditor window = new GrandiaSHOPFileEditor();
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
    public GrandiaSHOPFileEditor() {
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
        
        JLabel lblSvldFileEditor = new JLabel("SHOP File Editor");
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
        

        JCheckBox digitalMuseumChkBox = new JCheckBox("Digital Museum");
        digitalMuseumChkBox.setBounds(20, 112, 152, 23);
        frame.getContentPane().add(digitalMuseumChkBox);
        
        SHOPFileEditor editor = new SHOPFileEditor();
               
        
        btnInitialize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Attempting to parse files...");
                btnInitialize.setEnabled(false);
                editor.inputFilePath(parseInputFileDirField.getText())
                .outputFilePath(parseOutputFileDirField.getText() + "\\")
                .isDigitalMuseum(digitalMuseumChkBox.isSelected());
                editor.init();
                btnInitialize.setEnabled(true);
            }
        });
        
        Choice choice = new Choice();
        choice.setFont(null);
        choice.setBounds(131, 155, 299, 20);
        choice.add("8x16 BUY");
        choice.add("8x16 BUY CANCEL");
        choice.add("8x16 SELL");
        choice.add("8x16 SELL CANCEL");
        choice.add("8x16 STASH");
        choice.add("8x16 STASH CANCEL");
        choice.add("8x16 GET");
        choice.add("8x16 GET CANCEL");
        choice.add("8x16 EQUIP");
        choice.add("8x16 TRADE");
        choice.add("8x16 BUY2");
        choice.add("8x16 WILL TRADE");
        choice.add("8x16 SPACE");
        choice.add("8x16 CANNOT_TRADE");
        choice.add("8x16 WELCOME");
        choice.add("8x16 PICK AN ITEM TO BUY");
        choice.add("8x16 WHO WILL BUY");
        choice.add("8x16 PICK AN ITEM TO SELL");
        choice.add("8x16 BUY WHICH ATTRIBUTE");
        choice.add("8x16 PICK AN ITEM TO STASH");
        choice.add("8x16 PICK AN ITEM TO GET");
        choice.add("8x16 WHO IS BUYING");
        choice.add("8x16 YOU HAVE NO MANA EGGS");
        choice.add("8x16 YOU HAVE NO ITEMS TO GET");
        choice.add("8x16 WAS LEARNED");
        choice.add("MANA EGGS");
        choice.add("EGGS");
        choice.add("ATK");
        choice.add("DEF");
        choice.add("ACT");
        choice.add("MOV");
        choice.add("STR");
        choice.add("VIT");
        choice.add("WIT");
        choice.add("AGI");
        choice.add("WEAPON");
        choice.add("SHIELD");
        choice.add("ARMOR");
        choice.add("HELMET");
        choice.add("SHOES");
        choice.add("JEWELRY");
        choice.add("GOLD PCS");
        choice.add("STR2");
        choice.add("WIT2");
        choice.add("AGI2");
        choice.add("MAGIC");
        choice.add("LV UP PARAM");
        choice.add("MIX ATTRIBUTE");
        choice.add("THUNDER");
        choice.add("SNOW");
        choice.add("FOREST");
        choice.add("EXPLOSION");
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
