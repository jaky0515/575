import java.util.ArrayList;
import java.util.Scanner;

/*
 * Examples.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Examples extends ArrayList<Example> {

  private Attributes attributes;	// the attributes structure for these examples

  /**
   * Explicit constructor
   * @param attributes - the attributes for this set of examples
   */
  public Examples( Attributes attributes ) {
	  super();
	  this.attributes = attributes;
  }
  /**
   * Given the attributes structure, parses the tokens in the scanner, makes Examples, and adds them to this Examples object
   * @param scanner - a Scanner containing the examples' tokens
   * @throws Exception - if an index is out of bounds or if a parse error occurs
   */
  public void parse( Scanner scanner ) throws Exception {
	  if(scanner == null) {
		  throw new Exception("Invalid Scanner object passed-in");
	  }
	  // read examples
	  String line;
	  while(scanner.hasNextLine()) {
		  line = scanner.nextLine().trim();
		  if(line.equals("") || line.contains("@examples")) {
			  continue;
		  }
		  String[] lineElem = line.split(" ");
		  Example example = new Example(lineElem.length);
		  for(int i = 0; i < lineElem.length; i++) {
			  if(this.attributes.get(i) instanceof NumericAttribute) {
				  // numeric attribute value; store the numeric value
				  example.add(Double.parseDouble(lineElem[i]));
			  }
			  else {
				  // nominal attribute value; find the index of the value and store that index
				  example.add(Double.valueOf(((NominalAttribute) this.attributes.get(i)).getIndex(lineElem[i])));
			  }
		  }
		  this.add(example);
	  }
  }
  /**
   * Returns a string representation of this Examples object.
   * @return a string representation of this Examples object
   */
  public String toString() {
	  StringBuilder strBuilder = new StringBuilder();
	  strBuilder.append("\n@examples\n\n");
	  for(int i=0; i < this.size(); i++) {
		  Example example = this.get(i);
		  for(int j=0; j < example.size(); j++) {
			  if(this.attributes.get(j) instanceof NumericAttribute) {
				  // numeric attribute value
				  strBuilder.append(example.get(j));
			  }
			  else {
				  // nominal attribute value
				  strBuilder.append(((NominalAttribute) this.attributes.get(j)).getValue(example.get(j).intValue())); 
			  }
			  strBuilder.append((j == example.size()-1) ? "" : " ");
		  }
		  strBuilder.append("\n");
	  }
	  return strBuilder.toString();
  }

}