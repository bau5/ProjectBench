package com.bau5.projectbench.common.utils;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


/**
 * Created by bau5 on 5/21/2015.
 */
public class VersionChecker implements Runnable {

    private static final String changesURL = "https://raw.githubusercontent.com/bau5/ProjectBench/master/changes.xml";
    private static final String versionURL = "https://raw.githubusercontent.com/bau5/ProjectBench/master/version.xml";
    public static Properties versionProperties = new Properties();
    public static Properties changesProperties = new Properties();

    private static final int OUT_OF_DATE = 4;
    private static final int UP_TO_DATE = 3;
    private static final int FAILED = 2;
    private static final int NOT_DONE = 1;
    public static int STATE = 0;

    private static String remoteImportance = null;
    private static String remoteVersion = null;


    public static boolean isComplete() {
        return STATE > 2;
    }

    public static boolean didFail() {
        return STATE == FAILED;
    }

    public static boolean isUpToDate() {
        return STATE == UP_TO_DATE;
    }

    public static boolean isOutOfDate() {
        return STATE == OUT_OF_DATE;
    }

    private void checkVersion() {
        InputStream remoteVersionStream = null;
        STATE = NOT_DONE;
        try {
            URL versionurl = new URL(versionURL);
            remoteVersionStream = versionurl.openStream();
            versionProperties.loadFromXML(remoteVersionStream);
            String versionFromRemote = versionProperties.getProperty("Minecraft " + Loader.MC_VERSION);
            if (versionFromRemote != null) {
                String[] split = versionFromRemote.split("\\|");
                remoteVersion = split[0];
                remoteImportance = split[1];
                if (Reference.VERSION.equalsIgnoreCase(remoteVersion)) {
                    STATE = UP_TO_DATE;
                } else {
                    STATE = OUT_OF_DATE;
                }
            } else {
                STATE = FAILED;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (STATE == NOT_DONE) {
                STATE = FAILED;
            }
            try {
                if (remoteVersionStream != null) {
                    remoteVersionStream.close();
                }
            } catch (Exception ex) {
            }
        }

    }

    public static void checkLatestChanges() {
        InputStream remoteChangesStream = null;

        try {
            URL remoteChangesURL = new URL(changesURL);
            remoteChangesStream = remoteChangesURL.openStream();
            changesProperties.loadFromXML(remoteChangesStream);
            String changesFromRemote = changesProperties.getProperty(Loader.MC_VERSION + "-" + Reference.REMOTE_VERSION);
            if (changesFromRemote != null) {
                FMLLog.info("[Project Bench] Latest Changes: " + changesFromRemote);
                Reference.CHANGES = changesFromRemote;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (remoteChangesStream != null) {
                    remoteChangesStream.close();
                }
            } catch (Exception ex) {/*double frick*/}
        }
    }

    @Override
    public void run() {
        int tries = 0;
        FMLLog.info("Starting Version check.");
        try {
            while (tries < 3 && (! isComplete())) {
                tries++;
                checkVersion();
                if (STATE == OUT_OF_DATE) {
                    Reference.REMOTE_VERSION = remoteVersion;
                    Reference.REMOTE_IMPORTANCE = remoteImportance;
                    checkLatestChanges();
                    FMLLog.info("[Project Bench] Finished version checking. We're out of date. New -> " + remoteVersion);
                } else if (isUpToDate()) {
                    FMLLog.info("[Project Bench] Up to date.");
                }
            }
        } catch (Exception ex) {
        }
    }

    public static void go() {
        new Thread(new VersionChecker()).start();
    }
}
