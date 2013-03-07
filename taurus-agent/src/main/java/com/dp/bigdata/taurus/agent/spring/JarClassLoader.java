package com.dp.bigdata.taurus.agent.spring;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * JarsClassLoader
 * 
 * @author damon.zhu
 */
public class JarClassLoader {

    public void getAllJarFiles(File file, List<File> files) {
        if(file.exists()){
            if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
                files.add(file);
            }
            if (file.isDirectory()) {
                File[] _files = file.listFiles();
                for (File path : _files) {
                    getAllJarFiles(path,files);
                }
            }
        }
    }

    public URLClassLoader getClassLoader(String folder) throws MalformedURLException {
    	List<File> files = new ArrayList<File>();
        getAllJarFiles(new File(folder),files);
        URL[] urls = new URL[files.size()];
        for (int i = 0; i < files.size(); i++) {
            urls[i] = new URL("file:///" + files.get(i).getAbsolutePath());
        }
        return new URLClassLoader(urls);
    }
    
    public static void main(String[] args) {
		JarClassLoader jcl = new JarClassLoader();
		List<File> files = new ArrayList<File>();
		jcl.getAllJarFiles(new File("/Users/damonzhu/.m2/repository/"), files);
		for (int i = 0; i < files.size(); i++) {
			System.out.println(files.get(i));
		}
    }
    

}
