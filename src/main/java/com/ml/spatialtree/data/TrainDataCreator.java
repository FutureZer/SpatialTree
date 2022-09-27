package com.ml.spatialtree.data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TrainDataCreator {

    private final List<BufferedImage> features = new ArrayList<>();

    private List<DataRow> parsedData = new ArrayList<>();
    private Map<Color, Integer> uniqueAttrs = new HashMap<>();

    private final BufferedImage trueAttribute;

    private final int picHeight;
    private final int picWidth;

    public TrainDataCreator() throws IOException {
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band01.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band02.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band03.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band04.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band05.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band06.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band07.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band08.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/ndvi_sample.tif")));
        trueAttribute = ImageIO.read(new File("src/main/resources/dataset/attribute/Croptypes.tif"));
        picHeight = trueAttribute.getHeight();
        picWidth = trueAttribute.getWidth();
    }

    public int getPicWidth() {
        return picWidth;
    }

    public int getPicHeight() {
        return picHeight;
    }

    public TrainDataCreator(List<DataRow> data, Map<Color, Integer> map) throws IOException {
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band01.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band02.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band03.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band04.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band05.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band06.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band07.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/Band08.tif")));
        features.add(ImageIO.read(new File("src/main/resources/dataset/features/ndvi_sample.tif")));
        trueAttribute = ImageIO.read(new File("src/main/resources/dataset/attribute/Croptypes.tif"));
        picHeight = trueAttribute.getHeight();
        picWidth = trueAttribute.getWidth();
        uniqueAttrs = map;
        parsedData = data;
    }


    public List<DataRow> getParsedData() {
        return parsedData;
    }

    public Map<Color, Integer> getUniqueAttrs() {
        return uniqueAttrs;
    }

    /**
     * Creates table of data attributes and features
     *
     * @throws IOException when something bad happens with images
     */
    public void createTrainTable() throws IOException {
        Set<DataRow> dataset = new HashSet<>();
        for (int i = 0; i < trueAttribute.getWidth() - 10; i += 10) {
            for (int j = 0; j < trueAttribute.getHeight() - 20; j += 10) {
                List<Integer> currentFeat = new ArrayList<>();
                for (BufferedImage feature : features) {
                    currentFeat.add(new Color(feature.getRGB(i, j)).getRed());
                }
                Color attrColor = new Color(trueAttribute.getRGB(i, j));
                dataset.add(new DataRow(currentFeat, attrColor, getAllNeighbors(i, j)));
                Integer value = 1;
                if (uniqueAttrs.containsKey(attrColor)) {
                    value = uniqueAttrs.get(attrColor);
                    uniqueAttrs.replace(attrColor, value, value + 1);
                } else {
                    uniqueAttrs.put(attrColor, value);
                }
            }
        }
        parsedData = dataset.stream().toList();
    }

    private List<NeighborRow> getAllNeighbors(int i, int j) {
        List<NeighborRow> neighbors = new ArrayList<>();
        if (i != 0) {
            neighbors.add(initNeighbor(i - 1, j));
        }
        if (i != trueAttribute.getWidth()) {
            neighbors.add(initNeighbor(i + 1, j));
        }
        if (j != 0) {
            neighbors.add(initNeighbor(i, j - 1));
        }
        if (j != trueAttribute.getHeight()) {
            neighbors.add(initNeighbor(i, j + 1));
        }
        return neighbors;
    }

    private NeighborRow initNeighbor(int i, int j) {
        List<Integer> currentFeat = new ArrayList<>();
        for (BufferedImage feature : features) {
            currentFeat.add(new Color(feature.getRGB(i, j)).getRed());
        }
        Color attrColor = new Color(trueAttribute.getRGB(i, j));
        return new NeighborRow(currentFeat, attrColor);
    }

    public List<List<DataRow>> createTestTable(List<BufferedImage> feats) throws IOException {
        List<List<DataRow>> data = new ArrayList<>();
        for (int i = 0; i < feats.get(0).getWidth(); i++) {
            List<DataRow> currentLine = new ArrayList<>();
            for (int j = 0; j < feats.get(0).getHeight(); j++) {
                List<Integer> currentFeat = new ArrayList<>();
                for (BufferedImage feature : feats) {
                    currentFeat.add(new Color(feature.getRGB(i, j)).getRed());
                }
                Color attrColor = new Color(feats.get(0).getRGB(i, j));
                currentLine.add(new DataRow(currentFeat, attrColor));
            }
            data.add(currentLine);
        }
        return data;
    }

    public String keyElemToString(Color key, Integer value) {
        return key.getRed() +
                ";" +
                key.getGreen() +
                ";" +
                key.getBlue() +
                ";" +
                value;
    }

}
