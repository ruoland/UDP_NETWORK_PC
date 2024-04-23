import javax.swing.ImageIcon;
import javax.swing.event.AncestorListener;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TrayIconForTasks extends Application{
    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        Image icon = new Image("ttt.png");
        arg0.getIcons().add(icon);

        arg0.setOnHidden(event -> {
            System.out.println("헉");
        });
        arg0.show();;
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
