import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Utils.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Utils {

	public static int maxIndex( double[] p ) {
		int maxIdx = 0;
		double maxVal = p[0];
		for(int i = 1; i < p.length; i++) {
			if( maxVal < p[i] ) {
				maxVal = p[i];
				maxIdx = i;
			}
		}
		return maxIdx;
	}

	/**
	 * This method makes a "deep clone" of any object it is given.
	 * Copied from: https://alvinalexander.com/java/java-deep-clone-example-source-code
	 */
	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}