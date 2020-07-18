package com.fit.instrument;

import com.fit.config.Paths;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.SpecialCharacter;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;

import java.io.File;

/**
 * Instrument function
 *
 * @author DucAnh
 */
public class FunctionInstrumentation implements IFunctionInstrumentationGeneration {

    private IFunctionNode functionNode;

    private boolean normalizedMode = false;

    public FunctionInstrumentation() {
	}
    public FunctionInstrumentation(IFunctionNode fn) {
        functionNode = fn;
        normalizedMode = false;
    }

    public FunctionInstrumentation(IFunctionNode fn, boolean normalizedMode) {
        this.normalizedMode = normalizedMode;
        functionNode = fn;
    }

    public static void main(String[] args) {
        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4));

        INode function = Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "ch_cap(char)").get(0);

        System.out.println(new FunctionInstrumentation((IFunctionNode) function, true).generateInstrumentedFunction());
    }

    /**
     * The statement that is splitted in multiple line is convert into a
     * singline line, e.g.,
     * <p>
     * <p>
     * <pre>
     * int a =
     *
     * 		0;
     * </pre>
     * <p>
     * ----------------> "int a = 0"
     *
     * @param str
     * @return
     */
    public static String replaceBreakLine(String str) {
        return str.replaceAll("(\\n)|(\\r\\n)", " ");
    }

    @Override
    public String generateInstrumentedFunction() {
        return instrument(functionNode);
    }

    protected String getShortenContent(IASTNode node) {
        if (node != null) {
            if (node.getRawSignature().endsWith(SpecialCharacter.END_OF_STATEMENT)) {

            } else
                /*
				 * Ex: "( x ==1   )"------> "x=1". We normalize condition
				 */
                node = Utils.shortenAstNode(node);

            return node.getRawSignature();
        } else
            return "";
    }

    protected String esc(String str) {
        str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
        str = FunctionInstrumentation.replaceBreakLine(str);
        return str;
    }

    protected String addExtraCall(IASTStatement stm, String extra, String margin) {
        if (extra != null)
            extra = mark(extra, true, false);

        if (stm instanceof IASTCompoundStatement)
            return parseBlock((IASTCompoundStatement) stm, extra, margin);
        else {
            StringBuilder b = new StringBuilder();
            String inside = margin + SpecialCharacter.TAB;
            b.append(SpecialCharacter.OPEN_BRACE).append("mark(\"{\");").append(SpecialCharacter.LINE_BREAK)
                    .append(inside).append(inside).append(parseStatement(stm, inside))
                    .append(SpecialCharacter.LINE_BREAK).append(margin).append("mark(\"}\");")
                    .append(SpecialCharacter.CLOSE_BRACE);
            return b.toString();
        }
    }

    protected String instrument(IFunctionNode fn) {
        StringBuilder b = new StringBuilder();
        IASTFunctionDefinition fnDef = null;

        if (!normalizedMode)
            fnDef = fn.getAST();
        else
            try {
                fnDef = fn.normalizedASTtoInstrument().getNormalizedAST();
            } catch (Exception e) {
                e.printStackTrace();
                fnDef = fn.getAST();
            }

        b.append(getShortenContent(fnDef.getDeclSpecifier())).append(SpecialCharacter.SPACE).append(getShortenContent(fnDef.getDeclarator()))
                .append(parseBlock((IASTCompoundStatement) fnDef.getBody(), null, ""));

        return b.toString();
    }

    protected String mark(String arg, boolean end, boolean count) {
        String b = "mark(\"" + arg + "\")";

        if (end)
            b += ';';

        return b;
    }

    /**
     * XÃ¢y dá»±ng mÃ£ nguá»“n cho khá»‘i cÃ¢u lá»‡nh
     *
     * @param block  Ä‘á»‰nh ast á»©ng vá»›i khá»‘i
     * @param extra  mÃ£ nguá»“n cáº§n chÃ¨n ngay sau dáº¥u má»Ÿ ngoáº·c, cÃ³ thá»ƒ null
     * @param margin cÃ¡ch lá»�
     */
    protected String parseBlock(IASTCompoundStatement block, String extra, String margin) {
        StringBuilder b = new StringBuilder();
        String inside = margin + SpecialCharacter.TAB;

        b.append(SpecialCharacter.OPEN_BRACE).append("mark(\"{\");").append(SpecialCharacter.LINE_BREAK);
        if (extra != null)
            b.append(inside);

        for (IASTStatement stm : block.getStatements())
            b.append(inside).append(parseStatement(stm, inside)).append(SpecialCharacter.LINE_BREAK)
                    .append(SpecialCharacter.LINE_BREAK);

        b.append(margin).append("mark(\"}\");").append(SpecialCharacter.CLOSE_BRACE);
        return b.toString();
    }

    protected String parseStatement(IASTStatement stm, String margin) {
        StringBuilder b = new StringBuilder();

        if (stm instanceof IASTCompoundStatement)
            b.append(parseBlock((IASTCompoundStatement) stm, null, margin));
        else if (stm instanceof IASTIfStatement) {
            IASTIfStatement astIf = (IASTIfStatement) stm;
            IASTStatement astElse = astIf.getElseClause();
            String cond = getShortenContent(astIf.getConditionExpression());
            b.append("if (mark(\"").append(SpecialCharacter.MARK).append(esc(cond)).append("\") && (").append(cond)
                    .append(")) ");

            b.append(addExtraCall(astIf.getThenClause(), "", margin));

            if (astElse != null) {
                b.append(SpecialCharacter.LINE_BREAK).append(margin).append("else ");
                b.append(addExtraCall(astElse, "", margin));
            }

        } else if (stm instanceof IASTForStatement) {
            IASTForStatement astFor = (IASTForStatement) stm;
            IASTStatement astInit = astFor.getInitializerStatement();
            IASTExpression astCond = (IASTExpression) Utils.shortenAstNode(astFor.getConditionExpression());
            IASTExpression astIter = astFor.getIterationExpression();

            if (!(astInit instanceof IASTNullStatement))
                b.append(mark(esc(getShortenContent(astInit)), true, true)).append(SpecialCharacter.LINE_BREAK).append(margin);
            b.append("for (").append(getShortenContent(astInit));

            String cond = getShortenContent(astCond);
            if (astCond != null)
                b.append(" mark(\"").append(SpecialCharacter.MARK).append(SpecialCharacter.MARK).append(esc(cond))
                        .append("\") && (").append(cond).append(")");
            b.append("; ");

            if (astIter != null)
                b.append(mark(esc(getShortenContent(astIter)), false, true)).append(',');
            b.append(getShortenContent(astIter)).append(") ");

            // VÃ²ng For khÃ´ng cÃ³ Ä‘iá»�u kiá»‡n, khÃ´ng cÃ³ nhÃ¡nh táº¡i Ä‘Ã¢y
            if (astCond == null)
                b.append(parseStatement(astFor.getBody(), margin));
            else
                b.append(addExtraCall(astFor.getBody(), "", margin));

        } else if (stm instanceof IASTWhileStatement) {
            IASTWhileStatement astWhile = (IASTWhileStatement) stm;
            String cond = getShortenContent(astWhile.getCondition());

            b.append("while (mark(\"").append(SpecialCharacter.MARK).append(SpecialCharacter.MARK).append(esc(cond))
                    .append("\") && (").append(cond).append(")) ");

            b.append(addExtraCall(astWhile.getBody(), "", margin));

        } else if (stm instanceof IASTDoStatement) {
            IASTDoStatement astDo = (IASTDoStatement) stm;
            String cond = getShortenContent(astDo.getCondition());

            b.append("do ").append(addExtraCall(astDo.getBody(), "", margin)).append(SpecialCharacter.LINE_BREAK)
                    .append(margin).append("while (mark(\"").append(SpecialCharacter.MARK).append(SpecialCharacter.MARK)
                    .append(esc(cond)).append("\") && (").append(cond).append("));");

        } else if (stm instanceof ICPPASTTryBlockStatement) {
            ICPPASTTryBlockStatement astTry = (ICPPASTTryBlockStatement) stm;

            String extra = "start try";
            b.append(mark(extra, true, true));

            b.append(SpecialCharacter.LINE_BREAK).append(margin).append("try ");
            b.append(addExtraCall(astTry.getTryBody(), null, margin));

            for (ICPPASTCatchHandler catcher : astTry.getCatchHandlers()) {
                b.append(SpecialCharacter.LINE_BREAK).append(margin).append("catch (");

                String exception = catcher.isCatchAll() ? "..." : getShortenContent(catcher.getDeclaration());
                b.append(exception).append(") ");

                extra = SpecialCharacter.MARK + exception + SpecialCharacter.MARK;
                b.append(addExtraCall(catcher.getCatchBody(), extra, margin));
            }

            extra = "end catch";
            b.append(SpecialCharacter.LINE_BREAK).append(margin).append(mark(extra, true, true));

        } else if (stm instanceof IASTBreakStatement || stm instanceof IASTContinueStatement)
			/*
			 * Ä�á»‘i vá»›i cÃ¢u lá»‡nh continue, break: khÃ´ng cáº§n Ä‘Ã¡nh dáº¥u
			 */
            b.append(getShortenContent(stm));
        else {
            String raw = getShortenContent(stm);
            b.append(mark(esc(raw), true, true)).append(SpecialCharacter.SPACE).append(raw);
        }

        return b.toString();
    }

    @Override
    public IFunctionNode getFunctionNode() {
        return functionNode;
    }

    @Override
    public void setFunctionNode(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    @Override
    public boolean isNormalizedMode() {
        return normalizedMode;
    }

    @Override
    public void setNormalizedMode(boolean normalizedMode) {
        this.normalizedMode = normalizedMode;
    }

}
