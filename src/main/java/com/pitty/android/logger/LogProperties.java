package com.pitty.android.logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xuxuejun on 14-10-24.
 */
public class LogProperties {
    /**
     * For Lazy Singleton mode
     */
    private static class Holder{
        private static final LogProperties instance = new LogProperties();
    }

    /**
     * Singleton instance
     * @return
     */
    public LogProperties getInstance() {
        return Holder.instance;
    }

    private LogProperties() {

    }

    /**
     * Load Properties for config file.
     * @param properties
     * @throws IOException
     */
    private static void loadProperties(Properties properties) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = LogProperties.class.getClassLoader().getResourceAsStream(Constant.PROPERTIES_PATH);
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private Map<String, String> loadConfiguration() {
        Map<String, String> handlerMap = new HashMap<String, String>();

        // read properties file
        Properties properties = new Properties();
        try {
            loadProperties(properties);
        } catch (IOException e) {
            //TODO no config file, make log level O.
            return handlerMap;
        }

        // property file is empty
        if (!properties.propertyNames().hasMoreElements()) {
            //TODO no config file, make log level O.
            return handlerMap;
        }

        // parse properties to
        for (Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); ) {
            String propertyName = (String) names.nextElement();
            String propertyValue = properties.getProperty(propertyName);

            if (propertyName.startsWith(Constant.CONF_LOGGER_MODULE)) {
                // TODO module logger.
                String loggerName = propertyName.substring(Constant.CONF_LOGGER_MODULE.length());
                decodeLogger(propertyValue);
            } else if (propertyName.equals(Constant.CONF_ROOT)){
                // TODO global logger.
                decodeGlobalLogger(propertyValue);
            }
        }

        return handlerMap;
    }

    private void decodeLogger(String value) {}
    private void decodeGlobalLogger(String value) {}
}
