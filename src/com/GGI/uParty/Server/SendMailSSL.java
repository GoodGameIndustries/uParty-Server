package com.GGI.uParty.Server;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;


public class SendMailSSL {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static final String SMTP_AUTH_USER = "goodgameindustries@gmail.com";
    private static final String SMTP_AUTH_PWD  = "ewd070412";

   

    public void send(String email, String msg) throws Exception{
        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");
        // props.put("mail.smtps.quitwait", "false");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(false);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("uParty Confirmation");
        //message.setContent("This is a test", "text/plain");
        message.setContent(msg, "text/html;");

        message.addRecipient(Message.RecipientType.TO,
             new InternetAddress(email));

        transport.connect
          (SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);

        transport.sendMessage(message,
            message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }
}