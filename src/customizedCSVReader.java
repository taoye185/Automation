import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Iterator;

import org.openqa.selenium.remote.DesiredCapabilities;
import com.opencsv.CSVReader;

import io.appium.java_client.remote.MobileCapabilityType;

public class customizedCSVReader {
/*	This class is used to handle all the file input from configuration file
 * 
 */
	String filePath;
	DesiredCapabilities pairing;
//	String[][] temp;
	MWLogger log;

	
	public customizedCSVReader (String csvFilePath) throws IOException {
		filePath = csvFilePath;
		pairing = new DesiredCapabilities();
		log = new MWLogger();
	}
	
	public customizedCSVReader () throws IOException {
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
	
	public DesiredCapabilities readAndCreateDriver (String path, DesiredCapabilities cap, MWLogger log) throws IOException {
		FileReader fileReader = new FileReader(path); 
	    CSVReader csvReader = new CSVReader(fileReader); 
	    String[] nextRecord; 
	    int i = 0;
	    DesiredCapabilities driverCap = new DesiredCapabilities();
	    while ((nextRecord = csvReader.readNext()) != null) { 
	     	if (i>0 && nextRecord[0].equals("1")) {		//ignore title row(i==0) and inactive row (nextRecord[0]!=1)
	     		MWDriver driver = new MWDriver (nextRecord[1], nextRecord[3], cap, log);
	     		driverCap.setCapability(nextRecord[1], driver);
	  		} //if
	        i++;
	    }
	    csvReader.close();
		return driverCap; 
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
	
	public void readAndExecuteTestGroup (DesiredCapabilities cap, DesiredCapabilities driver, MWLogger log) throws MalformedURLException, IOException, InterruptedException {
/*	Pre: cap is a DesiredCapability with the relevant configurations already set correctly.
 *  path is a valid relative filepath that the corresponding configuration file would be read
* 	Post: The configuration settings are read and stored in the "paring" DesiredCapabilities
*/
		FileReader filereader = new FileReader((String) cap.getCapability("groupFile")); 
        CSVReader csvReader = new CSVReader(filereader); 
        String[] nextCase; 
        int caseRow = 0;
//        MWDriver mwd = new MWDriver (cap, log);
        log.initiateReport(cap);
        while ((nextCase = csvReader.readNext()) != null) { 
            if (caseRow > 0 && (nextCase[0].equals("1"))) { 	            //unless it is title row, construct and execute the method
            	String casefile = nextCase[2];
            	

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
    	            	MWDriver mwd = (MWDriver) driver.getCapability(nextRecord[1]);
    	            	switch (nextRecord[2]) {
/*    	            	case "open": {
    	            		mwd = new MWDriver (cap, log);
    	            	}
*/    	            	case "close": {
    	            		mwd.close();
    	            	}
    	            	default: {
    	    	            mwd.testScenarioConstructor(nextRecord); 
    	            	}
    	            	}

            		//it is assumed that the first column of the config file contains method name to be called
            		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
    	            }
    	            row++;
    	        } 
    	        casecsvReader.close();
    			log.logColorText(log.getLogCaseColor(), "Test Case: " + nextCase[1] + " completed.");
//    	        Reporter.log("<font color='blue'>Test Case: " + nextCase[0] + " completed.</font>");
    			log.report("-----------------------------------<br>");
//    			androidMobileDriver.quit();
    		//it is assumed that the first column of the config file contains method name to be called
    		//it is assumed that from the second column on and up to the 11th column, the config file contains parameters to be used in the called method
            }
            caseRow++;
        } 
        csvReader.close();	
 //       mwd.close();
 //       log.publishReport();
 //       this.test(mwd);
	}

	/*
	public void test(MWDriver mwd) throws IOException {
		
		String fileName = ".\\Test CSV.csv";
try {
	FileReader filereader = new FileReader("fileName"); 
	filereader.close();
}
catch (Exception e) {
	log.logConsole("error when opening filereader");
}
		File statText = new File(fileName);
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        BufferedWriter writer = new BufferedWriter(osw);
		DesiredCapabilities pages = mwd.choosePage("Android");
		Iterator<Object> mPage =  pages.asMap().values().iterator();
			while (mPage.hasNext()) {
				mobilePage tempPage =(mobilePage) mPage.next();	
				String name =  tempPage.getPageName();
				int count =  tempPage.returnCount();
	    		writer.write(name+","+count);
	    		writer.newLine();
	    		writer.flush();					
				
			}	//try
	}
	*/

	public void close(DesiredCapabilities driver, MWLogger log) throws IOException {

		Iterator<Object> dr =  driver.asMap().values().iterator();
		while (dr.hasNext()) {				//create an ElementList containing all pages
			((MWDriver)dr.next()).close();
		}
        log.publishReport();
	}
	
	
	
	public DesiredCapabilities[] readElementFile (String filePath) throws IOException {
		DesiredCapabilities elements = new DesiredCapabilities();
		DesiredCapabilities page = new DesiredCapabilities();
		
        FileReader filereader = new FileReader(filePath);  
        CSVReader csvReader = new CSVReader(filereader); 
        String[] nextRecord; 
        int i = 0;
        while ((nextRecord = csvReader.readNext()) != null) { 
        	nextRecord = this.padArray(nextRecord, 8);
        	if(i>0) {
        		pageElement pe = new pageElement(nextRecord, log);
        		elements.setCapability(nextRecord[0], pe);
        		//establish the ElementName->ElementXPath mapping
        		if (!page.asMap().containsKey(nextRecord[2])) {
 //       			log.logFile("pe is: " +pe.getID());
        			mobilePage newPage = new mobilePage(nextRecord[2], pe, log);
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
    				if (!nextRecord[7].equals("")) {
        				((mobilePage) page.getCapability(nextRecord[2])).setCount(Integer.parseInt(nextRecord[7]));    					
    				}

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
	
	
	
	
	
	public void writeElementFile (String filePath, ElementList page) throws IOException {
		log.logConsole("writeElement is called");
		DesiredCapabilities elements = new DesiredCapabilities();
//		DesiredCapabilities page = new DesiredCapabilities();
		
        FileReader filereader = new FileReader(filePath);  
        CSVReader csvReader = new CSVReader(filereader); 
        String output = "";
        String[] nextRecord; 
        int i = 0;
        while ((nextRecord = csvReader.readNext()) != null) { 
        	if (i>0) {
        		output += "\r\n";
        	}
        	nextRecord = this.padArray(nextRecord, 8);
        	output += this.writeArrayAsCSV(nextRecord,7);
        	if(i>0 && (nextRecord[5].equals("yes"))) {
        		int weight=0;
 /*       		
        		if(nextRecord.length>=8) {
        			if (!nextRecord[7].equals(null)) {
        				if(!nextRecord[7].equals("")) {
        					weight = Integer.parseInt(nextRecord[7]);
        				}
        			}

        		}
*/
        		mobilePage tempPage = (mobilePage) page.getElement(nextRecord[2]);
        		if (!tempPage.equals(null)) {
        			weight = tempPage.returnCount();
        		}
//        		weight += ((mobilePage) cap.getCapability(nextRecord[2])).returnCount();
        		log.logConsole("weight is: " + weight);
        		output = output  + weight;
 
        	}
            i++;
        } 
        csvReader.close();
        log.logConsole(filePath + "has been read.");

		File statText = new File(filePath);
        FileOutputStream is = new FileOutputStream(statText);
        OutputStreamWriter osw = new OutputStreamWriter(is);    
        BufferedWriter wr = new BufferedWriter(osw);
		wr.write(output);
		wr.flush();
		log.logConsole(filePath + "has been written.");
		wr.close();
	}
	
	public String writeArrayAsCSV (String[] array, int desiredLength) {
		String arrayString = "";
		for (int i=0; i<desiredLength; i++) {
			String elementString = "";
			if (i>=array.length) {
				elementString = "";
			}
			else if (array[i].equals(null)) {
				elementString = "";
			}
			else {
				elementString = array[i];
			}
			arrayString = arrayString +elementString+",";
		}
		return arrayString;
	}
	
	public String[] padArray (String[] array, int desiredLength) {
		String[] newArray = new String [desiredLength];
		for (int i=0; i<desiredLength; i++) {
			if (i<array.length) {
				newArray[i] = array [i];
			}
			else {
				newArray[i] = "";
			}
		}
		return newArray;
	}
}
