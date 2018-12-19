import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.logging.Level;
import java.util.logging.Logger;



public class AutomationMain{
	public static MWAndroidDriver androidMobileDriver;
	public static MWDriver browserDriver;
	static String testManagementFile = ".\\src\\Testcase configuration\\Test Management.csv";
	static String groupFile = "";
	//This configuration file contains all the test cases to be run during the test - QA engineers should update this file
	//any time test cases changes
	static String appFile = "";
	//This configuration file contains the app configurations and apk locations - QA engineers should update this file
	//any time a different app is to be tested.
	static String androidElementFile = "";
	//This configuration file contains the URI for all the relevant WebElement that the mobile app contains - QA engineers
	//should update this file whenever the app is modified in a way such that one or more WebElement has been created or changed
	//to a new URI.
	static String driverFile = "";
	static String chromeElementFile = "";
	static String deviceFile = "";
	static MWLogger log;

	static DesiredCapabilities capabilities = new DesiredCapabilities();
	
	@BeforeTest
	public void beforeTest( ) throws IOException {
		customizedCSVReader cr = new customizedCSVReader();	
		try { 	
			DesiredCapabilities config = cr.readPairing(testManagementFile);	//read Test Management Configuration File
			appFile = cr.returnPairingValue("appFile");			//retrieve the app file from the Test Management Configuration File
			deviceFile = cr.returnPairingValue("deviceFile"); 	//retrieve the device file from the Test Management Configuration File
			groupFile = cr.returnPairingValue("groupFile");		//retrieve the test gropu file from the Test Management Configuration File
			driverFile = cr.returnPairingValue("driverFile");	//retrieve the driver file from the Test Management Configuration File
			capabilities.merge(cr.readRowPairing(deviceFile));		//read device configuration
			capabilities.merge(cr.readPairing(appFile));			//read app configuration
			capabilities.merge(cr.readPairing(driverFile));			//read driver configuration
    		capabilities.setCapability("noReset", true); //non String type capabilities, could be put into configuration file with a bit modification on code, but I will leave it as-is for now
    		capabilities.setCapability("newCommandTimeout", 2000);
    		log = new MWLogger (capabilities);
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    } 		
	}

	@AfterTest
	public void afterTest( ) throws MalformedURLException{
		try {
//		sendPDFReportByGMail("taooyee@gmail.com", "Thankyou1", "ytao@mobeewave.com", "test Report", "");
		}
		catch (Exception e) {
			e.printStackTrace(); 
		}
//		There is an excellent tutorial for emailing testing result at https://www.guru99.com/pdf-emails-and-screenshot-of-test-reports-in-selenium.html,
//		The current functionality can be further expanded by following instructions from there.
	}

	
	@Test
	public static void appTesting() throws InterruptedException, IOException{
		// read from the groupFile configuration file and construct the respective test case methods
		customizedCSVReader cr = new customizedCSVReader(groupFile);
		DesiredCapabilities groupCase = cr.readPairing(groupFile);
		cr.readAndExecuteTestGroup(capabilities, log);
	}
	
	
	
	
	

	
}