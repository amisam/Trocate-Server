// File Name SendHTMLEmail.java

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

/**
 * same case as the GoogleMail class, was used to send emails but got server errors
 * so couldn't connect or send emails
 */
public class SendEmail {

	private static final String username = "binserverproject";
	private static final String password = "binadmin100!";

	private static String port = "465";

	public static String textMessage(String mSubject, String mText) {

		String sub = mSubject;
		if(sub == null) sub = "none";

		// Recipient's email ID needs to be mentioned.
		String to = username+"@gmail.com";

		// Sender's email ID needs to be mentioned
		String from = username+"@gmail.com";

		// Assuming you are sending email from localhost
		//String host = "localhost";
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(sub);

			// Now set the actual message
			message.setText(mText);

			// Send message
			System.out.println("transport message");
			Transport.send(message);
			System.out.println("Sent message successfully....");
		}catch (MessagingException mex) {
			mex.printStackTrace();
			return "send report failed";
		}
		return "Report sent";
	}

	public static void main (String[] args){
		//System.out.println("here");
		//System.out.println(SendEmail.textMessage("test", "This actually worked"));
		System.out.println("try send");
		try{Send("test", "Working!!!!");
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("broke");}

		System.out.println();

		System.out.println("try text message");
		try{textMessage("TextM", "Working");
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("broke");}

		System.out.println("finished");
	}



	public static void Send	(String title, String message)throws AddressException, MessagingException
	{
        //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.port", port);
        props.setProperty("mail.smtps.auth", "true");
        props.setProperty("mail.smtp.user", username);
        props.setProperty("mail.smtp.password", password);

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username + "@gmail.com", false));

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
