import java.io.IOException;
import java.util.Vector;

/*	This class extends Vector and provide the functionality to sort Objects (mobilePages or pageElements)
 *  according to the value of one of its attributes 
 * 
 * 
 * 
 */
public class ElementList extends Vector {
	String elementType;
	Vector list;
	MWLogger log;
	
	
	public ElementList(String type, Vector vl,MWLogger logs) {
		//constructor
		elementType = type;
		list = vl;
		log = logs;
	}
	public ElementList(String type,MWLogger logs) {
		//constructor
		elementType = type;
		list = new Vector();
		log = logs;
	}

	
	/*****************Get methods*************************************/
	public String get(int pos, String fieldName) throws IOException {
/* Pre: pos is an index number that does not exceed the length of the ElementList, 
 * fieldName matches the variable name of the data type specified by the "elementType" variable.
 * Post: The value of the desired variable at the specified position is specified. 
 * If pos exceeds the length of the ElementList, or if the elementType does not match,
 * an empty string is returned;
 */
		log.logFile("method get ("+pos + ", " + fieldName  +") is called.");
		if (this.size()==0) {
			return "";	//return empty string if the ElementList is empty
		}
		if (pos>=this.size()) {
			log.logConsole("Index " + pos + " exceeds the size of current ElementList, which is " + this.size());
		
			pos = Math.max(0, this.size()-1); 	//find the last element if the index exceeds ElementList size
		}
		switch (elementType) {
		case "pageElement": {
			return ((pageElement) this.elementAt(pos)).get(fieldName);
		}
		case "mobilePage": {
			return ((mobilePage) this.elementAt(pos)).get(fieldName);		
		}
		default: {
			log.logConsole("elementType not recognized.");
			return "";
		}
		}
	}
	
	public Object getElement(String ID) throws IOException {
		log.logFile("method get ("+ID   +") is called.");
		switch (elementType) {
		case "pageElement": {
			for (int i=0; i<this.size(); i++) {
				if (((pageElement) this.elementAt(i)).getID().equals(ID)) {
					return (pageElement) this.elementAt(i);
				}
			}
			return null;
		}
		case "mobilePage": {
			for (int i=0; i<this.size(); i++) {
				if (((mobilePage) this.elementAt(i)).getPageName().equals(ID)) {
					return (mobilePage) this.elementAt(i);
				}
			}
			return null;
		}
		default: {
			return null;
		}
		}
	}
	
	public ElementList sortby (String elementType, String fieldName, String order, String fieldType) throws IOException {
		log.logFile("method sortby ("+elementType + ", " + fieldName +","+ order +") is called.");
		ElementList sortedList = new ElementList(elementType,log);
		for (int i =0; i< this.size(); i++) {
			this.addObjectToSortedList(sortedList, 0, sortedList.size(), this.elementAt(i), fieldName,  order, fieldType);
		}
		return sortedList;
	}
	


	public ElementList addObjectToSortedList (ElementList ve, int startPos, int endPos, Object pe, String fieldName, String order, String fieldType) throws IOException {
		log.logFile("method addObjectToSortedList ("+ve + ", " + startPos + ", " + endPos+ ", " + pe+ ", " + fieldName + ", " + order+") is called.");
//		log.logFile(this.printList(ve, startPos, endPos, pe, fieldName));
		if (startPos <0) { startPos = 0;}
		if (endPos > ve.size()) {endPos = ve.size();}
		int midPos;
		
		if (ve.size()==0) {
			ve.add(pe);
			return ve;
		}
		else if ((endPos-startPos) == 0) {
			switch (order) {
			case "Asc": {
				if (this.compareInVector(ve, startPos, pe, fieldName, fieldType)<0) {
					ve.insertElementAt(pe, startPos+1);
					return ve;
				}
				else {
					ve.insertElementAt(pe, startPos);	
					return ve;
					}		
			}
			case "Desc": {
				if (this.compareInVector(ve, startPos, pe, fieldName, fieldType)<0) {
					ve.insertElementAt(pe, startPos);
					return ve;
				}
				else {
					ve.insertElementAt(pe, startPos+1);
					return ve;
				}				
			}
			default: {
				System.out.println("order unrecognized");
				return ve;
			}
			}

		}
		
		if ((endPos-startPos) %2 == 0) {
			midPos = startPos + (endPos-startPos)/2;
//			log.logFile("endPos, startPos, midPos is: "+endPos+", " + startPos +", "+ midPos);
		}
		else {
			midPos = startPos + (endPos-startPos-1)/2;
//			log.logFile("endPos, startPos, midPos is: "+endPos+", " + startPos +", "+ midPos);
		}
		

		if (this.compareInVector(ve, midPos, pe, fieldName, fieldType)==0) {
			ve.insertElementAt(pe, midPos);
//			log.logFile("compare vector returns equal");
//			ve.printListColumn("name", "count");
			return ve;
		}
		else if (this.compareInVector(ve, midPos, pe, fieldName, fieldType)>0) {
			switch (order) {
			case "Asc": {
//				log.logFile("compare vector returns positive");
				this.addObjectToSortedList(ve, startPos, midPos, pe, fieldName,order, fieldType);		
				break;
			}
			case "Desc": {
//				log.logFile("compare vector returns positive");
				this.addObjectToSortedList(ve, midPos+1, endPos, pe, fieldName,order, fieldType);		
				break;
			}
			default: {
				
			}
			}
	
		}
		else if (this.compareInVector(ve, midPos, pe, fieldName, fieldType)<0) {
			switch (order) {
			case "Asc": {
//				log.logFile("compare vector returns negative");
				this.addObjectToSortedList(ve, midPos+1, endPos, pe, fieldName,order, fieldType);
				break;
			}
			case "Desc": {
//				log.logFile("compare vector returns negative");
				this.addObjectToSortedList(ve, startPos, midPos, pe, fieldName,order, fieldType);	
				break;
			}
			default: {
				
			}
			}
		
		}
//		ve.printListColumn("name", "count");		
		return ve;
	}


/*	public int compare (Object p1, Object p2, String fieldName) {
		int result = ((String) ((BaseElements) p1).get(fieldName)).compareTo((String) ((BaseElements) p2).get(fieldName));
		return result;
	}
*/
	public int compareInVector (ElementList v1, int pos, Object p2, String fieldName, String fieldType) throws IOException {
		log.logFile("method compareInVector ("+v1 + ", " + pos + ", " + p2+ ", " + fieldName +") is called.");
		
		switch(fieldType) {
		case "Integer": {
			int v1Int = Integer.parseInt(v1.get(pos, fieldName));
			int p2Int;
			switch (v1.getType()) {
			case "pageElement": {
				p2Int = Integer.parseInt(((pageElement) p2).get(fieldName));
				break;
			}
			case "mobilePage": {
				p2Int = Integer.parseInt(((mobilePage) p2).get(fieldName));
				break;
			}
			default: {
				p2Int = 0;
			}
			}

			int result = Integer.compare(v1Int, p2Int);
//			log.logFile("v1, p2, result is: " + v1Int+ ", " + p2Int + ", "+ result);
					return result;
			
		}
		case "String": {
			String v1String = v1.get(pos, fieldName);
			String p2String;
			switch (v1.getType()) {
			case "pageElement": {
				p2String = ((pageElement) p2).get(fieldName);
				break;
			}
			case "mobilePage": {
				p2String = ((mobilePage) p2).get(fieldName);
				break;
			}
			default: {
				p2String = "";
			}
			}

			int result = v1String.compareTo(p2String);
					return result;
		}
		default: {
			return 0;
		}
		}


	}

	public String getType() {
		return elementType;
	}

	
	public String printList (ElementList ve, int startPos, int endPos, Object pe, String fieldName) {
		String out = "Array is: ";
		for (int i = 0; i<(endPos-startPos);i++) {
			out = out+ ((mobilePage) ve.elementAt(startPos+i)).get(fieldName)+", " ;
		}
		out += "the new item is: " + ((mobilePage) pe).get(fieldName);
		return out;
	}
	
	public void printListColumn (String field1, String field2) throws IOException {
		for (int i = 0; i<(this.size());i++) {
			log.logConsole(((mobilePage) this.elementAt(i)).get(field1)+", "+((mobilePage) this.elementAt(i)).get(field2)) ;
		}
	}
}
