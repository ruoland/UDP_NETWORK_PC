import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Main {
    static String deviceName;
    static boolean connect;
    public static void main(String[] args) throws UnknownHostException{
        deviceName = InetAddress.getLocalHost().getHostName();
        System.out.println("JEJllo world");
        
        start();
        Device findDevice = new Device();                    
    }

    public static void start(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Device findDevice = new Device();                    
                    findDevice.sendMessage("잘 된다 후후");
                    String message = findDevice.receive().trim();

                    switch (message) {
                        case "테스커-응답하라":
                            findDevice.sendDeviceInfo(deviceName+":첫연결");
                            connect = true;
                            break;
                        default:
                            System.out.println(message);
                            break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}