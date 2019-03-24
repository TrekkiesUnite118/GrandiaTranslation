package com.grandia.gui;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.grandia.file.ScriptPortraitCorrector;
import com.grandia.file.ScriptReconstructor;

import java.awt.Label;
import java.awt.Font;
import java.awt.TextField;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * 
 * This is the GUI for the Script Portrait Corrector. 
 * 
 * It is mostly generated code from Eclipse's WindowBuilder Plugin.
 * 
 * @author TrekkiesUnite118
 *
 */
public class GrandiaScriptPortraitCorrector {

    private JFrame frmGrandiaScriptPortraitCorrector;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GrandiaScriptPortraitCorrector window = new GrandiaScriptPortraitCorrector();
                    window.frmGrandiaScriptPortraitCorrector.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public GrandiaScriptPortraitCorrector() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmGrandiaScriptPortraitCorrector = new JFrame();
        frmGrandiaScriptPortraitCorrector.setTitle("Grandia Script Reconstructor");
        frmGrandiaScriptPortraitCorrector.setBounds(100, 100, 450, 335);
        frmGrandiaScriptPortraitCorrector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        frmGrandiaScriptPortraitCorrector.setJMenuBar(menuBar);
        
        JMenu mnFileMenu = new JMenu("File");
        menuBar.add(mnFileMenu);
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnFileMenu.add(mntmExit);
        frmGrandiaScriptPortraitCorrector.getContentPane().setLayout(null);
        
        Label trueScriptFileDirectoryLabel = new Label("True Script File Directory");
        trueScriptFileDirectoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        trueScriptFileDirectoryLabel.setBounds(10, 10, 142, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(trueScriptFileDirectoryLabel);
        
        TextField trueScriptFileDirectoryField = new TextField();
        trueScriptFileDirectoryField.setBounds(10, 32, 355, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(trueScriptFileDirectoryField);
        
        Button trueScriptFileDirectoryButton = new Button("...");
        trueScriptFileDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   trueScriptFileDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        trueScriptFileDirectoryButton.setBounds(371, 32, 33, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(trueScriptFileDirectoryButton);
        
        Label inputScriptFileDirectoryLabel = new Label("Input Script File Directory");
        inputScriptFileDirectoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        inputScriptFileDirectoryLabel.setBounds(10, 60, 248, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(inputScriptFileDirectoryLabel);
        
        TextField inputScriptFileDirectoryField = new TextField();
        inputScriptFileDirectoryField.setBounds(10, 88, 355, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(inputScriptFileDirectoryField);
        
        Button inputScriptFileDirectoryButton = new Button("...");
        inputScriptFileDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                   System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                   inputScriptFileDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        inputScriptFileDirectoryButton.setBounds(371, 88, 33, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(inputScriptFileDirectoryButton);
        
        TextField outputFileDirField = new TextField();
        outputFileDirField.setBounds(10, 144, 355, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(outputFileDirField);
        
        Label outputFileDirLabel = new Label("Output File Directory");
        outputFileDirLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        outputFileDirLabel.setBounds(10, 116, 248, 22);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(outputFileDirLabel);
        
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
        frmGrandiaScriptPortraitCorrector.getContentPane().add(outputFileDirButton);
        
        Button correctButton = new Button("Correct Portrait Codes");
        correctButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ScriptPortraitCorrector corrector = new ScriptPortraitCorrector()
                        .truthFilePath(trueScriptFileDirectoryField.getText())
                        .inputFilePath(inputScriptFileDirectoryField.getText())
                        .outputFilePath(outputFileDirField.getText() + "\\");
                
                correctButton.setEnabled(false);
                corrector.scanForPortraitCodes();
                correctButton.setEnabled(true);
            }
        });
        correctButton.setFont(new Font("Arial", Font.PLAIN, 28));
        correctButton.setBounds(10, 196, 394, 59);
        frmGrandiaScriptPortraitCorrector.getContentPane().add(correctButton);
    }
}


