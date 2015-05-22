package com.bau5.projectbench.common.utils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.HashMap;

/**
 * Created by bau5 on 5/22/2015.
 */
public class Config {

    private HashMap<String, String[]> settings = new HashMap<String, String[]>();
    private File configFile;
    private Configuration fmlconfig;

    public Config(FMLPreInitializationEvent ev){
        configFile = ev.getSuggestedConfigurationFile();
        initMap();
        loadConfig();
    }

    private void loadConfig(){
        Configuration config = new Configuration(configFile);
        this.fmlconfig = config;
        try{
            VERSION_CHECK = getBoolSetting("Version Check", true);
            RENDER_ITEM = getBoolSetting("Render Item", true);
            RENDER_BOUNCE = getBoolSetting("Bounce", true);
            RENDER_SPIN = getBoolSetting("Spin", true);

            RENDER_HEIGHT  = getDoubleSetting("Item Render Height", 0.5, 0, 1);
            ROTATION_SPEED = getDoubleSetting("Rotation Speed", 7, 0.1, 20);
            BOUNCE_HEIGHT  = getDoubleSetting("Bounce Height", 1, 0.1, 9);
            BOUNCE_SPEED   = getDoubleSetting("Bounce Speed", 1, 0.1, 2);

            fmlconfig.addCustomCategoryComment("Features", "True/false toggles for various features of the mod.");
            fmlconfig.addCustomCategoryComment("Feature Modifiers", "Modifiable values for various features of the mod.");
        }catch(Exception ex){
            FMLLog.info("[Project Bench] Failed loading configuration file.");
            ex.printStackTrace();
        }finally{
            config.save();
        }
    }

    private boolean getBoolSetting(String name, boolean defaultVal){
        if(!settings.containsKey(name)){
            return defaultVal;
        }
        String[] val = settings.get(name);
        return fmlconfig.get(val[0], name, defaultVal, val[1]).getBoolean(defaultVal);
    }

    private double getDoubleSetting(String name, double defaultVal, double min, double max){
        if(!settings.containsKey(name)){
            return defaultVal;
        }
        String[] val = settings.get(name);
        if(min != -1 && max != -1)
            return fmlconfig.get(val[0], name, defaultVal, val[1], min, max).getDouble(defaultVal);
        else{
            return fmlconfig.get(val[0], name, defaultVal, val[1]).getDouble(defaultVal);
        }
    }

    private void initMap(){
        //booleans
        final String str = "False disables ";
        final String boolCat = "Features";
        settings.put("Version Check", new String[]{ boolCat,
                str + "version checking at start up."});
        settings.put("Render Item", new String[]{ boolCat,
                str + "rendering of item above Project Bench"});
        settings.put("Bounce", new String[]{ boolCat,
                str + "bounce of item rendered above Project Bench"});
        settings.put("Spin", new String[]{boolCat,
                str + "spin of item rendered above Project Bench"});

        //ints
        final String intCat = "Feature Modifiers";
        settings.put("Item Render Height", new String[]{ intCat,
                "Affects the height of the item being rendered above bench (if enabled)."
                +"\nDefault: " + 0.5
                +"\nMin: " + 0
                +"\nMax: " + 1});
        settings.put("Rotation Speed", new String[]{ intCat,
                "Affects the speed of the rotation (if enabled)."
                +"\nDefault: " + 7
                +"\nMin: " + 0.1
                +"\nMax: " + 20});
        settings.put("Bounce Speed", new String[]{ intCat,
                "Affects the speed of bouncing item (if enabled)."
                +"\nDefault: " + 1
                +"\nMin: " + 0.1
                +"\nMax: " + 2});
        settings.put("Bounce Height", new String[]{ intCat,
                "Affects the total range of bouncing (if enabled)."
                +"\nDefault: " + 1
                +"\nMin: " + 0.1
                +"\nMax: " + 9});
        }

    public static boolean VERSION_CHECK = true;

    public static boolean RENDER_ITEM   = true;
    public static boolean RENDER_BOUNCE = true;
    public static boolean RENDER_SPIN   = true;

    public static double ROTATION_SPEED = 7;
    public static double RENDER_HEIGHT  = 0.5;
    public static double BOUNCE_HEIGHT  = 7;
    public static double BOUNCE_SPEED   = 7;
}
