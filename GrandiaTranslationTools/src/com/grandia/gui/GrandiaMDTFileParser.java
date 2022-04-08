package com.grandia.gui;

import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import java.awt.TextField;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Label;
import java.awt.Font;
import javax.swing.JRadioButton;

import com.grandia.file.MDTFileParser;
import com.grandia.file.MDTFileReconstructor;
import com.grandia.file.ScriptByteSwapper;

/**
 * 
 * This is the GUI for the MDT File Parser. 
 * 
 * It is mostly generated code from Eclipse's WindowBuilder Plugin.
 * 
 * @author TrekkiesUnite118
 *
 */
public class GrandiaMDTFileParser {

    private JFrame frmGrandiaTranslationTools;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GrandiaMDTFileParser window = new GrandiaMDTFileParser();
                    window.frmGrandiaTranslationTools.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GrandiaMDTFileParser() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmGrandiaTranslationTools = new JFrame();
        frmGrandiaTranslationTools.setTitle("Grandia Translation Tools");
        frmGrandiaTranslationTools.setBounds(100, 100, 700, 377);
        frmGrandiaTranslationTools.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        frmGrandiaTranslationTools.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFile.add(mntmQuit);
        frmGrandiaTranslationTools.getContentPane().setLayout(null);
        
        JRadioButton rdbtnSegaSaturn = new JRadioButton("Sega Saturn");
        rdbtnSegaSaturn.setFont(new Font("Arial", Font.PLAIN, 11));
        rdbtnSegaSaturn.setSelected(true);
        rdbtnSegaSaturn.setBounds(466, 42, 109, 23);
        frmGrandiaTranslationTools.getContentPane().add(rdbtnSegaSaturn);
        
        JRadioButton rdbtnSonyPlaystation = new JRadioButton("Sony Playstation");
        rdbtnSonyPlaystation.setFont(new Font("Arial", Font.PLAIN, 11));
        rdbtnSonyPlaystation.setBounds(466, 68, 117, 23);
        frmGrandiaTranslationTools.getContentPane().add(rdbtnSonyPlaystation);
        
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(rdbtnSegaSaturn);
        btnGroup.add(rdbtnSonyPlaystation);
        
        TextField parseInputFileDirField = new TextField();
        parseInputFileDirField.setBounds(121, 41, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(parseInputFileDirField);
        
        Button parseInputDirSearchButton = new Button("...");
        parseInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   parseInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        parseInputDirSearchButton.setBounds(426, 41, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(parseInputDirSearchButton);
        
        Label inputParseDirLabel = new Label("Input File Directory");
        inputParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputParseDirLabel.setBounds(10, 41, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(inputParseDirLabel);
        
        Label parserTitle = new Label("MDT/MDP File Parser");
        parserTitle.setFont(new Font("Arial", Font.BOLD, 14));
        parserTitle.setBounds(10, 13, 227, 22);
        frmGrandiaTranslationTools.getContentPane().add(parserTitle);
        
        Label outputParseDirLabel = new Label("Output File Directory");
        outputParseDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        outputParseDirLabel.setBounds(10, 69, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(outputParseDirLabel);
        
        TextField parseOutputFileDirField = new TextField();
        parseOutputFileDirField.setBounds(121, 69, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(parseOutputFileDirField);
        
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
        parseOutputDirSearchButton.setBounds(426, 69, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(parseOutputDirSearchButton);
        
        Button parseButton = new Button("Parse");
        parseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                String fileExt = "";
                boolean bigEndian = false;
                
                if(rdbtnSegaSaturn.isSelected()) {
                    fileExt = ".MDT";
                    bigEndian = true;
                }else if(rdbtnSonyPlaystation.isSelected()) {
                    fileExt = ".MDP";
                    bigEndian = false;
                }
                
                MDTFileParser parser = new MDTFileParser()
                    .inputFilePath(parseInputFileDirField.getText())
                    .outputFilePath(parseOutputFileDirField.getText() + "\\")
                    .fileExtension(fileExt)
                    .bigEndian(bigEndian);
                System.out.println("Attempting to parse files...");
                parseButton.setEnabled(false);
                parser.parse();
                parseButton.setEnabled(true);
            }
        });
        parseButton.setFont(new Font("Arial", Font.PLAIN, 20));
        parseButton.setBounds(589, 32, 85, 59);
        frmGrandiaTranslationTools.getContentPane().add(parseButton);
        
        
        Label sysSelectionlabel = new Label("Input System");
        sysSelectionlabel.setFont(new Font("Arial", Font.PLAIN, 12));
        sysSelectionlabel.setBounds(466, 13, 85, 22);
        frmGrandiaTranslationTools.getContentPane().add(sysSelectionlabel);
        
        Label convertLabel = new Label("Convert Playstation Scripts to Saturn");
        convertLabel.setFont(new Font("Arial", Font.BOLD, 14));
        convertLabel.setBounds(10, 110, 335, 22);
        frmGrandiaTranslationTools.getContentPane().add(convertLabel);
        
        Label inputConvertFileDirLabel = new Label("Input File Directory");
        inputConvertFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        inputConvertFileDirLabel.setBounds(10, 132, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(inputConvertFileDirLabel);
        
        TextField convertInputFileDirField = new TextField();
        convertInputFileDirField.setBounds(121, 132, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(convertInputFileDirField);
        
        Button convertInputDirSearchButton = new Button("...");
        convertInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   convertInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        convertInputDirSearchButton.setBounds(426, 132, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(convertInputDirSearchButton);
        
        Label outputConvertFileDirLabel = new Label("Output File Directory");
        outputConvertFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        outputConvertFileDirLabel.setBounds(10, 160, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(outputConvertFileDirLabel);
        
        TextField convertOutputFileDirField = new TextField();
        convertOutputFileDirField.setBounds(121, 160, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(convertOutputFileDirField);
        
        Button convertOutputDirSearchButton = new Button("...");
        convertOutputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   convertOutputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        convertOutputDirSearchButton.setBounds(426, 160, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(convertOutputDirSearchButton);
        
        Button convertButton = new Button("Convert");
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                ScriptByteSwapper scriptByteSwapper = new ScriptByteSwapper()
                        .inputFilePath(convertInputFileDirField.getText())
                        .outputFilePath(convertOutputFileDirField.getText() + "\\")
                        .fileExtension(".SCN");
                
                ScriptByteSwapper scriptHeaderByteSwapper = new ScriptByteSwapper()
                        .inputFilePath(convertInputFileDirField.getText())
                        .outputFilePath(convertOutputFileDirField.getText() + "\\")
                        .fileExtension(".OFS");
                
                ScriptByteSwapper scriptTextByteSwapper = new ScriptByteSwapper()
                        .inputFilePath(convertOutputFileDirField.getText())
                        .outputFilePath(convertOutputFileDirField.getText() + "\\")
                        .fileExtension(".SCN");
                
                convertButton.setEnabled(false);
                System.out.println("Attempting to byte swap script files...");
                scriptByteSwapper.swapBytes();
                
                System.out.println("Attempting to byte swap script header files...");
                scriptHeaderByteSwapper.swapBytes();
                
                System.out.println("Attempting to swap bytes for text portions...");
                scriptTextByteSwapper.swapTextBytes();
                
                convertButton.setEnabled(true);
            }
        });
        convertButton.setFont(new Font("Arial", Font.PLAIN, 20));
        convertButton.setBounds(466, 123, 208, 59);
        frmGrandiaTranslationTools.getContentPane().add(convertButton);
        
        Label reconstructLabel = new Label("Reconstruct Saturn MDT Files");
        reconstructLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reconstructLabel.setBounds(10, 202, 227, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructLabel);
        
        Label reconstructInputFileDirLabel = new Label("Input File Directory");
        reconstructInputFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        reconstructInputFileDirLabel.setBounds(10, 230, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructInputFileDirLabel);
        
        Label reconstructOutputFileDirLabel = new Label("Output File Directory");
        reconstructOutputFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        reconstructOutputFileDirLabel.setBounds(10, 258, 105, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructOutputFileDirLabel);
        
        TextField reconstructInputFileDirField = new TextField();
        reconstructInputFileDirField.setBounds(121, 230, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructInputFileDirField);
        
        TextField reconstructOutputFileDirField = new TextField();
        reconstructOutputFileDirField.setBounds(121, 258, 299, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructOutputFileDirField);
        
        Button reconstructInputDirSearchButton = new Button("...");
        reconstructInputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   reconstructInputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        reconstructInputDirSearchButton.setBounds(426, 230, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructInputDirSearchButton);
        
        Button reconstructOutputDirSearchButton = new Button("...");
        reconstructOutputDirSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   reconstructOutputFileDirField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        reconstructOutputDirSearchButton.setBounds(426, 258, 22, 22);
        frmGrandiaTranslationTools.getContentPane().add(reconstructOutputDirSearchButton);
        
        Button reconstructButton = new Button("Reconstruct");
        reconstructButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MDTFileReconstructor reconstructor = new MDTFileReconstructor()
                    .inputFilePath(reconstructInputFileDirField.getText())
                    .outputFilePath(reconstructOutputFileDirField.getText() + "\\");
                
                reconstructButton.setEnabled(false);
                reconstructor.reconstruct();
                reconstructButton.setEnabled(true);
            }
        });
        
        reconstructButton.setFont(new Font("Arial", Font.PLAIN, 20));
        reconstructButton.setBounds(466, 221, 208, 59);
        frmGrandiaTranslationTools.getContentPane().add(reconstructButton);
    }
}
