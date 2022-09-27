package com.ml.spatialtree.data;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DataRow {

    private final List<Integer> features;
    private final Color truthAttr;
    private final List<NeighborRow> neighbors;


    public DataRow(List<Integer> feat, Color attr) {
        features = feat;
        truthAttr = attr;
        neighbors = new ArrayList<>();
    }

    public DataRow(List<Integer> feat, Color attr, List<NeighborRow> rows) {
        features = feat;
        truthAttr = attr;
        neighbors = rows;
    }

    public DataRow(String line) {
        features = new ArrayList<>();
        String[] data = line.split(";");
        int index = 0;
        int blue = Integer.parseInt(data[data.length - ++index]);
        int green = Integer.parseInt(data[data.length - ++index]);
        int red = Integer.parseInt(data[data.length - ++index]);
        truthAttr = new Color(red, green, blue);
        for (int i = 0; i < data.length - index; ++i) {
            features.add(Integer.parseInt(data[i]));
        }
        neighbors = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        DataRow object = (DataRow) obj;
        boolean attEqual = equals(object.truthAttr, truthAttr);
        boolean featEqual = true;
        for (int i = 0; i < features.size(); ++i) {
            featEqual &= features.get(i) <= object.getFeature(i) && features.get(i) >= object.getFeature(i);
        }
        return attEqual && featEqual;
    }

    public static boolean equals(Color c1, Color c2) {
        return c1.getRed() == c2.getRed() &&
                c1.getBlue() == c2.getBlue() &&
                c1.getGreen() == c2.getGreen();
    }

    @Override
    public int hashCode() {
        StringBuilder data = new StringBuilder();
        for (Integer feature : features) {
            data.append(feature / 10);
        }
        data.append(truthAttr.toString());
        return Objects.hash(data.toString());
    }

    public Integer getFeature(int index) {
        return features.get(index);
    }

    public Integer getFeaturesSize() {
        return features.size();
    }

    public Color getTruthAttr() {
        return truthAttr;
    }

    public List<NeighborRow> getNeighbors() {
        return neighbors;
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

    public String toStringNeighbors() {
        StringBuilder result = new StringBuilder();
        for (NeighborRow neighbor : neighbors) {
            result.append(neighbor.toString());
            result.append(":");
        }
        return result.toString();
    }

    public void setNeighborsFromString(String line) {
        String[] split = Arrays.stream(line.split(":"))
                .filter(e -> e.trim().length() > 0).toArray(String[]::new);
        for (String neighbor: split) {
            neighbors.add(new NeighborRow(neighbor));
        }
    }

}