package com.basic.core.inter;

import org.apache.hadoop.io.WritableComparator;

/**
 * locate com.basic.storm.inter
 * Created by tj on 2017/5/9.
 */
public abstract class BinaryComparable implements Comparable<BinaryComparable> {
    public BinaryComparable() {
    }

    public abstract int getLength();

    public abstract byte[] getBytes();

    public int compareTo(BinaryComparable other) {
        return this == other?0: WritableComparator.compareBytes(this.getBytes(), 0, this.getLength(), other.getBytes(), 0, other.getLength());
    }

    public int compareTo(byte[] other, int off, int len) {
        return WritableComparator.compareBytes(this.getBytes(), 0, this.getLength(), other, off, len);
    }

    public boolean equals(Object other) {
        if(!(other instanceof BinaryComparable)) {
            return false;
        } else {
            BinaryComparable that = (BinaryComparable)other;
            return this.getLength() != that.getLength()?false:this.compareTo(that) == 0;
        }
    }

    public int hashCode() {
        return WritableComparator.hashBytes(this.getBytes(), this.getLength());
    }
}
