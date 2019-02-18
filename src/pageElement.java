import java.io.IOException;

/* This class allows user to record the xPath information of a WebElement.
 * not sure whether it should outright extend WebElement yet. will decide later.
 * 
 */
public class pageElement {
	private String ID;				//read in from the element configuration file
	private String xPath;			//read in from the element configuration file
	private String page;			//read in from the element configuration file
	private String field;			//read in from the element configuration file
	private String action;			//read in from the element configuration file
	private String isUniqueID;		//read in from the element configuration file
	private String expectedValue;	//read in from the element configuration file
	private int x1,x2,y1,y2;		//location coordinates corresponds to the WebElement location vaiables
	private MWLogger logs;
	
	public pageElement (String eID, String ePath, String ePage, String eField, String eAction, String isUnique, String eValue, MWLogger log) {
		//constructor
		ID = eID;
		xPath = ePath;
		page = ePage;
		field = eField;
		action = eAction;	
		isUniqueID = isUnique;
		expectedValue = eValue;
		logs = log;
	}
	
	public pageElement (String[] record, MWLogger log) {
		//constructor
		String[] data = new String[7];
		for (int i =0; i<7; i++) {
			if ((i<record.length) && (record[i] != null)) {
				data[i]=record[i];
			}
			else {
				data[i]="";
			}
		}
		ID = data[0];
		xPath = data[1];
		page = data[2];
		field = data[3];
		action = data[4];	
		isUniqueID = data[5];
		expectedValue = data[6];
		logs = log;
	}
	

/*****************Set methods*************************************/
	public pageElement (String eID, String ePath) {
		ID = eID;
		xPath = ePath;
	}

	public void setID(String eID) {
		ID = eID;
	}
	public void setPath(String ePath) {
		xPath = ePath;
	}
	public void setPage(String ePage) {
		page = ePage;
	}
	public void setField(String eField) {
		field = eField;
	}
	public void setAction(String eAction) {
		action = eAction;
	}
	
	public void setCoordinates(int xStart, int xEnd, int yStart, int yEnd) {
		x1 = xStart;
		x2 = xEnd;
		y1 = yStart;
		y2 = yEnd;
	}

	/*****************Get methods*************************************/
	
	public String getID() {
		return ID;
	}
	public String getPath() {
		return xPath;
	}
	public String getPage() {
		return page;
	}
	public String getField() {
		return field;
	}
	public String getAction() {
		return action;
	}
	
	public int getCoordinate (String CoordName) throws IOException {
		switch (CoordName) {
		case "x1": {return x1;}
		case "x2": {return x2;}
		case "y1": {return y1;}
		case "y2": {return y2;}
		default: {
			logs.logFile(CoordName + " is not a valid name. Please use x1, x2, y1 or y2 as input parameters");
			return 0;
		}

		}
	}
	
	
	public String get(String fieldName) throws IOException {
/*	Pre: none
 * 	Post: If fieldName matches one of the predefined variable name below, the method will return 
 *  the corresponding variable value as a string, otherwise it will return an empty string.
 */
		switch (fieldName) {
		case "ID": { return ID;}
		case "xPath": { return xPath;}
		case "page": { return page;}
		case "field": { return field;}
		case "action": { return action;}
		case "isUniqueID": { return isUniqueID;}
		case "expectedValue": { return expectedValue;}
		case "x1": { return Integer.toString(x1);}
		case "x2": { return Integer.toString(x2);}
		case "y1": { return Integer.toString(y1);}
		case "y2": { return Integer.toString(y2);}
		default: { 
			logs.logFile(fieldName + " is not a valid field name.");			
			return "";}
		}
	}
}
