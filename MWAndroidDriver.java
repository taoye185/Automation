import static org.junit.Assert.assertNotEquals;
import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.testng.Assert;
import org.testng.Reporter;

import com.opencsv.CSVReader;


import bsh.This;
import io.appium.java_client.android.*;

import java.util.logging.Level;
import java.util.logging.Logger;


public class MWAndroidDriver<T extends WebElement> extends AndroidDriver<T> {
/* This class extends the basic android driver and adds the following variables/methods to the 
 * automation testing specifically designed for mobeewave testing. 
 * 
 * pElement: a mapping between all the interesting WebElements and their XPath. Defined by the 
 * configuration file that's specific to each apk. Note the configuration file is currently partially complete
 * and needs to be maintained and expanded in the future.
 * 
 * pageName: a mapping between all the mobilePage identifiers and their actual mobilePage object. This is used
 * extensively to determine which page the app is currently displaying and what elements should be available
 * on the page.
 * 
 * public MWAndroidDriver (URL, DesiredCapabilities, String): a constructor method to read the configuration 
 * files for the above mentioned pElement mapping
 * 
 * testScenarioConstructor: This method will dynamically decide which specific method is going to be revoked 
 * depending on the parameters passed in.
 * 
 * clickButton: This method clicks a URI specified by the parameter - this is one of the basic method that
 * will be the basis of other more complex methods
 * 
 * inputText: This method will input a String into a text field specified by a URI - this is one of the basic method that
 * will be the basis of other more complex methods
 * 
 * clearText: This method will clear all contents from a text field specified by a URI - this is one of the basic method that
 * will be the basis of other more complex methods
 * 
 * setBooleanValue: This method will set a checkbox/switch or any clickable element that has an on/off boolean 
 * value to the desired state - this is one of the basic method that will be the basis of other more complex methods
 * 
 * test: This method is used to compare expected values and actual values to determine whether a test case is passed or not
 * 
 * logTestResult: This auxiliary method is used to log testing messages to the final testing report
 * 
 * 
 * merchantSignIn: This method detects whether the Merchant Signin page is active, and input the merchant ID
 * parameter if positive.
 * 
 * merchantPassword: This method detects whether the Merchant Password page is active, and input the password
 * parameter if positive.
 * 
 * enterNumPad: This method enters a string of digits into the numPad present.
 * 
 * enterEmptyPIN: This method detects whether the Enter PIN page is active, and click continue without enter a
 * PIN if positive.
 * 
 * clickNext: This method click next on the purchase description window - to be expanded into input descriptions 
 * later
 * 
 * showSideMenu: This method clicks the sidemenu button to open up the side menu - can be easily replaced with the
 * basic clickButton method, but since this would be used very frequently, decided it's worthwhile to have a method
 * just for this
 * 
 * findCurrentPage: This method will read through a list of unique page identifiers to determine which page is currently
 * open in the app. This is essential when facing scenarios that could lead to multiple possible outcomes.
 * 
 * waitUntilPage: This auxiliary method will check whether the app has loaded a specific page. If the target page has been
 * loaded or the timeout limit is reached, the method will exit, allowing the next step in the test to be taken
 * 
 * launch: A complex method design to automatically react to all different scenarios when a CBA/PEP app is launched,
 * from possible merchant sign-in, to possible provision, to possible user selection, etc, all the way until the user
 * is asked to enter their PIN for login. - This method is largely functional but not yet completed for a number of special
 * cases.
 * 
 * login: This method will automatically perform user login with the PIN supplied from configuration file
 * 
 * multiplePurchase: This method is a complex method design to make multiple purchases regardless of whether the purchase is
 * a success of a failure. It is assumed that the user is already logged in. - This method is largely functional but not yet 
 * completed for a number of special cases.
 * 
 * enterPurchaseAmount: This method enters the purchase amount on a numpad.
 * 
 * checkPageOverlap: This is a complex method that will determine whether any element on the page is having boundaries overlapping
 * with each other - not completed yet
 * 
 * isAligned: This is a complex method that will determine whether any element on the page is properly aligned or not
 * - not completed yet
 * 
 * isOverlap: This is an auxiliary method that will help the checkPageOverlap method during testing - not completed yet
 * 
 * coordIsPartiallyContainedIn: This is an auxiliary method that will help the checkPageOverlap method during 
 * testing - not completed yet
 * 
 * areaWithTwoRectangle: This is an auxiliary method that will help the checkPageOverlap method during 
 * testing - not completed yet
 */
	DesiredCapabilities cap = new DesiredCapabilities();
	private Map<String, String> pElement = new HashMap<String, String>();
	private Map<String, mobilePage> pageName = new HashMap<String, mobilePage>();
	private String elementName;
	private String elementPath;
//	private DesiredCapabilities[] pages = new DesiredCapabilities[];
	static private String logPassColor ="green";
	static private String logFailColor ="red";
	static private String logEmphasisColor ="brown";
	static private String logInfoColor ="grey";
	static private String logCaseColor ="blue";
	static private int defaultTimeOutSec = 15;
	MWLogger log = new MWLogger();
	
	
	
	public MWAndroidDriver(URL url, DesiredCapabilities capabilities) throws IOException {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 */
		super(url,capabilities);
		cap = capabilities;
		String elementConfig = (String) capabilities.getCapability("Android");

		customizedCSVReader cr = new customizedCSVReader("");
		DesiredCapabilities[] elementFileContent = cr.readElementFile((String)capabilities.getCapability("Android"));
		cap.setCapability("AndroidElement", elementFileContent[0]);
		cap.setCapability("AndroidPage", elementFileContent[1]);
		}
	
	public void testScenarioConstructor (String[] parameters) throws InterruptedException {
/* Pre: parameters[0] is a string that can be parsed into a non-negative integer
 * Post: a method corresponding to the methodName variable is called and executed. 
 * 		Data validation would be needed for this method which is not implemented yet.
 */
		int waitSec = Integer.parseInt(parameters[3]);
		String driverName = parameters[1];
		log.logConsole("The method \"" +parameters[0] + "("  + ")\" are called");
		switch (parameters[0]) {
			case "checkPageOverlap": {this.checkPageOverlap(waitSec, parameters[2]);break;}
			case "clickButton": {this.clickButton(waitSec, parameters[2]);break;}		
			case "clickNext": {this.clickNext(waitSec);break;}		
			case "enterEmptyPIN": {this.enterEmptyPIN(waitSec);break;}
			case "enterNumPad": {this.enterNumPad(waitSec, parameters[2]); break;}
			case "enterNumPadOK": {this.enterNumPadOK(waitSec, parameters[2]);break;}
			case "enterPurchaseAmount": {this.enterPurchaseAmount(waitSec, parameters[2]); break;}
			case "inputText": {this.inputText(waitSec, parameters[2], parameters[3]);break;}
			case "launch": {this.launch(waitSec);break;}
			case "logComment": {this.logComment(waitSec, parameters[2]); break;}
			case "login": {this.login(waitSec); break;}
			case "merchantPassword": {this.merchantPassword(waitSec, parameters[2]);break;}
			case "merchantSignin": {this.merchantSignin(waitSec, parameters[2]); break;}
			case "multiplePurchase": {this.multiplePurchase(waitSec, parameters[2],parameters[3],parameters[4],parameters[5],parameters[6],parameters[7] ); break;}
			//notice for multiplePurchase, variable waitSec represents number of purchases to be made, not seconds to wait
			case "reachPageByProcess": {this.reachPageByProcess(waitSec, parameters[2], parameters[3]);break;}
			case "setBooleanValue": {this.setBooleanValue(waitSec, parameters[2],  parameters[3]); break;}
			case "showSideMenu": {this.showSideMenu(waitSec);break;}
			case "singlePurchase": {this.singlePurchase(waitSec,parameters[2],parameters[3],parameters[4],parameters[5] ); break;}
			case "singlePurchaseUntil": {this.singlePurchaseUntil(waitSec,parameters[2],parameters[3],parameters[4],parameters[5], parameters[6]);break;}
//			case "test": {this.test(waitSec, parameters[5], parameters[6], parameters[7]);break;}
			case "waitUntilPage": {this.waitUntilPage(waitSec, parameters[2]); break;}
			default: log.logConsole(parameters[0] + " not found. No such method exists.");
		}
	}
	

	
	//The next 3 methods are the basic unit functions of this class (click/enter text/clear text) that forms the basis all other 
	//more complicated functions. More (for example, scrool up/down) basic functions are to be added as needed	
		public void clickButton (int waitSec, String URI) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
	 * Post: 	The corresponding button is clicked.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
				this.find(URI).click();
			}
			catch (Exception e) {
				log.logConsole("exception caught during button click, failed to find " + URI);
			}
		}
		
		public void inputText (int waitSec, String URI, String input ) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file.
	 * 			input can be empty but shoudn't be null
	 * Post: 	The input is typed into the field.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
			this.find(URI).sendKeys(input);
			}
			catch (Exception e) {
				log.logConsole("exception caught during text input, failed to find " + URI);
			}
		}
		
		public void clearText(int waitSec, String URI) throws InterruptedException {
	/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
	 * Post: 	The text field is cleared.
	 * 			use this method when no validation or boundary cases are expected
	 */			
			Thread.sleep(waitSec * 1000);
			try {
			this.find(URI).clear();
			}
			catch (Exception e) {
				log.logConsole("exception caught during text clearance, failed to find " + URI);
			}
		}
		
		public void setBooleanValue(int waitSec, String URI, String Value) throws InterruptedException {
/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
 * 			URI is an element that can be click and has a boolean value of on/off
 * 			Value is a String containing either "yes" or "no"
 * Post: 	The URI value is set to the value specified in the parameter
 */				
			Thread.sleep(waitSec*1000);
			String expectedValue = "false";
			if (Value.equals("yes")) {
				expectedValue = "true";
			}
			try {
				String currentValue = this.find(URI).getAttribute("checked");
//				System.out.println("the switch is: " + currentValue);
//				System.out.println("expecting: " + expectedValue);
//				System.out.println("they are equal? " + (expectedValue.equals(currentValue)));
				if (!(expectedValue.equals(currentValue)))  {
					this.find(URI).click();
					currentValue = this.find(URI).getAttribute("checked");
//					System.out.println("the switch is: " + currentValue);
//					Reporter.log(URI + " is set to: " + Value);
					this.logColorText(logInfoColor, URI + " is set to: " + Value);
				}
			}
			catch (Exception e) {
				log.logConsole("exception caught during checkbox clicking, failed to find " + URI);
			}
		}
		
		public WebElement find(String URI) {
			return this.findElementByXPath((String) ((pageElement)((Capabilities) cap.getCapability("AndroidElement")).getCapability(URI)).getPath());
		}
		
		
//The next set of functions uses the basic functions above to construct frequently used functions on the page level	
	public void merchantSignin(int waitSec, String ID) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			ID is not empty,
 * 			MerchantSignInID is accurately defined in configuration file,
 * 			MerchantSignInOK is accurately defined in configuration file.
 * Post: 	If the current page is not Merchant Signin page, do nothing,
 * 			otherwise input the merchant id as specified by the parameter "ID".
 */
//		Thread.sleep(waitSec * 1000);	//wait the current page to load
		try {
//			this.findElementByXPath(pElement.get("MerchantSignInID")).sendKeys(ID); 
			this.inputText(waitSec, "MerchantSignInID", ID); //input merchant ID
//			System.out.println("merchant id element found");
//			this.findElementByXPath(pElement.get("MerchantSignInOK")).click(); 
			this.clickButton(0,"MerchantSignInOK" ); // click OK
//			System.out.println("Sign in button clicked");
			}catch(Exception e) {
//			this.logMessage("exception caught, not on Merchant Sign IN page");
	    	//this.logerror(e);
			}
	}
	
	public void merchantPassword(int waitSec, String password) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			password is not empty,
 * 			MerchantPasswordpassword is accurately defined in configuration file,
 * 			MerchantPasswordContinue is accurately defined in configuration file.
 * Post: 	If the current page is not Merchant Password page, do nothing,
 * 			otherwise input the merchant password as specified by the parameter "password".
 */		
//		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
//		this.findElementByXPath(pElement.get("MerchantPasswordpassword")).sendKeys(password); 
//		this.findElementByXPath(pElement.get("MerchantPasswordContinue")).click(); // click OK
		this.inputText(waitSec, "MerchantPasswordpassword", password);//input password
		this.clickButton(0,"MerchantPasswordContinue" ); // click OK
		}
		catch(Exception e) {
//			this.logMessage("exception caught, not on Merchant Password page.");
	    	//this.logerror(e);		
		}
	}
	
	
	public void enterEmptyPIN(int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try to click OK without entering the PIN and verify whether the correct validation is
 * 			present.
 */		
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
			this.findElementByXPath("*[contains(@text,'6-digit')]"); //is validation message already present?
			Assert.assertFalse(true);//If the validation message is already present without any action, it is a bug.
		}
		catch (Exception e) {
//			this.logMessage("exception caught, validation message doesn't exist");
	    	//this.logerror(e);	
		}
		try {
//			this.findElementByXPath(pElement.get("EnterPINOK")).click(); 
			this.clickButton(0, "EnterPINOK");//Click OK without entering PIN
			Thread.sleep(500);   //very important otherwise the validation message does not appear in time
			Assert.assertEquals(this.findElementByXPath("*[contains(@text,'6-digit')]").getText(), "Please enter a 6-digit PIN"); 
			//If the correct validation messages appears, test passed
		}
		catch (Exception e) {
//			this.logMessage("exception caught, not on PIN entering page.");
	    	//this.logerror(e);	
		}
	}
	
	
	public void enterNumPadOK(int waitSec, String PIN) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try enter the PIN digits one by one then click OK.
 */	
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
			this.enterNumPad(waitSec, PIN);
			this.find("EnterPINOK").click(); //Click OK
		}
		catch(Exception e) {
//			this.logMessage("exception caught, not on PIN entering page.");
	    	//this.logerror(e);
		}
	}
	
	public void enterNumPad(int waitSec, String PIN) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
 * Post: 	If the current page is not Enter PIN page, do nothing,
 * 			otherwise try enter the PIN digits one by one then click OK.
 */	
		Thread.sleep(waitSec * 1000); //wait the current page to load
		try {
			log.logConsole("Number to be entered is: " + PIN);
			for (int i=0; i < PIN.length(); i++ ) {
				int digit = Character.getNumericValue(PIN.charAt(i));
				this.find("EnterPIN"+digit).click(); //Enter each digit of the PIN
			}
		}
		catch(Exception e) {
//			this.logMessage("exception caught, not on PIN entering page.");
	    	//this.logerror(e);
		}
	}

	public void clickNext(int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	If the current page is not a pop-up asking user to enter information then click next,
 * 			do nothing, otherwise click next and proceed.
 */	
//		Thread.sleep(waitSec * 1000);//wait the current page to load
		try {
//			this.findElementByXPath(pElement.get("PurchaseDescriptionNext")).click();
			this.clickButton(waitSec, "PurchaseDescriptionNext");
		}
		catch (Exception e) {
//			this.logMessage("exception caught, Next button not found");
	    	//this.logerror(e);
		}
	}
	
	public void showSideMenu (int waitSec) throws InterruptedException {
/* Pre: 	waitSec is a positive integer, 
 * Post: 	The Side Menu is displayed.
 */	
		this.clickButton(waitSec, "SideMenuShowMenu"); 
	}



	
	public void logColorText (String color, String text) {
		Reporter.log("<font color='"+color+"'>"+text +"</font>");
	}
	
	public void logComment (int waitSec, String comment ) {
		this.logColorText(logInfoColor, comment);
	}
	
	
//The next set of functions (launch & findCurrentPage & multiplePurchase, etc) are attempts to implement "reproduce steps" and are not completed yet.
	public void launch (int waitSec) throws InterruptedException {
/* Pre: This function should be used when the test case requires the user to luanch the app and reach the login page
 * Post: login page reached without any assertion used
 */
//		Thread.sleep(waitSec * 1000);
		String currentPage = "";
		int iteration = 0;
		while ((!currentPage.equals("EnterPIN")) && (iteration<10)) {
			iteration ++;
			currentPage = this.findCurrentPage(waitSec); //decide what to do depending on which page the user is on
			switch (currentPage) {
				case "ActivatingSecureElement": {
					Thread.sleep(30000);break;
					//Under Provisioning, wait for 30 seconds before rechecking progress
				}

				case "ConfirmNewPIN": {
					this.enterNumPadOK(0, (String) this.getCapabilities().getCapability("MerchantPIN"));
					break;					
				}
				case "CreateNewPassword": {
					this.inputText(0, "CreateNewPasswordEnterPassword", (String) this.getCapabilities().getCapability("MerchantNewPassword"));
					this.inputText(0, "CreateNewPasswordVerifyPassword", (String) this.getCapabilities().getCapability("MerchantNewPassword"));
					this.clickButton(0, "CreateNewPasswordContinue");
					break;
				}
				case "EnterNewPIN": {
					this.enterNumPadOK(0, (String) this.getCapabilities().getCapability("MerchantPIN"));
					break;
				}
				case "EnterPIN": {
						this.logColorText(logInfoColor,"App launched and awaiting for login.");
						// In Enter PIN page, do nothing
						break;
				}
				case "MerchantPassword": {
					this.merchantPassword(0, (String) this.getCapabilities().getCapability("MerchantPassword"));
					break;					
				}
				case "MerchantSignIn": {
					this.merchantSignin(0, (String) this.getCapabilities().getCapability("MerchantID"));
					// First time merchant registration page, entering merchant credentials
					// potential problem: taking too long to register, needs to add more checks and logics later
					break;
				}
				case "PaymentAcceptanceSetup": {
					this.clickButton(0, "PaymentAcceptanceSetupContinue");break;
					// Provision page, click continue to start provision
				}
				case "ProvisionUpdateComplete": {
					this.clickButton(0, "ProvisionUpdateCompleteDone");break;
					//Provision is done, proceed
				}
				case "ProvisionActivateComplete": {
					this.clickButton(0, "ProvisionActivateCompleteDone");break;
					//Provision is done, proceed
				}
				case "SignInSelection": {
					this.clickButton(0, (String) this.getCapabilities().getCapability("Username")); break;
					// Have multiple user to select from, select the user specified in Config file to advance to Enter PIN page
				}
				case "UpdateRequired": {
					this.clickButton(0, "UpdateRequiredUpdatenow"); break;
				}

				case "UpdatingSecureElement": {
					Thread.sleep(30000);break;
					//Under Provisioning, wait for 30 seconds before rechecking progress
				}
				default: {
					log.logConsole("This page "+currentPage+ " is not recognized.");
					iteration++;
					//current page not one of the above
				}
			}
		}

	}
	
	public void loginProcess (String currentPage) throws InterruptedException {
		switch (currentPage) {
			case "EnterPIN": {
				this.enterNumPadOK(0, (String) this.getCapabilities().getCapability("MerchantPIN"));
				this.logColorText(logInfoColor,"User Authentication performed.");
				break;
			}
			case "AllowBatterySettingsChange": {
				this.clickButton(0, "AllowBatterySettingsChangeContinue");
				break;
			}
			case "AllowTimeoutSettingsChange": {
				this.clickButton(0, "AllowTimeoutSettingsChangeContinue");
				break;
			}
			case "AndroidDeviceSettings": {
				this.clickButton(0, "AndroidDeviceSettingsback");
				break;
			}
			case "AndroidAlert": {
				this.clickButton(0, "AndroidAlertAllow");
				break;
			}
			default: {
				log.logConsole("This page "+currentPage+ " is not recognized.");
			}
		}
	}
	public void launchProcess (String currentPage) throws InterruptedException {
/* Pre: This function defines the default actions on each page for the launch process
 * Post: Depending on the current page the app is at, advance one page towards the end goal
 */
		log.logConsole("current page in launch process is: " + currentPage);
		switch (currentPage) {
			case "ActivatingSecureElement": {
				Thread.sleep(30000);break;
				//Under Provisioning, wait for 30 seconds before rechecking progress
			}
			case "ConfirmNewPIN": {
				this.enterNumPadOK(0, (String) this.getCapabilities().getCapability("MerchantPIN"));
				break;					
			}
			case "CreateNewPassword": {
				this.inputText(0, "CreateNewPasswordEnterPassword", (String) this.getCapabilities().getCapability("MerchantNewPassword"));
				this.inputText(0, "CreateNewPasswordVerifyPassword", (String) this.getCapabilities().getCapability("MerchantNewPassword"));
				this.clickButton(0, "CreateNewPasswordContinue");
				break;
			}
			case "EnterNewPIN": {
				this.enterNumPadOK(0, (String) this.getCapabilities().getCapability("MerchantPIN"));
				break;
			}
			case "EnterPIN": {
					this.logColorText(logInfoColor,"App launched and awaiting for login.");
					// In Enter PIN page, do nothing
					break;
			}
			case "MerchantPassword": {
				this.merchantPassword(0, (String) this.getCapabilities().getCapability("MerchantPassword"));
				break;					
			}
			case "MerchantSignIn": {
				this.merchantSignin(0, (String) this.getCapabilities().getCapability("MerchantID"));
				// First time merchant registration page, entering merchant credentials
				// potential problem: taking too long to register, needs to add more checks and logics later
				break;
			}
			case "PaymentAcceptanceSetup": {
				this.clickButton(0, "PaymentAcceptanceSetupContinue");break;
				// Provision page, click continue to start provision
			}
			case "ProvisionActivateComplete": {
				this.clickButton(0, "ProvisionActivateCompleteDone");break;
				//Provision is done, proceed
			}			
			case "ProvisionUpdateComplete": {
				this.clickButton(0, "ProvisionUpdateCompleteDone");break;
				//Provision is done, proceed
			}
			case "SignInSelection": {
				this.clickButton(0, (String) this.getCapabilities().getCapability("Username")); break;
				// Have multiple user to select from, select the user specified in Config file to advance to Enter PIN page
			}
			case "UpdateRequired": {
				this.clickButton(0, "UpdateRequiredUpdatenow"); break;
			}
			case "UpdatingSecureElement": {
				Thread.sleep(30000);break;
				//Under Provisioning, wait for 30 seconds before rechecking progress
			}
			default: {
				log.logConsole("This page "+currentPage+ " is not recognized.");
				//current page not one of the above
			}
		}	//switch
	}	//method
	
	public String reachPageByProcess (int timeoutIteration,String processName, String targetPage) throws InterruptedException {
		String currentPage = this.findCurrentPage(0);
		String previousPage = "";
		int iteration = 0;
		while ((!currentPage.equals(targetPage)) && (iteration<timeoutIteration)) {
			if (previousPage.equals(currentPage)) {
				log.logConsole(previousPage + " is equal to " + currentPage + ", do nothing");
				iteration ++;
			}

			else {
				switch (processName) {
					case "launch": {
						this.launchProcess(currentPage);
						break;
					}
					case "login": {
						this.loginProcess(currentPage);
						break;
					}
					default: {
						this.logColorText(logFailColor, "Process Undefined: Please check csv file used correct process name.");
						return currentPage;
					}
				}
			}		
			previousPage = currentPage;
			currentPage = this.findCurrentPage(1); //decide what to do depending on which page the user is on

		}
		return currentPage;
	}
	
	public String findCurrentPage(int waitSec) throws InterruptedException {
/* Pre: mobilePage has been initialized
 * This function is to be used as strictly a helper function to determine what page the app is currently displaying
 * Post: return the PageName associated with the current mobilePage
 */
		Thread.sleep(500);
		String currentPage = "";
		log.logConsole("");
		System.out.print("Current Page Verification in progress");
		DesiredCapabilities pages = (DesiredCapabilities) cap.getCapability("AndroidPage");
//		System.out.println(pages);
		
		Iterator<Object> mPage =  pages.asMap().values().iterator();
/*		System.out.println("mpage next is: "+mPage.next());
		System.out.println("mPage1 is: " + ((mobilePage) mPage.next()).getPageName());
		System.out.println("mPage2 is: " + ((mobilePage) mPage.next()).getPageName());
		System.out.println("mPage to string is: " + mPage.toString());
		*/
//		System.out.println("Page Name is: " +((mobilePage) mPage).getPageName());
//		System.out.println("Page Name is: " +((mobilePage) pages.getCapability(mPage.toString())).getPageName());		
//		for (int i=0; i<pages.asMap().size(); i++) {
//		for (int i=0; i<mobilePage.length; i++)	{
			while (mPage.hasNext()) {
			try {
				mobilePage tempPage =(mobilePage) mPage.next();	
//				String name = ( (mobilePage) pages.getCapability(mobilePage[i])).getPageName();
				String name =  tempPage.getPageName();
				
//				System.out.println("name is: " + name);
//				String id = ((mobilePage) pages.getCapability(mobilePage[i])).getUID();
				String id =  tempPage.getUID();
//				System.out.println("id is: " + id);
//				String value = ((mobilePage) pages.getCapability(mobilePage[i])).getUValue();
				String value =  tempPage.getUValue();
//				System.out.println("value is: " + value);
				if (!(id.isEmpty())) {
//				System.out.println("in first if");
				DesiredCapabilities andEle = (DesiredCapabilities) cap.getCapability("AndroidElement");
				pageElement pEle = (pageElement) andEle.getCapability(id);
//				System.out.println("pEle is: " + pEle);
				String xp = pEle.getPath();
//				System.out.println("xp is: " + xp);
				String title = this.findElementByXPath(xp).getText();
//					System.out.println("title is: "+ title);
					if (title.equals(value)) {
//						System.out.println("in 2nd if");
						log.logConsole("Current page is: "+name);
						return name;
					}
				}
				

			}
			catch (Exception e) {
				System.out.print(".");
			}
		} //while
		log.logConsole("Current page is: "+currentPage);
		return currentPage;
	}
	
	
	public void login(int waitSec) throws InterruptedException {
/*Pre: Username and MerchantPIN is appropriately defined in Config file
 * Post: user logs in with the username and PIN defined in config file
 */
		this.enterNumPadOK(waitSec, (String) this.getCapabilities().getCapability("MerchantPIN"));
		this.logColorText(logInfoColor,"User Authentication performed.");
	}
	
	public void multiplePurchase(int numPurchase, String tipType, String tipValue, String descriptionType, String descriptionValue, String emailType, String emailValue) throws InterruptedException {
/*Pre: numPurchase is a positive integer
 * Post: multiple purchases are performed 
 */ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions

		int i = 0;
		int iteration = 0;
		int waitSec = 1;

		while ((i<numPurchase) && (iteration<numPurchase*3)) {
			this.singlePurchase(1000, tipType,  tipValue,  descriptionType,  descriptionValue);
			String page = this.findCurrentPage(waitSec);
			log.logConsole("This is the " + (i+1)+"th purchase attempt");
			switch (page) {

				case "PurchaseResult": {
					this.clickButton(0, "PurchaseResultNoReceipt");
//					this.clickButton(0, "PurchaseResultEmailReceipt");
					log.logConsole("Purchase made.");
					this.logColorText(logInfoColor,"Transaction #"+(i+1)+" is made.");
					i++;
					break;
				}
				case "CardNotSupported": {
					this.clickButton(0, "CardNotSupportedTryAgain");
					log.logConsole("Purchase made.");
					this.logColorText(logInfoColor,"Transaction #"+(i+1)+" is denied due to card not supported.");
					i++;
					break;
				}
				case "PurchaseNotCompleted": {
					this.clickButton(0, "PurchaseNotCompletedDone");
					log.logConsole("Purchase made.");
					this.logColorText(logInfoColor,"Transaction #"+(i+1)+" is attempted but not completed.");
					i++;
					break;
				}
				case "ReceiptSentConfirmation": {
					this.clickButton(0, "ReceiptSentConfirmationDone"); 
					break;

				}
				
				case "NewTagScanned": {
					log.logConsole("Pressing key code 82, still needs implementation");
//					this.pressKeyCode(82);
					log.logConsole("Pressing AllAppListingCBADebug");
					this.clickButton(3, "AllAppListingCBADebug");
					waitSec = 5;
					break;
				}
				case "EmailReceiptForPurchase": {
					this.clearText(1, "EmailReceiptForPurchaseEmail");
					this.inputText(0, "EmailReceiptForPurchaseEmail", (String) this.getCapabilities().getCapability("CustomerEmail"));
					this.clickButton(0, "EmailReceiptForPurchaseNext");
					break;
				}
				
				default: {
//					iteration = numPurchase*3+1;
					log.logConsole("reached an unrecognized page: " + page);
					this.clickButton(0,"NoNetworkConnectionretry");
					//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
					waitSec = 5;
					
				}


			}
		}
		this.logColorText(logInfoColor,numPurchase + " consecutive purchases attempted.");
	}
	
	public void enterPurchaseAmount(int waitSec, String amount) throws InterruptedException {
/*Pre: amount is a string that can be parsed into an integer representing the amount of purchases
 * Post: amount specified is entered and ok is clicked
 */
		this.enterNumPadOK(waitSec, amount);
	}
	

	
	public String singlePurchase(int amount, String tipType, String tipValue, String descriptionType, String descriptionValue) throws InterruptedException {
/*Pre: Amount is an integer acceptable to the app, tipType can be "none", "default", "Percentage","Dollar", and "total";
 * tipValue are integers (in String type), descriptionType can be "none" or "yes"
 * the app is on the purchase page, waiting for an amount to be entered
 * Post: one single purchase is performed, or the app has not made any progress after a pre-configured timeout time(timeout still to be implemented).
 */ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions

		LocalTime startT = LocalTime.now();
		LocalTime endT = LocalTime.now();
		int iteration = 0;
		int waitSec = 1;
		String page = "";
		long betweenT = 0;
		while (iteration<10 ) { //break loop if timed out
			page = this.findCurrentPage(waitSec); //find the current page and decide action based on it
			iteration ++;	//time out counter increment
			switch (page) {
				case "Purchase" : {			
					this.enterPurchaseAmount(0, Integer.toString(amount));
					//In Purchase page, enter the amount of the purchase
					break;
				}
				case "TapToPay":{
					log.logConsole("waiting for card tap");
					startT = LocalTime.now();
					//nothing to do other than wait, start tracking processing time
					break;
				}
				case "ProcessingPayment": {
					log.logConsole("processing payment");
					//nothing to do other than wait
					break;
				}
				case "PurchaseDescription": {
					if (descriptionType.equals("yes")) {
						this.inputText(0, "PurchaseDecsriptionDescription", descriptionValue);
						//input the description according to parameter
					}
						this.clickButton(0, "PurchaseDescriptionNext");
						//click next
					break;
				}
				case "PurchaseResult": {	//in Purchase Result page
					endT = LocalTime.now();	//record processing end time
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is made, Processing time is: " + betweenT + " seconds." );
					return page;	//destination reached, end method
				}
				case "CardNotSupported": {	//in Purchase Result(Card not supported) page
					endT = LocalTime.now();
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is denied due to card not supported, Processing time is: " + betweenT + " seconds." );
					return page;
				}
				case "PurchaseNotCompleted": {	//in Purchase Result(Purchase not completed) page
					endT = LocalTime.now();
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
					return page;
				}
				case "NewTagScanned": {		//known bug encountered, external page on foreground, not implemented yet
					log.logConsole("Pressing key code 82, still needs implementation");
//					this.pressKeyCode(82);
					log.logConsole("Pressing AllAppListingCBADebug");
					this.clickButton(3, "AllAppListingCBADebug");
					break;
				}
				case "SetTipDuringPurchase": {
					this.setTipType(tipType, tipValue);
					break;
				}
				case "EnterCustomTip": {
					this.setTipValue(tipType, tipValue);
					break;
				} 	
				default: {
					log.logConsole("reached an unrecognized page: " + page);
					this.clickButton(0,"NoNetworkConnectionretry");
					//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
				}
			}	//end of switch
		}	//end of while
		return page;
	}	

	public String singlePurchaseUntil (int amount, String tipType, String tipValue, String descriptionType, String descriptionValue, String targetPage) throws InterruptedException {
/*Pre: Amount is an integer acceptable to the app, tipType can be "none", "default", "Percentage","Dollar", and "total";
 * tipValue are integers (in String type), descriptionType can be "none" or "yes"
 * the app is on the purchase page, waiting for an amount to be entered
 * Post: one single purchase is performed, or the app has not made any progress after a pre-configured timeout time(timeout still to be implemented).
 */ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions

		LocalTime startT = LocalTime.now();
		LocalTime endT = LocalTime.now();
		int iteration = 0;
		int timeOutSec = defaultTimeOutSec;
		String page = this.findCurrentPage(0);
		String previousPage = "";
		long betweenT = 0;
		while (iteration<10 ) { //break loop if timed out
			startT = LocalTime.now();
			page = this.waitUntilNewPage(timeOutSec,page); //find the current page and decide action based on it
			if (page.equals(targetPage)) {
				endT = LocalTime.now();	//record processing end time
				betweenT = ChronoUnit.SECONDS.between(startT, endT);
				switch (page) {
					case "PurchaseResult": {
						Reporter.log("Transaction is made, Processing time is: " + betweenT + " seconds." );
						return page;	//destination reached, end method
					}
					case "CardNotSupported": {
						Reporter.log("Transaction is made, Processing time is: " + betweenT + " seconds." );
						return page;	//destination reached, end method
					}
					case "PurchaseNotCompleted": {
						Reporter.log("Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
						return page;	//destination reached, end method
					}
					default: {
						Reporter.log("Target page: " + targetPage + " is reached, Processing time is: " + betweenT + " seconds." );
						return page;	//destination reached, end method
					}
				}
			}
			
			iteration ++;	//time out counter increment
			switch (page) {
				case "Purchase" : {			
					this.enterPurchaseAmount(0, Integer.toString(amount));
					//In Purchase page, enter the amount of the purchase
					break;
				}
				case "TapToPay":{
					log.logConsole("waiting for card tap");
					//nothing to do other than wait
					break;
				}
				case "ProcessingPayment": {
					log.logConsole("processing payment");
					//nothing to do other than wait
					break;
				}
				case "PurchaseDescription": {
					if (descriptionType.equals("yes")) {
						this.inputText(0, "PurchaseDecsriptionDescription", descriptionValue);
						//input the description according to parameter
					}
						this.clickButton(0, "PurchaseDescriptionNext");
						//click next
					break;
				}
				case "PurchaseResult": {	//in Purchase Result page
					endT = LocalTime.now();	//record processing end time
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is made, Processing time is: " + betweenT + " seconds." );
					return page;	//destination reached, end method
				}
				case "CardNotSupported": {	//in Purchase Result(Card not supported) page
					endT = LocalTime.now();
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is denied due to card not supported, Processing time is: " + betweenT + " seconds." );
					return page;
				}
				case "PurchaseNotCompleted": {	//in Purchase Result(Purchase not completed) page
					endT = LocalTime.now();
					betweenT = ChronoUnit.SECONDS.between(startT, endT);
					this.logColorText(logInfoColor,"Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
					return page;
				}
				case "NewTagScanned": {		//known bug encountered, external page on foreground, not implemented yet
					log.logConsole("Pressing key code 82, still needs implementation");
//					this.pressKeyCode(82);
					log.logConsole("Pressing AllAppListingCBADebug");
					this.clickButton(3, "AllAppListingCBADebug");
					break;
				}
				case "SetTipDuringPurchase": {
					this.setTipType(tipType, tipValue);
					break;
				}
				case "EnterCustomTip": {
					this.setTipValue(tipType, tipValue);
					break;
				} 	
				default: {
					log.logConsole("reached an unrecognized page: " + page);
					this.clickButton(0,"NoNetworkConnectionretry");
					//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
				}
			}	//end of switch
		}	//end of while
		return page;
	}	

	
	
	
	
	
	
	public void setTipValue (String tipType, String tipValue) throws InterruptedException  {
		int tipV = Integer.parseInt(tipValue);
		switch (tipType) {
			case "Percentage": { 
				this.clickButton(0, "AddTipTip%");
				break;
			}
			case "Dollar": {	
				this.clickButton(0, "AddTipTip$");
				break;
			}
			case "Total": {	
				this.clickButton(0, "AddTipTipTotalAmount");
				break;
			}			
			default: { 
				log.logConsole("Tip type " + tipType + " is not recognized.");
//				this.clickButton(0, "SetTipDuringPurchasePay"); 
				break;
			}
		
		}
		this.enterNumPadOK(0, tipValue);
	}
	
	public void setTipType (String tipType, String tipValue) throws InterruptedException  {
		switch (tipType) {
		case "none": {
			this.clickButton(0, "SetTipDuringPurchasePay"); 
			break;
			//no tip, just click pay
		}
		case "default": {
			switch (tipValue) {
				case "First": {
					this.clickButton(0, "SetTipDuringPurchaseFirstDefaultTipValue"); 
					//click on first default tip%
					break;
				}
				case "Second": {
					this.clickButton(0, "SetTipDuringPurchaseSecondDefaultTipValue"); 
					//click on second default tip%
					break;
				}							
				case "Third": {
					this.clickButton(0, "SetTipDuringPurchaseThirdDefaultTipValue"); 
					//click on third default tip%
					break;
				}
				default: {
					//if tipValue is not recognized, assume no tip needs to be selected
					log.logConsole("Tip value " + tipValue + " is not recognized.");
					break;
				}
			}
			this.clickButton(0, "SetTipDuringPurchasePay"); 
			//default tip % selected, now click pay
			break;
		}
		case "Percentage": { //custom tip needed
			this.clickButton(0, "SetTipDuringPurchaseCustomTip"); 		
			break;
		}
		case "Dollar": {	//custom tip needed
			this.clickButton(0, "SetTipDuringPurchaseCustomTip"); 		
			break;
		}
		case "Total": {	//custom tip needed
			this.clickButton(0, "SetTipDuringPurchaseCustomTip"); 		
			break;
		}
		default: {
			//if tipType is not recognized, assume no tip needs to be selected
			log.logConsole("Tip type " + tipType + " is not recognized.");
			this.clickButton(0, "SetTipDuringPurchasePay"); 
		}
	}
}

		
	public String waitUntilPage (int timeout, String targetPage) throws InterruptedException {
		LocalTime startT = LocalTime.now();
		String currentPage = this.findCurrentPage(0);
		while ((!targetPage.equals(currentPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
			currentPage = this.findCurrentPage(1);
		}
		return currentPage;
	}
	
	public String waitUntilNewPage (int timeout, String currentPage) throws InterruptedException {
		LocalTime startT = LocalTime.now();
		String newPage = this.findCurrentPage(0);
		while ((currentPage.equals(newPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
			newPage = this.findCurrentPage(1);
		}
		return newPage;
	}
	
	
	
//log related functions that's not fully implemented yet	
/*
	private void logMessage(String message)
	{
		testLog.log( Level.FINE, message ); 
	}
	private void logError(Exception ex)
	{
		testLog.log( Level.SEVERE, ex.toString(), ex );
	}
*/	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
//The next three functions are attempts to automate UI tests. They are not completed yet due to technical difficulties that
//needs more time to think through
	public boolean checkPageOverlap(int waitSec, String pName) throws InterruptedException {
/*Pre: mobilePage has been appropriated initialized
 * Post: return true if any elements in this page has boundaries overlapping. false if otherwise
 */
		Thread.sleep(waitSec * 1000);
		Vector URIs = pageName.get(pName).getAllElements();
		boolean isOverlapping = false;
		try {
		int numElement = URIs.size();
		for (int i = 0; i<numElement; i++) {
			for (int j=i+1; j<numElement;j++) {
//System.out.println((String)URIs.elementAt(i));
				WebElement a = this.find((String) URIs.elementAt(i));
//System.out.println((String)URIs.elementAt(j));
				WebElement b = this.find((String) URIs.elementAt(j));
//System.out.println("no exception when finding element");
				boolean compare = this.isOverlap(a, b);
				isOverlapping = (compare||isOverlapping);
//				if (compare == true) {
//				System.out.println(URIs.elementAt(i) + " and " + URIs.elementAt(j) + " is compared, their overlapping status is: "+ compare);
//				}
			}
		}
		} 
		catch(Exception e) {
System.out.println("exception caught when comparing overlaps");			
//this.logMessage("exception caught when comparing overlaps");
		}
		return isOverlapping;
		
	}
	
	public boolean isOverlap(WebElement w1, WebElement w2) {
/* Pre: w1 and w2 are elements currently displayed on current page
 * Post: return true if w1 and w2 has boundaries overlapping. false if otherwise
 */
//		This method is not properly implemented yet. need revision & debug
//		WebElement w1 = this.findElementByXPath(pElement.get(object));
//		WebElement w2 = this.findElementByXPath(pElement.get(object2));
		boolean isOverlap = false;
		int[] x = new int[4];
		int[] y = new int[4];
		
		x[0] = w1.getLocation().getX();
		y[0] = w1.getLocation().getY();
		int width1 = w1.getSize().getWidth();
		int height1 = w1.getSize().getHeight();		
		x[1]= x[0] + width1;
		y[1]= y[0] + height1;		
		
		x[2] = w2.getLocation().getX();
		y[2] = w2.getLocation().getY();
		int width2 = w2.getSize().getWidth();
		int height2 = w2.getSize().getHeight();		
		x[3]= x[2] + width2;
		y[3]= y[2] + height2;		
		
		isOverlap = this.coordIsPartiallyContainedIn(w1, w2) || this.coordIsPartiallyContainedIn(w2, w1);
		return isOverlap;
	}
	
	public boolean coordIsPartiallyContainedIn (WebElement w1, WebElement w2) {
		boolean isContained = false;

		int ax1 = w1.getLocation().getX();
		int ay1 = w1.getLocation().getY();
		int width1 = w1.getSize().getWidth();
		int height1 = w1.getSize().getHeight();		
		int ax2 = ax1 + width1;
		int ay2 = ay1 + height1;		
		int bx1 = w2.getLocation().getX();
		int by1 = w2.getLocation().getY();
		int width2 = w2.getSize().getWidth();
		int height2 = w2.getSize().getHeight();
		int bx2 = bx1 + width2;
		int by2 = by1 + height2;
		
		if ((bx2>=ax2) && (ax2 > bx1) && (by2>=ay2) && (ay2 > by1)  ) {
			log.logConsole("A possible overlap: 1st element has [" +ax1+ " ,"+ax2+ " ,"+ay1+ " ,"+ay2+ "]");
			log.logConsole("A possible overlap: 2nd element has [" +bx1+ " ,"+bx2+ " ,"+by1+ " ,"+by2+ "]");
			return true;
		}
		else if ( (ax1 < bx2) && (bx2<=ax2)&& (ay1 < by2)&&(by2<=ay2)) {
			log.logConsole("A possible overlap: 1st element has [" +ax1+ " ,"+ax2+ " ,"+ay1+ " ,"+ay2+ "]");
			log.logConsole("A possible overlap: 2nd element has [" +bx1+ " ,"+bx2+ " ,"+by1+ " ,"+by2+ "]");
			return true;
		}
//		System.out.println("not overlapping");
		return isContained;
	}
	
	public int areaWithTwoRectangle (int[] x, int[] y) {
		int area = 0;
		int minX, maxX,minY,maxY =0;
		minX = Math.min(x[0], x[2]);
		maxX = Math.max(x[1], x[3]);
		minY = Math.min(y[0], y[2]);
		maxY = Math.max(y[1], y[3]);
		
		for (int i= minX; i< maxX; i++ ) {
			for (int j = minY; j<maxY; j++) {
				if ((x[0]<=i && i<x[1] && y[0]<=j && j<y[1])|| (x[2]<=i && i<x[3] && y[2]<=j && j<y[3])) {
					area ++;
				}
			}
		}
		log.logConsole("area is: " + area);
		return area;
	}
	
	
	
	
	public boolean isAligned (String mode, String URI1, String URI2) {
/* Pre: all elements are appropriately defined and initialized
 * Post: return true if all elements are aligned. return false otherwise
 */
// not implemented yet		
		switch (mode) {
		
		case ("HorizontalTop"): {}
		case ("HorizontalMid"): {}
		case ("HorizontalBottom"): {}
		case ("VerticalLeft"): {}
		case ("VerticalMid"): {}
		case ("VerticalRight"): {}
		dafault: return true;
		
		}
		
		
		return true;
	}
	
}
