package com.fit.testdatagen.se.normalstatementparser;

import com.fit.testdatagen.se.ExpressionRewriterUtils;
import com.fit.testdatagen.se.memory.*;
import com.fit.utils.Utils;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;

/**
 * Parse pointer item assigned to expression <br/>
 * Ex1: *p1 = *p1 - 1<br/>
 * Ex2: *(p1+1) = *p2 + 1
 *
 * @author ducanhnguyen
 */
public class PointerItemToExpressionParser extends NormalBinaryAssignmentParser {

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);

        if (ast instanceof ICPPASTBinaryExpression) {
            IASTNode astLeft = ((ICPPASTBinaryExpression) ast).getOperand1();
            IASTNode astRight = ((ICPPASTBinaryExpression) ast).getOperand2();
            String reducedRightExpression = ExpressionRewriterUtils.rewrite(table, astRight.getRawSignature());

            String reducedLeftExpression = ExpressionRewriterUtils
                    .convertTwoLevelPointerItemToArrayItem(astLeft.getRawSignature());
            reducedLeftExpression = ExpressionRewriterUtils
                    .convertOneLevelPointerItemToArrayItem(reducedLeftExpression);

            PhysicalCell c = table.findPhysicalCellByName(reducedLeftExpression);
            if (c != null)
                /*
				 * Update value for cell
				 */
                c.setValue(reducedRightExpression);
            else {
                ISymbolicVariable ref = table.findorCreateVariableByName(reducedLeftExpression);

                LogicBlock block = null;
                if (ref instanceof OneLevelSymbolicVariable) {
                    OneLevelSymbolicVariable var = (OneLevelSymbolicVariable) ref;
                    block = var.getReference().getBlock();

                } else if (ref instanceof TwoLevelSymbolicVariable) {
                    TwoLevelSymbolicVariable var = (TwoLevelSymbolicVariable) ref;
                    block = var.getReference().getBlock();
                }

                if (block != null) {
					/*
					 * Get the reduce index of array item
					 */
                    String index = Utils.getReducedIndex(reducedLeftExpression, table);

                    PhysicalCell newCell = new PhysicalCell(reducedRightExpression);
                    block.addLogicalCell(newCell, index);
                }
            }
        }
    }

}
