package instantmessengerclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewtaylor
 */
public class Client {
    
    private Socket socket;
    private String hostname;
    int port;
    private BufferedReader networkIn;
    private PrintWriter networkOut;
    private KeyboardAdapter keyboardAdapter;
    private InstantMessengerGUI gui;
    
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        keyboardAdapter = new KeyboardAdapter(this);
    }
    
    public void connect(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        connect();
    }
    
    public void connect() {
        try {
            socket = new Socket(hostname, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        Thread listener = new Thread(() -> {
            try {
                networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                networkOut = new PrintWriter(socket.getOutputStream(), true);
                
                while (socket.isConnected()) {
                    String line = networkIn.readLine();
                    System.out.println(line);
                    gui.display(line + "\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listener.start();
    }
    
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public void startKeyboardAdapter() {
        keyboardAdapter.start();
    }
    
    public void openGUI() {
        gui = new InstantMessengerGUI(this);
        gui.setVisible(true);
    }
       
    public void send(String message) {
        networkOut.println(message);
    }
    
    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 8200);
        client.startKeyboardAdapter();
        client.openGUI();    
    }
}
