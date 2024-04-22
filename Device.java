import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Device {
    private InetAddress inetAddress;
    MulticastSocket multicastSocketToAndroid;
    String deviceName;
    Device(){
        try {
            
            inetAddress = InetAddress.getByName("230.0.0.1");
            deviceName = inetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //테스커 기기에서 보낸 메세지 받기
    public String receive(){
        try {
     
            while (true) {
                MulticastSocket multicastSocket = new MulticastSocket(5555);
                multicastSocket.joinGroup(inetAddress);
                multicastSocket.setLoopbackMode(true);

                byte[] data = new byte[55555];
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                multicastSocket.receive(datagramPacket);
                InetAddress ia = datagramPacket.getAddress();
                String message = new String(data, StandardCharsets.UTF_8);
                System.out.println("받은 메세지:" + message);
                multicastSocket.close();
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //테스커 기기로 보내기
    public void sendDeviceInfo(String message){
        try{
            if(multicastSocketToAndroid == null){
                multicastSocketToAndroid = new MulticastSocket();
                multicastSocketToAndroid.joinGroup(inetAddress);
                multicastSocketToAndroid.setLoopbackMode(true);
            }
            byte[] messageBuffer = message.getBytes("UTF-8");
            DatagramPacket dp = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, 5554);
            multicastSocketToAndroid.send(dp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        try{
            if(multicastSocketToAndroid == null){
                multicastSocketToAndroid = new MulticastSocket();
                multicastSocketToAndroid.joinGroup(inetAddress);
                multicastSocketToAndroid.setLoopbackMode(true);
            }
            String deviceName = InetAddress.getLocalHost().getHostName();
;
            byte[] messageBuffer = (deviceName+":"+message).getBytes("UTF-8");
            DatagramPacket dp = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, 5555);
            System.out.println(dp);

            multicastSocketToAndroid.send(dp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    

}
