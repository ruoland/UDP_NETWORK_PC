import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static final FindingDevice findingDevice = new FindingDevice();

    public static void main(String[] args) {
        findingDevice.start();
        findingDevice.loadDevice();
        commandSocket();

    }

    public static void commandSocket() {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(55556)) {
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                String[] utf = dis.readUTF().split(":");
                String device = utf[0];
                String command = utf[1];
                System.out.println(device + " - " + command);
                if (findingDevice.getDevice(device) != null) {
                    dos.writeUTF(device + "에게 잘 전송됨");

                    socket.close();
                    findingDevice.getDevice(device).sendMessage(command);

                } else {
                    dos.writeUTF(device + "는 연결된 적이 없거나 연결되지 않은 기기");
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

    }

}