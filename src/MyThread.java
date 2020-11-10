import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class MyThread extends Thread {

  private MyVector<UserInfo> users;
  private UserInfo user;

  public MyThread(MyVector<UserInfo> users, UserInfo user) {
    this.users = users;
    this.user = user;
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

  synchronized private void sendAllMessage(String message) {
    for (int i = 0; i < users.size(); ++i) {
      try {
        users.get(i).sendAlert(this.user.getUsername(), message);
      } catch (Exception e) {
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
        return;
      }
    }
  }

  synchronized private void isOnline(DataOutputStream dos) throws IOException {
    for (int i = 0; i < users.size(); ++i) {
      if (users.get(i).isLogin()
        && !this.user.getUsername().equals(users.get(i).getUsername())) {
        dos.writeBytes(users.get(i).getUsername() + "\n");
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
      dos.writeBytes("You have already logged in.\n");
      return;
    }
    if (userNameExist(name)) {
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

  // only for testing
  private void logging(String message) {
    if (Config.DEBUG) {
      System.out.println(message);
      for (int i = 0; i < users.size(); ++i) {
        System.out.println(users.get(i).getUsername());
      }
    }
  }

}
