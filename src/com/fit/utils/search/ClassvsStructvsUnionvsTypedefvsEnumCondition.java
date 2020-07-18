package com.fit.utils.search;

import com.fit.tree.object.*;

public class ClassvsStructvsUnionvsTypedefvsEnumCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof ClassNode || n instanceof StructNode || n instanceof SingleTypedefDeclaration
                || n instanceof UnionNode || n instanceof EnumNode)
            return true;
        else if (n instanceof VariableNode && !(n.getParent() instanceof FunctionNode))
            return true;
        return false;
    }
}
