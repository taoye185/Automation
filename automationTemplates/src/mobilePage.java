import java.util.Vector;

import org.openqa.selenium.WebElement;

public class mobilePage {
	
	private Vector pe = new Vector();
	String name;
	String uID = "";
	String uValue = "";

	public mobilePage (String[] elements) {
		for (int i=0;i<elements.length;i++) {
			pe.addElement(elements[i]);
		}
	}
	
	public mobilePage (String pageName, pageElement elements) {
		name = pageName;
		pe.addElement(elements);
	}
	
	
	public mobilePage (String pageName, pageElement[] elements) {
		name = pageName;
		for (int i=0;i<elements.length;i++) {
			pe.addElement(elements[i]);
		}
/*
		int totalLength = elements.length;
		int elementInPage = 0;
		for (int i=0;i<totalLength;i++) {
			if (elements[i].getPage()==pageName) {
				pe[elementInPage] = elements[i];
				elementInPage ++;
			}
		}
		//constructor to be implemented
*/	}

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
	
	public String getUID() {
		return uID;
	}
	
	public String getUValue() {
		return uValue;
	}
	
	


}
