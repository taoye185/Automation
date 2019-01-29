import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
/*
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
*/
import org.openqa.selenium.chrome.ChromeDriver;
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
	static BufferedWriter logWriter;
	static BufferedWriter reportWriter;
	private String reportHeader="";
	private String reportFooter="";
	private String reportContent="";
	private String reportName;
	
	//For Default: logLevel 1 = debug; 2 = console tracing; 4 = formal report
	
	public MWLogger (DesiredCapabilities cap) throws IOException {
		if (cap.asMap().containsKey("logPassColor")) 		{	logPassColor = (String) cap.getCapability("logPassColor");}
		if (cap.asMap().containsKey("logFailColor")) 		{	logFailColor = (String) cap.getCapability("logFailColor");}
		if (cap.asMap().containsKey("logEmphasisColor"))	{	logEmphasisColor = (String) cap.getCapability("logEmphasisColor"); }
		if (cap.asMap().containsKey("logInfoColor")) 		{	logInfoColor = (String) cap.getCapability("logInfoColor"); }
		if (cap.asMap().containsKey("logCaseColor")) 		{	logCaseColor = (String) cap.getCapability("logCaseColor"); }
		
		String fileName = ".\\logs\\Automation Log " + LocalDateTime.now().getYear()+LocalDateTime.now().getMonthValue()+LocalDateTime.now().getDayOfMonth()+LocalDateTime.now().getHour()+LocalDateTime.now().getMinute()+LocalDateTime.now().getSecond() + ".log";
		File statText = new File(fileName);
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        logWriter = new BufferedWriter(osw);
		logWriter.write("---------Start of logs---------");
		logWriter.newLine();
		logWriter.flush();


	}
	
	public MWLogger () throws IOException {
		//use default color
		String fileName = ".\\logs\\Automation Log " + LocalDateTime.now().getYear()+LocalDateTime.now().getMonthValue()+LocalDateTime.now().getDayOfMonth()+LocalDateTime.now().getHour()+LocalDateTime.now().getMinute()+LocalDateTime.now().getSecond() + ".log";
		File statText = new File(fileName);
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        logWriter = new BufferedWriter(osw);
		logWriter.write("---------Start of logs---------");
		logWriter.newLine();
		logWriter.flush();
	}
	
	public void initiateReport (DesiredCapabilities cap) throws FileNotFoundException {
		reportName = ".\\reports\\Test Report " + LocalDateTime.now().getYear()+LocalDateTime.now().getMonthValue()+LocalDateTime.now().getDayOfMonth()+LocalDateTime.now().getHour()+LocalDateTime.now().getMinute()+LocalDateTime.now().getSecond() + ".html";
		File reportText = new File(reportName);
        FileOutputStream ris = new FileOutputStream(reportText);
        OutputStreamWriter rosw = new OutputStreamWriter(ris); 
        reportWriter = new BufferedWriter(rosw);
	}
	
	public void publishReport () throws IOException {
		String headerPath = ".\\reports\\default header.txt";
		String footerPath = ".\\reports\\default footer.txt";
		reportHeader = new String (Files.readAllBytes(Paths.get(headerPath)));
		this.logFile("reportHeader is: "+ reportHeader);
		reportFooter = new String (Files.readAllBytes(Paths.get(footerPath)));		
		this.logFile("reportFooter is: "+ reportFooter);	
		this.logFile("reportContent is: "+ reportContent);
		
		reportWriter.write(reportHeader+"<br>");
		reportWriter.newLine();
		reportWriter.write(reportContent+"<br>");
		reportWriter.newLine();
		reportWriter.write(reportFooter);
		reportWriter.flush();
		
		ChromeDriver chd = new ChromeDriver();
		System.out.println(reportName);
		chd.get(System.getProperty("user.dir")+reportName.substring(1));
		
	}
	
	public void log(int level, String text) {
//		if (level >= logLevelConsole) {
	
//			this.logComment(text);
//		}
//		if (level >= logLevelReport) {
			Reporter.log(text);
			reportContent = reportContent + text + "\r\n";
//		}
	}

	public void logFile (String comment) throws IOException {
		String content = LocalTime.now()+":	" + comment;
		logWriter.write(content);
		logWriter.newLine();
		logWriter.flush();
	}
	
	public void logConsole (String comment) throws IOException {
		System.out.println(comment);
		this.logFile(comment);
	}
	
	/***********Report related logs *****************************/
	public void report(String text) throws IOException {
			Reporter.log(text);
			reportContent = reportContent + text + "\r\n";
			System.out.println(text);
			this.logFile(text);
			reportContent = reportContent +"<br>";
	}
	
	public void logColorText (String color, String text) throws IOException {
		this.report("<font color='"+color+"'>"+text +"</font>");
	}
	
	public void logComment (String comment ) throws IOException {
		this.logColorText(logInfoColor, comment);
	}
	
	public void logFailure (String text) throws IOException {
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
					Reporter.log(fieldName + " is equal to " + expectedValue + ", <font color='"+logPassColor+"'>test passed</font>.");
					reportContent += fieldName + " is equal to " + expectedValue + ", <font color='"+logPassColor+"'>test passed</font>.<br>\r\n" ;
					return testResult;
				}
				else {
					testResult = false;
					Reporter.log(fieldName + " is expected to be <font color='"+logEmphasisColor+"'>"+ expectedValue + "</font>, but is actually equal to: <font color='"+logEmphasisColor+"'>" + actualValue+ "</font>, <font color='red'>test failed</font>.");
					reportContent += fieldName + " is expected to be <font color='"+logEmphasisColor+"'>"+ expectedValue + "</font>, but is actually equal to: <font color='"+logEmphasisColor+"'>" + actualValue+ "</font>, <font color='red'>test failed</font>.<br>\r\n";
					return testResult;
				}
			}
			catch (AssertionError e) {
			// assertion failed
				testResult = false;
				Reporter.log(fieldName + " is expected to be <font color='"+logEmphasisColor+"'>"+ expectedValue + "</font>, but is actually equal to: <font color='"+logEmphasisColor+"'>" + actualValue+ "</font>, <font color='red'>test failed</font>.");
				reportContent += fieldName + " is expected to be <font color='"+logEmphasisColor+"'>"+ expectedValue + "</font>, but is actually equal to: <font color='"+logEmphasisColor+"'>" + actualValue+ "</font>, <font color='red'>test failed</font>.<br>\r\n";

				return testResult; 
			}
		}

	/**********Auxiliary functions **************/
	public String logArray (String[] content) {
		String outputArray = "[";
		for (int i = 0; i<content.length; i++) {
			outputArray = outputArray + "\""+content[i]+"\"";
			if (i<content.length-1) {
				outputArray = outputArray + ",";
			}
		}
		outputArray = outputArray + "]";
		return outputArray;
	}

	
	
	/*
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
    
    */
    
    public void close() throws IOException {
    	logWriter.close();
    	reportWriter.close();
    }

	public String getLogCaseColor() {
		return logCaseColor;
	}
}
