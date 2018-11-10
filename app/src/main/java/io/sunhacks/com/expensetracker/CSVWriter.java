package io.sunhacks.com.expensetracker;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CSVWriter {

    private static final String LOG_TAG = "CSVWRITER:";

    public boolean exportMessages(File exportFile,
                                  List<SpendingModel> listMessages, String stHeader, String stSeperator) {
        boolean isExported = false;
        Log.i(LOG_TAG, "exportMessages " + listMessages.size() + " " + exportFile);
        if (null != exportFile && null != listMessages
                && listMessages.size() > 0) {
            FileWriter fWriter = null;
            BufferedWriter bWriter = null;
            try {

                fWriter = new FileWriter(exportFile);
                bWriter = new BufferedWriter(fWriter);

                String stDecimalPrecision = "%.2f";

                bWriter.write(stHeader);
                bWriter.newLine();

                for (SpendingModel message : listMessages) {
                    Log.i(LOG_TAG, "loop model");
                    StringBuilder stBuilder = new StringBuilder();

                    stBuilder.append(String.format(stDecimalPrecision, message.getAmount()).replace(",", "."));
                    stBuilder.append(stSeperator);
                    stBuilder.append(message.getMerchant());
                    stBuilder.append(stSeperator);
                    stBuilder.append(message.getCategory());
                    stBuilder.append(stSeperator);
                    stBuilder.append(message.getAccount());
                    stBuilder.append(stSeperator);
                    Date time = message.getSmsTime();
                    stBuilder.append(time.getTime());
                    stBuilder.append(stSeperator);

                    bWriter.write(stBuilder.toString());
                    bWriter.newLine();
                    stBuilder = null;
                    isExported = true;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != bWriter) {
                    try {
                        bWriter.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    bWriter = null;
                }
                if (null != fWriter) {
                    try {
                        fWriter.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    fWriter = null;
                }
            }
        }
        return isExported;
    }
}

