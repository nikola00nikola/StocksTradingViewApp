package nativeMetode;

import java.util.ArrayList;

public class Parser {

	static {
		System.loadLibrary("Parser");	//definisana native metoda u c++ Parser.dll
	}
	
	public static native ArrayList<ArrayList<Double>> parse(String company, long start, long end);
	
}
