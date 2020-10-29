import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserInfo {

  private Socket socket;
  private boolean isLogin;
  private String userName;

  public UserInfo(Socket socket) {
    this.socket = socket;
    this.isLogin = false;
    this.userName = "";
  }

  public boolean isLogin() {
    return isLogin;
  }

  public void sendAlert(String fromUser, String message) throws IOException {
    if (this.isLogin) {
      new DataOutputStream(this.socket.getOutputStream())
        .writeBytes(fromUser + ": " + message + "\n");
    }
  }

  public void setUsername(String userName) {
    this.userName = userName;
  }

  public void setLogin(Boolean isLogin) {
    this.isLogin = isLogin;
  }

  public String getUsername() {
    return this.userName;
  }

  public Socket getSocket() {
    return this.socket;
  }
}
