package com.device.app;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class DeviceAndroid implements Device{

    private transient MulticastSocket multicastSocketToAndroid;
    private String deviceName;
    private transient boolean isDeviceConnect = false;

    DeviceAndroid(){
        try {
            multicastSocketToAndroid = Util.createSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 기기에서 보낸 메세지 받기
    public DatagramPacket receive() throws Exception {
        try (MulticastSocket multicastSocket = Util.createSocket(5554)) {
            multicastSocket.setSoTimeout(10000);
           
            System.out.println("앱에서 오는 응답 기다리는 중...");
            byte[] data = new byte[55555];
            DatagramPacket datagramPacket = Util.createPacket(data);
            multicastSocket.receive(datagramPacket);
            String message = new String(data, StandardCharsets.UTF_8);
            System.out.println("받은 메세지:" + message);
            isDeviceConnect = true;
            return datagramPacket;

        } catch (SocketTimeoutException e) {
            System.out.println("앱과 연결 실패, 재시작...");
            Main.restart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDeviceType() {
        return "Android";
    }

    /**
     * 서버와 주기적으로 통신함
     */
    public void checkDevice() {
        new Thread(() ->{
                try (MulticastSocket multicastSocket = Util.createSocket()){

                    while (true) {
                        byte[] messageBuffer = (deviceName+":AREYOUALIVE").getBytes("UTF-8");
                        DatagramPacket dp = Util.createPacket(messageBuffer, 5554);
                        multicastSocket.send(dp);

                        DatagramPacket receivePacket = Util.createPacket(messageBuffer, 5554);
                        multicastSocket.receive(receivePacket);
                        Thread.sleep(10000);
                    }
                }
                catch(SocketTimeoutException e){
                    e.printStackTrace();
                    System.out.println("서버와 연결 끊김");
                    isDeviceConnect = false;
                }
                 catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
    }
    // 연결된 기기로 정보 보내기
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


    @Override
    public String getDeviceName() {
        return deviceName;
    }
    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public boolean isDeviceConnect() {
        return isDeviceConnect;
    }

    @Override
    public void sendMessageToDevice(Device device, String message) {
        try {
            byte[] messageBuffer = Util.makeMessageBuffer(device, message);
            DatagramPacket dp = Util.createPacket(messageBuffer, 5555);
            multicastSocketToAndroid.send(dp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
