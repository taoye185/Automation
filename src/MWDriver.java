import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import io.appium.java_client.MobileElement;

public class MWDriver {
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
	
	MWLogger log;
	MWAndroidDriver and;
	WebDriver ffd;
	WebDriver chd;
	DesiredCapabilities cap;
	ElementList mPageList;
	
//	WebDriver sfd = new SafariDriver();
//	WebDriver edd = new EdgeDriver();
//	WebDriver ied = new InternetExplorerDriver();
//	WebElementWait wew = new WebElementWait();
	
	public MWDriver (DesiredCapabilities capabilities, MWLogger logs) throws IOException {
/* Pre: elementConfig is the path of a configuration file, listing all relevant URIs for the test project
 * Post: a MWAndroidDriver is constructed, while an URI-XPath mapping is established for all the relevant
 * 		elements
 * */	
		cap = capabilities;
		log = logs;
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\YeTao\\Desktop\\automation\\chromedriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\YeTao\\Desktop\\automation\\geckodriver.exe");

		if (((Object) cap.getCapability("Chrome")) != null) {
		chd = new ChromeDriver();
		customizedCSVReader cr = new customizedCSVReader("");
		DesiredCapabilities[] elementFileContent = cr.readElementFile((String)cap.getCapability("Chrome"));
		ElementList pageList = new ElementList("mobilePage",logs);
		Iterator<Object> mPage =  elementFileContent[1].asMap().values().iterator();
		while (mPage.hasNext()) {					
			pageList.add((mobilePage) mPage.next());
		}
		mPageList = pageList.sortby("mobilePage", "count", "Desc", "Integer");
		
		cap.setCapability("ChromeElement", elementFileContent[0]);
		cap.setCapability("ChromePage", elementFileContent[1]);
		log.logConsole("ChromeDriver created.");
		}
		

		
		if (((String) capabilities.getCapability("Firefox"))!= null) {
		ffd = new FirefoxDriver();
		customizedCSVReader cr = new customizedCSVReader("");
		DesiredCapabilities[] elementFileContent = cr.readElementFile((String)cap.getCapability("Firefox"));
		cap.setCapability("FirefoxElement", elementFileContent[0]);
		cap.setCapability("FirefoxPage", elementFileContent[1]);
		log.logConsole("FirefoxDriver created.");
		}
		
		if (((String) capabilities.getCapability("Android"))!= null) {
		and = new MWAndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities, log);
		customizedCSVReader cr = new customizedCSVReader("");
		DesiredCapabilities[] elementFileContent = cr.readElementFile((String)cap.getCapability("Android"));
		
/*		ElementList pageList = new ElementList("mobilePage",logs);
		Iterator<Object> mPage =  elementFileContent[1].asMap().values().iterator();
		while (mPage.hasNext()) {					
			pageList.add((mobilePage) mPage.next());
		}
		mPageList = pageList.sortby("mobilePage", "count", "Desc", "Integer");
*/		
		mPageList = and.getMPageList();
		cap.setCapability("AndroidElement", elementFileContent[0]);
		cap.setCapability("AndroidPage", elementFileContent[1]);
		log.logConsole("AndroidDriver created.");
		}
}



	
	public void testScenarioConstructor (String[] parameters) throws InterruptedException, IOException {
/* Pre: parameters[0] is a string that can be parsed into a non-negative integer
 * Post: a method corresponding to the methodName variable is called and executed. 
 * 		Data validation would be needed for this method which is not implemented yet.
 */
		int waitSec = Integer.parseInt(parameters[3]);
		String driverType = parameters[1];
		log.logConsole("The method \"" +parameters[2] + "("  + parameters[5]+ ")\" are called");
		
			switch (parameters[2]) {
			case "clickButton": {this.clickButton(driverType,parameters[4]);break;}	
			case "inputText": {this.inputText(driverType, parameters[4], parameters[5]);break;}	
			case "launch": {this.launch(driverType);break;}
			case "logComment": {this.logComment(parameters[4]); break;}
			case "scrollToBottom": {this.scrollToBottom(driverType);break;} //need refinement
			case "scrollToTop": {this.scrollToTop(driverType);break;} //need refinement
			case "scrollDown": {this.scrollDown(driverType);break;} //need refinement
			case "scrollUp": {this.scrollUp(driverType);break;} //need refinement
			case "setBooleanValue": {this.setBooleanValue(driverType, parameters[4], parameters[5]); break;}
			case "test": {this.test(driverType, parameters[7], Arrays.copyOfRange(parameters, 8, parameters.length));break;}
			case "wait": {this.wait(waitSec); break;}
			case "waitUntilPresent": {this.waitUntilElement (driverType, waitSec, parameters[4]);break;}
			case "waitUntilPage": {this.waitUntilPage(driverType, waitSec, parameters[4]); break;}
			default: {
				if (driverType.equals("Android")) {
					switch (parameters[2]) {
						case "checkPageOverlap": {and.checkPageOverlap(waitSec, parameters[4]);break;}
						case "clearNumPad": {and.clearNumPad(waitSec, parameters[4]); break;}						
						case "enterNumPad": {and.enterNumPad(waitSec, parameters[4]); break;}
						case "enterNumPadOK": {and.enterNumPadOK(waitSec, parameters[4]);break;}
						case "enterPurchaseAmount": {and.enterPurchaseAmount(waitSec, parameters[4]); break;}
						case "login": {and.login(waitSec); break;}
						case "merchantPassword": {and.merchantPassword(waitSec, parameters[4]);break;}
						case "merchantSignin": {and.merchantSignin(waitSec, parameters[4]); break;}
						case "multiplePurchase": {and.multiplePurchase(waitSec, parameters[4],parameters[5],parameters[6],parameters[7],parameters[8],parameters[9] ); break;}
						//notice for multiplePurchase, variable waitSec represents number of purchases to be made, not seconds to wait
						case "pickDate": {and.pickDate(parameters[4],parameters[5],parameters[6]);break;}
						case "reachPageByProcess": {and.reachPageByProcess(waitSec, parameters[4], parameters[5]);break;}
						case "reachPage": {and.reachPage("GP", parameters[4]);break;}
						case "showSideMenu": {and.showSideMenu(waitSec);break;}
						case "sign": {and.sign();break;}
						case "singlePurchase": {and.singlePurchase(waitSec,parameters[4],parameters[5],parameters[6],parameters[7] ); break;}
						case "singlePurchaseUntil": {and.singlePurchaseUntil(waitSec,parameters[4],parameters[5],parameters[6],parameters[7], parameters[8]);break;}
						default: log.logConsole(parameters[2] + " not found. No such method exists.");
					}
				}
				else {
//					this.nothing();
				}
			}
			
			
			}

	}
	
	public MWAndroidDriver getAndroidDriver() {
		return and;
	}
	
	public WebElement findByURI(String driverName, String URI) throws IOException {
		WebElement element;
		switch (driverName) {
		case "Android": {
			DesiredCapabilities andElement = (DesiredCapabilities) cap.getCapability("AndroidElement");
			pageElement pagee = (pageElement) andElement.getCapability(URI);
			String temp = (String) pagee.getPath();
			element = and.findElementByXPath(temp);
			break;
		}
		case "Chrome": {
			DesiredCapabilities chdElement = (DesiredCapabilities) cap.getCapability("ChromeElement");
			element = chd.findElement(By.xpath((String) ((pageElement) chdElement.getCapability(URI)).getPath()));
			break;
		}
		case "Firefox": {
			DesiredCapabilities ffdElement = (DesiredCapabilities) cap.getCapability("FirefoxElement");
			element = ffd.findElement(By.xpath((String) ((pageElement) ffdElement.getCapability(URI)).getPath()));
			break;
		}
		default: {
			log.logConsole("Driver Name " + driverName + " is unrecognized.");
			element = null;
		}
		}
		return element;
	}
	
	public Object chooseDriver (String driverName) throws IOException {
		switch(driverName) {
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
			return null;
		}
		}
	}
	
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
			return null;
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
			return null;
		}
		}
	}
	
	public void clickButton(String driverName, String URI) throws IOException {
		try {
			this.findByURI(driverName, URI).click();
		}
		catch (Exception e) {
			log.logConsole("exception caught during button click, failed to find " + URI);
		}
	}
	
	public void inputText(String driverName, String URI, String value) throws IOException {
		try {

			this.findByURI(driverName, URI).sendKeys(value);
		}
		catch (Exception e) {
			log.logConsole("exception caught during text entry, failed to find " + URI);
		}
			
	}
	
	public void setBooleanValue(String driverName, String URI, String value) throws IOException {
		String expectedValue = "false";
		if (value.equals("yes")) {
			expectedValue = "true";
		}
		try {
			if (this.findByURI(driverName, URI).getAttribute("checked").equals(expectedValue)) {
				//do nothing
			}
			else {
				this.findByURI(driverName, URI).click();
			}
		}
		catch (Exception e) {
			log.logConsole("exception caught during click, failed to find " + URI);
		}
	}
	
	public void launch(String driverName) throws InterruptedException, IOException {
		Object obj = this.chooseDriver(driverName);
		switch (driverName) {
		case "Android": {
			and.launch(120);
			break;
		}
		default: {
			((WebDriver) obj).get((String) cap.getCapability("StartingURL"));
		}
		}

	}
	
	public WebElement waitUntilElement (String driverName, int timeOutSec, String URI) throws IOException, InterruptedException {
		Object obj = this.chooseDriver(driverName);
		if (timeOutSec==0 ) {
			timeOutSec = 10;
		}
		switch (driverName) {
		case "Android": {
			return and.waitUntilElement(timeOutSec, URI);
		}
		default: {
			WebElement waitElement = (new WebDriverWait(((WebDriver) obj), timeOutSec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath((String) this.chooseCap(driverName).getCapability(URI))));	
			return waitElement;
		}
		}
	}
	
	public void nothing () {
		chd.get("http://integration-tms.mobeewave-hive.com/#/login");

		chd.get((String) cap.getCapability("StartingURL"));	
		ffd.get((String) cap.getCapability("StartingURL"));
		WebElement loginButton = (new WebDriverWait(chd, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("login")));		

		chd.findElement(By.name("username")).sendKeys("admin123");
		chd.findElement(By.name("password")).sendKeys("Mobeewave2015");
		loginButton.click();
	}
	
	public String findCurrentPage (String driverName) throws InterruptedException, IOException {
//		Thread.sleep(500);
		
			String currentPage = "";
			log.logConsole("");
			System.out.print("Current Page Verification in progress");
/*			DesiredCapabilities pages = this.choosePage(driverName);
//			System.out.println(pages);
			
			Iterator<Object> mPage =  pages.asMap().values().iterator();

				while (mPage.hasNext()) {
				*/
			for (int i=0; i<mPageList.size(); i++) {
				try {
//					mobilePage tempPage =(mobilePage) mPage.next();	
					mobilePage tempPage =(mobilePage) mPageList.elementAt(i);	
					String name =  tempPage.getPageName();
//					System.out.println("name is: " + name);
					String id =  tempPage.getUID();
//					System.out.println("id is: " + id);
					String value =  tempPage.getUValue();
//					System.out.println("value is: " + value);
					if (!(id.isEmpty())) {
					DesiredCapabilities driverEle = this.chooseCap(driverName);
					pageElement pEle = (pageElement) driverEle.getCapability(id);
					String xp = pEle.getID();
					String title = this.findByURI(driverName,xp).getText();
//					System.out.println("title is: "+ title);
						if (title.equals(value)) {
							log.logConsole("Current page is: "+name);
							log.logFile("page count is: " +((mobilePage) mPageList.elementAt(i)).get("count"));
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

	
	
public String reachPageByProcess (String driverName, int timeoutIteration,String processName, String targetPage) throws InterruptedException, IOException {
	String currentPage = this.findCurrentPage(driverName);
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
//					this.launchProcess(currentPage);
					break;
				}	//case
				case "login": {
//					this.loginProcess(currentPage);
					break;
				}	//case
				default: {
					log.logFailure("Process Undefined: Please check csv file used correct process name.");
					return currentPage;
				}	//default
			}	//switch
		}			//else
		previousPage = currentPage;
		currentPage = this.findCurrentPage(driverName); //decide what to do depending on which page the user is on

	}	//while
	return currentPage;
}



public String waitUntilPage (String driverName, int timeout, String targetPage) throws InterruptedException, IOException {
	LocalTime startT = LocalTime.now();
	String currentPage = this.findCurrentPage(driverName);
	while ((!targetPage.equals(currentPage)) && (ChronoUnit.SECONDS.between(startT, LocalTime.now())<timeout)) {
		currentPage = this.findCurrentPage(driverName);
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

public boolean test(String driverName, String method, String[] contents) throws InterruptedException, IOException {
/* Pre: 	waitSec is a positive integer, method is either "equal" or "isOnPage" (to be expanded)
* 			fieldName is a URI - if method is "isOnPage", fieldName can be null as it is not used.
* Post: 	The method returns a boolean state depending on whether the expected value equal to the actual value.
*/	
System.out.println("driverName is: " + driverName);
System.out.println("method is: " + method);
System.out.println("contents are: " +log.logArray(contents));

	boolean testResult = false;
	String actualValue = "";
			switch (method) {
		case "equal": {
			String fieldName = contents[0];
			String expectedValue = contents[1];
			try {
			actualValue = this.findByURI(driverName, fieldName).getText();
			}
			catch (Exception e) {
			actualValue = "element not found";
			}
			return log.logTestResult(fieldName, actualValue, expectedValue);
		}
		case "isOnPage": {
			String expectedValue = contents[1];
			actualValue = this.findCurrentPage(driverName);
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

public void scrollToBottom(String driverName) throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver(driverName);

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public void scrollToTop(String driverName) throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver(driverName);

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, 0)");
}

public void scrollDown(String driverName) throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver(driverName);

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public void scrollUp(String driverName) throws IOException {
	JavascriptExecutor js = (JavascriptExecutor) this.chooseDriver(driverName);

    //This will scroll the web page till end.		
    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
}

public Vector sortby (Vector ve, String fieldName) {
	Vector sortedList = new Vector();
	for (int i =0; i< ve.size(); i++) {
		this.addObjectToSortedList(sortedList, 0, ve.size(), ve.elementAt(0), fieldName);
	}
	return ve;
}

public Vector addObjectToSortedList (Vector ve, int startPos, int endPos, Object pe, String fieldName) {
	if (startPos <0) { startPos = 0;}
	if (endPos > ve.size()) {endPos = ve.size();}
	int midPos;
	
	if ((endPos-startPos) == 0) {
		if (this.compareInVector(ve, startPos, pe, fieldName)<0) {
			ve.insertElementAt(pe, startPos+1);
		}
		else {
			ve.insertElementAt(pe, startPos);
		}
	}
	
	if ((endPos-startPos) %2 == 0) {
		midPos = startPos + (endPos-startPos)/2;
	}
	else {
		midPos = startPos + (endPos-startPos-1)/2;
	}
	
	if (this.compareInVector(ve, midPos, pe, fieldName)==0) {
		ve.insertElementAt(pe, midPos);
		return ve;
	}
	else if (this.compareInVector(ve, midPos, pe, fieldName)<0) {
		this.addObjectToSortedList(ve, midPos+1, endPos, pe, fieldName);			
	}
	else if (this.compareInVector(ve, midPos, pe, fieldName)>0) {
		this.addObjectToSortedList(ve, startPos, midPos, pe, fieldName);			
	}
	
	return ve;
}


public int compare (Object p1, Object p2, String fieldName) {
	int result = ((String) ((BaseElements) p1).get(fieldName)).compareTo((String) ((BaseElements) p2).get(fieldName));
	return result;
}

public int compareInVector (Vector v1, int pos, Object p2, String fieldName) {
	int result = this.compare((pageElement) v1.elementAt(pos), p2, fieldName);
	return result;
}

public void close(String driverName) throws IOException {
try {
	Object obj = this.chooseDriver(driverName);

	DesiredCapabilities page = this.choosePage(driverName);
	String filePath = (String)cap.getCapability(driverName);
	customizedCSVReader cr = new customizedCSVReader("");
	cr.writeElementFile(filePath, page, mPageList);
	((WebDriver) obj).close();
}
catch (Exception e) {
	log.logConsole("error encountered when closing "+driverName);
}

}

}
	

