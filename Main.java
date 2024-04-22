import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Main {
    static String deviceName;
    public static void main(String[] args) throws UnknownHostException{
        deviceName = InetAddress.getLocalHost().getHostName();
        System.out.println("JEJllo world");
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    
                    Device findDevice = new Device();
                    findDevice.sendMessage(deviceName+":이것도 해보자고");
                    
                    String message = findDevice.receive().trim();
                    switch (message) {
                        case "테스커-응답하라":
                            findDevice.sendDeviceInfo(deviceName+":첫연결");
                            Thread.sleep(25000);
                            findDevice.sendMessage(deviceName+":명령어");
                            findDevice.sendMessage(deviceName+":테스커-되긴 하나");
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