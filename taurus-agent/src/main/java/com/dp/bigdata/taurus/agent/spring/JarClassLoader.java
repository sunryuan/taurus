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

    private final List<File> files = new ArrayList<File>();

    private void getAllJarFiles(File file) {
        if(file.exists()){
            if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
                files.add(file);
            }
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File path : files) {
                    getAllJarFiles(path);
                }
            }
        }
    }

    public URLClassLoader getClassLoader(String folder) throws MalformedURLException {
        getAllJarFiles(new File(folder));
        URL[] urls = new URL[files.size()];
        for (int i = 0; i < files.size(); i++) {
            urls[i] = new URL("file:///" + files.get(i).getAbsolutePath());
        }
        return new URLClassLoader(urls);
    }

    public static void main(String args[]) {
        JarClassLoader jcl = new JarClassLoader();
        jcl.getAllJarFiles(new File("D:\\workspace\\test-task-1\\target"));
    }

}
