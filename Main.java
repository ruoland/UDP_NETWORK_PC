package com.device.app;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    static DeviceManager findingDevice;
    static TrayAgain trayAgain = new TrayAgain();
    static DevicePC thisDevice;
    public static void main(String[] args) throws Exception {
        thisDevice = new DevicePC();
        restart();
        trayAgain.makeTrayIcon();
        commandSocket();
        commandReceive();
    }

    public static void restart() throws Exception {
        findingDevice = new DeviceManager();
        findingDevice.findDevice();
        findingDevice.loadDevice();
    }

    /**
     * 받은 명령어를 트레이 알림으로 표시
     */
    public static void commandReceive() {
        new Thread(() -> {
            while (true) {
                try (MulticastSocket multicastSocket = Util.createSocket(5555)) {
                    byte[] messageBuffer = new byte[256];
                    DatagramPacket dp = Util.createPacket(messageBuffer);
                    multicastSocket.receive(dp);
                    String[] deviceCommand = Util.splitMessage(dp.getData());
                    String deviceName = deviceCommand[0];

                    if(!deviceName.equals(Util.getThisPC().getHostName()))
                        return;
                    String command = deviceCommand[1];

                    TrayAgain.trayIcon.displayMessage(deviceName, command, null);
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

                    String[] deviceCommand = Util.splitM2essage(dis.readUTF());
                    Device device = DeviceManager.getDevice(deviceCommand[1]);
                    String command = deviceCommand[2];
                    System.out.println(device + " - " + command);

                    if (device != null) {
                        dos.writeUTF(device + "에게 잘 전송됨");

                        socket.close();
                        DeviceManager.thisDevice().sendMessageToDevice(device, command);

                    } else {
                        dos.writeUTF(device.getDeviceName() + "는 연결된 적이 없거나 연결되지 않은 기기");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();

    }

}