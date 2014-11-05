package com.pitty.android.logger;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    private static final String PACKAGE_NAME = Utils.class.getPackage().getName();

    private static final class CallerResolver extends SecurityManager {
        public Class<?> getCaller() {
            Class[] classContext = getClassContext();
            // sometimes class context is null (usually on new Android devices)
            if (classContext == null || classContext.length <= 0) {
                return null; // if class context is null or empty
            }

            boolean packageFound = false;
            for (Class aClass : classContext) {
                if (!packageFound) {
                    if (aClass.getPackage().getName().startsWith(PACKAGE_NAME)) {
                        packageFound = true;
                    }
                } else {
                    if (!aClass.getPackage().getName().startsWith(PACKAGE_NAME)) {
                        return aClass;
                    }
                }
            }
            return classContext[classContext.length - 1];
        }
    }

    private static final CallerResolver CALLER_RESOLVER = new CallerResolver();

    private static StackTraceElement getCallerStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace == null || stackTrace.length <= 0) {
            return null; // if stack trace is null or empty
        }

        boolean packageFound = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!packageFound) {
                if (stackTraceElement.getClassName().startsWith(PACKAGE_NAME)) {
                    packageFound = true;
                }
            } else {
                if (!stackTraceElement.getClassName().startsWith(PACKAGE_NAME)) {
                    return stackTraceElement;
                }
            }
        }
        return stackTrace[stackTrace.length - 1];
    }

    /**
     * Returns a name of a class that calls logging methods.
     * <p/>
     * Can be much faster than {@link #getCaller()} because
     * this method tries to use {@link SecurityManager} to get
     * caller context.
     *
     * @return the caller's name.
     */
    public static String getCallerClassName() {
        Class<?> caller = CALLER_RESOLVER.getCaller();
        if (caller == null) {
            StackTraceElement callerStackTrace = getCallerStackTrace();
            return callerStackTrace == null ? null : callerStackTrace.getClassName();
        } else {
            return caller.getName();
        }
    }

    /**
     * Returns stack trace element corresponding to a class that calls
     * logging methods.
     * <p/>
     * This method compares names of the packages of stack trace elements
     * with the package of this library to find information about caller.
     *
     * @return the caller stack trace element.
     */
    public static StackTraceElement getCaller() {
        return getCallerStackTrace();
    }

    /**
     * Shorten string.
     *
     * @param string modified string.
     * @param min the desired minimum length of result.
     *            if string length < abs(min), it will fill black space in left(min > 0)/right(min < 0).
     * @param length the desired length the string to cut.
     *               length > 0 cut string from begin of the string, length < 0 cut it from the end.
     * <p/>
     * <table border=1>
     * <tr>
     * <th>Example</th>
     * <th>Result</th>
     * </tr>
     * <tr> <td>("text", 6, 0)</td>   <td><pre>'  text'</pre></td> </tr>
     * <tr> <td>("text", -6, 0)</td>  <td><pre>'text  '</pre></td> </tr>
     * <tr> <td>("text", 0, 3)</td>  <td><pre>'tex'</pre></td>    </tr>
     * <tr> <td>("text", 0, -3)</td> <td><pre>'ext'</pre></td>    </tr>
     * </table>
     */
    public static String shorten(String string, int min, int length) {
        if (string == null) return null;

        String resultString = string;
        if (Math.abs(length) < resultString.length()) {
            if (length > 0) {
                resultString = string.substring(0, length);
            } else if (length < 0) {
                resultString = string.substring(string.length() + length, string.length());
            }
        }

        if (Math.abs(min) > resultString.length()) {
            return String.format("%" + min + "s", resultString);
        }

        return resultString;
    }

    /**
     * Shortens class name till the specified length.
     * <p/>
     * Note that only packages can be shortened so this method returns at least simple class name.
     *
     * @param className the class name.
     * @param maxLength the desired maximum length of result.
     * @param count the desired maximum count of packages
     * @return the shortened class name.
     */
    public static String shortenClassName(String className, int count, int maxLength) {

        className = shortenPackagesName(className, count);

        if (className == null) return null;
        if (maxLength == 0) return className;
        if (maxLength > className.length()) return className;

        if (maxLength < 0) {
            maxLength = - maxLength;
            StringBuilder builder = new StringBuilder();
            for (int index = className.length() - 1; index > 0; ) {
                int i = className.lastIndexOf('.', index);

                if (i == -1) {
                    if (builder.length() > 0
                            && builder.length() + index + 1 > maxLength) {
                        builder.insert(0, '*');
                        break;
                    }

                    builder.insert(0, className.substring(0, index + 1));
                } else {
                    if (builder.length() > 0
                            && builder.length() + (index + 1 - i) + 1 > maxLength) {
                        builder.insert(0, '*');
                        break;
                    }

                    builder.insert(0, className.substring(i, index + 1));
                }

                index = i - 1;
            }
            return builder.toString();

        } else {
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < className.length(); ) {
                int i = className.indexOf('.', index);

                if (i == -1) {
                    if (builder.length() > 0) {
                        builder.insert(builder.length(), '*');
                        break;
                    }

                    builder.insert(builder.length(), className.substring(index, className.length()));
                    break;
                } else {
                    if (builder.length() > 0
                            && i + 1 > maxLength) {
                        builder.insert(builder.length(), '*');
                        break;
                    }

                    builder.insert(builder.length(), className.substring(index, i + 1));
                }

                index = i + 1;
            }

            return builder.toString();
        }
    }

    /**
     * Shorten className with part between .  count.
     * @param className
     * @param count Min: Integer.MIN_VALUE / 2 ; Max: Integer.MAX_VALUE
     * @return
     *
     * * <p/>
     * <table border=1>
     * <tr>
     * <th>Example</th>
     * <th>Result</th>
     * </tr>
     * <tr> <td>("com.example.android.MainActivity", 0)</td>   <td><pre>'com.example.android.MainActivity'</pre></td> </tr>
     * <tr> <td>("com.example.android.MainActivity", -1)</td>  <td><pre>'example.android.MainActivity'</pre></td> </tr>
     * <tr> <td>("com.example.android.MainActivity", -2)</td>  <td><pre>'android.MainActivity'</pre></td>    </tr>
     * <tr> <td>("com.example.android.MainActivity", 2)</td> <td><pre>'com.example'</pre></td>    </tr>
     * <tr> <td>("com.example.android.MainActivity", 3)</td> <td><pre>'com.example.android'</pre></td>    </tr>
     * <tr> <td>("com.example.android.MainActivity", 10)</td> <td><pre>'com.example.android.MainActivity'</pre></td>    </tr>
     * <tr> <td>("com.example.android.MainActivity", -10)</td> <td><pre>'MainActivity'</pre></td>    </tr>
     * </table>
     */
    private static String shortenPackagesName(String className, int count) {
        if (count == 0 || className == null || className.length() <= 0) {
            return className;
        }
        String[] classNames = className.split("\\.");
        if (count >= classNames.length) {
            return className;
        } else if (Math.abs(count) >= classNames.length  || Math.abs(count) < 0) {
            return classNames[classNames.length - 1];
        }

        StringBuilder builder = new StringBuilder();
        if (count > 0) {
            for (int i = 0; i < count - 1; i++) {
                builder.append(classNames[i]).append(".");
            }
            builder.append(classNames[count - 1]);
        } else if (count < 0) {
            for (int i = -count; i < classNames.length - 1; i++) {
                builder.append(classNames[i]).append(".");
            }
            builder.append(classNames[classNames.length - 1]);
        }
        return builder.toString();
    }
}
