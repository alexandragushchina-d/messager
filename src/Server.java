import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  private int port;
  private String host;
  private ServerSocket serverSocket;
  private MyVector<UserInfo> users;
  private UserInfo user;

  public Server() {
    this.host = Config.HOST;
    this.port = Config.PORT;
  }

  public Server(String host, int port) {
    this.host = host;
    this.port = port;
    this.users = new MyVector<>();
  }

  public void runServer() throws IOException {
    serverSocket = new ServerSocket(port);
    Socket socket;

    while (true) {
      socket = serverSocket.accept();
      user = new UserInfo(socket);
      users.add(user);
      new MyThread(users, user).start();
    }
  }

}