package blayzer.privatehive;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    private static Point2D movePoint;

    public static String name;
    public static String key;
    public static String keyHash;
    public static String message;

    @FXML private Button exitButton;
    @FXML private Button minButton;
    @FXML private Button maxButton;
    @FXML private Pane topPane;
    @FXML private Pane loginPane;
    @FXML private Pane chatPane;
    @FXML private TextField nameField;
    @FXML private TextField keyField;
    @FXML private TextField messageField;
    @FXML private TextArea mainChatArea;
    @FXML private Label loginLabel;

    @FXML
    public void sendMessageButton() {
        if(!messageField.getText().isEmpty()) {
            String message = AES.encrypt(messageField.getText(), keyHash);
            String packet = name + "|" + keyHash + "|" + message;
            Network.out.println(packet);
            messageField.setText("");
        }
    }

    @FXML
    public void loginButton(){
        name = nameField.getText();
        if(name.isEmpty()) name = "Аноним";
        key = keyField.getText();
        if(key.isEmpty()) key = "Empty";
        keyHash = Utils.getHash(key);
        Network.connect();
        if(Network.isConnected) {
            loginPane.setVisible(false);
            chatPane.setVisible(true);
            //Срабатывает только если писать два раза. Разобраться.
            Network.out.println(name);
            Network.out.println(name);
            Network.out.println(keyHash);
            Network.out.println(keyHash);
            startMessagesThread();
        } else
            loginLabel.setText("Нет соединения с сервером");
    }

    @FXML
    public void exitButton(){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.hide();
        System.exit(0);
    }
    @FXML
    public void maxButton(){
        Stage stage = (Stage) maxButton.getScene().getWindow();
        if(!stage.isMaximized())
            stage.setMaximized(true);
        else
            stage.setMaximized(false);
    }
    @FXML
    public void minButton(){
        Stage stage = (Stage) minButton.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML
    public void moveWindowMousePressed(MouseEvent event){
        Stage stage = (Stage) topPane.getScene().getWindow();
        movePoint = new Point2D(event.getSceneX(), event.getSceneY());
    }
    @FXML
    public void moveWindowMouseDragged(MouseEvent event){
        Stage stage = (Stage) topPane.getScene().getWindow();
        if(movePoint != null) {
            stage.setX(event.getScreenX() - movePoint.getX());
            stage.setY(event.getScreenY() - movePoint.getY());
        }
    }
    public void startMessagesThread(){
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> {
                while(true) {
                    if(!mainChatArea.getText().endsWith(message + "\n") && message != null) {
                        mainChatArea.appendText(message + "\n");
                    }
                }
        });
    }
}
