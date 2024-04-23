import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FindingDevice {
    String pcName;
    boolean connect;
    HashMap<String, Device> androidDeviceMap = new HashMap<>();

    public void start() {
        Thread thread = new Thread(() -> {
            try {
                Device device = new Device();
                String receive = device.receive();
                if (receive == null)
                    return;
                    
                String[] deviceCommand = Util.splitMessage(receive);
                String deviceName = deviceCommand[0];
                String command = deviceCommand[1];

                if (command.equals("초기 연결")) {
                    System.out.println(deviceName + command);
                    device.androidName = deviceName;
                    addDevice(device);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public Device getDevice(String name) {
        return androidDeviceMap.get(name);
    }

    // 테스커 기기로 정보 보내기
    public void sendDeviceInfo(String message) {
        try {
            MulticastSocket multicastSocketToAndroid = Util.createSocket();
            byte[] messageBuffer = message.getBytes("UTF-8");
            DatagramPacket dp = Util.createPacket(messageBuffer, 5554);
            multicastSocketToAndroid.send(dp);
            multicastSocketToAndroid.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDevice(Device device) {
        if (androidDeviceMap.containsKey(device.androidName) && androidDeviceMap.get(device.androidName).isConnected){
            return;
        }
        androidDeviceMap.put(device.androidName, device);
        sendDeviceInfo(pcName + ":초기 연결");
        System.out.println(device.androidName + "기기와 연결됨");
        connect = true;
        saveDevice();
    }

    public void saveDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Device>>() {
        }.getType();
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("devices.json"));
            dos.writeUTF(gson.toJson(androidDeviceMap, type));
            dos.flush();
            dos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Device>>() {
        }.getType();
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("devices.json"));
            gson.fromJson(dis.readUTF(), type);
            dis.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
