package io.github.shadowchild.hrsetup;


/**
 * Created by Zach Piddock on 15/01/2016.
 */
public class Pair<V, K> {

    public V left;
    public K right;

    public Pair(V left, K right) {

        this.left = left;
        this.right = right;
    }

    public V getLeft() {

        return left;
    }

    public void setLeft(V left) {

        this.left = left;
    }

    public K getRight() {

        return right;
    }

    public void setRight(K right) {

        this.right = right;
    }
}
