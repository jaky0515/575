import java.util.Scanner;

/*
 * AttributeFactory.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class AttributeFactory extends Object {

	/**
	 * Processes a single attribute declaration, consisting of the keyword &attribute, name, and either the keyword numeric or a list of nominal values.
	 * @param scanner - a scanner containing the attribute's tokens
	 * @return the constructed attribute
	 * @throws Exception - if a parse exception occurs
	 */
	public static Attribute make( Scanner scanner ) throws Exception {
		if( scanner == null ) {
			throw new Exception("Invalid Scanner object passed-in!");
		}
		Attribute attr = null;
		String attrName = "";
		int idx = 0;
		try {
			while( scanner.hasNext() ) {
				String nextVal = scanner.next().trim();
				if(nextVal.contains("@attribute")) {
					// @attribute keyword; start the index
					idx = 0;
				}
				else if(idx == 1) {
					// set attribute name
					attrName = nextVal;
				}
				else if(idx == 2) {
					if(nextVal.toLowerCase().equals("numeric")) {
						// is a numeric attribute
						attr = new NumericAttribute(attrName);
						break;
					}
					else {
						// is a nominal attribute
						attr = new NominalAttribute(attrName);
						((NominalAttribute) attr).addValue(nextVal);
					}
				}
				else if(idx > 2) {
					((NominalAttribute) attr).addValue(nextVal);
				}
				idx++;
			}
		}
		catch(Exception e) {
			System.err.println("Parsing error occurred!");
			e.printStackTrace();
		}
		return attr;
	}

}