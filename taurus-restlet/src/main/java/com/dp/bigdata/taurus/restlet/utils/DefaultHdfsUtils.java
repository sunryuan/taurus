package com.dp.bigdata.taurus.restlet.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

/**
 * DefaultHdfsUtils
 * 
 * @author damon.zhu
 */
public class DefaultHdfsUtils implements HdfsUtils {
    private final String CONFIG_FILE = "restlet.properties";
    private final int BUFFER_SIZE = 1024 * 1024;
    private final Properties props = new Properties();
    private final Configuration conf = new Configuration();

    public void init() {
        try {
            InputStream in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(CONFIG_FILE);
            props.load(in);
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hadoop.security.authorization", "true");
        conf.set("dfs.namenode.kerberos.principal", props.getProperty("dfs.namenode.kerberos.principal"));
        conf.set("dp.hdfsclinet.kerberos.principal", props.getProperty("dp.hdfsclinet.kerberos.principal"));
        conf.set("dp.hdfsclinet.keytab.file", props.getProperty("dp.hdfsclinet.keytab.file"));
        UserGroupInformation.setConfiguration(conf);
        try {
            SecurityUtil.login(conf, "dp.hdfsclinet.keytab.file", "dp.hdfsclinet.kerberos.principal");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void writeFile(String srcFile, String destFile) throws IOException, FileNotFoundException {
        File file = new File(srcFile);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found");
        }
        byte[] buf = new byte[BUFFER_SIZE];
        FileInputStream input = new FileInputStream(file);
        FileSystem fs = FileSystem.get(URI.create(destFile), conf);
        Path destPath = new Path(destFile);
        if (fs.exists(destPath)) {
            fs.delete(destPath, true);
        }
        FSDataOutputStream hdfsoutput = fs.create(destPath, (short) 2);
        int num = input.read(buf);
        while (num != (-1)) {//是否读完文件
            hdfsoutput.write(buf, 0, num);//把文件数据写出网络缓冲区
            hdfsoutput.flush();//刷新缓冲区把数据写往客户端
            num = input.read(buf);//继续从文件中读取数据
        }
        input.close();
        hdfsoutput.close();
        fs.close();
    }

    @Override
    public void removeFile(String srcFile) throws IOException, FileNotFoundException {
        FileSystem fs = FileSystem.get(URI.create(srcFile), conf);
        Path path = new Path(srcFile);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
        fs.close();
    }

    @Override
    public void readFile(String srcFile, String destFile) throws IOException, FileNotFoundException {
        File file = new File(destFile);
        if (file.exists()) {
            file.delete();
        }
        byte[] buf = new byte[BUFFER_SIZE];
        FileOutputStream fos = new FileOutputStream(file);
        FileSystem fs = FileSystem.get(URI.create(srcFile), conf);
        FSDataInputStream hdfsInput = fs.open(new Path(srcFile));
        int num = hdfsInput.read(buf);
        while (num != (-1)) {//是否读完文件
            fos.write(buf, 0, num);//把文件数据写出网络缓冲区
            fos.flush();//刷新缓冲区把数据写往客户端
            num = hdfsInput.read(buf);//继续从文件中读取数据
        }
        hdfsInput.close();
        fos.close();
        fs.close();
    }
}
