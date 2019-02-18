import java.io.IOException;
import java.net.MalformedURLException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;



public class AutomationMain{
//	public static MWAndroidDriver androidMobileDriver;
//	public static MWDriver browserDriver;
	static String testManagementFile = ".\\src\\Testcase configuration\\Test Management.csv";
	static String groupFile = "";
	//This configuration file contains all the test cases to be run during the test - QA engineers should update this file
	//any time test cases changes
	static String appFile = "";
	//This configuration file contains the app configurations and apk locations - QA engineers should update this file
	//any time a different app is to be tested.
//	static String androidElementFile = "";
	//This configuration file contains the URI for all the relevant WebElement that the mobile app contains - QA engineers
	//should update this file whenever the app is modified in a way such that one or more WebElement has been created or changed
	//to a new URI.
	static String driverFile = "";
//	static String chromeElementFile = "";
	static String deviceFile = "";
	static MWLogger log; 

	static DesiredCapabilities configurationList = new DesiredCapabilities();
	static DesiredCapabilities driverList = new DesiredCapabilities();
	
	@BeforeTest
	public void beforeTest( ) throws IOException {
		customizedCSVReader cr = new customizedCSVReader();	
		try { 	
			cr.readPairing(testManagementFile);	//read Test Management Configuration File
			appFile = cr.returnPairingValue("appFile");			//retrieve the app file from the Test Management Configuration File
			deviceFile = cr.returnPairingValue("deviceFile"); 	//retrieve the device file from the Test Management Configuration File
			groupFile = cr.returnPairingValue("groupFile");		//retrieve the test gropu file from the Test Management Configuration File
			driverFile = cr.returnPairingValue("driverFile");	//retrieve the driver file from the Test Management Configuration File
			configurationList.merge(cr.readRowPairing(deviceFile));		//read device configuration
			configurationList.merge(cr.readPairing(appFile));			//read app configuration
			configurationList.merge(cr.readPairing(driverFile));			//read driver configuration
			configurationList.setCapability("noReset", true); //non String type capabilities, could be put into configuration file with a bit modification on code, but I will leave it as-is for now
			configurationList.setCapability("newCommandTimeout", 2000);
    		log = new MWLogger (configurationList);
    		driverList = cr.readAndCreateDriver(driverFile, configurationList, log);	//read number of different drivers exist for the tests
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
		cr.readAndExecuteTestGroup(configurationList, driverList, log);
		cr.close(driverList, log);
	}
	
}