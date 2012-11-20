package com.dp.bigdata.taurus.agent;

import java.io.IOException;

public class Test {
	public static void main(String []args) {
		ProcessBuilder pb = new ProcessBuilder("sudo", "-u", "renyuan.sun", "-s", "\"sleep 30\"");
		
		Process p =null;
		try {
			p = pb.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.destroy();
	}
}