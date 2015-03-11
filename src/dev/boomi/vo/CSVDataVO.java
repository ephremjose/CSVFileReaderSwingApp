package dev.boomi.vo;

/**
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Class Name   : CSVDataVO.java
 * Purpose      : Dell Boomi Interview Assignment.
 * Date         : 03/11/2015
 * Version      : 1.0.0.0
 *
 * Summary      : This is a Value Object (VO) class used to hold the values of each line
 *                of the CSV file in the corresponding variables.
 *                An ArrayList of this VO is used to store the complete data.
 */


public class CSVDataVO {

    String date;
    String rowNumber;
    String description;
    String value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
