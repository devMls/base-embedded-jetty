/**
 To change this license header, choose License Headers in Project Properties.
 To change this template file, choose Tools | Templates
 and open the template in the editor.
 */
package util;

/**

 @author mlarr
 Example
 https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp#send-email-in-java-smtp-with-tls-authentication

 compatible with javalite async
 */
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Date;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import static org.javalite.app_config.AppConfig.p;
import org.javalite.async.Command;

public class MailClient extends Command {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MailClient.class);

    private static final String host = p("SMTPServerHost");
    private static final String port = p("SMTPServerPort");
    private static final boolean auth = true;
    private static final boolean ttls = true;
    private static final String fromEmail = p("SMTPServerEmailFrom");
    private static final String password = p("SMTPServerEmailPassword");

    private String toEmail;
    private String subject;
    private String body;

    public void MailClient() {
    }

    public void MailClient(String toEmail, String subject, String body) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
    }

    public static void sendMail(String toEmail, String subject, String body) {

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", host); //SMTP Host
        props.put("mail.smtp.port", port); //TLS Port
        props.put("mail.smtp.auth", auth); //enable authentication
        props.put("mail.smtp.starttls.enable", ttls); //enable STARTTLS

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        try {
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail));

            msg.setReplyTo(InternetAddress.parse(fromEmail, false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            log.info("Message is ready");

            Transport.send(msg);

            log.info("EMail Sent Successfully!!");

        } catch (Exception e) {
            log.error("error sending email", e);
        }
    }

    public void execute() {
        sendMail(toEmail, subject, body);
    }

}
