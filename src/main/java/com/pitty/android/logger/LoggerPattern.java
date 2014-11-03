package com.pitty.android.logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * This class responsible for formatting messages for {@link PatternLoggerHandler}.
 */
public abstract class LoggerPattern {

    public static class PlainLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        private final String string;

        public PlainLoggerPattern(int count, int length, String string) {
            super(count, length);
            this.string = string;
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            return string;
        }

    }

    public static class DateLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        private final SimpleDateFormat dateFormat;

        public DateLoggerPattern(int count, int length, String dateFormat) {
            super(count, length);
            if (dateFormat != null) {
                this.dateFormat = new SimpleDateFormat(dateFormat);
            } else {
                this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            }

        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            return dateFormat.format(new Date());
        }

    }

    public static class LevelLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        public LevelLoggerPattern(int count, int length) {
            super(count, length);
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            return level.toString();
        }
    }


    public static class LoggerLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        private int loggerCount;
        private int loggerLength;

        public LoggerLoggerPattern(int count, int length, int loggerCount, int loggerLength) {
            super(count, length);
            this.loggerCount = loggerCount;
            this.loggerLength = loggerLength;
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            return Utils.shortenClassName(loggerName, loggerCount, loggerLength);
        }
    }

    public static class CallerLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        private int callerCount;
        private int callerLength;

        public CallerLoggerPattern(int count, int length, int callerCount, int callerLength) {
            super(count, length);
            this.callerCount = callerCount;
            this.callerLength = callerLength;
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            if (caller == null) {
                throw new IllegalArgumentException("Caller not found");
            } else {
                String callerString;
                if (caller.getLineNumber() < 0) {
                    callerString = String.format("%s#%s", caller.getClassName(), caller.getMethodName());
                } else {
                    callerString = String.format("%s#%s:%d", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
                }
                return Utils.shortenClassName(callerString, callerCount, callerLength);
            }
        }

        @Override
        protected boolean isCallerNeeded() {
            return true;
        }
    }

    public static class SourceLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        public SourceLoggerPattern(int count, int length) {
            super(count, length);
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            if (caller == null) {
                throw new IllegalArgumentException("Caller not found");
            } else {
                StringBuilder builder = new StringBuilder();
                if (caller.isNativeMethod()) {
                    builder.append("(native)");
                } else {
                    if (caller.getFileName() == null) {
                        builder.append("(unknown)");
                    } else {
                        if (caller.getLineNumber() >= 0) {
                            builder.append(String.format("(%s:%d)", caller.getFileName(), caller.getLineNumber()));
                        } else {
                            builder.append(String.format("(%s)", caller.getFileName()));
                        }
                    }
                }
                return builder.toString();
            }
        }

        @Override
        protected boolean isCallerNeeded() {
            return true;
        }
    }

    public static class ConcatenateLoggerPattern extends com.pitty.android.logger.LoggerPattern {

        private final List<com.pitty.android.logger.LoggerPattern> loggerPatternList;

        public ConcatenateLoggerPattern(int count, int length, List<com.pitty.android.logger.LoggerPattern> loggerPatternList) {
            super(count, length);
            this.loggerPatternList = new ArrayList<com.pitty.android.logger.LoggerPattern>(loggerPatternList);
        }

        public void addPattern(com.pitty.android.logger.LoggerPattern loggerPattern) {
            loggerPatternList.add(loggerPattern);
        }

        @Override
        protected String doApply(StackTraceElement caller, String loggerName, LEVEL level) {
            StringBuilder builder = new StringBuilder();
            for (com.pitty.android.logger.LoggerPattern loggerPattern : loggerPatternList) {
                builder.append(loggerPattern.apply(caller, loggerName, level));
            }
            return builder.toString();
        }

        @Override
        protected boolean isCallerNeeded() {
            for (com.pitty.android.logger.LoggerPattern loggerPattern : loggerPatternList) {
                if (loggerPattern.isCallerNeeded()) {
                    return true;
                }
            }
            return false;
        }
    }

    private final int count;
    private final int length;

    private LoggerPattern(int count, int length) {
        this.count = count;
        this.length = length;
    }

    public final String apply(StackTraceElement caller, String loggerName, LEVEL level) {
        String string = doApply(caller, loggerName, level);
        return Utils.shorten(string, count, length);
    }

    protected abstract String doApply(StackTraceElement caller, String loggerName, LEVEL level);

    protected boolean isCallerNeeded() {
        return false;
    }

    public static com.pitty.android.logger.LoggerPattern compile(String pattern) {
        try {
            return pattern == null ? null : new Compiler().compile(pattern);
        } catch (Exception e) {
//            LoggerManager.getLogger(Logger.ROOT_LOGGER_NAME)
//                    .e(e, "cannot parse pattern: '%s'", pattern);
            return new PlainLoggerPattern(0, 0, pattern);
        }

    }

    public static class Compiler {

        private String patternString;
        private int position;
        private List<ConcatenateLoggerPattern> queue;

        private final java.util.regex.Pattern PERCENT_PATTERN =
                java.util.regex.Pattern.compile("%%");
        private final java.util.regex.Pattern NEWLINE_PATTERN =
                java.util.regex.Pattern.compile("%n");
        private final java.util.regex.Pattern LEVEL_PATTERN =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?level");
        private final java.util.regex.Pattern LOGGER_PATTERN =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?logger(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        private final java.util.regex.Pattern CALLER_PATTERN =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?caller(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        private final java.util.regex.Pattern SOURCE_PATTERN =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?source");
        private final java.util.regex.Pattern DATE_PATTERN =
                java.util.regex.Pattern.compile("%date(\\{(.*?)\\})?");
        private final java.util.regex.Pattern CONCATENATE_PATTERN =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?\\(");
        private final java.util.regex.Pattern DATE_PATTERN_SHORT =
                java.util.regex.Pattern.compile("%d(\\{(.*?)\\})?");
        private final java.util.regex.Pattern LEVEL_PATTERN_SHORT =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?p");
        private final java.util.regex.Pattern LOGGER_PATTERN_SHORT =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?c(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        private final java.util.regex.Pattern CALLER_PATTERN_SHORT =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?C(\\{([+-]?\\d+)?(\\.([+-]?\\d+))?\\})?");
        private final java.util.regex.Pattern SOURCE_PATTERN_SHORT =
                java.util.regex.Pattern.compile("%([+-]?\\d+)?(\\.([+-]?\\d+))?s");

        public com.pitty.android.logger.LoggerPattern compile(String string) {
            if (string == null) {
                return null;
            }

            this.position = 0;
            this.patternString = string;
            this.queue = new ArrayList<ConcatenateLoggerPattern>();
            queue.add(new ConcatenateLoggerPattern(0, 0, new ArrayList<com.pitty.android.logger.LoggerPattern>()));

            while (string.length() > position) {

                int index = string.indexOf("%", position);
                int bracketIndex = string.indexOf(")", position);
                if (queue.size() > 1 && bracketIndex < index) {
                    queue.get(queue.size() - 1).addPattern(new PlainLoggerPattern(0, 0, string.substring(position, bracketIndex)));
                    queue.get(queue.size() - 2).addPattern(queue.remove(queue.size() - 1));
                    position = bracketIndex + 1;
                }
                if (index == -1) {
                    queue.get(queue.size() - 1).addPattern(new PlainLoggerPattern(0, 0, string.substring(position)));
                    break;
                } else {
                    queue.get(queue.size() - 1).addPattern(new PlainLoggerPattern(0, 0, string.substring(position, index)));
                    position = index;
                    parse();
                }
            }

            return queue.get(0);
        }

        private void parse() {
            Matcher matcher;
            if ((matcher = findPattern(PERCENT_PATTERN)) != null) {
                queue.get(queue.size() - 1).addPattern(new PlainLoggerPattern(0, 0, "%"));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(NEWLINE_PATTERN)) != null) {
                queue.get(queue.size() - 1).addPattern(new PlainLoggerPattern(0, 0, "\n"));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(LEVEL_PATTERN)) != null || (matcher = findPattern(LEVEL_PATTERN_SHORT)) != null) {
                int count = Integer.parseInt(matcher.group(1) == null ? "0" : matcher.group(1));
                int length = Integer.parseInt(matcher.group(3) == null ? "0" : matcher.group(3));
                queue.get(queue.size() - 1).addPattern(new LevelLoggerPattern(count, length));
                position = matcher.end();
                return;
            }
            // the order is important because short logger pattern may match long caller occurrence
            if ((matcher = findPattern(CALLER_PATTERN)) != null || (matcher = findPattern(CALLER_PATTERN_SHORT)) != null) {
                int count = Integer.parseInt(matcher.group(1) == null ? "0" : matcher.group(1));
                int length = Integer.parseInt(matcher.group(3) == null ? "0" : matcher.group(3));
                int countCaller = Integer.parseInt(matcher.group(5) == null ? "0" : matcher.group(5));
                int lengthCaller = Integer.parseInt(matcher.group(7) == null ? "0" : matcher.group(7));
                queue.get(queue.size() - 1).addPattern(new CallerLoggerPattern(count, length, countCaller, lengthCaller));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(SOURCE_PATTERN)) != null || (matcher = findPattern(SOURCE_PATTERN_SHORT)) != null) {
                int count = Integer.parseInt(matcher.group(1) == null ? "0" : matcher.group(1));
                int length = Integer.parseInt(matcher.group(3) == null ? "0" : matcher.group(3));
                queue.get(queue.size() - 1).addPattern(new SourceLoggerPattern(count, length));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(LOGGER_PATTERN)) != null || (matcher = findPattern(LOGGER_PATTERN_SHORT)) != null) {
                int count = Integer.parseInt(matcher.group(1) == null ? "0" : matcher.group(1));
                int length = Integer.parseInt(matcher.group(3) == null ? "0" : matcher.group(3));
                int countLogger = Integer.parseInt(matcher.group(5) == null ? "0" : matcher.group(5));
                int lengthLogger = Integer.parseInt(matcher.group(7) == null ? "0" : matcher.group(7));
                queue.get(queue.size() - 1).addPattern(new LoggerLoggerPattern(count, length, countLogger, lengthLogger));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(DATE_PATTERN)) != null || (matcher = findPattern(DATE_PATTERN_SHORT)) != null) {
                String dateFormat = matcher.group(2);
                queue.get(queue.size() - 1).addPattern(new DateLoggerPattern(0, 0, dateFormat));
                position = matcher.end();
                return;
            }
            if ((matcher = findPattern(CONCATENATE_PATTERN)) != null) {
                int count = Integer.parseInt(matcher.group(1) == null ? "0" : matcher.group(1));
                int length = Integer.parseInt(matcher.group(3) == null ? "0" : matcher.group(3));
                queue.add(new ConcatenateLoggerPattern(count, length, new ArrayList<com.pitty.android.logger.LoggerPattern>()));
                position = matcher.end();
                return;
            }

            throw new IllegalArgumentException();
        }

        private Matcher findPattern(java.util.regex.Pattern pattern) {
            Matcher matcher = pattern.matcher(patternString);
            return matcher.find(position) && matcher.start() == position ? matcher : null;
        }

    }
}


