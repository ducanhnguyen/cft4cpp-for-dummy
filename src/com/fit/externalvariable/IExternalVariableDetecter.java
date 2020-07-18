package com.fit.externalvariable;

import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.IVariableNode;
import com.fit.utils.search.ISearch;

import java.util.List;

/**
 * Find all external variables of a function
 *
 * @author ducanhnguyen
 */
public interface IExternalVariableDetecter extends ISearch {
    /**
     * Find external variables of a function
     *
     * @return
     */
    List<IVariableNode> findExternalVariables();

    IFunctionNode getFunction();

    void setFunction(IFunctionNode function);
}