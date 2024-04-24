package com.device.app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeviceManager {

    //안드로이드와 연결된 기기
    private final static HashMap<String, DeviceAndroid> androidDeviceMap = new HashMap<>();
    private final static HashMap<String, DevicePC> pcDeviceMap = new HashMap<>();
    private final static DevicePC thisPC = new DevicePC();;
    public void findDevice() {
        new Thread(() -> {
            try {
                Device device = new DeviceTemp();
                DatagramPacket receive = device.receive();
                if (receive == null) //전달 받은 메세지가 없음
                    return;

                String[] deviceNameTypeCommand = Util.splitM2essage(Util.getString(receive.getData()));

                String deviceType = deviceNameTypeCommand[0];
                String deviceName = deviceNameTypeCommand[1];
                String command = deviceNameTypeCommand[2];

                if (command.equals("초기 연결")) {
                    if(deviceType.equals("Android")) {
                        DeviceAndroid deviceAndroid = new DeviceAndroid();
                        deviceAndroid.setDeviceName(deviceName);
                        addDevice(deviceAndroid);
                    }
                    if(deviceType.equals("PC")) {
                        DevicePC devicePC = new DevicePC(deviceName, receive.getAddress());
                        devicePC.setDeviceName(deviceName);
                        addDevice(devicePC);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static Device getDevice(String name) {
        if(androidDeviceMap.containsKey(name))
            return androidDeviceMap.get(name);
        else if(pcDeviceMap.containsKey(name));
            return pcDeviceMap.get(name);
    }

    public void addDevice(DeviceAndroid device) {
        androidDeviceMap.put(device.getDeviceName(), device);
        device.sendDeviceInfo(device.getDeviceName() + ":초기 연결");
        System.out.println(device.getDeviceName() + "기기와 연결됨");
        TrayAgain.addDeviceMenu(device.getDeviceName(), device);
        saveDevice();
    }

    public void addDevice(DevicePC device) {
        pcDeviceMap.put(device.getDeviceName(), device);
        device.sendDeviceInfo(device.getDeviceName() + ":초기 연결");
        System.out.println(device.getDeviceName() + "기기와 연결됨");
        TrayAgain.addDeviceMenu(device.getDeviceName(), device);
        saveDevice();
    }

    public void removeDevice(String device){
        if (!checkConnection(device)){
            return;
        }
        DeviceAndroid fireDevice = androidDeviceMap.get(device);
        DeviceManager.thisDevice().sendMessageToDevice(fireDevice, "이 기기는 해고 되었습니다.3234ㅏ9459307*/");
        System.out.println(fireDevice.getDeviceName() + "와 연결 끊김");
        androidDeviceMap.remove(device);
        TrayMenu.removeDevice(device);
    }

    public static DevicePC thisDevice(){
        return thisPC;
    }

    public void saveDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, DeviceAndroid>>() {
        }.getType();
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("devices.json"));
            dos.writeUTF(gson.toJson(androidDeviceMap, type));
            dos.flush();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDevice() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, DeviceAndroid>>() {
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

    public boolean checkConnection(String androidName){
        return androidDeviceMap.containsKey(androidName) && androidDeviceMap.get(androidName).isDeviceConnect();
    }
}
