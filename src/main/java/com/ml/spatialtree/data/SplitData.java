package com.ml.spatialtree.data;

import com.ml.spatialtree.tree.Question;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class SplitData {

    private final List<DataRow> trueRows;
    private final List<DataRow> falseRows;

    private final Map<Color, Integer> trueUnique;

    private final Map<Color, Integer> falseUnique;

    // Question which is divide dataset
    private final Question question;

    // Information gain of current split and neighborhood split autocorrelation ratio
    private double infoGain = 0;
    private double nsar = 0;

    public SplitData(Question question, List<DataRow> trueRows, List<DataRow> falseRows,
              Map<Color, Integer> trueUniqueColors, Map<Color, Integer> falseUniqueColors, double nsar) {
        this.question = question;
        this.trueRows = trueRows;
        this.falseRows = falseRows;
        trueUnique = trueUniqueColors;
        falseUnique = falseUniqueColors;
        this.nsar = nsar;
    }

    public void setInfoGain(double infoGain) {
        this.infoGain = infoGain;
    }


    public double getInfoGain() {
        return infoGain;
    }

    public double getNsar() { return nsar; }

    public Question getQuestion() {
        return question;
    }

    public List<DataRow> getTrueRows() {
        return trueRows;
    }

    public List<DataRow> getFalseRows() {
        return falseRows;
    }

    public Map<Color, Integer> getTrueUnique() {
        return trueUnique;
    }

    public Map<Color, Integer> getFalseUnique() {
        return falseUnique;
    }

}
