

/* This class allows user to record the xPath information of a WebElement.
 * not sure whether it should outright extend WebElement yet. will decide later.
 * 
 */
public class pageElement {
	private String ID;
	private String xPath;
	private String page;
	private String field;
	private String action;
	private String isUniqueID;
	private String expectedValue;
	private int x1,x2,y1,y2;

	
	public pageElement (String eID, String ePath, String ePage, String eField, String eAction, String isUnique, String eValue) {
		ID = eID;
		xPath = ePath;
		page = ePage;
		field = eField;
		action = eAction;	
		isUniqueID = isUnique;
		expectedValue = eValue;
	}
	
	public pageElement (String[] record) {
		for (int i =0; i<7; i++) {
			if (record[i] != null) {
			}
			else {
				record[i]="";
			}
		}
		ID = record[0];
		xPath = record[1];
		page = record[2];
		field = record[3];
		action = record[4];	
		isUniqueID = record[5];
		expectedValue = record[6];
	}
	
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
	
	public int getCoordinate (String CoordName) {
		switch (CoordName) {
		case "x1": {return x1;}
		case "x2": {return x2;}
		case "y1": {return y1;}
		case "y2": {return y2;}
		default: System.out.println(CoordName + " is not a valid name. Please use x1, x2, y1 or y2 as input parameters");
		return 0;
		
		}
		

	
	}
	
	
	public Object get(String fieldName) {
		switch (fieldName) {
		case "ID": { return ID;}
		case "xPath": { return xPath;}
		case "page": { return page;}
		case "field": { return field;}
		case "action": { return action;}
		case "isUniqueID": { return isUniqueID;}
		case "expectedValue": { return expectedValue;}
		case "x1": { return x1;}
		case "x2": { return x2;}
		case "y1": { return y1;}
		case "y2": { return y2;}
		default: { return "";}
		}
	}
}
