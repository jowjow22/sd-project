package helpers.singletons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import records.Request;
import records.Response;

public class IOServerConnection {
    private static IOServerConnection instance = null;
    private PrintWriter out;
    private BufferedReader in;

    private final Json json = Json.getInstance();

    public IOServerConnection(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }
    private IOServerConnection() {
    }

    public void setIO(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void send(Response<?> response) {
        String requestRaw = json.toJson(response);
        System.out.println("[LOG]: Sending response: " + requestRaw);
        out.println(requestRaw);
    }

    public <DT> Request<DT> receive(Class<DT> dataClass, String responseRaw) throws IOException {
        if(responseRaw == null){
            System.out.println("[LOG]: Connection closed by client or error");
            return null;
        }
        System.out.println("[LOG]: Receiving request: " + responseRaw);
        Request<?> request = json.fromJson(responseRaw, Request.class);
        return request.withDataClass(dataClass);
    }
    public void close() throws IOException {
        out.close();
        in.close();
    }
}
