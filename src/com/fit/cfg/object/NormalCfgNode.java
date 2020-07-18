package com.fit.cfg.object;

import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Represent normal statements (not flag statement, scope statement, etc.)
 *
 * @author ducanh
 */
public class NormalCfgNode extends CfgNode {

    private IASTNode ast;

    public NormalCfgNode(IASTNode node) {
        ast = node;
        setContent(ast.getRawSignature());
        setAstLocation(node.getFileLocation());
    }

    public IASTNode getAst() {
        return ast;
    }

    public void setAst(IASTNode ast) {
        this.ast = ast;
        setContent(ast.getRawSignature());
    }

    @Override
    public String toString() {
        return ast.getRawSignature();
    }
}
