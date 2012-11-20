//package com.dp.bigdata.taurus.agent.exec;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import org.apache.commons.exec.ExecuteStreamHandler;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//public class DefaultExecuteStreamHandler implements ExecuteStreamHandler {
//	
//
//	private static final Log s_logger = LogFactory.getLog(DefaultExecuteStreamHandler.class);
//
//	private final static int BUF_SIZE = 4096;
//	private final static int MAX_OUT_SIZE = 256 * 1024 * 1;   //256K
//	
//	private int stdOutMaxSize = MAX_OUT_SIZE;
//	private int stdErrMaxSize = MAX_OUT_SIZE;
//	
//	DefaultExecuteStreamHandler(){}
//	
//	DefaultExecuteStreamHandler(int stdOutMaxSize, int stdErrMaxSize){
//		this.stdOutMaxSize = stdOutMaxSize;
//		this.stdErrMaxSize = stdErrMaxSize;
//	}
//
//	private ByteArrayOutputStream stdOut = new ByteArrayOutputStream(BUF_SIZE);
//	private ByteArrayOutputStream stdErr = new ByteArrayOutputStream(BUF_SIZE);
//
//	@Override
//	public void setProcessErrorStream(InputStream is) throws IOException {
//		setStream(stdErr, is, "stdErr", stdErrMaxSize);
//	}
//
//	@Override
//	public void setProcessInputStream(OutputStream os) throws IOException {}
//
//	@Override
//	public void setProcessOutputStream(InputStream is) throws IOException {
//		setStream(stdOut, is, "stdOut", stdOutMaxSize);
//	}
//
//	@Override
//	public void start() throws IOException {}
//	
//	private void setStream(ByteArrayOutputStream rs, InputStream is, String streamName, int maxSize) throws IOException{
//		byte[] buf = new byte[BUF_SIZE];
//		int cnt;
//		while ((cnt = is.read(buf)) > 0) {
//			if (rs.size() < maxSize) {
//				rs.write(buf, 0, cnt);
//				if (s_logger.isDebugEnabled()) {
//					s_logger.debug(new String(buf, 0, cnt));
//				}
//			} else {
//				s_logger.warn(streamName + " is too large, will discard");
//			}
//		}
//	}
//
//	@Override
//	public void stop() {}
//
//	public byte[] getStdOut() {
//		return stdOut.toByteArray();
//	}
//
//	public byte[] getStdErr() {
//		return stdErr.toByteArray();
//	}
//
//}
