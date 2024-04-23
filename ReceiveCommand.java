import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class ReceiveCommand {

    public static void main(String[] args) {
        String device = args[0];
        String deviceCommand= getStringArray(args, 1, args.length, " ");
        System.out.println(device + ":"+deviceCommand);
        try {
            Socket socket = new Socket(InetAddress.getByName("localhost"), 55556);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(device+":"+deviceCommand);
            Thread.sleep(1000);
            System.out.println(dis.readUTF());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringArray(String[] args, int start, int end, String space){
        StringBuffer buffer = new StringBuffer();
        for(int i = start; i < end;i++){
            buffer.append(args[i]).append(space);
        }
        return buffer.toString();
    }
}
