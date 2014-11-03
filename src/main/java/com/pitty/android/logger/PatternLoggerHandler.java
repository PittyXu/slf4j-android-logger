package com.pitty.android.logger;

import android.util.Log;

/**
 * The basic implementation of {@link LoggerHandler} interface.
 * <p/>
 * This log handler is configured with a logging level, a tag and
 * a message patterns.
 * <p/>
 * The logging level parameter is the minimal level of log messages printed
 * by this handler instance. The logging level can be {@code null} which
 * means no messages should be printed using this logger.
 * <p/>
 * <b>Attention</b>: Android may set its own requirement for logging level
 * using {@link android.util.Log#isLoggable(String, int)} method. This logger doesn't take
 * it into account in {@link #isEnabled(LEVEL)} method.
 * <p/>
 * The patterns are format strings written according to a special rules described
 * below. Log messages will be formatted and printed as it is specified in
 * the tag and the message pattern. The tag pattern configures log tag used
 * to print messages. The message pattern configures a head of the message but
 * not whole message printed to log.
 * <p/>
 * <table border=1>
 * <tr>
 * <th>The tag pattern</th>
 * <td>TAG</td>
 * </tr>
 * <tr>
 * <th>The message pattern</th>
 * <td>%d{yyyy-MM-dd}: </td>
 * </tr>
 * <tr>
 * <th>Resulting tag</th>
 * <td>TAG</td>
 * </tr>
 * <tr>
 * <th>Resulting message</th>
 * <td>2013-07-12: &lt;incoming message&gt;</td>
 * </tr>
 * </table>
 * <p/>
 * The tag and the message patterns are wrote according to similar rules.
 * So we will show only one pattern in further examples.
 * <p/>
 * The patterns is strings that contains a set of placeholders and other
 * special marks. Each special mark should start with '%' sign. To escape
 * this sign you can double it.
 * <p/>
 * The list of special marks and placeholders:
 * <table border=1>
 * <caption>Conversion marks</caption>
 * <tr>
 * <th>Mark</th><th>Effect</th>
 * </tr>
 * <tr>
 * <td>%%</td>
 * <td>Escapes special sign. Prints just one '%' instead.</td>
 * </tr>
 * <tr>
 * <td>%n</td>
 * <td>Prints a new line character '\n'.</td>
 * </tr>
 * <tr>
 * <td>%d{date format} %date{date format}</td>
 * <td>Prints date/time of a message. Date format
 * should be supported by {@link java.text.SimpleDateFormat}.
 * Default date format is "yyyy-MM-dd HH:mm:ss.SSS".</td>
 * </tr>
 * <tr>
 * <td>%p %level</td>
 * <td>Prints logging level of a message.</td>
 * </tr>
 * <tr>
 * <td>%c{count.length} %logger{count.length}</td>
 * <td>Prints a name of the logger. The algorithm will shorten some part of
 * full logger name to the specified length. You can find examples below.
 * <table border=1>
 * <tr>
 * <th>Conversion specifier</th>
 * <th>Logger name</th>
 * <th>Result</th>
 * </tr>
 * <tr>
 * <td>%logger</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.android.MainActivity</td>
 * </tr>
 * <tr>
 * <td>%logger{0}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.android.MainActivity</td>
 * </tr>
 * <tr>
 * <td>%logger{3}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.android</td>
 * </tr>
 * <tr>
 * <td>%logger{-1}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>example.android.MainActivity</td>
 * </tr>
 * <tr>
 * <td>%logger{.0}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.android.MainActivity</td>
 * </tr>
 * <td>%logger{.30}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.android.*</td>
 * </tr>
 * <tr>
 * <td>%logger{.15}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>com.example.*</td>
 * </tr>
 * <tr>
 * <td>%logger{.-25}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>*.android.MainActivity</td>
 * </tr>
 * <tr>
 * <td>%logger{3.-18}</td>
 * <td>com.example.android.MainActivity</td>
 * <td>*.example.android</td>
 * </tr>
 * <tr>
 * <td>%logger{-3.-10}</td>
 * <td>com.example.android.MainActivity$SubClass</td>
 * <td>MainActivity$SubClass</td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * <tr>
 * <td>%C{count.length} %caller{count.length}</td>
 * <td>Prints information about a caller class which causes the logging event.
 * Additional parameters 'count' and 'length' means the same as
 * the parameters of %logger. Examples:
 * <table border=1>
 * <tr>
 * <th>Conversion specifier</th>
 * <th>Caller</th>
 * <th>Result</th>
 * </tr>
 * <tr>
 * <td>%caller</td>
 * <td>Class com.example.android.MainActivity at line 154</td>
 * <td>com.example.android.MainActivity:154</td>
 * </tr>
 * <tr>
 * <td>%caller{-3.-15}</td>
 * <td>Class com.example.android.MainActivity at line 154</td>
 * <td>MainActivity:154</td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * <tr>
 * <td>%(...)</td>
 * <td>Special mark used to grouping parts of message. Format modifiers
 * (if specified) are applied on whole group. Examples:
 * <table border=1>
 * <tr>
 * <th>Example</th>
 * <th>Result</th>
 * </tr>
 * <tr>
 * <td>[%50(%d %caller{-3.-15})]</td>
 * <td><pre>[          2013-07-12 19:45:26.315 MainActivity:154]</pre></td>
 * </tr>
 * <tr>
 * <td>[%-50(%d %caller{-3.-15})]</td>
 * <td><pre>[2013-07-12 19:45:26.315 MainActivity:154          ]</pre></td>
 * </tr>
 * </table>
 * </td>
 * </tr>
 * </table>
 * <p/>
 * After special sign '%' user can add format modifiers. The modifiers
 * is similar to standard modifiers of {@link java.util.Formatter} conversions.
 * <table border=1>
 * <tr>
 * <th>Example</th>
 * <th>Result</th>
 * </tr>
 * <tr> <td>%6(text)</td>   <td><pre>'  text'</pre></td> </tr>
 * <tr> <td>%-6(text)</td>  <td><pre>'text  '</pre></td> </tr>
 * <tr> <td>%.3(text)</td>  <td><pre>'tex'</pre></td>    </tr>
 * <tr> <td>%.-3(text)</td> <td><pre>'ext'</pre></td>    </tr>
 * </table>
 */
public class PatternLoggerHandler implements LoggerHandler {
    private final LEVEL level;
    private String tag;
    private final String tagPattern;
    private final String messagePattern;
    private final LoggerPattern compiledTagLoggerPattern;
    private final LoggerPattern compiledMessageLoggerPattern;

    /**
     * Creates new {@link PatternLoggerHandler}.
     *
     * @param level          the level.
     * @param tagPattern     the tag pattern.
     * @param messagePattern the message pattern.
     */
    public PatternLoggerHandler(LEVEL level, String tag, String tagPattern, String messagePattern) {
        if (null == level) {
            this.level = LEVEL.V;
        } else {
            this.level = level;
        }
        if (null != tag) {
            this.tag = loggerNameToTag(tag);
        }
        this.tagPattern = tagPattern;
        this.compiledTagLoggerPattern = LoggerPattern.compile(tagPattern);
        this.messagePattern = messagePattern;
        this.compiledMessageLoggerPattern = LoggerPattern.compile(messagePattern);
    }

    /**
     * Returns the level.
     *
     * @return the level.
     */
    public LEVEL getLevel() {
        return level;
    }

    /**
     * Returns the tag messagePattern.
     *
     * @return the tag messagePattern.
     */
    public String getTagPattern() {
        return tagPattern;
    }

    /**
     * Returns the message messagePattern.
     *
     * @return the message messagePattern.
     */
    public String getMessagePattern() {
        return messagePattern;
    }

    @Override
    public boolean isEnabled(LEVEL level) {
        return this.level != null && level != null && (this.level.ordinal() >= level.ordinal());
    }

    @Override
    public void print(LEVEL level,
                      Throwable throwable, String messageFormat, Object... args) throws IllegalArgumentException {
        if (isEnabled(level)) {
            String message;

            if (messageFormat == null) {
                if (args != null && args.length > 0) {
                    throw new IllegalArgumentException("message format is not set but arguments are presented");
                }

                if (throwable == null) {
                    message = "";
                } else {
                    message = Log.getStackTraceString(throwable);
                }
            } else {
                if (throwable == null) {
                    message = String.format(messageFormat, args);
                } else {
                    message = String.format(messageFormat, args) + '\n' + Log.getStackTraceString(throwable);
                }
            }

            StackTraceElement caller = null;
            if ((compiledTagLoggerPattern != null && compiledTagLoggerPattern.isCallerNeeded())
                    || (compiledMessageLoggerPattern != null && compiledMessageLoggerPattern.isCallerNeeded())) {
                caller = Utils.getCaller();
            }

            String tag = compiledTagLoggerPattern == null ? getTagName() : compiledTagLoggerPattern.apply(caller, getTagName(), level);
            String messageHead = compiledMessageLoggerPattern == null ? "" : compiledMessageLoggerPattern.apply(caller, getTagName(), level);

            if (messageHead.length() > 0 && !Character.isWhitespace(messageHead.charAt(0))) {
                messageHead = messageHead + " ";
            }
            Log.println(level.toLog(), tag, messageHead + message);
        }
    }

    public String getTagName() {
        if (null == this.tag) {
            return Constant.ANONYMOUS_TAG;
        }
        return this.tag;
    }

    /**
     * If tag not set set the tag, or do nothing.
     * @param tag
     */
    public void setTagName(String tag) {
        if (null == this.tag) {
            this.tag = loggerNameToTag(tag);
        }
    }

    /**
     * Tag names cannot be longer than 23  characters on Android platform.
     *
     * Returns the short logger tag (up to 23 characters) for the given logger name.
     * Traditionally loggers are named by fully-qualified Java classes; this
     * method attempts to return a concise identifying part of such names.
     *
     * See also:
     * android/system/core/include/cutils/property.h
     * android/frameworks/base/core/jni/android_util_Log.cpp
     * dalvik.system.DalvikLogging
     *
     */
    private String loggerNameToTag(String loggerName) {
        // Anonymous logger
        if (loggerName == null) {
            return Constant.ANONYMOUS_TAG;
        }

        int length = loggerName.length();
        if (length <= Constant.TAG_MAX_LENGTH) {
            return loggerName;
        }

        int tagLength = 0;
        int lastTokenIndex = 0;
        int lastPeriodIndex;
        StringBuilder tagName = new StringBuilder(Constant.TAG_MAX_LENGTH + 3);
        while ((lastPeriodIndex = loggerName.indexOf('.', lastTokenIndex)) != -1) {
            tagName.append(loggerName.charAt(lastTokenIndex));
            // token of one character appended as is otherwise truncate it to one character
            int tokenLength = lastPeriodIndex - lastTokenIndex;
            if (tokenLength > 1) {
                tagName.append('*');
            }
            tagName.append('.');
            lastTokenIndex = lastPeriodIndex + 1;

            // check if name is already too long
            tagLength = tagName.length();
            if (tagLength > Constant.TAG_MAX_LENGTH) {
                return getSimpleName(loggerName);
            }
        }

        // Either we had no useful dot location at all
        // or last token would exceed TAG_MAX_LENGTH
        int tokenLength = length - lastTokenIndex;
        if (tagLength == 0 || (tagLength + tokenLength) > Constant.TAG_MAX_LENGTH) {
            return getSimpleName(loggerName);
        }

        // last token (usually class name) appended as is
        tagName.append(loggerName, lastTokenIndex, length);
        return tagName.toString();
    }

    private String getSimpleName(String loggerName) {
        // Take leading part and append '*' to indicate that it was truncated
        int length = loggerName.length();
        int lastPeriodIndex = loggerName.lastIndexOf('.');
        return lastPeriodIndex != -1 && length - (lastPeriodIndex + 1) <= Constant.TAG_MAX_LENGTH
            ? loggerName.substring(lastPeriodIndex + 1)
            : '*' + loggerName.substring(length - Constant.TAG_MAX_LENGTH + 1);
    }
}
