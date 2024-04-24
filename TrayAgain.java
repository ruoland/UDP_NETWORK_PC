import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TrayAgain {
    static TrayIcon trayIcon;
    static TrayMenu trayFrame;
    static String toolTip = "명령어 감시중...";

    public static void makeTrayIcon() {
        try {
            trayIcon = new TrayIcon(ImageIO.read(new File("./mark.jpg")), "트레이 아이콘");
            trayIcon.addActionListener(e -> System.out.println("트레이 아이콘 클릭됨"));
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("툴팁");

            trayFrame = new TrayMenu(trayIcon);
            trayFrame.addItem("기기 찾는 중", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                }
            });
            trayFrame.addItem("연결된 기기", new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            trayIcon.displayMessage("메세지 전달하기", "안녕!", TrayIcon.MessageType.INFO);
                        }
            });
            SystemTray.getSystemTray().add(trayIcon);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public static void addDeviceMenu(String menu, Device device){
        TrayMenu.addItem(menu, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int select = JOptionPane.showConfirmDialog(null, "이 기기와의 연결을 끊을까요?", "기기 연결 끊기", JOptionPane.YES_NO_OPTION);
                if(select == JOptionPane.YES_OPTION){
                    Main.findingDevice.removeDevice(device.androidName);
                    System.out.println(device.androidName);

                }
                else if(select == JOptionPane.NO_OPTION){
                    return;
                }
            }
        });
    }

    public static void setToolTip(String toolTipArg){
        toolTip = toolTipArg;
        trayIcon.setToolTip(toolTip);
    }
}
