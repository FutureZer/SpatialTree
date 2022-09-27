package com.ml.spatialtree;

import com.ml.spatialtree.data.DataRow;
import com.ml.spatialtree.data.TrainDataCreator;
import com.ml.spatialtree.filereader.TifFileReader;
import com.ml.spatialtree.filereader.TifFileReaderException;
import com.ml.spatialtree.tree.Node;
import com.ml.spatialtree.tree.TreeBuilder;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialController {

    private static final String DATASET_PATH = "src/main/resources/dataset/data.csv";
    private static final String UNIQUE_COLOR_PATH = "src/main/resources/dataset/unique.csv";
    private static final String NEIGHBOR_DATA_PATH = "src/main/resources/dataset/neighbors.txt";
    private static final String RESULT_IMG_PATH = "src/main/resources/result/result.tif";
    private static final String SAVED_CLASSIFIER = "src/main/resources/result/tree.bin";

    @FXML
    private ImageView classificationView;

    @FXML
    void createTree(MouseEvent event) {
        WaitDialog wait = new WaitDialog();
        Thread th = new Thread(() -> {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVED_CLASSIFIER))) {
                TrainDataCreator creator = new TrainDataCreator(readData(), readUnique());
                TreeBuilder builder = new TreeBuilder(creator.getParsedData());
                Node root = builder.buildTree(creator.getParsedData(), creator.getUniqueAttrs());
                out.writeObject(root);
                out.flush();
                Platform.runLater(wait::close);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            th.start();
            wait.showAndWait();
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Classifier (tree) has created written successfully");
        alert.showAndWait();
    }


    @FXML
    void showClassification(MouseEvent event) {
        List<List<DataRow>> rows;
        try {
            TrainDataCreator creator = new TrainDataCreator(readData(), readUnique());
            TifFileReader reader = new TifFileReader(creator);
            rows = creator.createTestTable(reader.selectDownloadedFeatures());
        } catch (TifFileReaderException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error occurred");
            alert.setHeaderText("Impossible to do classification");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        WaitDialog wait = new WaitDialog();
        Thread th = new Thread(() -> {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVED_CLASSIFIER))) {
                TrainDataCreator creator = new TrainDataCreator(readData(), readUnique());
                Node root = (Node) in.readObject();
                BufferedImage img = new BufferedImage(creator.getPicWidth(), creator.getPicHeight(),
                        BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < creator.getPicWidth(); ++i) {
                    for (int j = 0; j < creator.getPicHeight(); ++j) {
                        Color current = classify(rows.get(i).get(j), root);
                        img.setRGB(i, j, current.getRGB());
                    }
                }
                ImageIO.write(img, "tif", new File(RESULT_IMG_PATH));
                classificationView.setImage(SwingFXUtils.toFXImage(img, null));
                Platform.runLater(wait::close);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            th.start();
            wait.showAndWait();
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void parseDataToFiles(MouseEvent event) {
        WaitDialog wait = new WaitDialog();
        Thread th = new Thread(() -> {
            try (FileWriter dataWriter = new FileWriter(DATASET_PATH);
                 FileWriter uniqueWriter = new FileWriter(UNIQUE_COLOR_PATH);
                 FileWriter neighbourWriter = new FileWriter(NEIGHBOR_DATA_PATH)) {
                TrainDataCreator creator = new TrainDataCreator();
                creator.createTrainTable();
                List<DataRow> data = creator.getParsedData();
                Map<Color, Integer> unique = creator.getUniqueAttrs();
                for (DataRow dataEl : data) {
                    dataWriter.write(dataEl.toString() + "\n");
                    neighbourWriter.write(dataEl.toStringNeighbors() + "\n");
                }
                for (Color key : unique.keySet()) {
                    uniqueWriter.write(creator.keyElemToString(key, unique.get(key)) + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(wait::close);
        });
        try {
            th.start();
            wait.showAndWait();
            th.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Data has been written successfully");
        alert.showAndWait();
    }

    private Color classify(DataRow row, Node node) {
        if (node.isLeaf()) {
            return node.getPrediction();
        } else {
            if (node.getQuestion().match(row)) {
                return classify(row, node.getTrueBranch());
            } else {
                return classify(row, node.getFalseBranch());
            }
        }
    }

    private List<DataRow> readData() throws IOException {
        try (BufferedReader dataReader = new BufferedReader(new FileReader(DATASET_PATH));
             BufferedReader neighborReader = new BufferedReader(new FileReader(NEIGHBOR_DATA_PATH))) {
            List<DataRow> rows = new ArrayList<>();
            String line = dataReader.readLine();
            String neighbor = neighborReader.readLine();
            while (line != null && neighbor != null) {
                DataRow currentRow = new DataRow(line);
                currentRow.setNeighborsFromString(neighbor);
                rows.add(currentRow);
                line = dataReader.readLine();
                neighbor = neighborReader.readLine();
            }
            return rows;
        }
    }

    private Map<Color, Integer> readUnique() throws IOException {
        try (BufferedReader dataReader = new BufferedReader(new FileReader(UNIQUE_COLOR_PATH))) {
            Map<Color, Integer> uni = new HashMap<>();
            String line = dataReader.readLine();
            while (line != null) {
                String[] read = line.split(";");
                Color key = new Color(Integer.parseInt(read[0]),
                        Integer.parseInt(read[1]),
                        Integer.parseInt(read[2]));
                uni.put(key, Integer.parseInt(read[read.length - 1]));
                line = dataReader.readLine();
            }
            return uni;
        }
    }
}