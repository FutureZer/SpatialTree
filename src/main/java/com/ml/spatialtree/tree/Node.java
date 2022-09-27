package com.ml.spatialtree.tree;

import java.awt.*;
import java.io.Serializable;

public class Node implements Serializable {

    private Color prediction = null;

    private Node trueBranch = null;
    private Node falseBranch = null;
    private Question question = null;

    private boolean isLeaf = false;

    public Node(Question question, Node trueNode, Node falseNode) {
        this.question = question;
        trueBranch = trueNode;
        falseBranch = falseNode;
    }

    public Node(Color prediction) {
        this.prediction = prediction;
        isLeaf = true;
    }

    public Color getPrediction() {
        return prediction;
    }

    public Node getFalseBranch() {
        return falseBranch;
    }

    public Node getTrueBranch() {
        return trueBranch;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
}
