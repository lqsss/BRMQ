package main.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by liqiushi on 2017/11/22.
 */
public class PropertiesReader {


    private Properties pros = null;

    private static class ConfigurationHolder {
        private static PropertiesReader configuration = new PropertiesReader();
    }

    public static PropertiesReader getInstance() {
        return ConfigurationHolder.configuration;
    }

    public String getValue(String key) {
        return pros.getProperty(key);
    }

    private PropertiesReader() {
        readConfig();
    }


    private void readConfig() {
        pros = new Properties();
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("")
                    .getPath() + "args.properties");
            reader = new InputStreamReader(in,"UTF-8");
            pros.load(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
