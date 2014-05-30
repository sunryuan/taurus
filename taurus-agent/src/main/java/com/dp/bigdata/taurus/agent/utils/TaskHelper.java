/**
 * Project: taurus-agent File Created at 2013-2-6 $Id$ Copyright 2013 dianping.com. All rights reserved. This software is the
 * confidential and proprietary information of Dianping Company. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with
 * dianping.com.
 */
package com.dp.bigdata.taurus.agent.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * upload task log and deploy task program file
 * 
 * @author renyuan.sun
 */
public class TaskHelper {
	private static final Log LOGGER = LogFactory.getLog(TaskHelper.class);

	private final Configuration conf = new Configuration();

	private final static int BUFFER_SIZE = 1024 * 1024;

	private final static String HDFS_LOG_PATH = AgentEnvValue.getHdfsValue(AgentEnvValue.HDFS_HOST, "")
	      + "/user/workcron/taurus/logs/";

	private final static String LOG_HEAD = "<html><head>    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">    <title>log</title>"
	      + "<style type=\"text/css\"></style><style>.stderr {background-color: #f5ebeb;}.stdout {background-color: #f5ebeb;}"
	      + "body, table td, select {font-family: Arial Unicode MS, Arial, sans-serif;font-size: medium;}</style></head>"
	      + "<body><div><div> <h1>ExitCode</h1> </div> <div class=\"stderr\">";

	private final static String LOG_STDERR = "</div> <div> <h1>StdErr</h1> </div> <div class=\"stderr\">";

	private final static String LOG_STDOUT = "</div> <div> <h1>StdOut</h1> </div> <div class=\"stdout\">";

	private final static String LOG_END = "</div></div></body></html>";

	private final static String HTML_LINE_SPLITTER = "</br>";

	public TaskHelper() {
		conf.set("hadoop.security.authentication", "kerberos");
		conf.set("hadoop.security.authorization", "true");
		conf.set("dfs.namenode.kerberos.principal", AgentEnvValue.getHdfsValue(AgentEnvValue.NAMENODE_PRINCIPAL));
		conf.set("dp.hdfsclinet.kerberos.principal", AgentEnvValue.getHdfsValue(AgentEnvValue.KERBEROS_PRINCIPAL));
		conf.set(
		      "dp.hdfsclinet.keytab.file",
		      AgentEnvValue.getValue(AgentEnvValue.AGENT_ROOT_PATH)
		            + AgentEnvValue.getHdfsValue(AgentEnvValue.KEYTAB_FILE));
		conf.set("fs.hdfs.impl.disable.cache", "true");
		UserGroupInformation.setConfiguration(conf);
		try {
			SecurityUtil.login(conf, "dp.hdfsclinet.keytab.file", "dp.hdfsclinet.kerberos.principal");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void uploadLog(int returnValue, String errorFile, String logFile, String targetFile, String fileName)
	      throws IOException {
		long logFileLength = new File(logFile).length();
		File target = new File(targetFile);
		target.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
		writer.write(LOG_HEAD);
		writer.write(String.valueOf(returnValue) + HTML_LINE_SPLITTER);

		// write stderr
		InputStreamReader isr;
		BufferedReader reader;
		if (errorFile != null) {
			long errorFileLength = new File(errorFile).length();
			writer.write(LOG_STDERR);
			isr = new InputStreamReader(new FileInputStream(errorFile), System.getProperty("file.encoding"));
			reader = new BufferedReader(isr);
			writeLogToFile(reader, writer, errorFileLength, BUFFER_SIZE);
			reader.close();
		}

		// writer stdout
		writer.write(LOG_STDOUT);
		isr = new InputStreamReader(new FileInputStream(logFile), System.getProperty("file.encoding"));
		reader = new BufferedReader(isr);
		writeLogToFile(reader, writer, logFileLength, BUFFER_SIZE);
		reader.close();
		writer.write(LOG_END);
		writer.close();
		writeFileToHdfs(targetFile, HDFS_LOG_PATH + "/" + fileName);
	}

	public void deployTask(String fileName, String localFileName) throws FileNotFoundException, IOException,
	      ArchiveException {
		File localFile = new File(localFileName);
		if (!localFile.exists()) {
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
		}
		// readFileFromHdfs(fileName, localFileName);
		readFileFromHttp(fileName, localFileName);
		File inputFile = new File(localFileName);
		extractFile(localFileName, inputFile.getParent());
	}

	private void readFileFromHttp(String httpUrl, String saveFile) throws IOException {
		int byteread = 0;
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			LOGGER.debug(String.format("transfer from %s to %s", httpUrl, saveFile));
			URL url = null;
			url = new URL(httpUrl);

			URLConnection conn = url.openConnection();
			inStream = conn.getInputStream();
			fs = new FileOutputStream(saveFile);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
		} finally {
			if (fs != null) {
				fs.flush();
				fs.close();
			}
			if (inStream != null) {
				inStream.close();
			}
		}

	}

	private void extractFile(String fileName, String outputDirName) throws FileNotFoundException, IOException,
	      ArchiveException {
		File inputFile = new File(fileName);
		File outputDir = new File(outputDirName);
		File result = null;
		if (fileName.endsWith(".gz")) {
			result = FileExtractUtils.unGzip(inputFile, outputDir);
		} else if (fileName.endsWith(".bz2")) {
			result = FileExtractUtils.unBzip(inputFile, outputDir);
		} else if (fileName.endsWith(".zip")) {
			FileExtractUtils.unZip(inputFile, outputDir);
		} else if (fileName.endsWith(".tar")) {
			FileExtractUtils.unTar(inputFile, outputDir);
		} else{
			return;
		}
		inputFile.delete();
		if (result != null && result.getName().endsWith(".tar")) {
			FileExtractUtils.unTar(result, outputDir);
		}
		if (result != null) {
			result.delete();
		}
	}

	private void writeLogToFile(BufferedReader reader, BufferedWriter writer, long fileSize, int size)
	      throws IOException {
		if (fileSize > size) {
			reader.skip(fileSize - size);
		}
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.write(line + HTML_LINE_SPLITTER);
		}
	}

	private void writeFileToHdfs(String srcFile, String destFile) throws IOException {
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
		while (num != (-1)) {// 是否读完文件
			hdfsoutput.write(buf, 0, num);// 把文件数据写出网络缓冲区
			hdfsoutput.flush();// 刷新缓冲区把数据写往客户端
			num = input.read(buf);// 继续从文件中读取数据
		}
		input.close();
		hdfsoutput.close();
		fs.close();
	}

	@SuppressWarnings("unused")
   private void readFileFromHdfs(String srcFile, String destFile) throws IOException, FileNotFoundException {
		File file = new File(destFile);
		if (file.exists()) {
			file.delete();
		}
		byte[] buf = new byte[BUFFER_SIZE];
		FileOutputStream fos = new FileOutputStream(file);
		FileSystem fs;
		FSDataInputStream hdfsInput;
		try {
			fs = FileSystem.get(URI.create(srcFile), conf);
			hdfsInput = fs.open(new Path(srcFile));
			int num = hdfsInput.read(buf);
			while (num != (-1)) {// 是否读完文件
				fos.write(buf, 0, num);// 把文件数据写出网络缓冲区
				fos.flush();// 刷新缓冲区把数据写往客户端
				num = hdfsInput.read(buf);// 继续从文件中读取数据
			}
			hdfsInput.close();
			fos.close();
			fs.close();
		} catch (IOException e) {
			if (file.exists()) {
				file.delete();
			}
			throw e;
		}
	}
}
