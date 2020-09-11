import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

  private int port;
  private String host;
  private ServerSocket serverSocket;
  private Vector<Socket> users;

  public Server() {
    this.host = "localhost";
    this.port = 8080;
  }

  public Server(String host, int port) {
    this.host = host;
    this.port = port;
    this.users = new Vector<>();
  }

  public void runServer() throws IOException {
    serverSocket = new ServerSocket(port);
    Socket socket;

    while (true) {
      socket = serverSocket.accept();
      users.add(socket);
      new MyThread(users, socket).start();
    }
  }

}