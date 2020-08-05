package logger;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoggerTest {
  public static final Path LOGS_DIRECTORY = Path.of("src/test/resources");
  public static final Path LOG_FILE_NAME = Path.of("error.log");
  public static final Path LOG_FILE_ABSOLUTE_PATH = LOGS_DIRECTORY.resolve(LOG_FILE_NAME);

  @After
  public void deleteLogFile() throws IOException {
    Files.deleteIfExists(LOGS_DIRECTORY.resolve(LOG_FILE_NAME));
    Files.deleteIfExists(LOGS_DIRECTORY);
  }

  @Test
  public void testThatIfLogsDirAndFileDoNotExistTheyAreCreated() {
    boolean existsBeforeCreatingLogger = Files.exists(LOG_FILE_ABSOLUTE_PATH);
    new Logger(LOGS_DIRECTORY, LOG_FILE_NAME);
    boolean existsAfterCreatingLogger = Files.exists(LOG_FILE_ABSOLUTE_PATH);

    String assertMessage = "Logs directory and file must be created if they do not exist.";
    boolean assertCondition = !existsBeforeCreatingLogger && existsAfterCreatingLogger;

    assertTrue(assertMessage, assertCondition);
  }

  @Test
  public void testThatIfLogFileExistsItIsNotRecreated() throws IOException {
    Files.createDirectory(LOGS_DIRECTORY);
    Files.createFile(LOG_FILE_ABSOLUTE_PATH);

    FileTime logFileCreationTimeBeforeCreatingLogger =
        Files.readAttributes(LOG_FILE_ABSOLUTE_PATH, BasicFileAttributes.class).creationTime();
    new Logger(LOGS_DIRECTORY, LOG_FILE_NAME);
    FileTime logFileCreationTimeAfterCreatingLogger =
        Files.readAttributes(LOG_FILE_ABSOLUTE_PATH, BasicFileAttributes.class).creationTime();

    String assertMessage = "Log file must be recreated if it already exists.";
    assertEquals(
        assertMessage,
        logFileCreationTimeBeforeCreatingLogger,
        logFileCreationTimeAfterCreatingLogger);
  }

  @Test
  public void testThatInfoMessagesAreNotWrittenToLogFiles() throws IOException {
    Logger logger = new Logger(LOGS_DIRECTORY, LOG_FILE_NAME);

    long fileSizeBeforeLoggingInfoMessage =
        Files.readAttributes(LOG_FILE_ABSOLUTE_PATH, BasicFileAttributes.class).size();
    logger.info("test message");
    long fileSizeAfterLoggingInfoMessage =
        Files.readAttributes(LOG_FILE_ABSOLUTE_PATH, BasicFileAttributes.class).size();

    String assertMessage = "Info messages should be written only to System.out.";
    assertEquals(assertMessage, fileSizeBeforeLoggingInfoMessage, fileSizeAfterLoggingInfoMessage);
  }

  @Test
  public void testThatErrorMessagesAreWrittenToLogFile() throws IOException {
    Logger logger = new Logger(LOGS_DIRECTORY, LOG_FILE_NAME);
    logger.error("error message", new IOException("exception message"));

    String assertMessage = "Error message must be written to log file!";
    String logFileContent = Files.readString(LOG_FILE_ABSOLUTE_PATH);
    boolean assertCondition =
        logFileContent.contains("error message") && logFileContent.contains("exception message");

    assertTrue(assertMessage, assertCondition);
  }
}
