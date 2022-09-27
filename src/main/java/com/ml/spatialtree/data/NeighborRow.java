package com.ml.spatialtree.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NeighborRow {

    private final List<Integer> features;
    private final Color truthAttr;

    public NeighborRow(List<Integer> features, Color truthAttr) {
        this.features = features;
        this.truthAttr = truthAttr;
    }

    public NeighborRow(String line) {
        features = new ArrayList<>();
        String[] data = line.split(";");
        int index = 0;
        int blue = Integer.parseInt(data[data.length - ++index]);
        int green = Integer.parseInt(data[data.length - ++index]);
        int red = Integer.parseInt(data[data.length - ++index]);
        truthAttr = new Color(red, green, blue);
        for (int i = 0; i < data.length - index + 1; ++i) {
            features.add(Integer.parseInt(data[i]));
        }
    }

    public Color getTruthAttr() { return truthAttr; }

    public Integer getFeature(int index) {
        return features.get(index);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Integer feature : features) {
            result.append(feature);
            result.append(";");
        }
        result.append(truthAttr.getRed());
        result.append(";");
        result.append(truthAttr.getGreen());
        result.append(";");
        result.append(truthAttr.getBlue());
        return result.toString();
    }
}
