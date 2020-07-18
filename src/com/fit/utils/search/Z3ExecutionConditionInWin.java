package com.fit.utils.search;

import com.fit.config.AbstractSetting;
import com.fit.config.ISettingv2;
import com.fit.tree.object.ExeNode;
import com.fit.tree.object.INode;

import java.io.File;

public class Z3ExecutionConditionInWin extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ExeNode
                && n.getAbsolutePath().endsWith(File.separator + AbstractSetting.getValue(ISettingv2.SOLVER_Z3_NAME)))
            return true;
        return false;
    }
}
