package com.pitty.android.logger;

import android.util.Log;

/**
 * Created by Pitty on 14/10/20.
 */
public enum  LEVEL {
    O, //OFF
    A,  // Assert
    E, // Error
    W, // Warn
    I, // Info
    D, // Debug
    V // Verbose
    ;

    public int toLog() {
        switch (this) {
            case A:
                return Log.ASSERT;
            case E:
                return Log.ERROR;
            case W:
                return Log.WARN;
            case I:
                return Log.INFO;
            case D:
                return Log.DEBUG;
            case V:
                return Log.VERBOSE;
            default:
                return 0;
        }
    }
}