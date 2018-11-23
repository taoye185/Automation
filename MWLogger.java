import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.Reporter;

public class MWLogger {

	static private String logPassColor ="green";
	static private String logFailColor ="red";
	static private String logEmphasisColor ="brown";
	static private String logInfoColor ="grey";
	static private String logCaseColor ="blue";
	static private int logLevelConsole = 2;
	static private int logLevelReport = 4;
	//For Default: logLevel 1 = debug; 2 = console tracing; 4 = formal report
	
	public MWLogger (DesiredCapabilities cap) {
		logPassColor = (String) cap.getCapability("logPassColor");
		logFailColor = (String) cap.getCapability("logFailColor");
		logEmphasisColor = (String) cap.getCapability("logEmphasisColor");
		logInfoColor = (String) cap.getCapability("logInfoColor");
		logCaseColor = (String) cap.getCapability("logCaseColor");
	}
	
	public MWLogger () {
		//use default color
	}
	
	public void log(int level, String text) {
		if (level >= logLevelConsole) {
			this.logComment(text);
		}
		if (level >= logLevelReport) {
			Reporter.log(text);
		}
	}
	
	public void logColorText (String color, String text) {
		Reporter.log("<font color='"+color+"'>"+text +"</font>");
	}
	
	public void logComment (String comment ) {
		this.logColorText(logInfoColor, comment);
	}
	
	public void logConsole (String comment) {
		System.out.println(comment);
	}
	
	public void logFailure (String text) {
		this.logColorText(logFailColor, text);
	}
	
	public boolean logTestResult (String fieldName, String actualValue, String expectedValue) {
		/* Pre: 	none
		* Post: 	The method logs the test result to the HTML test result page.
		*/		
			boolean testResult = false;
			try {

				Assert.assertEquals(actualValue, expectedValue);
				if (actualValue.equals(expectedValue)) {
					testResult = true;
					Reporter.log(fieldName + " is equal to " + expectedValue + ", <font color='green'>test passed</font>.");
					return testResult;
				}
				else {
					testResult = false;
					Reporter.log(fieldName + " is expected to be <font color='brown'>"+ expectedValue + "</font>, but is actually equal to: <font color='brown'>" + actualValue+ "</font>, <font color='red'>test failed</font>.");
					return testResult;
				}
			}
			catch (AssertionError e) {
			// assertion failed
				testResult = false;
				Reporter.log(fieldName + " is expected to be <font color='brown'>"+ expectedValue + "</font>, but is actually equal to: <font color='brown'>" + actualValue+ "</font>, <font color='red'>test failed</font>.");
				return testResult; 
			}
		}
	
	
	
    private static void sendPDFReportByGMail(String from, String pass, String to, String subject, String body) {
    //The email functionality to send out reports through emails after testings are done. 

    	Properties props = System.getProperties();

    	String host = "smtp.gmail.com";
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", host);
    	props.put("mail.smtp.user", from);
    	props.put("mail.smtp.password", pass);
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.auth", "true");

    	Session session = Session.getDefaultInstance(props);
    	MimeMessage message = new MimeMessage(session);

    	try {

    		//Set from address
    		message.setFrom(new InternetAddress(from));
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    		//Set subject
    		message.setSubject(subject);
    		message.setText(body);
    		BodyPart objMessageBodyPart = new MimeBodyPart();
    		objMessageBodyPart.setText("Greetings from the QA team! This is an auto-generated email for the automated test cases just completed. Please Find The Attached Report File!");
    		Multipart multipart = new MimeMultipart();
    		multipart.addBodyPart(objMessageBodyPart);
    		objMessageBodyPart = new MimeBodyPart();
    		//Set path to the pdf report file
    		//String filename = System.getProperty("user.dir")+"\\Default test.pdf";
    		//Create data source to attach the file in mail
    		DataSource source = new FileDataSource("C:\\Users\\YeTao\\eclipse-workspace\\automationTemplates\\test-output\\emailable-report.html");
    		objMessageBodyPart.setDataHandler(new DataHandler(source));
    		objMessageBodyPart.setFileName("Test Report.html");
    		multipart.addBodyPart(objMessageBodyPart);
    		message.setContent(multipart);
    		Transport transport = session.getTransport("smtp");
    		transport.connect(host, from, pass);
    		transport.sendMessage(message, message.getAllRecipients());
    		transport.close();
    	}

    	catch (AddressException ae) {
    		ae.printStackTrace();
    	}

    	catch (MessagingException me) {
    		me.printStackTrace();
    	}

    }
}
