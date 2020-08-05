package logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static final String DATE_TIME_FORMAT = "[yyyy/MM/dd HH:mm:ss]";
    public static final String INFO_LABEL = "[INFO]";
    public static final String ERROR_LABEL = "[ERROR]";
    public static final String FATAL_LABEL = "[FATAL]";
    
    private Path logFile;
    private DateTimeFormatter dateTimeFormatter;
    
    public Logger(Path logsDirectory, Path logFileName) {
	dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
	createLogFileIfNotExists(logsDirectory, logFileName);
    }
    
    private void createLogFileIfNotExists(Path logsDirectory, Path logFileName) {
	try {
	    if(!Files.exists(logsDirectory)) {
		Files.createDirectory(logsDirectory);
	    }
	    
	    this.logFile = logsDirectory.resolve(logFileName);
	    if(!Files.exists(this.logFile)) {
		Files.createFile(this.logFile);
	    }
	} catch(IOException e) {
	    error("I/O error occurred during log file creation. System.out will be used.", e);
	}
    }
    
    public void error(String message, Throwable cause) {
	appendLog(ERROR_LABEL + ' ' + getErrorLogMessage(message, cause));
    }
    
    private void appendLog(String logMessage) {
	if(Files.exists(logFile)) {
	    try {
		Files.write(logFile, logMessage.getBytes(), StandardOpenOption.APPEND);
	    } catch(IOException e) {
		System.err.format("IO exception occurred while attempting to add logs to %s .", logFile);
	    }
	} else {
	    System.out.println(logMessage);
	}
    }
    
    private String getErrorLogMessage(String message, Throwable cause) {
	return getLogMessage(message) + cause.getMessage() + '\n' + ExceptionUtils.getStackTrace(cause) + '\n';
    }
    
    private String getLogMessage(String message) {
	return '[' + getCurrentDateTime() + "] " + message + '\n';
    }
    
    private String getCurrentDateTime() {
	return dateTimeFormatter.format(LocalDateTime.now());
    }
    
    public void info(String message) {
	System.out.println(INFO_LABEL + ' ' + getLogMessage(message));
    }
    
    public void fatal(String message, Throwable cause) {
	appendLog(FATAL_LABEL + ' ' + getErrorLogMessage(message, cause));
    }
}
