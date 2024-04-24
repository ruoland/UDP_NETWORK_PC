package com.device.app;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DevicePC implements Device{
    private String deviceName;
    private InetAddress deviceAddress;
    private transient boolean isDeviceConnect;
    private boolean thisDevice;
    public DevicePC(){
        deviceAddress = Util.getThisPC();
        this.deviceName = deviceAddress.getHostName();
        thisDevice = true;
    }
    public DevicePC(String pcName, InetAddress inetAddress){
        this.deviceName = pcName;
        this.deviceAddress = inetAddress;
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
    public DatagramPacket receive() throws Exception {
        return null;
    }

    @Override
    public String getDeviceType() {
        return "PC";
    }

    public void sendDeviceInfo(String message) {
        try {
            MulticastSocket multicastSocketToPC = Util.createSocket();
            byte[] messageBuffer = message.getBytes("UTF-8");
            DatagramPacket dp = Util.createPacket(messageBuffer, 5554);
            multicastSocketToPC.send(dp);
            multicastSocketToPC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void sendMessageToDevice(Device device, String message) {

    }
}
