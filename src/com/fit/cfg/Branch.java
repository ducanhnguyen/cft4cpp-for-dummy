package com.fit.cfg;

import com.fit.cfg.object.ICfgNode;

/**
 * Represent a branch in CFG
 *
 * @author ducanhnguyen
 */
public class Branch {
    private ICfgNode start;

    private ICfgNode end;

    public Branch(ICfgNode start, ICfgNode end) {
        this.start = start;
        this.end = end;
    }

    public ICfgNode getStart() {
        return start;
    }

    public void setStart(ICfgNode start) {
        this.start = start;
    }

    public ICfgNode getEnd() {
        return end;
    }

    public void setEnd(ICfgNode end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Branch) {
            Branch b = (Branch) obj;
            if (b.getStart().equals(getStart()) && b.getEnd().equals(getEnd()))
                return true;
            else
                return false;
        } else
            return false;
    }

    @Override
    public String toString() {
        if (end != null)
            return "(" + start.getContent() + ", " + end.getContent() + ")";
        else
            return "(" + start.getContent() + ", [end CFG])";
    }
}
