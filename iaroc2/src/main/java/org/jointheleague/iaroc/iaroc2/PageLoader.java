package org.jointheleague.iaroc.iaroc2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PageLoader {
	
	public static InputStream getPage(String path){
		try {
			return new FileInputStream(new File("pages/" + path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
