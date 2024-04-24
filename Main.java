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

    static FindingDevice findingDevice;
    static TrayAgain trayAgain = new TrayAgain();

    public static void main(String[] args) throws Exception {
        restart();
        trayAgain.makeTrayIcon();
        commandSocket();
        commandReceive();
    }

    public static void restart() throws Exception {
        findingDevice = new FindingDevice();
        findingDevice.pcName = InetAddress.getLocalHost().getHostName();
        findingDevice.start();
        findingDevice.loadDevice();
    }

    public static void commandReceive() {
        new Thread(() -> {
            while (true) {
                try (MulticastSocket multicastSocket = Util.createSocket(5555)) {
                    byte[] messageBuffer = new byte[256];
                    DatagramPacket dp = Util.createPacket(messageBuffer);
                    multicastSocket.receive(dp);

                    String[] deviceCommand = Util.splitMessage(dp.getData());
                    String device = deviceCommand[0];
                    if(!device.equals(FindingDevice.pcName))
                        return;
                    String command = deviceCommand[1];
                    System.out.println(device + " - " + command);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
        ;
    }

    public static void commandSocket() {
        new Thread(() -> {
            while (true) {
                try (ServerSocket serverSocket = new ServerSocket(55556)) {
                    Socket socket = serverSocket.accept();
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                    String[] deviceCommand = Util.splitMessage(dis.readUTF());
                    String device = deviceCommand[0];
                    String command = deviceCommand[1];
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
        }).start();

    }

}