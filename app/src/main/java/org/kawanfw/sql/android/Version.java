package org.kawanfw.sql.android;

public class Version {

	private static String APP_NAME = "Awake SQL Demo";
	private static String APP_VERSION = "v4.0 - 27-nov-2015";
	private static String COPYRIGHT = "\u00a9 KawanSoft";
	
	public static String getFullVersion(){
		
		String fullVersion = APP_NAME + System.getProperty("line.separator")  + APP_VERSION;
		fullVersion += System.getProperty("line.separator") + COPYRIGHT;
		
		return fullVersion;
	}
}
