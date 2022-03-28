package es.lumsoft.teleapp;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application{

    private Pane root = new Pane();
    private Scene scene;

    private final Button add = new Button("Add");
    private final VBox chatBox = new VBox(5);
    private List<Label> messages = new ArrayList<>();
    private ScrollPane container = new ScrollPane();
    private int index = 0;



    @Override
    public void start(Stage stage) throws Exception{

        initChatBox();
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        root.getChildren().addAll(container,add);
        scene = new Scene(root,800,600);
        stage.setScene(scene);
        stage.show();

    }

    private void initChatBox(){

        container.setPrefSize(700, 400);
        container.setContent(chatBox);

        chatBox.getStyleClass().add("chatbox");

        add.setOnAction(evt->{

            messages.add(new Label("I'm a message"));

            if(index%2==0){

                messages.get(index).setAlignment(Pos.CENTER_LEFT);
                System.out.println("1");

            }else{

                messages.get(index).setAlignment(Pos.CENTER_RIGHT);
                System.out.println("2");

            }


            chatBox.getChildren().add(messages.get(index));
            index++;

        });



    }


    public static void main(String[] args) {
        launch(args);
    }

}