import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.safari.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.ie.*;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import io.appium.java_client.MobileElement;

public class MWDriver {
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
	private ElementList mPageList;		//a list of all the pages present in an app. this really should be app/driver specific, but for now, I'm
										//putting it like this until the restructuring is implemented.
	private DesiredCapabilities elementList; //all the elements read from the element file
	private String driverType;
	private String driverName;
	
	private MWAndroidDriver and;
	private WebDriver ffd;
	private WebDriver chd;
	
//	private DesiredCapabilities[] pages = new DesiredCapabilities[];
	static private String logPassColor ="green";
	static private String logFailColor ="red";
	static private String logEmphasisColor ="brown";
	static private String logInfoColor ="grey";
	static private String logCaseColor ="blue";
	
	static private int defaultTimeOutSec = 15;

	public RemoteTouchScreen touch;

	
//	WebDriver sfd = new SafariDriver();
//	WebDriver edd = new EdgeDriver();
//	WebDriver ied = new InternetExplorerDriver();
//	WebElementWait wew = new WebElementWait();
	
	public MWDriver (String dName, String dType, DesiredCapabilities capabilities, MWLogger logs) throws IOException {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 * */	
		cap = capabilities;
		log = logs;
		driverType = dType;
		driverName = dName;
		elementList = new DesiredCapabilities();
		mPageList = new ElementList("mobilePage", log);
		System.setProperty("webdriver.chrome.driver", ".\\resources\\chromedriver.exe");	
		System.setProperty("webdriver.gecko.driver", ".\\resources\\geckodriver.exe");		

		
		customizedCSVReader cr = new customizedCSVReader("");
		DesiredCapabilities[] elementFileContent = cr.readElementFile((String)cap.getCapability(dName));			//read in all the element and pages from the associated element configuration file
		ElementList pageList = new ElementList("mobilePage",logs);
		Iterator<Object> mPage =  elementFileContent[1].asMap().values().iterator();
		while (mPage.hasNext()) {				//create an ElementList containing all pages
			pageList.add((mobilePage) mPage.next());
		}
		mPageList = pageList.sortby("mobilePage", "count", "Desc", "Integer"); //sort the ElementList according to historical data, the more frequently a page is visited, the earlier it is placed in the list
		
		elementList = elementFileContent[0];

		switch (dType) {
		case "Chrome": {
			chd = new ChromeDriver();			
			log.logConsole("ChromeDriver created.");		}
		case "Firefox": {
			ffd = new FirefoxDriver();
			log.logConsole("FirefoxDriver created.");		}
		case "Android": {
			and = new MWAndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities, log);
			log.logConsole("AndroidDriver created.");		}
		default: {
			log.logConsole("unrecognized driver type " + dType + "specified.");
		}
		}
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
			case "clickButton": {this.clickButton(parameters[4]);break;}	
			case "inputText": {this.inputText(parameters[4], parameters[5]);break;}	
			case "launch": {this.launch();break;}
			case "logComment": {this.logComment(parameters[4]); break;}
			case "scrollToBottom": {this.scrollToBottom();break;} //need refinement
			case "scrollToTop": {this.scrollToTop();break;} //need refinement
			case "scrollDown": {this.scrollDown();break;} //need refinement
			case "scrollUp": {this.scrollUp();break;} //need refinement
			case "setBooleanValue": {this.setBooleanValue(parameters[4], parameters[5]); break;}
			case "test": {this.test(parameters[7], Arrays.copyOfRange(parameters, 8, parameters.length));break;}
			case "wait": {this.wait(waitSec); break;}
			case "waitUntilPresent": {this.waitUntilElement (waitSec, parameters[4]);break;}
			case "waitUntilPage": {this.waitUntilPage(waitSec, parameters[4]); break;}
			case "checkPageOverlap": {this.checkPageOverlap(waitSec, parameters[4]);break;}
			case "clearNumPad": {this.clearNumPad(parameters[4]); break;}						
			case "enterNumPad": {this.enterNumPad(parameters[4]); break;}
			case "enterNumPadOK": {this.enterNumPadOK(parameters[4]);break;}
			case "enterPurchaseAmount": {this.enterPurchaseAmount(parameters[4]); break;}
			case "login": {this.login(waitSec); break;}
			case "merchantPassword": {this.merchantPassword(parameters[4]);break;}
			case "merchantSignin": {this.merchantSignin(parameters[4]); break;}
			case "multiplePurchase": {this.multiplePurchase(waitSec, parameters[4],parameters[5],parameters[6],parameters[7],parameters[8],parameters[9] ); break;}
						//notice for multiplePurchase, variable waitSec represents number of purchases to be made, not seconds to wait
			case "pickDate": {this.pickDate(parameters[4],parameters[5],parameters[6]);break;}
			case "reachPageByProcess": {this.reachPageByProcess(waitSec, parameters[4], parameters[5]);break;}
			case "reachPage": {this.reachPage("GP", parameters[4]);break;}
			case "showSideMenu": {this.showSideMenu(waitSec);break;}
			case "sign": {this.sign();break;}
			case "singlePurchase": {this.singlePurchase(waitSec,parameters[4],parameters[5],parameters[6],parameters[7] ); break;}
			case "singlePurchaseUntil": {this.singlePurchaseUntil(waitSec,parameters[4],parameters[5],parameters[6],parameters[7], parameters[8]);break;}
			default: log.logConsole(parameters[2] + " not found. No such method exists.");
					}
	}
	
	
	
	
	
/********************"Testable Project" related methods*******************************/	
	
	public Object chooseDriver () throws IOException {
		/* Pre: driverName is a valid, case sensitive string that corresponds to one of the supported drivers
		 * 		The corresponding driver has already been initialized - this really should be more carefully coded with error handles/checks later
		 * Post: the corresponding driver is returned. note that a null is returned if driverName is incorrect.
		 */
		switch(driverType) {
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
	
	public DesiredCapabilities chooseCap () throws IOException {
		return elementList;
	}
	
	public ElementList choosePage () throws IOException {
		return mPageList;
	}
	
	
	
	/********************"Driver" related methods ********************************/	
	
	public WebElement findByXPath(String xpath) throws IOException {
		switch (driverType) {
		case "Android": {
			return and.findElementByXPath(xpath);
		}
		case "Chrome": {
			return chd.findElement(By.xpath(xpath));
		}
		case "Firefox": {
			return ffd.findElement(By.xpath(xpath));
		}
		default: {
			log.logConsole("Driver type " + driverType + " is not implemented yet.");
			return null;
		}
		}		
	}
	
	
	public WebElement findByURI(String URI) throws IOException {
/*	Pre: 	driverName is a valid, case sensitive string that corresponds to one of the supported drivers
 * 			URI is a string defined in the element configuration file and is a unique identifier for the WebElement in question
 * 	Post:	The WebElement whose xpath matching the xpath attribute of the URI is returned
 * 
 * 			Note that android and web browsers use slightly different methods in determining such WebElement.
 */
		String xpath = ((pageElement) elementList.getCapability(URI)).getPath();
		return this.findByXPath(xpath);
	}
	
	
	public void clickButton(String URI) throws IOException {
		try {
			this.findByURI(URI).click();
		}
		catch (Exception e) {
			log.logConsole("exception caught during button click, failed to find " + URI);
		}
	}
	
	public void inputText(String URI, String value) throws IOException {
		try {

			this.findByURI(URI).sendKeys(value);
		}
		catch (Exception e) {
			log.logConsole("exception caught during text entry, failed to find " + URI);
		}
			
	}
	
	public void setBooleanValue(String URI, String value) throws IOException {
		String expectedValue = "false";
		if (value.equals("yes")) {
			expectedValue = "true";
		}
		try {
			if (this.findByURI(URI).getAttribute("checked").equals(expectedValue)) {
				//do nothing
			}
			else {
				this.findByURI(URI).click();
			}
		}
		catch (Exception e) {
			log.logConsole("exception caught during click, failed to find " + URI);
		}
	}
	
	public void launch() throws InterruptedException, IOException {
		switch (driverType) {
		case "Android": {
			and.launch(120);
			break;
		}
		default: {
			((WebDriver)this.chooseDriver()).get((String) cap.getCapability("StartingURL"));
		}
		}

	}
	

	
	public String findCurrentPage () throws InterruptedException, IOException {
			String currentPage = "";
			System.out.print("Current Page Verification in progress");
			for (int i=0; i<mPageList.size(); i++) {
				try {
					mobilePage tempPage =(mobilePage) mPageList.elementAt(i);	
					String name =  tempPage.getPageName();
					String id =  tempPage.getUID();
					String value =  tempPage.getUValue();
					if (!(id.isEmpty())) {
					pageElement pEle = (pageElement) elementList.getCapability(id);
					String xp = pEle.getID();
					String title = this.findByURI(xp).getText();
						if (title.equals(value)) {
							log.logConsole("Current page is: "+name);
							tempPage.incrementCount();
							log.logFile("page count is: " +((mobilePage) mPageList.elementAt(i)).get("count"));
							return name;
						}	//if
					}	//if
				}	//try
				catch (Exception e) {
					System.out.print(".");
				}	//catch
			} //while
			log.logConsole("Current page is: "+currentPage);
			return currentPage;
		}

	
	
public String reachPageByProcess (int timeoutIteration,String processName, String targetPage) throws InterruptedException, IOException {
	String currentPage = this.findCurrentPage();
	String previousPage = "";
	int iteration = 0;
	while ((!currentPage.equals(targetPage)) && (iteration<timeoutIteration)) {
		if (previousPage.equals(currentPage)) {
			log.logConsole(previousPage + " is equal to " + currentPage + ", do nothing");
			iteration ++;
		}	//if

		else {
			switch (processName) {
				case "launch": {
					this.launchProcess(currentPage);
					break;
				}	//case
				case "login": {
					this.loginProcess(currentPage);
					break;
				}	//case
				default: {
					log.logFailure("Process Undefined: Please check csv file used correct process name.");
					return currentPage;
				}	//default
			}	//switch
		}			//else
		previousPage = currentPage;
		currentPage = this.findCurrentPage(); //decide what to do depending on which page the user is on

	}	//while
	return currentPage;
}



public String waitUntilPage (int timeout, String targetPage) throws InterruptedException, IOException {
	LocalTime startT = LocalTime.now();
	String currentPage = this.findCurrentPage();
	while ((!targetPage.equals(currentPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
		currentPage = this.findCurrentPage();
		Thread.sleep(500);
	}
	return currentPage;
}

/*
public String waitUntilNewPage (String driverName, int timeout, String currentPage) throws InterruptedException {
	LocalTime startT = LocalTime.now();
	String newPage = this.findCurrentPage(driverName);
	while ((currentPage.equals(newPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
		newPage = this.findCurrentPage(driverName);
		Thread.sleep(500);
	}
	return newPage;
}
*/

public boolean test(String method, String[] contents) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, method is either "equal" or "isOnPage" (to be expanded)
* 			fieldName is a URI - if method is "isOnPage", fieldName can be null as it is not used.
* Post: 	The method returns a boolean state depending on whether the expected value equal to the actual value.
*/	
System.out.println("method is: " + method);
System.out.println("contents are: " +log.logArray(contents));

	boolean testResult = false;
	String actualValue = "";
			switch (method) {
		case "equal": {
			String fieldName = contents[0];
			String expectedValue = contents[1];
			try {
			actualValue = this.findByURI(fieldName).getText();
			}
			catch (Exception e) {
			actualValue = "element not found";
			}
			return log.logTestResult(fieldName, actualValue, expectedValue);
		}
		case "isOnPage": {
			String expectedValue = contents[1];
			actualValue = this.findCurrentPage();
			return log.logTestResult("Current page", actualValue, expectedValue);
		}
		case "isSorted": {
			String fieldName = contents[0];
			String sortOrder = contents[1];
			//not finished yet
			
			}
		default: {
			testResult = false;
//			Reporter.log(method + " is not a defined test methodology, no test was conducted.");
			log.logColorText("red", method + " is not a defined test methodology, no test was conducted.");
			return testResult;	
		}
	}
}




public void logComment (String comment ) throws IOException {
	log.logComment(comment);
}

public void wait(int waitSec) throws InterruptedException {
	Thread.sleep(waitSec*1000);
}

public void scrollToBottom() throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver();

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public void scrollToTop() throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver();

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, 0)");
}

public void scrollDown() throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver();

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public void scrollUp() throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver();

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public void close() throws IOException {
		try {
			Object obj = this.chooseDriver();
			String filePath = (String)cap.getCapability(driverName);
			customizedCSVReader cr = new customizedCSVReader("");
			cr.writeElementFile(filePath, mPageList);
			switch (driverType) {
			case "Android": {
				and.closeApp();
			}
			default: {
				((WebDriver) obj).close();				
			}
			}

		}
		catch (Exception e) {
			log.logConsole("error encountered when closing "+driverName);
		}
	}



//The next set of functions uses the basic functions above to construct frequently used functions on the page level	
public void merchantSignin(String ID) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, 
* 			ID is not empty,
* 			MerchantSignInID is accurately defined in configuration file,
* 			MerchantSignInOK is accurately defined in configuration file.
* Post: 	If the current page is not Merchant Signin page, do nothing,
* 			otherwise input the merchant id as specified by the parameter "ID".
*/
	log.logFile("method merchantSignin ("+ ID  +") is called.");
//Thread.sleep(waitSec * 1000);	//wait the current page to load
	switch (driverType) {
	case "Android": {
		try {
			this.inputText("MerchantSignInID", ID); //input merchant ID
			this.clickButton("MerchantSignInOK" ); // click OK
			}
		catch(Exception e) {
			log.logConsole("exception caught, not on Merchant Sign IN page");
			}
	}
	default: {
			log.logConsole("driverType " + driverType + "not implemented for this method");
	}
	}

}

public void merchantPassword(String password) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, 
* 			password is not empty,
* 			MerchantPasswordpassword is accurately defined in configuration file,
* 			MerchantPasswordContinue is accurately defined in configuration file.
* Post: 	If the current page is not Merchant Password page, do nothing,
* 			otherwise input the merchant password as specified by the parameter "password".
*/		
	log.logFile("method merchantPassword ("+ password  +") is called.");
//Thread.sleep(waitSec * 1000); //wait the current page to load

	switch (driverType) {
	case "Android": {
		try {
			this.inputText( "MerchantPasswordpassword", password);//input password
			this.clickButton("MerchantPasswordContinue" ); // click OK
			}
		catch(Exception e) {
			log.logConsole("exception caught, not on Merchant Password page.");
		}
	}
	default: {
		log.logConsole("driverType " + driverType + "not implemented for this method");
	}
	}
}


public void clearText(String URI) throws InterruptedException, IOException {
/* Pre: 	waitSec is a non-negative integer, URI are accurately defined in the configuration file
* Post: 	The text field is cleared.
* 			use this method when no validation or boundary cases are expected
*/			
	log.logFile("method clearText (" + URI  +") is called.");

	try {
	this.findByURI(URI).clear();
	}
	catch (Exception e) {
		log.logConsole("exception caught during text clearance, failed to find " + URI);
	}
}



public void enterNumPadOK(String PIN) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, 
* 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
* Post: 	If the current page is not Enter PIN page, do nothing,
* 			otherwise try enter the PIN digits one by one then click OK.
*/	
log.logFile("method enterNumPadOK ("+ PIN  +") is called.");

try {
	this.enterNumPad(PIN);
	this.findByURI("EnterPINOK").click(); //Click OK
}
catch(Exception e) {
	log.logConsole("exception caught, not on PIN entering page.");

}
}
public void clearNumPad(String numOfClear) throws IOException {
int iteration = Integer.parseInt(numOfClear);
try {
for (int i=0; i<iteration; i++) {
	this.findByURI("EnterPINDelete").click(); //Enter each digit of the PIN
}
}
catch(Exception e) {
	log.logConsole("exception caught while trying to clear numpad.");
}
}

public void enterNumPad(String PIN) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, 
* 			The numerous numpad buttons on PIN page are all accurately defined in configuration file.
* Post: 	If the current page is not Enter PIN page, do nothing,
* 			otherwise try enter the PIN digits one by one then click OK.
*/	
log.logFile("method enterNumPad ("+ PIN  +") is called.");
try {
	log.logConsole("Number to be entered is: " + PIN);
	for (int i=0; i < PIN.length(); i++ ) {
		int digit = Character.getNumericValue(PIN.charAt(i));
		this.findByURI("EnterPIN"+digit).click(); //Enter each digit of the PIN
	}
}
catch(Exception e) {
	log.logConsole("exception caught, not on PIN entering page.");
}
}

public void pickDate (String tYear, String tMonth, String tDay) throws IOException {
log.logFile("method pickDate ("+tYear + ", " + tMonth  +", " +tDay  +") is called.");
try {
int year = Integer.parseInt(tYear);
int month = Integer.parseInt(tMonth);
int day = Integer.parseInt(tDay);
boolean done = false;
while (!done) {
String firstDayofSelectedMonth = this.findByURI("DatePickerFirstDay").getAttribute("contentDescription");
log.logConsole("first day of selected month is: "+firstDayofSelectedMonth);
int[] date = this.parseDatePickerString(firstDayofSelectedMonth);
int targetMonth = year*100+month;
log.logConsole("target month is:"+targetMonth);
int selectedMonth = date[0]*100+date[1];
log.logConsole("selected month is:"+selectedMonth);
if (targetMonth == selectedMonth) {
	log.logFile("at target month");
	this.selectDay(day);
	done = true;
}
else if (targetMonth < selectedMonth) {
	log.logFile("target month is earlier");
	this.clickButton("DatePickerPreviousMonth");
}
else {
	log.logFile("target month is later");
	this.clickButton("DatePickerNextMonth");
}
Thread.sleep(500);
}
}
catch (Exception e) {
	log.logConsole("Error Trying to select date.");
}
this.findByURI("DatePickerOK").click();
}

public void selectDay (int day) throws IOException {
log.logFile("method selectDay ("+day  +") is called.");
String xpath = "//android.view.View[@resource-id='android:id/month_view']/android.view.View[@text="+day+"]";
this.findByXPath(xpath).click();
}

public int[] parseDatePickerString (String dateString) throws IOException {
log.logFile("method parseDatePickerString ("+dateString  +") is called.");
int[] date = new int[3];
date[2]= Integer.parseInt(dateString.substring(0, 2));
date[0] = Integer.parseInt(dateString.substring(dateString.length()-4, dateString.length()));
String month = dateString.substring(3, dateString.length()-5);
log.logFile("The Year is parsed to be: "+date[0]);
log.logFile("The month is parsed to be: "+month);
log.logFile("The day is parsed to be: "+date[2]);
switch (month) {

case "January": {
	date[1]=1;break;
}
case "February": {
	date[1]=2;break;
}
case "March": {
	date[1]=3;break;
}
case "April": {
	date[1]=4;break;
}
case "May": {
	date[1]=5;break;
}
case "June": {
	date[1]=6;break;
}
case "July": {
	date[1]=7;break;
}
case "August": {
	date[1]=8;break;
}
case "September": {
	date[1]=9;break;
}
case "October": {
	date[1]=10;break;
}
case "November": {
	date[1]=11;break;
}
case "December": {
	date[1]=12;break;
}
}
return date;
}

public void showSideMenu (int waitSec) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, 
* Post: 	The Side Menu is displayed.
*/	
log.logFile("method showSideMenu ("+waitSec + ") is called.");
this.wait(waitSec);
this.clickButton("SideMenuShowMenu"); 
}

/*
public void logColorText (String color, String text) {
Reporter.log("<font color='"+color+"'>"+text +"</font>");
}

public void logComment (int waitSec, String comment ) {
this.logColorText(logInfoColor, comment);
}*/


//The next set of functions (launch & findCurrentPage & multiplePurchase, etc) are attempts to implement "reproduce steps" and are not completed yet.
public void launch (int waitSec) throws InterruptedException, IOException {
log.logFile("method launch ("+waitSec + ") is called.");		
this.launch("CBA", waitSec);
}

public void launch (String appName, int waitSec) throws InterruptedException, IOException {
/* Pre: This function should be used when the test case requires the user to luanch the app and reach the login page
* Post: login page reached without any assertion used
*/
//Thread.sleep(waitSec * 1000);
log.logFile("method launch ("+appName + ", " + waitSec  +") is called.");
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
			this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
			break;					
		}
		case "CreateNewPassword": {
			this.inputText("CreateNewPasswordEnterPassword", (String) cap.getCapability("MerchantNewPassword"));
			this.inputText("CreateNewPasswordVerifyPassword", (String) cap.getCapability("MerchantNewPassword"));
			this.clickButton("CreateNewPasswordContinue");
			break;
		}
		case "EnterNewPIN": {
			this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
			break;
		}
		case "EnterPIN": {
				log.logColorText(logInfoColor,"App launched and awaiting for login.");
				// In Enter PIN page, do nothing
				break;
		}
		case "MerchantPassword": {
			this.merchantPassword((String) cap.getCapability("MerchantPassword"));
			break;					
		}
		case "MerchantSignIn": {
			this.merchantSignin((String) cap.getCapability("MerchantID"));
			// First time merchant registration page, entering merchant credentials
			// potential problem: taking too long to register, needs to add more checks and logics later
			break;
		}
		case "PaymentAcceptanceSetup": {
			this.clickButton("PaymentAcceptanceSetupContinue");break;
			// Provision page, click continue to start provision
		}
		case "ProvisionUpdateComplete": {
			this.clickButton("ProvisionUpdateCompleteDone");break;
			//Provision is done, proceed
		}
		case "ProvisionActivateComplete": {
			this.clickButton("ProvisionActivateCompleteDone");break;
			//Provision is done, proceed
		}
		case "SignInSelection": {
			this.clickButton((String) cap.getCapability("Username")); break;
			// Have multiple user to select from, select the user specified in Config file to advance to Enter PIN page
		}
		case "UpdateRequired": {
			this.clickButton("UpdateRequiredUpdatenow"); break;
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

public void loginProcess (String currentPage) throws InterruptedException, IOException {
log.logFile("method loginProcess ("+currentPage +") is called.");

switch (currentPage) {
	case "EnterPIN": {
		this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
		log.logColorText(logInfoColor,"User Authentication performed.");
		break;
	}
	case "AllowBatterySettingsChange": {
		this.clickButton("AllowBatterySettingsChangeContinue");
		break;
	}
	case "AllowTimeoutSettingsChange": {
		this.clickButton("AllowTimeoutSettingsChangeContinue");
		break;
	}
	case "AndroidDeviceSettings": {
		this.clickButton("AndroidDeviceSettingsback");
		break;
	}
	case "AndroidAlert": {
		this.clickButton("AndroidAlertAllow");
		break;
	}
	default: {
		log.logConsole("This page "+currentPage+ " is not recognized.");
	}
}
}
public void launchProcess (String currentPage) throws InterruptedException, IOException {
/* Pre: This function defines the default actions on each page for the launch process
* Post: Depending on the current page the app is at, advance one page towards the end goal
*/

log.logFile("method launchProcess ("+currentPage  +") is called.");
log.logConsole("current page in launch process is: " + currentPage);
switch (currentPage) {
	case "ActivatingSecureElement": {
		Thread.sleep(30000);break;
		//Under Provisioning, wait for 30 seconds before rechecking progress
	}
	case "ConfirmNewPIN": {
		this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
		break;					
	}
	case "CreateNewPassword": {
		this.inputText("CreateNewPasswordEnterPassword", (String) cap.getCapability("MerchantNewPassword"));
		this.inputText("CreateNewPasswordVerifyPassword", (String) cap.getCapability("MerchantNewPassword"));
		this.clickButton("CreateNewPasswordContinue");
		break;
	}
	case "EnterNewPIN": {
		this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
		break;
	}
	case "EnterPIN": {
			log.logColorText(logInfoColor,"App launched and awaiting for login.");
			// In Enter PIN page, do nothing
			break;
	}
	case "MerchantPassword": {
		this.merchantPassword((String) cap.getCapability("MerchantPassword"));
		break;					
	}
	case "MerchantSignIn": {
		this.merchantSignin((String) cap.getCapability("MerchantID"));
		// First time merchant registration page, entering merchant credentials
		// potential problem: taking too long to register, needs to add more checks and logics later
		break;
	}
	case "PaymentAcceptanceSetup": {
		this.clickButton("PaymentAcceptanceSetupContinue");break;
		// Provision page, click continue to start provision
	}
	case "ProvisionActivateComplete": {
		this.clickButton("ProvisionActivateCompleteDone");break;
		//Provision is done, proceed
	}			
	case "ProvisionUpdateComplete": {
		this.clickButton("ProvisionUpdateCompleteDone");break;
		//Provision is done, proceed
	}
	case "SignInSelection": {
		this.clickButton((String) cap.getCapability("Username")); break;
		// Have multiple user to select from, select the user specified in Config file to advance to Enter PIN page
	}
	case "UpdateRequired": {
		this.clickButton("UpdateRequiredUpdatenow"); break;
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




public String clickOnPage (String expectedPage, String targetPage, String[] URIs) throws InterruptedException, IOException {
log.logFile("method clickOnPage("+expectedPage + ", "+targetPage+ ", "+URIs+") is called.");
String cPage = this.findCurrentPage(0);
if (cPage.equals(expectedPage)) {
	for (int i=0; i<URIs.length; i++) {
		this.clickButton(URIs[i]);
		Thread.sleep(500);
	}
	return this.waitUntilPage(30, targetPage);
	}
else if (cPage.equals(targetPage)) {
	log.logComment("Already reached " + targetPage +".");
	return cPage;
}
else {
	log.logComment("unexpected page " + cPage + " reached");
	return cPage;
}
}

public String clickOnPage (String expectedPage, String targetPage, String URI) throws InterruptedException, IOException {
log.logFile("method clickOnPage("+expectedPage + ", "+targetPage+ ", "+URI+") is called.");
String[] URIs = new String[1];
URIs[0] = URI;
return this.clickOnPage(expectedPage, targetPage, URIs);
}

public String goToSidemenuPages (String targetPage) throws InterruptedException, IOException {
log.logFile("method goToSidemenuPage("+targetPage+") is called.");
String currentPage = this.findCurrentPage(0);
if (currentPage.equals(targetPage)) {
	return currentPage;
}
String[] URIs = new String[2];
URIs[0] = "PurchaseSideMenu";
switch (targetPage) {
case "Purchase": {
	URIs[1] = "SideMenuPurchase";break;
}
case "TransactionReports": {
	URIs[1] = "SideMenuTransactionReports";break;
}
case "TransactionHistory": {
	URIs[1] = "SideMenuTransactionHistory";break;
}
case "UserManagement": {
	URIs[1] = "SideMenuUserManagement";break;
}
case "ContactUs": {
	URIs[1] = "SideMenuContactUs";break;
}
case "Help": {
	URIs[1] = "SideMenuHelp";break;
}
default: {
	
}
}
return this.clickOnPage(currentPage, targetPage, URIs);
}

public String reachPage (String appName, String targetPage) throws InterruptedException, IOException {
log.logFile("method reachPage("+appName + ", "+targetPage+") is called.");		
switch (appName) {
	default: {
		
		switch (targetPage) {
		case "AccountLocked": {}
		case "ActivatingSecureElement": {}
		case "AddNewUser": {}
		case "AddTip": {}
		case "AllowBatterySettingsChange": {}
		case "AllowTimeoutSettingsChange": {}
		case "AndroidAlert": {}
		case "AndroidDeviceSettings": {}
		case "CancelTransaction": {}
		case "CardNotSupported": {}
		case "ConfirmNewPIN": {}
		case "ConnectionErrorDuringProvision": {}
		case "ContactUs": {
			String currentPage = this.reachPage(appName, "Purchase");
			return this.goToSidemenuPages(targetPage);
		}
		case "CreateNewPassword": {}
		case "DatePicker": {}
		case "DeleteUserConfirmation": {}
		case "EditUser": {}
		case "EmailReceiptForPurchase": {}
		case "EnterNewPIN": {}
		case "EnterOldPIN": {}
		case "EnterPIN": {
			this.launch(appName,1);
			return this.waitUntilPage(30, "EnterPIN");
		}
		case "ForgotPIN": {
			String currentPage = this.reachPage(appName, "EnterPIN");
			return this.clickOnPage("EnterPIN", targetPage, "EnterPINForgotPIN");
		}
		case "InvalidCredentials": {}
		case "MerchantPassword": {}
		case "MerchantSignIn": {}
		case "NewTagScanned": {}
		case "NoNetworkConnection": {}
		case "PaymentAcceptanceSetup": {}
		case "PINUpdated": {}
		case "PleaseRestartYourPhone": {}
		case "ProcessingPayment": {}
		case "ProvisionActivateComplete": {}
		case "ProvisionUpdateComplete": {}
		case "Purchase": {
			try { this.findByURI("PurchaseSideMenu");} //is there a sidemenu button available?
			catch (Exception e) {	//if no, proceed from sign in
				String currentPage = this.reachPage(appName, "EnterPIN");
				if (currentPage.equals("EnterPIN")) {
					this.login(0);
					return this.waitUntilPage(30, targetPage);
				}
				else {
					log.logComment("unexpected page " + currentPage + " reached");
					return currentPage;
				}
			}			
			//if a sidemenu is found, proceed from sidemenu
			return this.goToSidemenuPages(targetPage);
		}
		case "PurchaseDescription": {}
		case "PurchaseNotCompleted": {}
		case "PurchaseResult": {}
		case "PurchaseTimedOut": {}
		case "ReceiptSentConfirmation": {}
		case "ReportSentConfirmation": {}
		case "ResetPIN": {}
		case "SecurityImage": {}
		case "SettingsAdvanced": {
			String currentPage = this.reachPage(appName, "SettingsTerminal");
			return this.clickOnPage("SettingsTerminal", targetPage, "SettingsTerminalAdvancedTab");
		}
		case "SettingsTerminal": {
			String currentPage = this.reachPage(appName, "Purchase");
			String cPage = this.findCurrentPage(0);
			if (cPage.equals("Purchase")) {
				this.clickButton("PurchaseSideMenu");
				this.clickButton("SideMenuSettings");
				this.login(1);
				return this.waitUntilPage(30, targetPage);
			}
			else if (cPage.equals(targetPage)) {
				log.logComment("Already reached " + targetPage +".");
				return cPage;
			}
			else {
				log.logComment("unexpected page " + cPage + " reached");
				return cPage;
			}
		}
		case "SetTipDuringPurchase": {}
		case "SignInSelection": {}
		case "TapToPay": {}
		case "TermsAndConditions": {}
		case "TransactionDetails": {}
		case "TransactionHistory": {
			String currentPage = this.reachPage(appName, "Purchase");
			return this.goToSidemenuPages(targetPage);
		}
		case "TransactionReports": {
			String currentPage = this.reachPage(appName, "Purchase");
			return this.goToSidemenuPages(targetPage);
		}
		case "TransactionSearch": {}
		case "UpdateRequired": {}
		case "UpdatingSecureElement": {}
		case "UserCreated": {}
		case "UserManagement": {
			String currentPage = this.reachPage(appName, "Purchase");
			return this.goToSidemenuPages(targetPage);
		}
		case "VoidProcessingPayment": {}
		default: {
		}
		}
	}
	}

String currentPage = this.findCurrentPage(1);
return currentPage;
}

public String findCurrentPage(int waitSec) throws InterruptedException, IOException {
/* Pre: mobilePage has been initialized
* This function is to be used as strictly a helper function to determine what page the app is currently displaying
* Post: return the PageName associated with the current mobilePage
*/
log.logFile("method findCurrentPage("+waitSec +") is called.");
Thread.sleep(500);
String currentPage = "";
log.logConsole("");
System.out.print("Current Page Verification in progress");
/*		DesiredCapabilities pages = (DesiredCapabilities) cap.getCapability("AndroidPage");
Iterator<Object> mPage =  pages.asMap().values().iterator();

	while (mPage.hasNext()) {
*/
for (int i=0; i<mPageList.size(); i++) {
	try {
//		mobilePage tempPage =(mobilePage) mPage.next();	
		mobilePage tempPage =(mobilePage) mPageList.elementAt(i);
		String name =  tempPage.getPageName();
		String id =  tempPage.getUID();
		String value =  tempPage.getUValue();
		if (!(id.isEmpty())) {
		DesiredCapabilities andEle = (DesiredCapabilities) cap.getCapability("AndroidElement");
		pageElement pEle = (pageElement) andEle.getCapability(id);
		String xp = pEle.getPath();
		String title = this.findByXPath(xp).getText();
			if (title.equals(value)) {
				log.logConsole("Current page is: "+name);
				tempPage.incrementCount();
				return name;
			}
			else {
				System.out.print(".");
//				log.logFile("match not found: " + id + " is not equal to " + value + " , but equal to " + title);
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


public void login(int waitSec) throws InterruptedException, IOException {
/*Pre: Username and MerchantPIN is appropriately defined in Config file
* Post: user logs in with the username and PIN defined in config file
*/
log.logFile("method login("+waitSec +") is called.");
this.wait(waitSec);
this.enterNumPadOK((String) cap.getCapability("MerchantPIN"));
log.logColorText(logInfoColor,"User Authentication performed.");
}

public void multiplePurchase(int numPurchase, String tipType, String tipValue, String descriptionType, String descriptionValue, String emailType, String emailValue) throws InterruptedException, IOException {
/*Pre: numPurchase is a positive integer
* Post: multiple purchases are performed 
*/ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions
log.logFile("method multiplePurchase("+numPurchase + ", "+tipType+ ", "+tipValue+ ", "+descriptionType+ ", "+descriptionValue+ ", "+emailType+ ", "+emailValue+") is called.");
int i = 0;
int iteration = 0;
int waitSec = 1;

while ((i<numPurchase) && (iteration<numPurchase*3)) {
	this.singlePurchase(1000, tipType,  tipValue,  descriptionType,  descriptionValue);
	String page = this.findCurrentPage(waitSec);
	log.logConsole("This is the " + (i+1)+"th purchase attempt");
	switch (page) {

		case "PurchaseResult": {
			this.clickButton("PurchaseResultNoReceipt");
//			this.clickButton(0, "PurchaseResultEmailReceipt");
			log.logConsole("Purchase made.");
			log.logColorText(logInfoColor,"Transaction #"+(i+1)+" is made.");
			i++;
			break;
		}
		case "CardNotSupported": {
			this.clickButton("CardNotSupportedTryAgain");
			log.logConsole("Purchase made.");
			log.logColorText(logInfoColor,"Transaction #"+(i+1)+" is denied due to card not supported.");
			i++;
			break;
		}
		case "PurchaseNotCompleted": {
			this.clickButton("PurchaseNotCompletedDone");
			log.logConsole("Purchase made.");
			log.logColorText(logInfoColor,"Transaction #"+(i+1)+" is attempted but not completed.");
			i++;
			break;
		}
		case "ReceiptSentConfirmation": {
			this.clickButton("ReceiptSentConfirmationDone"); 
			break;

		}
		
		case "NewTagScanned": {
			log.logConsole("Pressing key code 82, still needs implementation");
//			this.pressKeyCode(82);
			log.logConsole("Pressing AllAppListingCBADebug");
			this.wait(3);
			this.clickButton("AllAppListingCBADebug");
			waitSec = 5;
			break;
		}
		case "EmailReceiptForPurchase": {
			this.wait(1);
			this.clearText("EmailReceiptForPurchaseEmail");
			this.inputText("EmailReceiptForPurchaseEmail", (String) cap.getCapability("CustomerEmail"));
			this.clickButton("EmailReceiptForPurchaseNext");
			break;
		}
		
		default: {
//			iteration = numPurchase*3+1;
			log.logConsole("reached an unrecognized page: " + page);
			this.clickButton("NoNetworkConnectionretry");
			//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
			waitSec = 5;
			
		}


	}
}
log.logColorText(logInfoColor,numPurchase + " consecutive purchases attempted.");
}

public void enterPurchaseAmount(String amount) throws InterruptedException, IOException {
/*Pre: amount is a string that can be parsed into an integer representing the amount of purchases
* Post: amount specified is entered and ok is clicked
*/
log.logFile("method enterPurchaseAmount("+amount+") is called.");
this.enterNumPadOK(amount);
}



public String singlePurchase(int amount, String tipType, String tipValue, String descriptionType, String descriptionValue) throws InterruptedException, IOException {
/*Pre: Amount is an integer acceptable to the app, tipType can be "none", "default", "Percentage","Dollar", and "total";
* tipValue are integers (in String type), descriptionType can be "none" or "yes"
* the app is on the purchase page, waiting for an amount to be entered
* Post: one single purchase is performed, or the app has not made any progress after a pre-configured timeout time(timeout still to be implemented).
*/ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions
log.logFile("method singlePurchase("+amount + ", "+tipType+ ", "+tipValue+ ", "+descriptionType+ ", "+descriptionValue+ ") is called.");

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
			this.enterPurchaseAmount(Integer.toString(amount));
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
				this.inputText("PurchaseDescriptionDescription", descriptionValue);
				//input the description according to parameter
			}
				this.clickButton("PurchaseDescriptionNext");
				//click next
			break;
		}
		case "PurchaseResult": {	//in Purchase Result page
			endT = LocalTime.now();	//record processing end time
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is made, Processing time is: " + betweenT + " seconds." );
			return page;	//destination reached, end method
		}
		case "CardNotSupported": {	//in Purchase Result(Card not supported) page
			endT = LocalTime.now();
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is denied due to card not supported, Processing time is: " + betweenT + " seconds." );
			return page;
		}
		case "PurchaseNotCompleted": {	//in Purchase Result(Purchase not completed) page
			endT = LocalTime.now();
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
			return page;
		}
		case "NewTagScanned": {		//known bug encountered, external page on foreground, not implemented yet
			log.logConsole("Pressing key code 82, still needs implementation");
//			this.pressKeyCode(82);
			log.logConsole("Pressing AllAppListingCBADebug");
			this.wait(3);
			this.clickButton("AllAppListingCBADebug");
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
		case "AddTip": {
			this.setTipValue(tipType, tipValue);
			break;
		} 
		default: {
			log.logConsole("reached an unrecognized page: " + page);
			this.clickButton("NoNetworkConnectionretry");
			//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
		}
	}	//end of switch
}	//end of while
return page;
}	

public String singlePurchaseUntil (int amount, String tipType, String tipValue, String descriptionType, String descriptionValue, String targetPage) throws InterruptedException, IOException {
/*Pre: Amount is an integer acceptable to the app, tipType can be "none", "default", "Percentage","Dollar", and "total";
* tipValue are integers (in String type), descriptionType can be "none" or "yes"
* the app is on the purchase page, waiting for an amount to be entered
* Post: one single purchase is performed, or the app has not made any progress after a pre-configured timeout time(timeout still to be implemented).
*/ 
//This method still needs further refining to allow ability to deal with different kinds of non-optimal conditions
log.logFile("method singlePurchase("+amount + ", "+tipType+ ", "+tipValue+ ", "+descriptionType+ ", "+descriptionValue+ ") is called.");

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
				log.report("Transaction is made, Processing time is: " + betweenT + " seconds." );
				return page;	//destination reached, end method
			}
			case "CardNotSupported": {
				log.report("Transaction is made, Processing time is: " + betweenT + " seconds." );
				return page;	//destination reached, end method
			}
			case "PurchaseNotCompleted": {
				log.report("Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
				return page;	//destination reached, end method
			}
			default: {
				log.report("Target page: " + targetPage + " is reached, Processing time is: " + betweenT + " seconds." );
				return page;	//destination reached, end method
			}
		}
	}
	
	iteration ++;	//time out counter increment
	switch (page) {
		case "Purchase" : {			
			this.enterPurchaseAmount(Integer.toString(amount));
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
				this.inputText("PurchaseDecsriptionDescription", descriptionValue);
				//input the description according to parameter
			}
				this.clickButton("PurchaseDescriptionNext");
				//click next
			break;
		}
		case "PurchaseResult": {	//in Purchase Result page
			endT = LocalTime.now();	//record processing end time
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is made, Processing time is: " + betweenT + " seconds." );
			return page;	//destination reached, end method
		}
		case "CardNotSupported": {	//in Purchase Result(Card not supported) page
			endT = LocalTime.now();
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is denied due to card not supported, Processing time is: " + betweenT + " seconds." );
			return page;
		}
		case "PurchaseNotCompleted": {	//in Purchase Result(Purchase not completed) page
			endT = LocalTime.now();
			betweenT = ChronoUnit.SECONDS.between(startT, endT);
			log.logColorText(logInfoColor,"Transaction is attempted but not completed, Processing time is: " + betweenT + " seconds." );
			return page;
		}
		case "NewTagScanned": {		//known bug encountered, external page on foreground, not implemented yet
			log.logConsole("Pressing key code 82, still needs implementation");
//			this.pressKeyCode(82);
			log.logConsole("Pressing AllAppListingCBADebug");
			this.wait(3);
			this.clickButton("AllAppListingCBADebug");
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
			this.clickButton("NoNetworkConnectionretry");
			//if an page is not recognized, try to see if there is a button "RETRY". if yes, click it
		}
	}	//end of switch
}	//end of while
return page;
}	







public void setTipValue (String tipType, String tipValue) throws InterruptedException, IOException  {
log.logFile("method setTipValue("+tipType+ ", "+tipValue+ ") is called.");

int tipV = Integer.parseInt(tipValue);
switch (tipType) {
	case "Percentage": { 
		this.clickButton("AddTipTip%");
		break;
	}
	case "Dollar": {	
		this.clickButton("AddTipTip$");
		break;
	}
	case "Total": {	
		this.clickButton("AddTipTipTotalAmount");
		break;
	}			
	default: { 
		log.logConsole("Tip type " + tipType + " is not recognized.");
//		this.clickButton(0, "SetTipDuringPurchasePay"); 
		break;
	}

}
this.enterNumPadOK(tipValue);
}

public void setTipType (String tipType, String tipValue) throws InterruptedException, IOException  {
log.logFile("method setTipType("+tipType+ ", "+tipValue+ ") is called.");

switch (tipType) {
case "none": {
	this.clickButton("SetTipDuringPurchasePay"); 
	break;
	//no tip, just click pay
}
case "default": {
	switch (tipValue) {
		case "First": {
			this.clickButton("SetTipDuringPurchaseFirstDefaultTipValue"); 
			//click on first default tip%
			break;
		}
		case "Second": {
			this.clickButton("SetTipDuringPurchaseSecondDefaultTipValue"); 
			//click on second default tip%
			break;
		}							
		case "Third": {
			this.clickButton("SetTipDuringPurchaseThirdDefaultTipValue"); 
			//click on third default tip%
			break;
		}
		default: {
			//if tipValue is not recognized, assume no tip needs to be selected
			log.logConsole("Tip value " + tipValue + " is not recognized.");
			break;
		}
	}
	this.clickButton("SetTipDuringPurchasePay"); 
	//default tip % selected, now click pay
	break;
}
case "Percentage": { //custom tip needed
	this.clickButton("SetTipDuringPurchaseCustomTip"); 		
	break;
}
case "Dollar": {	//custom tip needed
	this.clickButton("SetTipDuringPurchaseCustomTip"); 		
	break;
}
case "Total": {	//custom tip needed
	this.clickButton("SetTipDuringPurchaseCustomTip"); 		
	break;
}
default: {
	//if tipType is not recognized, assume no tip needs to be selected
	log.logConsole("Tip type " + tipType + " is not recognized.");
	this.clickButton("SetTipDuringPurchasePay"); 
}
}
}




public String waitUntilNewPage (int timeout, String currentPage) throws InterruptedException, IOException {
log.logFile("method waitUntilNewPage("+timeout + ", "+currentPage+ ") is called.");

LocalTime startT = LocalTime.now();
String newPage = this.findCurrentPage(0);
while ((currentPage.equals(newPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
	newPage = this.findCurrentPage(1);
}
return newPage;
}



//The next three functions are attempts to automate UI tests. They are not completed yet due to technical difficulties that
//needs more time to think through
public boolean checkPageOverlap(int waitSec, String pName) throws InterruptedException, IOException {
/*Pre: mobilePage has been appropriated initialized
* Post: return true if any elements in this page has boundaries overlapping. false if otherwise
*/
Thread.sleep(waitSec * 1000);
Vector URIs = ((mobilePage) mPageList.getElement(pName)).getAllElements();
boolean isOverlapping = false;
try {
int numElement = URIs.size();
for (int i = 0; i<numElement; i++) {
	for (int j=i+1; j<numElement;j++) {
//System.out.println((String)URIs.elementAt(i));
		WebElement a = this.findByURI((String) URIs.elementAt(i));
//System.out.println((String)URIs.elementAt(j));
		WebElement b = this.findByURI((String) URIs.elementAt(j));
//System.out.println("no exception when finding element");
		boolean compare = this.isOverlap(a, b);
		isOverlapping = (compare||isOverlapping);
//		if (compare == true) {
//		System.out.println(URIs.elementAt(i) + " and " + URIs.elementAt(j) + " is compared, their overlapping status is: "+ compare);
//		}
	}
}
} 
catch(Exception e) {
System.out.println("exception caught when comparing overlaps");			
//this.logMessage("exception caught when comparing overlaps");
}
return isOverlapping;

}

public boolean isOverlap(WebElement w1, WebElement w2) throws IOException {
/* Pre: w1 and w2 are elements currently displayed on current page
* Post: return true if w1 and w2 has boundaries overlapping. false if otherwise
*/
//This method is not properly implemented yet. need revision & debug
//WebElement w1 = this.findElementByXPath(pElement.get(object));
//WebElement w2 = this.findElementByXPath(pElement.get(object2));
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

public boolean coordIsPartiallyContainedIn (WebElement w1, WebElement w2) throws IOException {
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
//System.out.println("not overlapping");
return isContained;
}

public int areaWithTwoRectangle (int[] x, int[] y) throws IOException {
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
//not implemented yet		
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



public void tap(String URI) throws IOException {
log.logConsole("starting to tap " + URI);
Coordinates coord = null;
try {
	System.out.println("find element");
	WebElement el = this.findByURI(URI);
//	System.out.println("find location");
//	Point pt = el.getLocation();
//	int x = pt.getX();
//	int y = pt.getY();
	System.out.println("find coord");
	Thread.sleep(5000);
	coord = ((Locatable)el).getCoordinates();
	System.out.println(coord.toString());
}
catch (Exception e) {
	System.out.println("error duing coordinates finding");
}
try {
System.out.println("stap");
Thread.sleep(5000);
touch.singleTap(coord);
}
catch (Exception e) {
	System.out.println("error duing coordinates stap");	
	System.out.println(e);
}		
try {
	System.out.println("dtap");			
	Thread.sleep(5000);
	touch.doubleTap(coord);
}
catch (Exception e) {
	System.out.println("error duing coordinates dtap");		
	System.out.println(e);
}		
try {
	System.out.println("flick");
	Thread.sleep(5000);
	touch.flick(coord, 1, 1, 300);
}
catch (Exception e) {
	System.out.println("error duing coordinates flick");	
	System.out.println(e);
}		
try {
	System.out.println("move");
	Thread.sleep(5000);
	touch.move(5, 5);
}
catch (Exception e) {
	System.out.println("error duing coordinates move");		
	System.out.println(e);
}		
try {
	System.out.println("scroll");
	Thread.sleep(5000);
	touch.scroll(coord, 5, 5);
}
catch (Exception e) {
	System.out.println("error duing coordinates scroll");	
	System.out.println(e);
}
try {
	
	
	


//touch.singleTap((Coordinates) this.find(URI).getLocation());
//touch.singleTap((Coordinates) this.find(URI).getLocation().moveBy(10, 10));

//touch.down(x, y).move(moveX, moveY).perform();
}
catch (Exception e) {

}
}

public void sign() throws IOException {
this.tap("SignatureSignArea"); 
}

public TouchScreen getTouch() {
return touch;
}

public ElementList getMPageList() {
return mPageList;
}

public WebElement waitUntilElement(int timeOutSec, String URI) throws InterruptedException, IOException {
	
	switch (driverType) {
	case "Android": {
		try {
			return this.findByURI(URI);
		}
		catch (Exception e) {
			if (timeOutSec > 0) {
				Thread.sleep(1000);
				timeOutSec --;
				return this.waitUntilElement(timeOutSec, URI);
			}
			else {
				log.logConsole(URI + " was not found, process timed out.");
				return null;
			}
		}
	}
	default: {
			Object obj = this.chooseDriver();
			if (timeOutSec==0 ) {
				timeOutSec = 10;
			}
			WebElement waitElement = (new WebDriverWait(((WebDriver) obj), timeOutSec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath((String) elementList.getCapability(URI))));	
			return waitElement;
	}
	}
}



}
	

