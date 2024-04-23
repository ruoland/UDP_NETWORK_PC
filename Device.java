import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Device {
    private InetAddress inetAddress;
    transient MulticastSocket multicastSocketToAndroid;
    String androidName, pcName;
    transient boolean isConnected = false;

    Device() {
        try {
            inetAddress = InetAddress.getByName("230.0.0.1");
            pcName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // 맨처음부터 기기에서 보낸 메세지 받기
    public String receive() throws Exception {
        try (MulticastSocket multicastSocket = Util.createSocket(5554)) {
            multicastSocket.setSoTimeout(10000);
           
            System.out.println("앱에서 오는 응답 기다리는 중...");
            byte[] data = new byte[55555];
            DatagramPacket datagramPacket = Util.createPacket(data);
            multicastSocket.receive(datagramPacket);
            String message = new String(data, StandardCharsets.UTF_8);
            System.out.println("받은 메세지:" + message);
            isConnected = true;
            return message;

        } catch (SocketTimeoutException e) {
            System.out.println("앱과 연결 실패, 재시작...");
            Main.restart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 서버와 주기적으로 통신함
     */
    public void checkDevice() {
        Thread cThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (MulticastSocket multicastSocket = new MulticastSocket()){
                    InetAddress inetAddress = InetAddress.getByName("230.0.0.1");
                    multicastSocket.joinGroup(inetAddress);
                    multicastSocket.setLoopbackMode(true);
                    while (true) {
                        byte[] messageBuffer = (pcName+":AREYOUALIVE").getBytes("UTF-8");
                        DatagramPacket dp = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, 5554);
                        multicastSocket.send(dp);
                        
                        DatagramPacket receivePacket = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, 5554);
                        multicastSocket.receive(receivePacket);
                        Thread.sleep(10000);
                    }

                }
                catch(SocketTimeoutException e){
                    e.printStackTrace();
                    System.out.println("서버와 연결 끊김");
                    isConnected = false;
                }
                 catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cThread.start();
    }

    public void sendMessage(String message) {
        try {
            if (multicastSocketToAndroid == null) {
                multicastSocketToAndroid = new MulticastSocket();
                multicastSocketToAndroid.joinGroup(inetAddress);
                multicastSocketToAndroid.setLoopbackMode(true);

            }
            String deviceName = InetAddress.getLocalHost().getHostName();

            byte[] messageBuffer = (deviceName + ":" + message).getBytes("UTF-8");
            DatagramPacket dp = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, 5555);
            multicastSocketToAndroid.send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (multicastSocketToAndroid != null)
            multicastSocketToAndroid.close();
    }

}
