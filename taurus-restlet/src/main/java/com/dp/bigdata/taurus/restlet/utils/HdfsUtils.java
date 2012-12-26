package com.dp.bigdata.taurus.restlet.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * HdfsUtils
 * 
 * @author damon.zhu
 */
public interface HdfsUtils {

    public void writeFile(String srcFile, String destFile) throws IOException, FileNotFoundException;

    public void removeFile(String srcFile) throws IOException, FileNotFoundException;
    
    public void readFile(String srcFile, String destFile) throws IOException, FileNotFoundException;
}
