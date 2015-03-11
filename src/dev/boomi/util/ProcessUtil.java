package dev.boomi.util;

import dev.boomi.logger.Logger;
import dev.boomi.vo.CSVDataVO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Class Name   : ProcessUtil.java
 * Purpose      : Dell Boomi Interview Assignment.
 * Date         : 03/11/2015
 * Version      : 1.0.0.0
 *
 * Summary      : This JAVA Class contains the logic for processing the input CSV file.
 *
 * Assumption   : DESCRIPTION data of the CSV file does not have any comma in it.
 *
 *
 */
public class ProcessUtil  {

    final int NUMBER_OF_COLUMNS = 4;
    /**
     * Declaring all the regular expressions being used as argument for the various split() methods.
     */
    final String REGEX_CSV      = ",";
    final String REGEX_DATE     = "/";
    final String REGEX_TIME     = ":";
    final String REGEX_VALUE    = "\\.";

    Logger logger;

    public ProcessUtil() {
        this.logger = new Logger();
    }

    /**
     * extractCSVFileData() method takes the File as input and returns the data in the file as a 2D String array.
     *
     * @param csvFileName
     * @return
     * @throws Exception
     */
    public String[][] extractCSVFileData(File csvFileName) throws Exception{

        /**
         * Declaring all variable for this method.
         */
        String[][] csvDataArray     = null;     //2D array to hold the final data returned to FileChooser.
        String[] lineDataArray      = null;     //Array to hold the data of each line after splitting it using REGEX_CSV.
        BufferedReader br           = null;     //For reading each line.
        String currLine             = "";       //Holds the currentLine being processed.
        String date                 = "";       //Holds the value of the date field in the input line.
        String description          = "";       //Holds the value of description field in the input line.
        CSVDataVO csvDataVO         = null;     //Creating an object of the VO class to hold the data for each line.
        int numOfRows               = 0;        //Keeps track of the number of rows being added in the arrayList. (After removing duplicates.)
        int numOfLines              = 0;        //Keeps track of the line number of the CSV file being processed.
        int duplicateCount          = 0;        //Keeps track of number of duplicate records.

        ArrayList<CSVDataVO> csvDataArrayList     = new ArrayList<CSVDataVO>();     //ArrayList of CSVDataVOs to hold the complete data of the CSV file.
        LinkedHashSet<String> closedListOfRows    = new LinkedHashSet<String>();    //LinkedHashSet used to keep track of each record to check for duplicates.

        try{

            br = new BufferedReader(new FileReader(csvFileName));

            logger.log("Reading file. \n");
            //Reading the file till the end, line by line.
            while((currLine = br.readLine()) != null){

                /**
                 * If the current line is not empty, we call the overloaded split(regex,limit) method
                 * to ensure to catch all trailing commas at the end.
                 */
                if(currLine != null) {
                    lineDataArray = currLine.split(REGEX_CSV, -1);
                }
                if(lineDataArray != null) {

                    /**
                     * If       - the line has anything other than exactly 3 or exactly 4 commas --> throw Exception.
                     * Else if  - the line has 4 commas, with some trailing data after the last comma --> throw Exception.
                     * Else     - process the line.
                     */
                    if (lineDataArray.length != 4 && lineDataArray.length != 5) {
                            logger.log("INCORRECT_DATA at Line Number : " + ++numOfLines + " - Number of commas : " + (lineDataArray.length-1) + "\n");
                            throw new Exception("INCORRECT_DATA at Line Number : " + ++numOfLines + " - Number of commas : " + (lineDataArray.length-1));
                    }
                    else if(lineDataArray.length == 5 && lineDataArray[4]!=null && lineDataArray[4].length()>0){
                        logger.log("INCORRECT_DATA at Line Number : " + ++numOfLines + " - Unwanted characters at end of line. " + "\n");
                        throw new Exception("INCORRECT_DATA at Line Number : " + ++numOfLines + " - Unwanted characters at end of line. ");
                    }
                    else {

                        date = lineDataArray[0];
                        description = lineDataArray[2];

                        /*
                        Checking if we have already processed any line with the exact same data and description combination.
                        If not, add the combination to the linkedHashSet and process the line.
                         */
                        if(!closedListOfRows.contains(date + description)){

                            closedListOfRows.add(date + description);

                            csvDataVO = new CSVDataVO();
                            csvDataVO.setDate(formatDate(date, numOfLines+1));                  //Calling formatDate method.
                            csvDataVO.setRowNumber(lineDataArray[1]);
                            csvDataVO.setDescription(description);
                            csvDataVO.setValue(formatValue(lineDataArray[3], numOfLines+1));    //Calling formatValue method.

                            csvDataArrayList.add(csvDataVO);

                        }
                        else{
                            duplicateCount++;
                        }
                    }

                }
                numOfLines ++;
            }

            logger.log(" Number of duplicate records : " + duplicateCount +"\n");

            /*
            Check the number of rows added to the ArrayList.
            If we have at least one record successfully added, we add that to the 2D array and return it.
             */


            logger.log(" Adding the values from ArrayList to Array. \n");
            numOfRows = csvDataArrayList.size();
            if(numOfRows>0) {
                csvDataArray = new String[numOfRows][NUMBER_OF_COLUMNS];
                String[] tempLineArray;
                for (int i = 0; i < numOfRows; i++) {

                    tempLineArray = new String[NUMBER_OF_COLUMNS];

                    tempLineArray[0] = String.valueOf(i+1);
                    tempLineArray[1] = csvDataArrayList.get(i).getDate();
                    tempLineArray[2] = csvDataArrayList.get(i).getValue();
                    tempLineArray[3] = csvDataArrayList.get(i).getDescription();

                    csvDataArray[i] = tempLineArray;
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return csvDataArray;
    }

    /**
     *
     * formatDate() method to format the input date from CSV file as per specifications mentioned.
     *
     * @param inputDate
     * @param rowNum
     * @return
     * @throws Exception
     */
    public String formatDate ( String inputDate, int rowNum ) throws Exception
    {


        String formattedDate = "";      //variable to be returned to calling function.
        String dateField;               //variable to hold the date part of inputDate.
        String timeField;               //variable to hold the time part of inputDate.
        String amOrPM;                  //variable to keep track if the time is in the AM or PM.
        String[] dateArray;             //array to hold the elements of the date after splitting dateField.
        String[] timeArray;             //array to hold the elements of the time after splitting timeField.
        int hourOfTime;                 //integer value to hold the hour.

        /**
         * If the input date is either empty of null, no processing is done.
         */
        if(inputDate==null || inputDate.length()==0)
            return inputDate;

        /**
        Case where the input date is non empty, but does not have either T or Z, as per the requirement - exception is thrown.
         */
        if(inputDate!=null && inputDate.length()>0 && (!inputDate.contains("T") || !inputDate.contains("Z")))
        {
            logger.log("INCORRECT_DATA for Input Date - Either 'T' or 'Z' not present at line Number : " + rowNum + "\n");
            throw new Exception("INCORRECT_DATA for Input Date - Either 'T' or 'Z' not present at line Number : " + rowNum);
        }
        else
        {
            try {
                //Using substring to extract the date and time.
                dateField = inputDate.substring(0, inputDate.indexOf("T"));
                timeField = inputDate.substring(inputDate.indexOf("T")+1,inputDate.length()-1);

                //Splitting the dateField using REGEX_DATE = "/".
                dateArray = dateField.split(REGEX_DATE);

                //Removing leading zeroes from day and month using Integer parsing. Adding the date to the formattedDate.
                formattedDate += Integer.parseInt(dateArray[0]);
                formattedDate += REGEX_DATE + Integer.parseInt(dateArray[1]);
                formattedDate += REGEX_DATE + dateArray[2].substring(2, dateArray[2].length());
                formattedDate += " ";

                //Splitting the timeField using REGEX_TIME = ":".
                timeArray       = timeField.split(REGEX_TIME);
                hourOfTime      = Integer.parseInt(timeArray[0]);

                /**
                 * If       - hour is AM
                 * Else if  - hour is PM
                 * Else     - throw exception.
                 */
                if(hourOfTime >= 0 && hourOfTime <=11){
                    amOrPM = " AM";
                }
                else if(hourOfTime >=12 && hourOfTime <= 23 ){
                    amOrPM = " PM";
                    if(hourOfTime != 12)
                        hourOfTime -= 12;
                }
                else{
                    throw new Exception(" - Hour not in range at line Number : " + rowNum);
                }

                //Adding the time to the formattedDate.
                formattedDate  += hourOfTime;
                formattedDate  += REGEX_TIME + Integer.parseInt(timeArray[1]);
                formattedDate  += amOrPM;

                formattedDate  += " EDT";

            }catch (Exception ex)
            {
                logger.log("INCORRECT_DATA - Date field should be of format : MM/DD/YYYYTHH:MM:SSZ " + ex.getMessage() + " at line Number : " + rowNum + "\n");
                /*
                If any other exception happens, it is handled.
                 */
                throw new Exception("INCORRECT_DATA - Date field should be of format : MM/DD/YYYYTHH:MM:SSZ " + ex.getMessage() + " at line Number : " + rowNum);
            }
        }

        logger.log(" Input date : " + inputDate + ", Formatted Date : " + formattedDate + "\n");
        return formattedDate;

    }

    /**
     *
     * formatValue() method to format the input value from CSV file as per specifications mentioned.
     *
     * @param inputValue
     * @param rowNum
     * @return
     * @throws Exception
     */
    public String formatValue( String inputValue, int rowNum ) throws Exception
    {

        String formattedValue   = "$";      //variable to hold the formatted value.
        String[] valueArray;                //Array to hold the split values if input value has a period.
        String dollars;                     //variable to hold the dollars part.
        String cents;                       //variable to hold the cents part.

        try {
            /**
             * if       - input value has some data in it --> Process.
             * else     - return input value.
             */
            if(inputValue != null && inputValue.length()>0){

                /*
                Check if the input value has a period in it - then do corresponding processing.
                 */
                if(inputValue.indexOf('.') != -1){

                    //Splitting the input value using period.
                    valueArray = inputValue.split(REGEX_VALUE,-1);

                    /**
                     * If there are more than one period, then throw excpetion.
                     * Else process.
                     */
                    if(valueArray.length>2){
                        logger.log("INCORRECT_DATA - Value has multiple periods at line Number : " + rowNum + "\n");
                        throw new Exception("INCORRECT_DATA - Value has multiple periods at line Number : " + rowNum);
                    }
                    else {

                        dollars = valueArray[0];
                        cents   = valueArray[1];

                        try{

                            /*
                            Validating the input value to see if they are actually numeric values.
                             */
                            if(dollars!=null && cents != null && dollars.length()>0 && cents.length()>0) {
                                Double.parseDouble(dollars);
                                Integer.parseInt(cents);

                            }

                        }
                        catch (Exception er){
                            logger.log("INCORRECT_DATA - Value can have only numeric data. Error at line Number : " + rowNum+ "\n");
                            throw new Exception("INCORRECT_DATA - Value can have only numeric data. Error at line Number : " + rowNum);
                        }

                        //If there is nothing in the dollars part, adding a Zero.
                        if(dollars!=null && dollars.length()==0){
                            dollars += "0";
                        }
                        if(cents!=null){

                            //If the cents part is empty, adding two trailing zeroes.
                            if(cents.length()==0){
                                cents += "00";
                            }
                            //If the cents part has only one numeral, adding one trailing zero.
                            else if(cents.length()==1){
                                cents += "0";
                            }
                            //If the cents part is more than two decimal parts - throw exception.
                            else if(cents.length()>2){
                                logger.log("INCORRECT_DATA - Value can have only up to two decimal places. Error at line Number : " + rowNum + "\n");
                                throw new Exception("INCORRECT_DATA - Value can have only up to two decimal places. Error at line Number : " + rowNum);
                            }
                        }

                    }

                    //Finally formattedValue is generated.
                    formattedValue += (dollars + "." + cents);
                }
                /**
                 * If there is no period in the input value.
                 */
                else{

                    /*
                    Validating the input value to see if they are actually numeric values.
                    */
                    try{
                        Double.parseDouble(inputValue);
                    }
                    catch(Exception ex){
                        logger.log("INCORRECT_DATA - Value can have only numeric data. Error at line Number : " + rowNum + "\n");
                        throw new Exception("INCORRECT_DATA - Value can have only numeric data. Error at line Number : " + rowNum);
                    }

                    /**
                     * If the input value is more than thousand, adding comma at each 1000th place.
                     *
                     * First the input value is reversed and kept in inputValueReverse.
                     * Then it is added to the tempString variable one character at time.
                     * After every three additions to tempString, a comma is added if that is not the last number.
                     * tempString is reversed and added to formattedValue.
                     */
                    if(inputValue.length()>3){
                        String inputValueReverse = (new StringBuffer (inputValue)).reverse().toString();
                        String tempString = "";
                        int commaIndex = 0;
                        for (int i = 0; i < inputValueReverse.length() ; i++) {

                            tempString += inputValueReverse.charAt(i);
                            commaIndex++;

                            //Comma will be added only if the current character is not the last numeral.
                            if(commaIndex==3 && i != inputValueReverse.length()-1) {
                                tempString += ",";
                                commaIndex = 0;
                            }
                        }

                        //Finally adding it to formattedValue and adding the .00 part.
                        formattedValue += ((new StringBuffer (tempString)).reverse().toString() + ".00");
                    }
                    else{

                        /*
                        IF the input value is not more than thousand, simply add .00 and return.
                         */
                        formattedValue += (inputValue + ".00");
                    }
                }

            }
            else{
                return inputValue;
            }
        } catch (Exception e) {

            logger.log("Exception at formatValue : " + e.getMessage());
            if(e.getMessage()!=null && e.getMessage().contains("INCORRECT_DATA")){
                throw new Exception(e.getMessage());
            }
            throw new Exception("INCORRECT_DATA - Check the format of Value.");
        }

        logger.log(" Input Value : " + inputValue + ", Formatted Value : " + formattedValue + "\n");
        return formattedValue;

    }
}
