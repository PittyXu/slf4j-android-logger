package com.pitty.android.logger;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Pitty on 14-10-24.
 */
public class LoggerProperties {
    /**
     * For Lazy Singleton mode
     */
    private static class Holder{
        private static final LoggerProperties instance = new LoggerProperties();
    }

    /**
     * Singleton instance
     * @return
     */
    public static LoggerProperties getInstance() {
        return Holder.instance;
    }

    private final Map<String, LoggerHandler> mHandlerMap;
    private LEVEL mGlobalLevel = LEVEL.V;

    private LoggerProperties() {
        mHandlerMap = loadConfiguration();
    }

    public LoggerHandler getHandler(String tag) {
        if (null == mHandlerMap || mHandlerMap.isEmpty()) {
            // No Config, close log.
            return null;
        }
        LoggerHandler handler = mHandlerMap.get(tag);
        if (null != handler) {
            handler.setTagName(tag);
            return handler;
        }
        String tmp = tag;
        while (tmp.lastIndexOf(Constant.CONF_LOGGER_PACKAGE_SPLIT) > 0) {
            tmp = tmp.substring(0, tmp.lastIndexOf(Constant.CONF_LOGGER_PACKAGE_SPLIT));
            handler = mHandlerMap.get(tmp + ".*");
            if (null != handler) {
                handler.setTagName(tag);
                return handler;
            }
        }
        // Default handler.
        return new PatternLoggerHandler(mGlobalLevel, tag, null, null);
    }

    /**
     * Load Config to Map.
     *
     * @return
     */
    private Map<String, LoggerHandler> loadConfiguration() {
        if (!Constant.LOG) {
            mGlobalLevel = LEVEL.O;
            return null;
        }
        Map<String, LoggerHandler> handlerMap = new HashMap<String, LoggerHandler>();
        // read properties file
        Properties properties = new Properties();
        try {
            loadProperties(properties);
        } catch (IOException e) {
            mGlobalLevel = LEVEL.O;
            return handlerMap;
        }

        // property file is empty
        if (!properties.propertyNames().hasMoreElements()) {
            mGlobalLevel = LEVEL.O;
            return handlerMap;
        }
        Map<String, String> handlerTempMap = new HashMap<String, String>();
        String globalLogger = null;
        // parse properties
        for (Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); ) {
            String propertyName = ((String) names.nextElement()).replaceAll("\\s|\\t|\\r|\\n", "");
            String propertyValue = properties.getProperty(propertyName).replaceAll("\\s|\\t|\\r|\\n", "");

            if (propertyName.startsWith(Constant.CONF_LOGGER_MODULE)) {
                String loggerName = propertyName.substring(Constant.CONF_LOGGER_MODULE.length());
                handlerTempMap.put(loggerName, propertyValue);
            } else if (propertyName.equals(Constant.CONF_ROOT)){
                globalLogger = propertyValue;
            }
        }
        // parse global (root) config.
        LEVEL globalLevel = null;
        HashSet<String> exListString = new HashSet<String>();
        if (!TextUtils.isEmpty(globalLogger)) {
            String[] globalLoggers = globalLogger.split(Constant.CONF_LOGGER_SPLIT);
            if (null != globalLoggers) {
                try {
                    globalLevel = globalLoggers.length > 0 ? LEVEL.valueOf(globalLoggers[0]) : null;
                } catch (IllegalArgumentException e) {
                }
                if (globalLoggers.length > 1) {
                    exListString.addAll(Arrays.asList(globalLoggers[1].split(Constant.CONF_LOGGER_ITEM_SPLIT)));
                }
            }
        }
        // Set module configs to map.
        Set<String> keys = handlerTempMap.keySet();
        for (String key : keys) {
            String value = handlerTempMap.get(key);
            if (!TextUtils.isEmpty(value)) {
                String[] values = value.split(Constant.CONF_LOGGER_SPLIT);
                if (null != values) {
                    LEVEL loggerLevel = globalLevel;
                    if (null == globalLevel || exListString.contains(key)) {
                        // global not set, or in expect list. use the module set.
                        try {
                            loggerLevel = values.length > 0 ? LEVEL.valueOf(values[0]) : globalLevel;
                        } catch (IllegalArgumentException e) {
                        }
                    } else {
                    // global set and not in expect list, use global set.
                    }
                    String tag = values.length > 1 ? values[1] : null;
                    String tagPattern = values.length > 2 ? values[2] : null;
                    String messagePattern = values.length > 3 ? values[3] : null;
                    handlerMap.put(key, new PatternLoggerHandler(loggerLevel, tag, tagPattern, messagePattern));
                }
            }
        }
        if (null != globalLevel) {
            mGlobalLevel = globalLevel;
        }
        return handlerMap;
    }

    /**
     * Load Properties for config file.
     * @param properties
     * @throws IOException
     */
    private void loadProperties(Properties properties) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = LoggerProperties.class.getClassLoader().getResourceAsStream(Constant.PROPERTIES_PATH);
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
