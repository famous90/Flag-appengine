package com.flag.engine.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.flag.engine.models.FeedbackMessage;

public class MailUtils {

	public static void sendFeedbackMail(FeedbackMessage feedbackMessage) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(feedbackMessage.getEmail(), "DalShop User"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("team.tankers@gmail.com", "Tankers"));
			msg.setSubject("DalShop feedback message");
			msg.setText(feedbackMessage.getMessage());
			Transport.send(msg);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
