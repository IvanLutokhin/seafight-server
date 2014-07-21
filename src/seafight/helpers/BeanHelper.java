package seafight.helpers;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BeanHelper {
	public static Object get(String file) throws IOException {
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
		Object object = decoder.readObject();
		decoder.close();
		
		return object;
	}
}