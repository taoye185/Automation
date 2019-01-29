import java.util.Vector;

public class mobilePage  {
	
//	private Vector pe = new Vector();
	private ElementList pe;
	String name;
	String uID = "";
	String uValue = "";
	int findCount;
	MWLogger logs;

	public mobilePage (String[] elements, MWLogger log) {
		//constructor
		pe = new ElementList("pageElement", log);
		logs = log;
		for (int i=0;i<elements.length;i++) {
			pe.addElement(elements[i]);
		}
		findCount = 0;
	}
	
	public mobilePage (String pageName, pageElement elements, MWLogger log) {
		//constructor
		name = pageName;
		logs = log;

		try {pe.addElement(elements);
		}
		catch (Exception e) {
			pe = new ElementList("pageElement", log);
			pe.addElement(elements);
		}
	}
	
	
	public mobilePage (String pageName, pageElement[] elements, MWLogger log) {
		//constructor
		name = pageName;
		logs = log;
		pe = new ElementList("pageElement", log);
		for (int i=0;i<elements.length;i++) {
			pe.addElement(elements[i]);
		}
	}

	public boolean checkPageOverlap() {
		boolean isOverlapping = false;
		int numElement = pe.size();
		for (int i = 0; i<numElement; i++) {
			for (int j=i; j<numElement;j++) {
				isOverlapping = (this.isOverlap(pe.elementAt(i), pe.elementAt(j))||isOverlapping);
			}
		}
		return isOverlapping;
	}
	
	public boolean isOverlap(Object pe1, Object pe2) {
		return true;
	}
	
/*****************Get methods*************************************/
	
	public pageElement getElementByName (String URI) {
		pageElement pe = new pageElement("a","b");
		return pe;
	}
	
	public Vector getAllElements( ) {
		return pe;
	}
	
	public String getPageName() {
		return name;
	}
	
	
	public void addElement(pageElement we) {
		pe.addElement(we);
	}
	
	public void setUniqueID(String ID, String Value) {
		uID = ID;
		uValue = Value;
	}
	
	public void setCount (int count) {
		findCount = count;
	}
	public String getUID() {
		return uID;
	}
	
	public String getUValue() {
		return uValue;
	}
	
	
	public void incrementCount () {
		findCount ++;
	}
	
	public int returnCount() {
		return findCount;
	}
	
	public String get(String fieldName) {
		switch (fieldName) {
		case "pageElementList": { return pe.toString();}
		case "name": { return name;}
		case "uID": { return uID;}
		case "uValue": { return uValue;}
		case "count": { return Integer.toString(findCount);}
		default: { return "";}
		}
	}

}


