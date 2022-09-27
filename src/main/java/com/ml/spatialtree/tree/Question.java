package com.ml.spatialtree.tree;

import com.ml.spatialtree.data.DataRow;
import com.ml.spatialtree.data.NeighborRow;

import java.io.Serializable;

/**
 * A class is used to partition dataset
 */
public class Question implements Serializable {

    // The column index of the feature
    private final int column;

    // Condition of the question (for features division)
    private final int condition;

    public Question(int columnInd, int cond) {
        column = columnInd;
        condition = cond;
    }

    /**
     * Method should show if current row matches the question
     * @param value specific row of dataset
     * @return true if row matches the question, otherwise false
     */
    public boolean match(DataRow value) {
        return value.getFeature(column) >= condition;
    }

    public boolean match(NeighborRow value) {
        return value.getFeature(column) >= condition;
    }
}
