import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Reporter;

import com.opencsv.CSVReader;

import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class customizedCSVReader {
/*	This class is used to handle all the file input from configuration file
 * 
 */
	String filePath;
	DesiredCapabilities pairing;
	String[][] temp;
	MWLogger log;

	
	public customizedCSVReader (String csvFilePath) {
		filePath = csvFilePath;
		pairing = new DesiredCapabilities();
		log = new MWLogger();
	}
	
	public customizedCSVReader () {
		filePath = "";
		pairing = new DesiredCapabilities();
		log = new MWLogger();
	}
	
	public DesiredCapabilities readPairing (String path) throws IOException {
/*	Pre: path is a valid relative filepath that the corresponding configuration file would be read
 * 	Post: The configuration settings are read and stored in the "paring" DesiredCapabilities
 */
		FileReader fileReader = new FileReader(path); 
	    CSVReader csvReader = new CSVReader(fileReader); 
	    String[] nextRecord; 
	    int i = 0;
	    while ((nextRecord = csvReader.readNext()) != null) { 
	     	if (i>0 && nextRecord[0].equals("1")) {		//ignore title row(i==0) and inactive row (nextRecord[0]!=1)
	     		pairing.setCapability(nextRecord[1], nextRecord[2]);
	  		} //if
	        i++;
	    }
	    csvReader.close();
		return pairing; 
	}
	
	public String returnPairingValue (String key) {
/*	Pre: The configuration value type is String or a type that can be changed to String
 * 	Post: The corresponding value is returned as a String. If the "pairing" configuration 
 *  is empty or if the key is not found, return an empty string
 */
		String value="";
		try {
		value = (String) pairing.getCapability(key);
		}
		catch (Exception e) {
			System.out.println("configuration "+key+" is not found.");
		}
		return value;
	}

	public DesiredCapabilities readRowPairing (String path) throws IOException {
/*	Pre: path is a valid relative filepath that the corresponding configuration file would be read
 * 	This case is different than the readPairing method because the configuration are written 
 * 	in the same row instead of in the same column
 * 	Post: The configuration settings are read and stored in the "paring" DesiredCapabilities
 */
		FileReader filereader = new FileReader(path); 
		//read the remaining capability value from the configuration file
        CSVReader csvReader = new CSVReader(filereader); 
        String[] nextRecord; 
        int j = 0;
        while ((nextRecord = csvReader.readNext()) != null) { //this code is only good for one device initiation. will need to modify to accommodate for multiple devices
	        	if (j>0 && Integer.parseInt(nextRecord[0])==1) {			            		//unless this is title row, set the capabilities
     			pairing.setCapability(MobileCapabilityType.APPIUM_VERSION, nextRecord[1]); 
     			pairing.setCapability(MobileCapabilityType.PLATFORM_VERSION, nextRecord[2]);
     			pairing.setCapability(MobileCapabilityType.PLATFORM_NAME,nextRecord[3]);
     			pairing.setCapability(MobileCapabilityType.AUTOMATION_NAME,nextRecord[4]);
     			pairing.setCapability(MobileCapabilityType.DEVICE_NAME, nextRecord[5]);
      		}
            j++;
        } 
            csvReader.close();
		return pairing;
		
	}
	
	public void setPath (String path) {
		filePath = path;
	}
	
	public void readAndExecuteTestGroup (DesiredCapabilities cap) throws MalformedURLException, IOException, InterruptedException {
/*	Pre: cap is a DesiredCapability with the relevant configurations already set correctly.
 *  path is a valid relative filepath that the corresponding configuration file would be read
* 	Post: The configuration settings are read and stored in the "paring" DesiredCapabilities
*/
		FileReader filereader = new FileReader((String) cap.getCapability("groupFile")); 
        CSVReader csvReader = new CSVReader(filereader); 
        String[] nextCase; 
        int caseRow = 0;
        while ((nextCase = csvReader.readNext()) != null) { 
            if (caseRow > 0) { 	            //unless it is title row, construct and execute the method
            	String casefile = nextCase[2];
            	
            	MWDriver mwd = new MWDriver (cap);
 /*
            	MWAndroidDriver androidMobileDriver = null;
            	if (cap.getCapability("Android") != null) {
    			androidMobileDriver = new MWAndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
    			System.out.println(nextCase[1] + " androidMobileDriver created.");
            	}//construct a MWAndroidDriver to allow more flexibility and encapsulation compared to the standard AndroidDriver	
*/
//            	androidMobileDriver.logColorText("blue", "Test Case: " + nextCase[0] + " initiated.");

    	        FileReader casefilereader = new FileReader(casefile); 
    	        CSVReader casecsvReader = new CSVReader(casefilereader); 
    	        String[] nextRecord; 	    			
    	        int row = 0;
    	        while ((nextRecord = casecsvReader.readNext()) != null) { 
    	            if (row > 0 && (nextRecord[0].equals("1"))) { 	            //unless it is title row, construct and execute the method
    	            mwd.testScenarioConstructor(nextRecord); 
            		//it is assumed that the first column of the config file contains method name to be called
            		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
    	            }
    	            row++;
    	        } 
    	        casecsvReader.close();
    			log.logColorText("blue", "Test Case: " + nextCase[0] + " completed.");
//    	        Reporter.log("<font color='blue'>Test Case: " + nextCase[0] + " completed.</font>");
    			Reporter.log("-----------------------------------<br>");
//    			androidMobileDriver.quit();
    		//it is assumed that the first column of the config file contains method name to be called
    		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
            }
            caseRow++;
        } 
        csvReader.close();	
	}
	
	public DesiredCapabilities[] readElementFile (String filePath) throws IOException {
		DesiredCapabilities elements = new DesiredCapabilities();
		DesiredCapabilities page = new DesiredCapabilities();
		
        FileReader filereader = new FileReader(filePath);  
        //reads the elementConfig configuration file. the ID field of the file are assumed to be unique
        CSVReader csvReader = new CSVReader(filereader); 
        String[] nextRecord; 
        int i = 0;
        while ((nextRecord = csvReader.readNext()) != null) { 
        	if(i>0) {
        		pageElement pe = new pageElement(nextRecord);
        		elements.setCapability(nextRecord[0], pe);
        		//establish the ElementName->ElementXPath mapping
        		if (!page.asMap().containsKey(nextRecord[2])) {
        			mobilePage newPage = new mobilePage(nextRecord[2], pe);
        			page.setCapability(nextRecord[2], newPage);
  //      			pageName.put(nextRecord[2], newPage);
        			//establish the PageName -> mobilePage object mapping
        		}
        		else {
        			((mobilePage) page.getCapability(nextRecord[2])).addElement(pe);
        			// if a PageName already exist, simply record the element under the existing page
        		}
    			if (nextRecord[5].equals("yes")) {
    				((mobilePage) page.getCapability(nextRecord[2])).setUniqueID(nextRecord[0], nextRecord[6]);
    				//If an element is identified as a "unique identifier" for a page, record this 
    				//information by set the Unique ID and Unique Value variables for the mobilePage
//    				System.out.println( nextRecord[0]+" is a unique element with value: " + nextRecord[6]);
    			}
//        		System.out.println(pageName.get(nextRecord[2]).getPageName());
//        		System.out.println(pageName.get(nextRecord[2]).getAllElements().toString());
        	}
            i++;
        } 
        csvReader.close();
		
		
		DesiredCapabilities[] elementAndPage = new DesiredCapabilities[2];
		elementAndPage[0] = elements;
		elementAndPage[1] = page;
		return elementAndPage;
	}
}
