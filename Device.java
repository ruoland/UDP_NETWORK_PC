package com.device.app;

import java.net.DatagramPacket;

public interface Device {
    public String getDeviceName();
    public void setDeviceName(String name);
    public void sendMessageToDevice(Device device, String message);

    public boolean isDeviceConnect();

    public DatagramPacket receive() throws Exception;

    public String getDeviceType();


}
