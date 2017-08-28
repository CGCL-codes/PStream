package com.basic.core.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tj on 2017/1/11.
 */
public class PropertiesUtil {
    public static final String fileName="/dstream.properties";

    public static Properties pro;

    static{
        pro=new Properties();
        try {
            InputStream in = Object. class .getResourceAsStream( fileName );
            pro.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getProperties(String name){
        return pro.getProperty(name);
    }

    public static void setProperties(String name ,String value){
        pro.setProperty(name, value);
    }
}
