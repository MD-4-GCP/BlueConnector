package de.dietzm.blueblue.clitools;

import java.io.File;
import java.util.Properties;
import java.io.FileOutputStream;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class DestinationCreation {
    
    private static final String DESTINATION_NAME1 = "dest";

    public static void main(String[] args) {
        
        Properties connectProperties = new Properties(); 
        
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,"52.200.141.134"); 
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,"00");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,"300");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER,"MDONE");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,"<pass>"); 
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG,"en");

        File destCfg = new File(DESTINATION_NAME1+".jcoDestination"); 
        
        try {
            FileOutputStream fos = new FileOutputStream(destCfg,false);
            connectProperties.store(fos, "for tests only !"); 
            fos.close();
        } catch(Exception e){
            throw new RuntimeException("Unable to create the destination files", e);
        }

    }
}