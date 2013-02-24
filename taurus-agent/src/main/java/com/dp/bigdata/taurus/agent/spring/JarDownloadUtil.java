package com.dp.bigdata.taurus.agent.spring;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * JarDownloadUtil
 * 
 * @author damon.zhu
 */
public class JarDownloadUtil {

    public static String downloadByHttp(String url) throws ClientProtocolException, IOException {
        String[] splits = url.split("/");
        String fileName = splits[splits.length - 1];

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //long len = entity.getContentLength();
            InputStream is = entity.getContent();
            // write the file to whether you want it.
            OutputStream os = new FileOutputStream(fileName);
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            os.close();
        }

        return fileName;
    }

    public static void main(String args[]) {
        String url = "http://alpha.ci.dp/view/group/job/alpha-group-job/ws/group-levelupgrade-job/target/GroupLevelUpgrade.jar";
        String[] splits = url.split("/");
        String fileName = splits[splits.length - 1];
        System.out.println(fileName);

        try {
            JarDownloadUtil.downloadByHttp(url);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
