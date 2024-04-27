package helpers.singletons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class IOConnection {
    private static IOConnection instance = null;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private String SERVER_HOST = "localhost";
    private int SERVER_PORT = 20001;

    private IOConnection(String serverHost, int serverPort) {
        this.SERVER_HOST = serverHost;
        this.SERVER_PORT = serverPort;
    }

    private IOConnection() {
    }

    public static IOConnection getInstance(String serverHost, int serverPort) {
        if (instance == null) {
            instance = new IOConnection(serverHost, serverPort);
        }
        return instance;
    }
    public static IOConnection getInstance() {
        if (instance == null) {
            instance = new IOConnection();
        }
        return instance;
    }
    public void connect() throws IOException, UnknownHostException {
        this.socket = new Socket(SERVER_HOST, SERVER_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        setIO(out, in);
    }

    public void send(String message) {
        out.println(message);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    private void setIO(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

}
