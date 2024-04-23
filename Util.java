import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Util {
    static InetAddress inetAddress;

    static{
        try {
            inetAddress = InetAddress.getByName("230.0.0.1");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static MulticastSocket createSocket(int port) throws Exception{
        MulticastSocket multicastSocket;
        if(port != 0)
            multicastSocket= new MulticastSocket(port);
        else
            multicastSocket = new MulticastSocket();

        multicastSocket.joinGroup(inetAddress);
        multicastSocket.setLoopbackMode(true);
        multicastSocket.setReceiveBufferSize(1024);
        return multicastSocket;
    }
    public static MulticastSocket createSocket() throws Exception{
        return createSocket(0);
    }
    public static DatagramPacket createPacket(byte[] messageBuffer, int port){
        DatagramPacket datagramPacket;
        if(port != 0)
            datagramPacket = new DatagramPacket(messageBuffer, messageBuffer.length, inetAddress, port);
        else
            datagramPacket = new DatagramPacket(messageBuffer, messageBuffer.length);
        return datagramPacket;
    }
    public static DatagramPacket createPacket(byte[] messageBuffer){
        return createPacket(messageBuffer, 0);
    }

    public static String getString(byte[] buffer){
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static String[] splitMessage(byte[] messageBuffer){
        String[] deviceCommand = new String(messageBuffer, StandardCharsets.UTF_8).trim().split(":", 2);
        return deviceCommand;
    }
    public static String[] splitMessage(String messageBuffer){
        String[] deviceCommand = messageBuffer.trim().split(":", 2);
        return deviceCommand;
    }
}
