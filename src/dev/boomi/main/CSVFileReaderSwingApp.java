package dev.boomi.main;

import javax.swing.JFrame;
import dev.boomi.ui.FileChooser;


/**
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Class Name   : CSVFileReaderSwingApp.java
 * Purpose      : Dell Boomi Interview Assignment.
 * Date         : 03/11/2015
 * Version      : 1.0.0.0
 *
 * Summary      : This is the main class for the JAVA Swing application.
 *                It creates the JFrame and calls the FileChooser class.
 *
 */

    public class CSVFileReaderSwingApp {

        public static void main(String args[]) {
            JFrame frame = new JFrame("DELL BOOMI ASSIGNMENT - CSV FILE READER");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new FileChooser());
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        }

}

