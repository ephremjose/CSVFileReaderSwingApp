package dev.boomi.ui;

/**
 * Created by rephrem on 3/10/2015.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import dev.boomi.util.ProcessUtil;
import dev.boomi.logger.Logger;

/**
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Class Name   : FileChooser.java
 * Purpose      : Dell Boomi Interview Assignment.
 * Date         : 03/11/2015
 * Version      : 1.0.0.0
 *
 * Summary      : This is the JAVA class for the Swing Panel that lets the user choose the CSV file to process.
 *
 * Assumption   : DESCRIPTION data of the CSV file does not have any comma in it.
 *
 * Main UI Elements:
 *
 *      i. JTable dataTable         : This JTable is used to display the contents of the CSV file. It has four columns.
 *                                      (1) ROW_NUMBER  :   Column to list the number of each row, incremented by one for each subsequent row.
 *                                                      This value is not the same as the Row Number of input CSV file.
 *                                      (2) DATE        :   Column to hold the date value from CSV file after processing as per requirements.
 *                                      (3) VALUE       :   Column to hold the monetary dollar value from CSV file after processing as per requirements.
 *                                      (4) DESCRIPTION :   Column to hold the Description data from CSV File.
 *
 *      ii. JButton openButton      : Opens the JFileChooser.
 *      iii.JButton doneButton      : Closes the application when clicked.
 *      iv. JPanel southPanel       : Panel used to hold the two buttons, and is added to the South of the main Frame. Uses Grid Layout.
 *      v. JFileChooser fileChooser : Opens up file browser for the CSV File when OPEN button is clicked.
 *
 */

public class FileChooser extends JPanel implements ActionListener{


    /**
     * Declaring all variables required for the Swing UI.
     */
    private JPanel southPanel;
    private JFileChooser fileChooser;
    private JButton openButton;
    private JButton doneButton;
    private JTable dataTable;
    private JScrollPane scrollPane;
    private ProcessUtil processUtil;
    private File csvFile;
    private int returnValue;
    private boolean firstLoad = true;
    private DefaultTableModel model;
    private DefaultTableCellRenderer centerRenderer;
    private String[][] data;
    private Logger logger;

    /**
     * Constructor to initialize the declared variables.
     */
    public FileChooser(){

        southPanel      = new JPanel(new GridLayout(1,1,0,0));
        fileChooser     = new JFileChooser();
        openButton      = new JButton("OPEN");
        doneButton      = new JButton("DONE");
        dataTable       = new JTable();
        processUtil     = new ProcessUtil();
        model           = new DefaultTableModel();
        centerRenderer  = new DefaultTableCellRenderer();
        scrollPane      = new JScrollPane(dataTable);
        logger          = new Logger();

        setPreferredSize(new Dimension(750,500));
        setLayout(new BorderLayout());

        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        dataTable.setModel(model);
        dataTable.setEnabled(false);

        model.addColumn("ROW_NUMBER");
        model.addColumn("DATE");
        model.addColumn("VALUE");
        model.addColumn("DESCRIPTION");

        southPanel.add(openButton);
        southPanel.add(doneButton);

        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        openButton.addActionListener(this);
        doneButton.addActionListener(this);

    }

    /**
     * Overriding actionPerformed method of ActionListener interface to listen for button clicks from User.
     *
     * @param e
     */

    @Override
    public void actionPerformed(ActionEvent e) {

        /**
         * If the User clicks the OPEN button.
         */
        if(e.getSource() == openButton){

            logger.log("\n\nUser clicked OPEN Button.");

            returnValue = fileChooser.showOpenDialog(null);

            /**
            If the value is true - means that the user was successfully able to open the file.
             */
            if(returnValue == JFileChooser.APPROVE_OPTION){

                //Assign the chosen file to the local variable.
                csvFile = fileChooser.getSelectedFile();

                try {



                    logger.log("\nProcessing file : " + csvFile.getName() + "\n");

                    /**
                    If it is the first time the user is loading any file, then do nothing.
                    Else, clear all the existing rows from the JTable.
                     */
                    if(firstLoad)
                    {
                        firstLoad = false;
                    }
                    else
                    {
                        logger.log("Subsequent Load. Removing existing rows from table. \n");
                        for (int j = model.getRowCount() -1; j > -1; j--) {
                            model.removeRow(j);
                        }
                    }

                    /**
                    Calling the ProcessUtil Class to process the file selected by user.
                     */
                    logger.log("Calling extractCSVFileData() \n");
                    data = processUtil.extractCSVFileData(csvFile);

                    /**
                    Show error to User if the input file is empty.
                    Else, if the file is processed successfully, then add the rows onto the JTable.
                     */
                    if(data == null)
                    {
                        logger.log("User selected an empty file. Throwing exception. \n");
                        JOptionPane.showMessageDialog(null,"CSV File is empty!","",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        logger.log("Adding rows from csv file to JTable. Number of Rows : " + data.length+" \n");
                        for (int i = 0; i < data.length; i++) {
                            model.addRow(data[i]);
                        }
                    }

                    //Rendering the ROW_NUMBER column to fit to the middle.
                    dataTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );

                }
                /**
                Accounting for any exception from ProcessUtil class. If stacktrace is needed, use ex.printStackTrace().
                 */
                catch (Exception ex)
                {
                    /**
                    If it is a thrown Exception, then show that error to the USER.
                    All code thrown exceptions will have the phrase INCORRECT_DATA in it.
                     */
                    String errorMessage;
                    if(ex.getMessage()!= null && ex.getMessage().contains("INCORRECT_DATA")) {
                        errorMessage = ex.getMessage();
                    }
                    else {
                        errorMessage = "CSV File is not in the proper format!";
                    }

                    logger.log("Caught Exception in processing : " + ex.getMessage()  + "\n");
                    JOptionPane.showMessageDialog(null, errorMessage, "", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        /**
         * If the user clicks the DONE button, then exit the application.
         */
        if(e.getSource() == doneButton){

            logger.log("Exiting application. \n");
            System.exit(0);
        }
    }



}
