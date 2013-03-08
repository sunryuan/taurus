/**
 * Project: taurus-agent
 * 
 * File Created at 2013-2-27
 * $Id$
 * 
 * Copyright 2013 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dp.bigdata.taurus.agent.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.dp.bigdata.taurus.zookeeper.common.utils.ClassLoaderUtils;

/**
 * TODO Comment of FileExtractUtils
 * @author renyuan.sun
 *
 */
public class FileExtractUtilsTest {
    
    private String getAbsolutePath(String str){
        return ClassLoaderUtils.getDefaultClassLoader().getResource(str).getFile() ;
    }
    @Test
    public void TestUnzip(){
        String path =  getAbsolutePath("extract/test.zip");
        File file  = new File(path);
        try {
            FileExtractUtils.unZip(file);
            File testFile1 = new File(file.getParent()+"/test/test1.txt");
            assertTrue(testFile1.exists());
            FileUtils.deleteDirectory(new File(file.getParent()+"/test"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            
        
    }
    
    @Test
    public void TestunTarunGzip(){
        String path =  getAbsolutePath("extract/test.tar.gz");
        File file  = new File(path);
        try {
            FileExtractUtils.unTar(FileExtractUtils.unGzip(file));
            File testFile1 = new File(file.getParent()+"/test/test1.txt");
            assertTrue(testFile1.exists());
            FileUtils.deleteDirectory(new File(file.getParent()+"/test"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void TestunTarunBzip(){
        String path =  getAbsolutePath("extract/test.tar.bz2");
        File file  = new File(path);
        try {
            FileExtractUtils.unTar(FileExtractUtils.unBzip(file));
            File testFile1 = new File(file.getParent()+"/test/test1.txt");
            assertTrue(testFile1.exists());
            FileUtils.deleteDirectory(new File(file.getParent()+"/test"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
    }

}
