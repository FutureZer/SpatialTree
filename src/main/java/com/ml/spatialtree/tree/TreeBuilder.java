package com.ml.spatialtree.tree;

import com.ml.spatialtree.data.DataRow;
import com.ml.spatialtree.data.NeighborRow;
import com.ml.spatialtree.data.SplitData;
import com.ml.spatialtree.data.TrainDataCreator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TreeBuilder {

    private final int[] featMin;
    private final int[] featMax;

    // How significant our neighborhood split autocorrelation ratio will be in the model
    private static final double ALPHA = 0.3;

    public TreeBuilder(List<DataRow> rows) {
        featMin = new int[rows.get(0).getFeaturesSize()];
        featMax = new int[rows.get(0).getFeaturesSize()];
        Arrays.fill(featMin, 255);
        Arrays.fill(featMax, 0);
        for (int i = 0; i < rows.size(); ++i) {
            for (int j = 0; j < rows.get(i).getFeaturesSize(); ++j) {
                if (featMin[j] > rows.get(i).getFeature(j)) {
                    featMin[j] = rows.get(i).getFeature(j);
                }
                if (featMax[j] < rows.get(i).getFeature(j)) {
                    featMax[j] = rows.get(i).getFeature(j);
                }
            }
        }
    }

    /**
     * Divide dataset by two sets
     *
     * @param question question for dataset division
     * @return Pair of lists (list with rows that matches the question and which is not)
     */
    private SplitData partition(List<DataRow> dataset, Question question) {
        List<DataRow> trueRows = new ArrayList<>();
        Map<Color, Integer> trueClassCount = new HashMap<>();
        List<DataRow> falseRows = new ArrayList<>();
        Map<Color, Integer> falseClassCount = new HashMap<>();
        double nsar = 0;
        for (DataRow row : dataset) {
            nsar += currentNsar(row, question);
            Color key = row.getTruthAttr();
            Integer value = 1;
            if (question.match(row)) {
                trueRows.add(row);
                if (trueClassCount.containsKey(key)) {
                    value = trueClassCount.get(key);
                    trueClassCount.replace(key, value, value + 1);
                } else {
                    trueClassCount.put(key, value);
                }
            } else {
                falseRows.add(row);
                if (falseClassCount.containsKey(key)) {
                    value = falseClassCount.get(key);
                    falseClassCount.replace(key, value, value + 1);
                } else {
                    falseClassCount.put(key, value);
                }
            }
        }
        nsar /= dataset.size();
        return new SplitData(question, trueRows, falseRows, trueClassCount, falseClassCount, nsar);
    }

    private double currentNsar(DataRow row, Question question) {
        double startGamma = 0;
        double divisionGamma = 0;
        for (NeighborRow neighbor : row.getNeighbors()) {
            if (DataRow.equals(row.getTruthAttr(), neighbor.getTruthAttr())) {
                startGamma++;
                if (question.match(row) == question.match(neighbor)) {
                    divisionGamma++;
                }
            }
        }
        startGamma += startGamma == 0 ? 1 : 0;
        return divisionGamma / startGamma;
    }

    /**
     * Calculates gini impurity of given dataset
     *
     * @param rows   dataset of values
     * @param unique all different attributes of this dataset with their count
     * @return probability that random classification will match random object
     */
    private double gini(List<DataRow> rows, Map<Color, Integer> unique) {
        double impurity = 1;
        double size = rows.size();
        for (Color key : unique.keySet()) {
            double probOfCurr = unique.get(key) / size;
            impurity -= probOfCurr;
        }
        return impurity;
    }

    /**
     * Calculates information gain of given split
     *
     * @param children          current split
     * @param parentUncertainty uncertainty of parent node
     * @return information gain
     */
    private double infoGain(SplitData children, double parentUncertainty) {
        List<DataRow> trueChild = children.getTrueRows();
        List<DataRow> falseChild = children.getFalseRows();
        double prob = (double) (trueChild.size()) / (trueChild.size() + falseChild.size());
        return parentUncertainty - prob * gini(trueChild, children.getTrueUnique()) -
                (1 - prob) * gini(falseChild, children.getFalseUnique());
    }

    private SplitData bestSplit(List<DataRow> rows, Map<Color, Integer> unique) {
        SplitData bestSplit = null;
        double uncertainty = gini(rows, unique);
        int featuresSize = rows.get(0).getFeaturesSize();

        for (int i = 0; i < featuresSize; ++i) {
            for (int j = featMin[i]; j <= featMax[i]; j += 2) {
                Question currentQuestion = new Question(i, j);
                SplitData currentSplit = partition(rows, currentQuestion);
                List<DataRow> trueSet = currentSplit.getTrueRows();
                List<DataRow> falseSet = currentSplit.getFalseRows();
                if (trueSet.size() == 0 || falseSet.size() == 0) {
                    continue;
                }
                double spatialGain = (1 - ALPHA) * infoGain(currentSplit, uncertainty) +
                        ALPHA * currentSplit.getNsar();
                currentSplit.setInfoGain(spatialGain);
                if (bestSplit == null || spatialGain >= bestSplit.getInfoGain()) {
                    bestSplit = currentSplit;
                }
            }
        }

        return bestSplit;
    }

    public Node buildTree(List<DataRow> rows, Map<Color, Integer> uniqueAttrs) {
        SplitData split = bestSplit(rows, uniqueAttrs);
        if (split == null || uniqueAttrs.keySet().size() == 1) {
            Color prediction = Color.WHITE;
            Integer val = 0;
            for (Color key : uniqueAttrs.keySet()) {
                if (uniqueAttrs.get(key) > val) {
                    prediction = key;
                }
            }
            return new Node(prediction);
        }

        Node trueNode = buildTree(split.getTrueRows(), split.getTrueUnique());
        Node falseNode = buildTree(split.getFalseRows(), split.getFalseUnique());

        return new Node(split.getQuestion(), trueNode, falseNode);
    }
}
