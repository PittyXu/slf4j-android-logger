slf4j-android-logger PRD-v1.0
====================
## Target
An Android Logger support slf4j api.

## Feature
1. Support slf4j api.
2. Support origin Log api.
3. Support load config file.
4. Support Global config to open/close log.
    * include a config switcher
    * a code-inside switcher (use "static final" to let compiler delete the log when release)
5. Support module as Tag, class for second Tag, log level support to module.
6. Give a proguard the log code exclude the project when release.

## Config File Design
1. Level set
    * O : close the log.
    * V : VerBose (All log show)
    * D : Debug
    * I : Info
    * W : Warn
    * E : Error
    * A : Assert
1. Global open/close switcher
    * root= LEVEL: {MODULE, MODULE}(Except module)
    * if set **LEVEL** will control all module, modules set will overwritten.
    * if set **MODULE** list (split by ,), those modules' setting will not overwritten.
2. Module set
    * module.{MODULE NAME} = LEVEL:{TAG NAME}:{FORMATTER HEADER}
    * **MODULE NAME** the module name input by the java code.
    * **LEVEL** this MODULE show log level
    * **TAG NAME** this MODULE Tag name showed, if not set it's Tag is the MODULE NAME
    * **FORMATTER HEADER** may include the SubTag(which may defined by fileName, className, methodName), Time, Code Line, Addition Text etc.
3. No set
    if not list in the file, the module will have a default set, is O{no config file} > V{has config file but on root node} > root node level.