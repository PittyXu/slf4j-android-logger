package org.slf4j.impl;

import com.pitty.android.logger.Constant;
import com.pitty.android.logger.LEVEL;
import com.pitty.android.logger.LoggerProperties;
import com.pitty.android.logger.PatternLoggerHandler;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * <p>A simple implementation that delegates all log requests to the Google Android
 * logging facilities. Note that this logger does not support {@link org.slf4j.Marker}.
 * Methods taking marker data as parameter simply invoke the eponymous method
 * without the Marker argument, discarding any marker data in the process.</p>
 *
 * <p>The logging levels specified for SLF4J can be almost directly mapped to
 * the levels that exist in the Google Android platform. The following table
 * shows the mapping implemented by this logger.</p>
 *
 * <table border="1">
 * <tr><th><b>SLF4J<b></th><th><b>Android</b></th></tr>
 * <tr><td>TRACE</td><td>{@link android.util.Log#VERBOSE}</td></tr>
 * <tr><td>DEBUG</td><td>{@link android.util.Log#DEBUG}</td></tr>
 * <tr><td>INFO</td><td>{@link android.util.Log#INFO}</td></tr>
 * <tr><td>WARN</td><td>{@link android.util.Log#WARN}</td></tr>
 * <tr><td>ERROR</td><td>{@link android.util.Log#ERROR}</td></tr>
 * </table>
 *
 * <p>Use loggers as usual:
 * <ul>
 *     <li>
 *         Declare a logger<br/>
 *         <code>private static final Logger logger = LoggerFactory.getLogger(MyClass.class);</code>
 *     </li>
 *     <li>
 *         Invoke logging methods, e.g.,<br/>
 *         <code>logger.debug("Some log message. Details: {}", someObject);</code><br/>
 *         <code>logger.debug("Some log message with varargs. Details: {}, {}, {}", someObject1, someObject2, someObject3);</code>
 *     </li>
 * </ul>
 * </p>
 *
 * <p>Logger instances created using the LoggerFactory are named either according to the name
 * or the fully qualified class name of the class given as a parameter.
 * Each logger name will be used as the log message tag on the Android platform.
 * However, tag names cannot be longer than 23 characters so if logger name exceeds this limit then
 * it will be truncated by the LoggerFactory. The following examples illustrate this.
 * <table border="1">
 * <tr><th><b>Original Name<b></th><th><b>Truncated Name</b></th></tr>
 * <tr><td>org.example.myproject.mypackage.MyClass</td><td>o*.e*.m*.m*.MyClass</td></tr>
 * <tr><td>o.e.myproject.mypackage.MyClass</td><td>o.e.m*.m*.MyClass</td></tr>
 * <tr><td>org.example.ThisNameIsWayTooLongAndWillBeTruncated</td><td>*LongAndWillBeTruncated</td></tr>
 * <tr><td>ThisNameIsWayTooLongAndWillBeTruncated</td><td>*LongAndWillBeTruncated</td></tr>
 * </table>
 * </p>
 *
 * @author Andrey Korzhevskiy <a.korzhevskiy@gmail.com>
 */
class AndroidLoggerAdapter extends MarkerIgnoringBase {
    PatternLoggerHandler handler;

    /**
     * Package access allows only {@link AndroidLoggerFactory} to instantiate
     * SimpleLogger instances.
     */
    AndroidLoggerAdapter(String tag) {
        this.handler = LoggerProperties.getInstance().getHandler(tag);
        if (null != this.handler) {
            this.name = handler.getTagName();
        }
    }

    /**
     * Is this logger instance enabled for the VERBOSE level?
     *
     * @return True if this Logger is enabled for level VERBOSE, false otherwise.
     */
    public boolean isTraceEnabled() {
        return isLoggable(LEVEL.V);
    }

    /**
     * Log a message object at level VERBOSE.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void trace(String msg) {
        log(LEVEL.V, msg, null);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level VERBOSE.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void trace(String format, Object arg) {
        formatAndLog(LEVEL.V, format, arg);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the VERBOSE level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void trace(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.V, format, arg1, arg2);
    }

    /**
     * Log a message at level VERBOSE according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the VERBOSE level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void trace(String format, Object... argArray) {
        formatAndLog(LEVEL.V, format, argArray);
    }

    /**
     * Log an exception (throwable) at level VERBOSE with an accompanying message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void trace(String msg, Throwable t) {
        log(LEVEL.V, msg, t);
    }

    /**
     * Is this logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for level DEBUG, false otherwise.
     */
    public boolean isDebugEnabled() {
        return isLoggable(LEVEL.D);
    }

    /**
     * Log a message object at level DEBUG.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void debug(String msg) {
        log(LEVEL.D, msg, null);
    }

    /**
     * Log a message at level DEBUG according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for level DEBUG.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void debug(String format, Object arg) {
        formatAndLog(LEVEL.D, format, arg);
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void debug(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.D, format, arg1, arg2);
    }

    /**
     * Log a message at level DEBUG according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the DEBUG level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void debug(String format, Object... argArray) {
        formatAndLog(LEVEL.D, format, argArray);
    }

    /**
     * Log an exception (throwable) at level DEBUG with an accompanying message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void debug(String msg, Throwable t) {
        log(LEVEL.V, msg, t);
    }

    /**
     * Is this logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level, false otherwise.
     */
    public boolean isInfoEnabled() {
        return isLoggable(LEVEL.I);
    }

    /**
     * Log a message object at the INFO level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void info(String msg) {
        log(LEVEL.I, msg, null);
    }

    /**
     * Log a message at level INFO according to the specified format and argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void info(String format, Object arg) {
        formatAndLog(LEVEL.I, format, arg);
    }

    /**
     * Log a message at the INFO level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.I, format, arg1, arg2);
    }

    /**
     * Log a message at level INFO according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the INFO level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void info(String format, Object... argArray) {
        formatAndLog(LEVEL.I, format, argArray);
    }

    /**
     * Log an exception (throwable) at the INFO level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void info(String msg, Throwable t) {
        log(LEVEL.I, msg, t);
    }

    /**
     * Is this logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level, false
     *         otherwise.
     */
    public boolean isWarnEnabled() {
        return isLoggable(LEVEL.W);
    }

    /**
     * Log a message object at the WARN level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void warn(String msg) {
        log(LEVEL.W, msg, null);
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void warn(String format, Object arg) {
        formatAndLog(LEVEL.W, format, arg);
    }

    /**
     * Log a message at the WARN level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.W, format, arg1, arg2);
    }

    /**
     * Log a message at level WARN according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the WARN level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void warn(String format, Object... argArray) {
        formatAndLog(LEVEL.W, format, argArray);
    }

    /**
     * Log an exception (throwable) at the WARN level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void warn(String msg, Throwable t) {
        log(LEVEL.W, msg, t);
    }

    /**
     * Is this logger instance enabled for level ERROR?
     *
     * @return True if this Logger is enabled for level ERROR, false otherwise.
     */
    public boolean isErrorEnabled() {
        return isLoggable(LEVEL.E);
    }

    /**
     * Log a message object at the ERROR level.
     *
     * @param msg
     *          - the message object to be logged
     */
    public void error(String msg) {
        log(LEVEL.E, msg, null);
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * argument.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg
     *          the argument
     */
    public void error(String format, Object arg) {
        formatAndLog(LEVEL.E, format, arg);
    }

    /**
     * Log a message at the ERROR level according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param arg1
     *          the first argument
     * @param arg2
     *          the second argument
     */
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(LEVEL.E, format, arg1, arg2);
    }

    /**
     * Log a message at level ERROR according to the specified format and
     * arguments.
     *
     * <p>
     * This form avoids superfluous object creation when the logger is disabled
     * for the ERROR level.
     * </p>
     *
     * @param format
     *          the format string
     * @param argArray
     *          an array of arguments
     */
    public void error(String format, Object... argArray) {
        formatAndLog(LEVEL.E, format, argArray);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying
     * message.
     *
     * @param msg
     *          the message accompanying the exception
     * @param t
     *          the exception (throwable) to log
     */
    public void error(String msg, Throwable t) {
        log(LEVEL.E, msg, t);
    }

    private void formatAndLog(LEVEL priority, String format, Object... argArray) {
        if (isLoggable(priority)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            _log(priority, ft.getMessage(), ft.getThrowable());
        }
    }

    private void log(LEVEL priority, String message, Throwable throwable) {
        if (isLoggable(priority)) {
            _log(priority, message, throwable);
        }
    }

    private boolean isLoggable(LEVEL level) {
        return Constant.LOG && handler != null && handler.isEnabled(level);
    }

    private void _log(LEVEL level, String message, Throwable throwable) {
        handler.print(getName(), level, throwable, message);
    }
}
