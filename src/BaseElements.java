import java.util.Vector;

public class BaseElements {
	Vector pages = new Vector();
	String name = "";

	public BaseElements () {
		name = "";
	}
	
	public Object get(String fieldName) {
		switch (fieldName) {
		case "name": { return name;}
		default: {return name;}
		}
	}
	
	
}
