package com.pitty.android.logger;

/**
 * Created by Pitty on 14/10/20.
 */
public class Constant {
    // static log switcher
    public static final boolean LOG = true;
    // assets folder name
    private static final String ASSETS_FOLDER_NAME = "/assets/";
    // properties file name in assets
    private static final String PROPERTIES_NAME = "android-logger.properties";
    // properties file path
    protected static final String PROPERTIES_PATH = ASSETS_FOLDER_NAME + PROPERTIES_NAME;

    // Android Log Tag length is default 23
    public static final int TAG_MAX_LENGTH = 23;
    // Default Tag
    public static final String ANONYMOUS_TAG = "null";

    protected static final String CONF_ROOT = "root";
    protected static final String CONF_LOGGER_MODULE = "module:";
    protected static final String CONF_LOGGER_SPLIT = ":";
    protected static final String CONF_LOGGER_ITEM_SPLIT = ",";
    protected static final String CONF_LOGGER_PACKAGE_SPLIT = ".";
}
