package com.pitty.android.logger;

import com.pitty.android.logger.LoggerPattern.ConcatenatePattern;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoggerPatternTest {
    StackTraceElement caller = Utils.getCaller();
    LEVEL level = LEVEL.D;
    String loggerName = "com.pitty.android";

    LoggerPattern spacePattern = new LoggerPattern.PlainPattern(0, 0, " ");
    LoggerPattern colonPattern = new LoggerPattern.PlainPattern(0, 0, ":");
    LoggerPattern tabPattern = new LoggerPattern.PlainPattern(0, 0, "\n");
    LoggerPattern levelPattern = new LoggerPattern.LevelPattern(1, 1);
    LoggerPattern dataPattern = new LoggerPattern.DatePattern(0, 0, "HH:mm:ss");
    LoggerPattern loggerPattern = new LoggerPattern.LogPattern(0, 0, 30, 0);
    LoggerPattern callerPattern = new LoggerPattern.CallerPattern(0, 0, 30, 0);
    LoggerPattern sourcePattern = new LoggerPattern.SourcePattern(0, 0);

    ConcatenatePattern concatenatePattern = new ConcatenatePattern(0, 0, new ArrayList<LoggerPattern>());
    ConcatenatePattern concatenatePatternChild = new ConcatenatePattern(60, 0, new ArrayList<LoggerPattern>());


    @Test
    public void patternTests() {
        Assert.assertEquals("D", levelPattern.apply(caller, loggerName, level));

        Assert.assertEquals(new SimpleDateFormat("HH:mm:ss").format(new Date()),
                dataPattern.apply(caller, loggerName, level));

        Assert.assertEquals(":", colonPattern.apply(caller, loggerName, level));

        Assert.assertEquals("\n", tabPattern.apply(caller, loggerName, level));

        Assert.assertEquals("com.pitty.android.logger.LoggerPatternTest#<init>:15", callerPattern.apply(caller, loggerName, level));

        Assert.assertEquals("com.pitty.android", loggerPattern.apply(caller, loggerName, level));

        concatenatePattern.addPattern(dataPattern);
        concatenatePattern.addPattern(spacePattern);
        concatenatePattern.addPattern(levelPattern);
        concatenatePattern.addPattern(spacePattern);

        concatenatePatternChild.addPattern(loggerPattern);
        concatenatePatternChild.addPattern(spacePattern);
        concatenatePatternChild.addPattern(callerPattern);
        concatenatePatternChild.addPattern(sourcePattern);

        concatenatePattern.addPattern(concatenatePatternChild);
        concatenatePattern.addPattern(colonPattern);
        concatenatePattern.addPattern(tabPattern);

        Assert.assertEquals("HH:mm:ss D com.pitty.android com.example.PatternTest#<init>:15(PatternTest.java:15):\n".substring(8),
                concatenatePattern.apply(caller, loggerName, level).substring(8));
    }

    @Test
    public void compileTest() {

        LoggerPattern.Compiler compiler = new LoggerPattern.Compiler();

        Assert.assertEquals("%e", compiler.compile("%%e").apply(caller, loggerName, level));
        Assert.assertEquals("abc\nde", compiler.compile("abc%nde").apply(caller, loggerName, level));
        Assert.assertEquals("%de", compiler.compile("%%de").apply(caller, loggerName, level));
        Assert.assertEquals("%\nde", compiler.compile("%%%nde").apply(caller, loggerName, level));
        Assert.assertEquals("%%%", compiler.compile("%%%%%%").apply(caller, loggerName, level));

        Assert.assertEquals("D", compiler.compile("%1.1level").apply(caller, loggerName, level));
        Assert.assertEquals("abc%de\nfD", compiler.compile("abc%%de%nf%1.1level").apply(caller, loggerName, level));

        Assert.assertEquals("com.pitty.android", compiler.compile("%logger").apply(caller, loggerName, level));
        Assert.assertEquals("com.pitty.android&main", compiler.compile("%logger&main").apply(caller, loggerName, level));
        Assert.assertEquals("abc%de\nfc", compiler.compile("abc%%de%nf%1.1logger{1}").apply(caller, loggerName, level));

        Assert.assertEquals("%\nde%", compiler.compile("%%%nde%%").apply(caller, loggerName, level));

        compiler.compile("%d").apply(caller, loggerName, level);
        Assert.assertNull(compiler.compile(null));


        Assert.assertEquals("D", compiler.compile("%1.1p").apply(caller, loggerName, level));

        Assert.assertEquals("com.example%com.pitty%", compiler.compile("%caller{2}%%%c{2}%%").apply(caller, loggerName, level));

        Assert.assertEquals("PatternTest#<init>:15", compiler.compile("%caller{-2}").apply(caller, loggerName, level));
        Assert.assertEquals("com.example", compiler.compile("%caller{+2}").apply(caller, loggerName, level));
        Assert.assertEquals("*.PatternTest#<init>:15", compiler.compile("%caller{.-20}").apply(caller, loggerName, level));
        Assert.assertEquals("com.example.*", compiler.compile("%caller{.+20}").apply(caller, loggerName, level));
        Assert.assertEquals("PatternTest#<init>:15", compiler.compile("%caller{-2.-20}").apply(caller, loggerName, level));
        Assert.assertEquals("com.example", compiler.compile("%caller{+2.+20}").apply(caller, loggerName, level));

        Assert.assertEquals("(PatternTest.java:15)", compiler.compile("%source").apply(caller, loggerName, level));
        Assert.assertEquals("(PatternTest.java:15)", compiler.compile("%s").apply(caller, loggerName, level));

        Assert.assertEquals(
                "HH:mm:ss DEBUG                      com.pitty.android PatternTest#<init>:15:\n".substring(8),
                compiler.compile("%d{HH:mm:ss} %5level %60(%logger{30.30} %caller{-2.20}):%n").apply(caller, loggerName, level).substring(8));
    }
}

