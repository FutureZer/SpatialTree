package com.ml.spatialtree.filereader;

import com.ml.spatialtree.data.TrainDataCreator;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TifFileReader {

    private final TrainDataCreator creator;

    public TifFileReader(TrainDataCreator creator) {
        this.creator = creator;
    }

    public List<BufferedImage> selectDownloadedFeatures() throws TifFileReaderException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.tif"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        List<BufferedImage> img = new ArrayList<>();
        int featuresSize = creator.getParsedData().get(0).getFeaturesSize();
        if (files.size() != featuresSize) {
            throw new TifFileReaderException("You should select " + featuresSize + " pictures");
        }
        for (File file: files) {
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (!extension.equals("tif")) {
                throw new TifFileReaderException("All files should be .tif");
            }
            try {
                img.add(ImageIO.read(file));
            } catch (IOException e) {
                throw new TifFileReaderException("Impossible to read image");
            }
        }
        return img;
    }
}
