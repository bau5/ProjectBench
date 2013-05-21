package bau5.mods.projectbench.common;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class VersionChecker implements Runnable{

	private static VersionChecker instance = new VersionChecker();
	
	private static final String remoteFileLocation = "https://raw.github.com/bau5/ProjectBench/master/version.xml";
	private static final String remoteChangesLocation = "https://raw.github.com/bau5/ProjectBench/master/changes.xml";
	public static Properties remoteVersionProperties = new Properties();
	public static Properties remoteChangesProperties = new Properties();
	
	public static final byte NOT_DONE = 0;
	public static final byte UP_TO_DATE = 1;
	public static final byte OUT_OF_DATE = 2;
	public static final byte FAILED = 3;
	
	private static byte result = NOT_DONE;
	public static String remoteVersion = null;
	public static String remoteVersionImportance = null;
	public static String remoteUpdateLocation = null;
	private static String mode = ProjectBench.DEV_ENV ? "D" : "R";
	
	public static void checkVersion(){
		InputStream remoteVersionStream = null;
		result = NOT_DONE;
		
		try{
			URL remoteVersionURL = new URL(remoteFileLocation);
			remoteVersionStream = remoteVersionURL.openStream();
			remoteVersionProperties.loadFromXML(remoteVersionStream);
			String versionFromRemote = remoteVersionProperties.getProperty(Loader.instance().getMCVersionString() +":" +mode);

			if(versionFromRemote != null){
				String[] versionSplit = versionFromRemote.split("\\|");
				if(versionSplit[0] != null)
					remoteVersion = versionSplit[0];
				if(versionSplit[1] != null)
					remoteVersionImportance = versionSplit[1];
				if(remoteVersion != null){
					Reference.LATEST_VERSION = remoteVersion;
					Reference.UPDATE_IMPORTANCE = remoteVersionImportance;
					if(ProjectBench.DEV_ENV){
						if(remoteVersion.equalsIgnoreCase(Reference.DEV_VERSION))
							result = UP_TO_DATE;
						else
							result = OUT_OF_DATE;
					}else if(!ProjectBench.DEV_ENV){
						if(remoteVersion.equalsIgnoreCase(Reference.RELEASE_VERSION))
							result = UP_TO_DATE;
						else
							result = OUT_OF_DATE;
					}
				}
				else{
					result = FAILED;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally{
			if(result == NOT_DONE)
				result = FAILED;
			try{
				if(remoteVersionStream != null)
					remoteVersionStream.close();
			}catch(Exception ex){}
		}
		
	}
	
	public static void checkLatestChanges(){
		InputStream remoteChangesStream = null;
		
		try{
			URL remoteChangesURL = new URL(remoteChangesLocation);
			remoteChangesStream = remoteChangesURL.openStream();
			remoteChangesProperties.loadFromXML(remoteChangesStream);
			String changesFromRemote = remoteChangesProperties.getProperty(Reference.LATEST_VERSION);
			if(changesFromRemote != null){
				System.out.println("ProjectBench Latest Changes: " +changesFromRemote);
				Reference.LATEST_CHANGES = changesFromRemote;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(remoteChangesStream != null)
					remoteChangesStream.close();
			}catch(Exception ex){}
		}
			
	}
	
	@Override
	public void run() {
		int tries = 0;
		System.out.println("ProjectBench: Starting version check.");
		
		try{
			while(tries < 3 && (result != OUT_OF_DATE && result != UP_TO_DATE)){
				checkVersion();
				tries++;
				if(result == OUT_OF_DATE){
					Reference.UP_TO_DATE = false;
					Reference.UPDATE_IMPORTANCE = remoteVersionImportance;
					checkLatestChanges();
					TickRegistry.registerTickHandler(new VersionCheckTicker(), Side.CLIENT);
				}
				if(result == UP_TO_DATE)
					System.out.println("ProjectBench: Project Bench is up to date.");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void go(){
		new Thread(instance).start();
	}
	
}