import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

  private File logEvent;
  private File logError;

  public Logger(File logEvent, File logError) {
    this.logEvent = logEvent;
    this.logError = logError;
  }

  public void logEvent(String event) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(logEvent, true));
        writer.write(event);
        writer.newLine();
        writer.flush();
        writer.close();
  }

  public void logError(String error) throws IOException {
      BufferedWriter writer = new BufferedWriter(new FileWriter(logError, true));
      writer.write(error);
      writer.newLine();
      writer.flush();
      writer.close();
  }
}
