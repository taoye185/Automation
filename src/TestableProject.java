import java.io.IOException;
import java.util.Arrays;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestableProject {
	/* This class needs further restructure - as it currently stands, it represents both the concept of a "testable project"
	 * (ie. an app or a portal system accessible by browser, or a combination of such) as well as the concept of a
	 * "group of different driver actions" that has Android/Chrome/Firefox actions implemented.
	 * 
	 * As a result, this should be split into two separated classes in the ideal world, but since the current version
	 * satisfies the current requirements (mainly testing one app/browser at a time without external interactions), the expected
	 * restructure is on hold.
	 * 
	 * One major drawbacks for the delayed restructure is there are a lot of similar (and possibly redundant) code implementing the
	 * same behavior in the MWDriver class and MWAndroidDriver class, due to it is not determined which action will be performed at 
	 * which driver level yet
	 * 
	 * 
	 */
	
	private MWLogger log;
	private DesiredCapabilities cap;	//configurations read in from the CSV files
	private DesiredCapabilities driverList;
	

	public TestableProject (DesiredCapabilities capabilities, MWLogger logs) throws IOException {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 * */	
		cap = capabilities;
		log = logs;
		driverList = new DesiredCapabilities();
}

	public void addDriverToList (String driverName, MWDriver driver) {
		driverList.setCapability(driverName, driver);
	}
	

	public MWDriver getDriver (String driverName) {
		return (MWDriver) driverList.getCapability(driverName);
	}


	
	public void testScenarioConstructor (String[] parameters) throws InterruptedException, IOException {
/* Pre: parameters[3] is a string that can be parsed into a non-negative integer
 * 		parameters[1] is one of "Android", "Chrome" or "Firefox"
 * 		parameters[2] corresponds to one of the methods defined below (case sensitive)
 * Post: a method corresponding to the methodName variable is called and executed. 
 * 		Data validation would be needed for this method which is not implemented yet.
 * 
 * To be implemented: 
 * 1. a method to parse this method and generate a HTML page allowing testers to select from
 * the available methods/parameters and read its descriptions instead of having to rely on memories about what
 * to write on the CSV script file - the list of methods needs to be softcoded and parsed and updated as requested.
 * 2. a class to allow the user to submit the above mentioned HTML form and generate the CSV file as needed.
 * 3. In order to achieve these, does it make sense to add an "availableMethod" class and an "availableMethodList" class? need to think it through
 */
		int waitSec = Integer.parseInt(parameters[3]);
		String dName = parameters[1];
		log.logConsole("The method \"" +parameters[2] + "("  + parameters[5]+ ")\" are called");
		
			switch (parameters[2]) {	//these methods has been implemented under MWDriver class and should work for all drivers defined
			case "clickButton": {this.getDriver(dName).clickButton(parameters[4]);break;}	
			case "inputText": {this.getDriver(dName).inputText(parameters[4], parameters[5]);break;}	
			case "launch": {this.getDriver(dName).launch();break;}
			case "logComment": {this.getDriver(dName).logComment(parameters[4]); break;}
			case "scrollToBottom": {this.getDriver(dName).scrollToBottom();break;} //need refinement
			case "scrollToTop": {this.getDriver(dName).scrollToTop();break;} //need refinement
			case "scrollDown": {this.getDriver(dName).scrollDown();break;} //need refinement
			case "scrollUp": {this.getDriver(dName).scrollUp();break;} //need refinement
			case "setBooleanValue": {this.getDriver(dName).setBooleanValue(parameters[4], parameters[5]); break;}
			case "test": {this.getDriver(dName).test(parameters[7], Arrays.copyOfRange(parameters, 8, parameters.length));break;}
			case "wait": {this.getDriver(dName).wait(waitSec); break;}
			case "waitUntilPresent": {this.getDriver(dName).waitUntilElement (waitSec, parameters[4]);break;}
			case "waitUntilPage": {this.getDriver(dName).waitUntilPage(waitSec, parameters[4]); break;}
			case "checkPageOverlap": {this.getDriver(dName).checkPageOverlap(waitSec, parameters[4]);break;}
			case "clearNumPad": {this.getDriver(dName).clearNumPad(parameters[4]); break;}						
			case "enterNumPad": {this.getDriver(dName).enterNumPad(parameters[4]); break;}
			case "enterNumPadOK": {this.getDriver(dName).enterNumPadOK(parameters[4]);break;}
			case "enterPurchaseAmount": {this.getDriver(dName).enterPurchaseAmount(parameters[4]); break;}
			case "login": {this.getDriver(dName).login(waitSec); break;}
			case "merchantPassword": {this.getDriver(dName).merchantPassword(parameters[4]);break;}
			case "merchantSignin": {this.getDriver(dName).merchantSignin(parameters[4]); break;}
			case "multiplePurchase": {this.getDriver(dName).multiplePurchase(waitSec, parameters[4],parameters[5],parameters[6],parameters[7],parameters[8],parameters[9] ); break;}
						//notice for multiplePurchase, variable waitSec represents number of purchases to be made, not seconds to wait
			case "pickDate": {this.getDriver(dName).pickDate(parameters[4],parameters[5],parameters[6]);break;}
			case "reachPageByProcess": {this.getDriver(dName).reachPageByProcess(waitSec, parameters[4], parameters[5]);break;}
			case "reachPage": {this.getDriver(dName).reachPage("GP", parameters[4]);break;}
			case "showSideMenu": {this.getDriver(dName).showSideMenu(waitSec);break;}
			case "sign": {this.getDriver(dName).sign();break;}
			case "singlePurchase": {this.getDriver(dName).singlePurchase(waitSec,parameters[4],parameters[5],parameters[6],parameters[7] ); break;}
			case "singlePurchaseUntil": {this.getDriver(dName).singlePurchaseUntil(waitSec,parameters[4],parameters[5],parameters[6],parameters[7], parameters[8]);break;}
			default: log.logConsole(parameters[2] + " not found. No such method exists.");
					}
	}
	


	
	
	
/********************"Testable Project" related methods*******************************/	
	
//	public Object chooseDriver (String driverName) throws IOException {
		/* Pre: driverName is a valid, case sensitive string that corresponds to one of the supported drivers
		 * 		The corresponding driver has already been initialized - this really should be more carefully coded with error handles/checks later
		 * Post: the corresponding driver is returned. note that a null is returned if driverName is incorrect.
		 */
/*		switch(driverName) {
		case "Android": {
			return and; 
		}
		case "Chrome": {
			return chd; 
		}
		case "Firefox": {
			return ffd; 
		}
		default: {
			log.logConsole("Specified driver not found");
			return null;	//this is asking for trouble in the future as the other methods dosn't catch null appropriately. don't have time to make the appropriate error handling right now though.
		}
		}
	}
	*/
	public DesiredCapabilities chooseCap (String driverName) throws IOException {
		switch(driverName) {
		case "Android": {
			return (DesiredCapabilities) (cap.getCapability("AndroidElement")); 
		}
		case "Chrome": {
			return (DesiredCapabilities) (cap.getCapability("ChromeElement"));  
		}
		case "Firefox": {
			return (DesiredCapabilities) (cap.getCapability("FirefoxElement"));  
		}
		default: {
			log.logConsole("Specified driver not found");
			return null; //this is asking for trouble in the future as the other methods dosn't catch null appropriately. don't have time to make the appropriate error handling right now though.
		}
		}
	}
	
	public DesiredCapabilities choosePage (String driverName) throws IOException {
		switch(driverName) {
		case "Android": {
			return (DesiredCapabilities) (cap.getCapability("AndroidPage")); 
		}
		case "Chrome": {
			return (DesiredCapabilities) (cap.getCapability("ChromePage"));  
		}
		case "Firefox": {
			return (DesiredCapabilities) (cap.getCapability("FirefoxPage"));  
		}
		default: {
			log.logConsole("Specified page list not found");
			return null; //this is asking for trouble in the future as the other methods dosn't catch null appropriately. don't have time to make the appropriate error handling right now though.
		}
		}
	}
	
	
}
	

