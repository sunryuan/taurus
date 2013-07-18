package com.dp.bigdata.taurus.agent.spring;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.dp.bigdata.taurus.framework.ApplicationContextProvider;

/**
 * JarsClassLoader
 * 
 * @author damon.zhu
 */
public class JarClassLoader extends URLClassLoader {

	public JarClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	private static void getAllJarFiles(File file, List<File> files) {
		if (file.exists()) {
			if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
				files.add(file);
			}
			if (file.isDirectory()) {
				File[] _files = file.listFiles();
				for (File path : _files) {
					getAllJarFiles(path, files);
				}
			}
		}
	}

	public static URL[] getClassLoader(String folder)
			throws MalformedURLException {
		List<File> files = new ArrayList<File>();
		getAllJarFiles(new File(folder), files);
		URL[] urls = new URL[files.size()];
		for (int i = 0; i < files.size(); i++) {
			urls[i] = new URL("file:///" + files.get(i).getAbsolutePath());
		}
		return urls;
	}

	@SuppressWarnings("rawtypes")
    public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class c = findLoadedClass(name);
		if (c == null) {
			if (name.startsWith("org.apache.log4j")
					|| name.startsWith("org.springframework")
					|| name.startsWith(ApplicationContextProvider.class.getName())) {
				c = super.findClass(name);
			} else {
				c = super.loadClass(name);
			}
		}
		return c;
	}
}
