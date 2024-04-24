package com.device.app;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DeviceTemp implements Device{
    private String type;
    @Override
    public String getDeviceName() {
        return "temp";
    }

    @Override
    public void setDeviceName(String name) {

    }

    @Override
    public void sendMessageToDevice(Device device, String message) {

    }

    @Override
    public boolean isDeviceConnect() {
        return false;
    }

    @Override
    public DatagramPacket receive() {
        try (MulticastSocket multicastSocket = Util.createSocket(5554)) {
            multicastSocket.setSoTimeout(10000);

            System.out.println("앱에서 오는 응답 기다리는 중...");
            byte[] data = new byte[55555];
            DatagramPacket datagramPacket = Util.createPacket(data);
            multicastSocket.receive(datagramPacket);
            String message = Util.getString(data);
            System.out.println("받은 메세지:" + message);
            return datagramPacket;

        } catch (SocketTimeoutException e) {
            System.out.println("앱과 연결 실패, 재시작...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDeviceType() {
        return null;
    }
}
