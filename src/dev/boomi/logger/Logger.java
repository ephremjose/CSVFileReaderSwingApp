package dev.boomi.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Class Name   : Logger.java
 * Purpose      : Dell Boomi Interview Assignment.
 * Date         : 03/11/2015
 * Version      : 1.0.0.0
 *
 * Summary      : This JAVA Class is used to generate logs. All logs are added to output.log file.
 *
 */

public class Logger {

    /**
     *
     * This method is used to output all the logs to the output.log file, thereby keeping the console output clean.
     *
     * @param value
     */
    public void log(String value)
    {
        File logFile = new File("output.log");
        try{

            FileWriter fileWriter = new FileWriter(logFile.getName(), true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(value);
            bufferedWriter.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
