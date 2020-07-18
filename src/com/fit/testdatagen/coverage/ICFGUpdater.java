package com.fit.testdatagen.coverage;

import com.fit.cfg.ICFG;

/**
 * Update visited statement in CFG
 *
 * @author ducanhnguyen
 */
public interface ICFGUpdater {
    /**
     * Update visited nodes in CFG
     */
    void updateVisitedNodes();

    String[] getTestpath();

    void setTestpath(String[] testpath);

    ICFG getCfg();

    void setCfg(ICFG cfg);
}