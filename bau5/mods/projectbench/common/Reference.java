package bau5.mods.projectbench.common;

public class Reference {
	public static Reference instance = new Reference();
	
	public static final String DEV_VERSION = "2.0dev10";
	public static final String RELEASE_VERSION = "1.7.5";
	public static final String PB_VERSION = ProjectBench.DEV_ENV ? DEV_VERSION : RELEASE_VERSION;	
	public static String LATEST_CHANGES = "[null]";
	public static String LATEST_VERSION = "[null]";
	public static String UPDATE_IMPORTANCE = "[null]";
	public static String UPDATE_URL = "http://goo.gl/JnF4H";
	public static boolean UP_TO_DATE = true;
	
	public static final String MOD_NAME = "Project Bench";
	public static final String MOD_ID   = "bau5_ProjectBench";
}
