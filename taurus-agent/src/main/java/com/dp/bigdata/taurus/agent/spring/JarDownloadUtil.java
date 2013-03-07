package com.dp.bigdata.taurus.agent.spring;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

/**
 * JarDownloadUtil
 * 
 * @author damon.zhu
 */
public class JarDownloadUtil {
	
	private static String host = "10.1.1.81";
	private static String username = "anonymous";
	private static String passwd = "";

    public static String downloadFromFTP(String url,String jarPath) {
        String[] splits = url.split("/");
        String filename = splits[splits.length - 1];
        FTPClient client = new FTPClient();
        FileOutputStream fos = null;
 
        try {
            client.connect(host);
            client.login(username, passwd);
 
            //
            // The remote filename to be downloaded.
            //
            fos = new FileOutputStream(jarPath + "/" + filename);
            //
            // Download file from FTP server
            //
            client.retrieveFile(url,fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filename;
    }
}
