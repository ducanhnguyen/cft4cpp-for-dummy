package com.fit.cfg.testpath;

public interface IPartialTestpath extends ITestpath {
    /**
     * Get the full path of the given test path.
     *
     * @return
     */
    @Override
    String getFullPath();

    boolean getFinalConditionType();

    /**
     * @param finalConditionType true if the final condition is true, false if it is false
     */
    void setFinalConditionType(boolean finalConditionType);
}
