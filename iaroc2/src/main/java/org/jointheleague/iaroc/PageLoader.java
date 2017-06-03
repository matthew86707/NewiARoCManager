//package org.jointheleague.iaroc.iaroc2;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.SequenceInputStream;
//
//public class PageLoader {
//
//	public static InputStream getPage(String path, boolean includeStyling, boolean includeNavbar){
//
//		try {
//			FileInputStream navbar = new FileInputStream("WebContent/emptyTest.html");
//			if(includeNavbar){
//				navbar = new FileInputStream(new File("WebContent/navbar.html"));
//			}
//
//			return new SequenceInputStream (navbar, new SequenceInputStream(new FileInputStream(new File("WebContent/style.html")), new FileInputStream(new File("WebContent/" + path))));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//}
