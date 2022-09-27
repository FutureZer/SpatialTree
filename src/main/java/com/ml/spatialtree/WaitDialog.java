package com.ml.spatialtree;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WaitDialog extends Stage {

    WaitDialog() {
        Pane root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        Rectangle back = new Rectangle(350, 150, Color.WHITESMOKE);
        back.setStroke(Color.BLACK);
        back.setStrokeWidth(1.5);

        Text headerText = new Text("Loading...");
        headerText.setFont(Font.font(20));

        Text contentText = new Text("Program is working right now");
        contentText.setFont(Font.font(16));

        VBox box = new VBox(10, headerText, new Separator(Orientation.HORIZONTAL), contentText);
        box.setPadding(new Insets(25));
        root.getChildren().addAll(back, box);

        setScene(new Scene(root, null));
    }

}
