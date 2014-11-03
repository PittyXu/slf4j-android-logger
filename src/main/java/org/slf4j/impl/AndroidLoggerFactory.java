/**
 * Copyright (c) 2004-2013 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.impl;

import com.pitty.android.logger.Utils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of {@link org.slf4j.ILoggerFactory} returning
 * the appropriate named {@link AndroidLoggerAdapter} instance.
 */
public class AndroidLoggerFactory implements ILoggerFactory {
    private final ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

    /**
     * Return an appropriate {@link AndroidLoggerAdapter} instance by name.
     * @param name the name.
     * @return the {@link Logger} implementation.
     */
    @Override
    public Logger getLogger(String name) {
        Logger logger = loggerMap.get(name);
        if (logger == null) {
            Logger newInstance = new AndroidLoggerAdapter(name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            logger = oldInstance == null ? newInstance : oldInstance;
        }
        return logger;
    }

    /**
     * Returns logger corresponding to the specified class.
     *
     * @param aClass the class.
     * @return the {@link Logger} implementation.
     */
    public Logger getLogger(Class<?> aClass) {
        return getLogger(aClass == null ? null : aClass.getName());
    }

    /**
     * Returns logger corresponding to the caller class.
     *
     * @return the {@link Logger} implementation.
     */
    public Logger getLogger() {
        return getLogger(Utils.getCallerClassName());
    }
}
