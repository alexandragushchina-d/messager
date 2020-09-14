import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class MyThread extends Thread {

  private Socket socket;
  private Vector<Socket> users;
  private String userName;
  private boolean isLogin;

  public MyThread(Vector<Socket> users, Socket socket) {
    this.users = users;
    this.socket = socket;
    this.isLogin = false;
  }

  @Override
  public void run() {
    try {
      OutputStream os = socket.getOutputStream();
      DataOutputStream dos = new DataOutputStream(os);
      InputStream is = socket.getInputStream();
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
      case "msg":
        sendMessage(command[1], dos);
        break;
      case "login":
        checkLogin(command[1].split(" ", 2));
        break;
      case "reg":
        registration(command[1].split(" ", 2), dos);
        break;
      default:
        dos.writeBytes("Incorrect input. Try it again.\n");
        break;
    }
  }

  synchronized private void sendMessage(String message, DataOutputStream dos) throws IOException {
    for (int i = 0; i < users.size(); ++i) {
      if (isLogin) {
        try {
          new DataOutputStream(users.get(i).getOutputStream()).writeBytes(userName + ": " + message + "\n");
        } catch (Exception e) {
          users.remove(i);
          --i;
          break;
        }
      } else {
        dos.writeBytes("Please log in or create account.\n");
        return;
      }
    }
  }

  synchronized private void checkLogin(String[] data) throws FileNotFoundException {
    FileInputStream file =
      new FileInputStream("file_path/src/bd");
    Scanner sc = new Scanner(file);
    while (sc.hasNext()) {
      String[] info = sc.nextLine().split("::", 2);
      if (info[0].equals(data[0]) && info[1].equals(data[1])) {
        this.userName = data[0];
        this.isLogin = true;
        return;
      }
    }
  }

  synchronized private void registration(String[] data, DataOutputStream dos) throws IOException {
    if (isLogin) {
      dos.writeBytes("You logged in.");
      return;
    }
    if (userNameExist(data)) {
      dos.writeBytes("This username exists. Try it again.\n");
      return;
    }
    BufferedWriter writer = new BufferedWriter(new FileWriter
      ("file_path/src/bd", true));
    writer.append(data[0]);
    writer.append("::");
    writer.append(data[1]);
    writer.append("\n");
    writer.close();
    this.isLogin = true;
    this.userName = data[0];
    dos.writeBytes("Your registration was successful.\n");
  }

  synchronized private boolean userNameExist(String[] data) throws FileNotFoundException {
    FileInputStream file =
      new FileInputStream("file_path/src/bd");
    Scanner sc = new Scanner(file);
    while (sc.hasNext()) {
      String[] info = sc.nextLine().split("::", 2);
      if (info[0].equals(data[0])) {
        return true;
      }
    }
    return false;
  }

}
