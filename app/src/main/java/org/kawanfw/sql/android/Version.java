package org.kawanfw.sql.android;

public class Version {

	private static final String APP_NAME = "Awake SQL Demo";
	private static final String APP_VERSION = "v5.0 - 08-apr-2021";
	private static final String COPYRIGHT = "\u00a9 KawanSoft";
	
	public static String getFullVersion(){
		
		String fullVersion = APP_NAME + System.getProperty("line.separator")  + APP_VERSION;
		fullVersion += System.getProperty("line.separator") + COPYRIGHT;
		
		return fullVersion;
	}
}
