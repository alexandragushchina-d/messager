import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MyThread extends Thread {

  private MyVector<UserInfo> users;
  private UserInfo user;
  private Logger logging;

  public MyThread(MyVector<UserInfo> users, UserInfo user) {
    this.users = users;
    this.user = user;
    this.logging = new Logger(new File(Config.LOG_PATH_FILE), new File(Config.LOG_PATH_FILE_ERROR));
  }

  @Override
  public void run() {
    try {
      OutputStream os = this.user.getSocket().getOutputStream();
      DataOutputStream dos = new DataOutputStream(os);
      InputStream is = this.user.getSocket().getInputStream();
      DataInputStream dis = new DataInputStream(is);

      while (true) {
        processUserInput(dis, dos);
      }

    } catch (Exception e) {
      try {
        logging.logError(logErrorStringBuilder(e.getMessage(), "", "run"));
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  private void processUserInput(DataInputStream dis, DataOutputStream dos) throws IOException {
    String inputLine = dis.readLine();
    String[] command = inputLine.split(" ", 2);

    switch (command[0]) {
      case "global":
        sendAllMessage(command[1]);
        break;
      case "online":
        isOnline(dos);
        break;
      case "msg":
        sendMessage(command[1].split(" ", 2), dos);
        break;
      case "login":
        checkLogin(command[1].split(" ", 2), dos);
        break;
      case "reg":
        registration(command[1].split(" ", 2), dos);
        break;
      default:
        dos.writeBytes("Input is invalid. Try it again.\n");
        break;
    }
  }

  synchronized private void sendAllMessage(String message) throws IOException {
    for (int i = 0; i < users.size(); ++i) {
      try {
        users.get(i).sendAlert(this.user.getUsername(), message);
        logging.logEvent(logEventStringBuilder(message, "global"));
      } catch (Exception e) {
        logging.logError(logErrorStringBuilder(e.getMessage(), "global", "sendAllMessage"));
        users.remove(i);
        --i;
        break;
      }
    }
  }

  synchronized private void sendMessage(String[] data, DataOutputStream dos) throws IOException {
    if (data.length <= 1) {
      dos.writeBytes("Input is invalid. Try \"msg userName message\".\n");
      return;
    }

    String toUserName = data[0];
    String message = data[1];

    for (int i = 0; i < users.size(); ++i) {
      if (users.get(i).getUsername().trim().equals(toUserName.trim()) && users.get(i).isLogin()) {
        users.get(i).sendAlert(this.user.getUsername(), message);
        logging.logEvent(logEventStringBuilder(message, "msg"));
        return;
      }
    }
  }

  synchronized private void isOnline(DataOutputStream dos) throws IOException {
    for (int i = 0; i < users.size(); ++i) {
      if (users.get(i).isLogin()
        && !this.user.getUsername().equals(users.get(i).getUsername())) {
        dos.writeBytes(users.get(i).getUsername() + "\n");
        logging.logEvent(logEventStringBuilder("", "online"));
      }
    }
  }

  synchronized private void checkLogin(String[] data, DataOutputStream dos) throws IOException {
    if (data.length <= 1) {
      dos.writeBytes("Input is invalid. Try it again.\n");
      return;
    }

    String name = data[0];
    String password = data[1];
    FileInputStream file =
      new FileInputStream(Config.FILE_PATH);
    Scanner sc = new Scanner(file);

    while (sc.hasNext()) {
      String[] info = sc.nextLine().split("::", 2);
      if (info[0].equals(name) && info[1].equals(password)) {
        this.user.setUsername(name.trim());
        this.user.setLogin(true);
        logging.logEvent(logEventStringBuilder(name + password, "login"));
        return;
      }
    }
  }

  synchronized private void registration(String[] data, DataOutputStream dos) throws IOException {
    if (data.length <= 1) {
      dos.writeBytes("Input is invalid. Try it again.\n");
      return;
    }

    String name = data[0];
    String password = data[1];

    if (this.user.isLogin()) {
      logging.logEvent(logEventStringBuilder(name + password, "reg"));
      dos.writeBytes("You have already logged in.\n");
      return;
    }
    if (userNameExist(name)) {
      logging.logEvent(logEventStringBuilder(name + password, "reg"));
      dos.writeBytes("This username exists. Try it again.\n");
      return;
    }
    if (checkPassword(password)) {
      BufferedWriter writer = new BufferedWriter(new FileWriter
        (Config.FILE_PATH, true));
      writer.append(name);
      writer.append("::");
      writer.append(password);
      writer.append("\n");
      writer.close();
      this.user.setUsername(name.trim());
      this.user.setLogin(true);
      logging.logEvent(logEventStringBuilder(name + password, "reg"));
      dos.writeBytes("Your registration is successful.\n");
    } else {
      dos.writeBytes("The password must consist of at least 8 characters that are a combination of letters" +
        "in both uppercase and lowercase and a digit, but must not contain a colon.\n");
    }
  }

  synchronized private boolean userNameExist(String name) throws FileNotFoundException {
    FileInputStream file =
      new FileInputStream(Config.FILE_PATH);
    Scanner sc = new Scanner(file);

    while (sc.hasNext()) {
      String[] info = sc.nextLine().split("::", 2);
      if (info[0].equals(name)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkPassword(String password) {
    if (password.length() < 8) {
      return false;
    }
    char ch;
    boolean capitalFlag = false;
    boolean lowerCaseFlag = false;
    boolean numberFlag = false;
    boolean existColon = false;
    for (int i = 0; i < password.length(); ++i) {
      ch = password.charAt(i);
      if (Character.isDigit(ch)) {
        numberFlag = true;
      } else if (Character.isUpperCase(ch)) {
        capitalFlag = true;
      } else if (Character.isLowerCase(ch)) {
        lowerCaseFlag = true;
      } else if (ch == ':') {
        existColon = true;
      }
    }
    return (numberFlag && capitalFlag && lowerCaseFlag && !existColon);
  }

  private String logEventStringBuilder(String message, String eventName) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return (dtf.format(now) + ";" + eventName + ";" + this.user.getUsername() + ";" + message);
  }

  private String logErrorStringBuilder(String message, String eventName, String methodName) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return (dtf.format(now) + ";" + eventName + ";" + this.user.getUsername() + ";"
      + message + ";" + methodName);
  }

}
