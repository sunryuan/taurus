/**
 * @author renyuan.sun
 * @version 2011-3-2 
 */

package com.dp.bigdata.taurus.alert;


import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailHelper{
	/**
	* send email
	* @throws MessagingException
	* @throws Exception
	*/
	public static void sendMail(MailInfo mailInfo) throws MessagingException{
		Properties props = new Properties();
		props.put("mail.smtp.host", mailInfo.getHost());
		props.put("mail.smtp.auth", "true");
		if(mailInfo.getHost().indexOf("smtp.gmail.com") >= 0)	//Google SMTP server use 465 or 587 port
		{
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
		}
		Session mailSession = Session.getDefaultInstance(props);
		
		Message message = new MimeMessage(mailSession);
		message.setFrom(new InternetAddress(mailInfo.getFrom()));
		String []receivers = mailInfo.getTo().split(",");
		for(String oneReceiver:receivers){
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(oneReceiver));// add receivers
		}
		message.setSubject(mailInfo.getSubject());	
		String display = mailInfo.getFormat() + ";charset=" + mailInfo.getCharset();
		if(mailInfo.getFormat().equals("")||mailInfo.getCharset().equals(""))
			message.setText(mailInfo.getContent());				//set mail text
		else
			message.setContent(mailInfo.getContent(),display);	// set mail text and charset
		
		message.saveChanges();
		
		Transport transport=mailSession.getTransport("smtp");
		transport.connect(mailInfo.getUser(),mailInfo.getPassword());
		transport.sendMessage(message,message.getAllRecipients());
		transport.close();
	}
}

   
