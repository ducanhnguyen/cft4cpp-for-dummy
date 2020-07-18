package com.fit.testdatagen.coverage;

import com.fit.cfg.CFGGenerationforBranchvsStatementCoverage;
import com.fit.cfg.ICFG;
import com.fit.cfg.ICFGGeneration;
import com.fit.cfg.object.*;
import com.fit.cfg.testpath.ITestpath;
import com.fit.config.Paths;
import com.fit.instrument.FunctionInstrumentation;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.tree.object.IFunctionNode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;

import java.io.File;

/**
 * Update visited statement in CFG
 *
 * @author ducanhnguyen
 */
@Deprecated
public class CFGUpdater implements ICFGUpdater {

    private String[] testpath;

    private ICFG cfg;

    public CFGUpdater(String testpath, ICFG cfg) {
        this.testpath = testpath.split(ITestpath.SEPARATE_BETWEEN_NODES);
        this.cfg = cfg;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));

        IFunctionNode functionNode = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "check_anagram(char[],char[])").get(0);

        FunctionNormalizer fnNorm = functionNode.normalizedASTtoInstrument();
        String normalizedCoverage = fnNorm.getNormalizedSourcecode();
        IFunctionNode clone = (IFunctionNode) functionNode.clone();
        clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

        CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);
        ICFG cfg = cfgGen.generateCFG();
        cfg.resetVisitedState();
        cfg.setIdforAllNodes();
        //
        String tp = "int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>c = 0;=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;";
        String tp2 = "int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>{=>first[a[c]-'a']++;=>c++;=>}=>a[c] != 0=>c = 0;=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;";

        new CFGUpdater(tp, cfg).updateVisitedNodes();
        new CFGUpdater(tp2, cfg).updateVisitedNodes();
        System.out.print(cfg.getUnvisitedStatements());
    }

    @Override
    public void updateVisitedNodes() {
        int iterTestpath = 0;
        ICfgNode currentCfgNode = cfg.getBeginNode();

        while (currentCfgNode != null && iterTestpath < testpath.length) {
            iterTestpath = getNextNormalPosition(testpath, iterTestpath);

            if (iterTestpath == -1)
                break;
            String nextContent = testpath[iterTestpath];

            String nextContentLevel2 = null;
            if (iterTestpath + 1 < testpath.length)
                nextContentLevel2 = testpath[iterTestpath + 1];

            currentCfgNode.setVisit(true);

            ICfgNode next = findNextCfgNode(currentCfgNode, nextContent, nextContentLevel2);

            if (next == null) {
                if (currentCfgNode.getContent().equals(nextContent))
                    /*
					 * Sometimes, a statement is duplicated many times
					 * continuously (e.g., condition of while-do block)
					 */
                    iterTestpath++;
                else {
                    int count = 0;
                    ICfgNode t = null;
                    for (ICfgNode s : cfg.getAllNodes())
                        if (s.getContent().equals(nextContent)) {
                            count++;
                            t = s;
                        }
                    if (count == 1)
                        currentCfgNode = t;
                    else
                        currentCfgNode = null;
                }
            } else {
                iterTestpath++;

                // update the visited branches
                if (currentCfgNode instanceof ConditionCfgNode) {
                    ConditionCfgNode conCfgNode = (ConditionCfgNode) currentCfgNode;
                    if (getNextNode(conCfgNode, true).getId() == next.getId())
                        conCfgNode.setVisitedTrueBranch(true);
                    else
                        conCfgNode.setVisitedFalseBranch(true);
                }

                currentCfgNode = next;
                next.setVisit(true);
            }
        }
    }

    /**
     * Get next node from the current node that is not {, or }
     *
     * @param currentNode
     * @return
     */
    private ICfgNode getNextNode(ICfgNode currentNode, boolean kindBranch) {
        currentNode = kindBranch == true ? currentNode.getTrueNode() : currentNode.getFalseNode();
        while (currentNode != null && currentNode instanceof ScopeCfgNode && currentNode.getTrueNode() != null
                && currentNode.getFalseNode() != null) {
            currentNode = currentNode.getFalseNode();
        }
        return currentNode;
    }

    /**
     * Get next normal statement from a specific location
     *
     * @param testpath
     * @param currentPosition
     * @return
     */
    private int getNextNormalPosition(String[] testpath, int currentPosition) {
        final int UNDEFINED = -1;
        int nextPosition = currentPosition;
        while (nextPosition < testpath.length && (testpath[nextPosition].equals(BeginFlagCfgNode.BEGIN_FLAG)
                || testpath[nextPosition].equals(EndFlagCfgNode.END_FLAG)
                || testpath[nextPosition].equals(ScopeCfgNode.SCOPE_CLOSE)
                || testpath[nextPosition].equals(ScopeCfgNode.SCOPE_OPEN)))
            nextPosition++;

        if (nextPosition < testpath.length)
            return nextPosition;
        else
            return UNDEFINED;
    }

    /**
     * Find next cfg node based on the content of two next node
     *
     * @param currentCfgNode    Current cfg node
     * @param nextContent       the next content on the given test path
     * @param nextContentLevel2 the next of the next content on the given test path
     * @return
     */
    private ICfgNode findNextCfgNode(ICfgNode currentCfgNode, String nextContent, String nextContentLevel2) {
        ICfgNode next = null;

        if (currentCfgNode.isCondition())
            next = findNextCfgNodefromConditon(currentCfgNode, nextContent, nextContentLevel2);
        else
            next = findNextCfgNodeByContent(currentCfgNode.getTrueNode(), nextContent);
        return next;
    }

    private ICfgNode findNextCfgNodefromConditon(ICfgNode currentCfgNode, String nextContent,
                                                 String nextContentLevel2) {
        ICfgNode next = null;
		/*
		 * Find the next node toward to true branch
		 */
        ICfgNode next1 = findNextCfgNodeByContent(currentCfgNode.getTrueNode(), nextContent);
		/*
		 * Find the next node toward to false branch
		 */
        ICfgNode next2 = findNextCfgNodeByContent(currentCfgNode.getFalseNode(), nextContent);

		/*
		 * The next node is only one!
		 * 
		 * Ex: if (x>0) x--; else x=x+1;
		 */
        if (next1 != null && next2 == null)
            next = next1;

        else if (next1 == null && next2 != null)
            next = next2;

        else if (next1 != null && next2 != null)
            if (nextContentLevel2 != null)
				/*
				 * We found two next nodes are candidates.
				 * 
				 * Ex: if (x>0) x--; else x--;
				 * 
				 * Ex: if (x>0) x = x-1; else x = x-1;
				 */
                if (next1.getTrueNode().getContent().equals(nextContentLevel2)
                        || next1.getFalseNode().getContent().equals(nextContentLevel2))
                    next = next1;
                else
                    next = next2;
        return next;
    }

    /**
     * Starting find vertex by its content from a specified vertex
     *
     * @param currentCfgNode    the vertex is not a condition
     * @param contentNextVertex
     * @return
     */
    private ICfgNode findNextCfgNodeByContent(ICfgNode currentCfgNode, String contentNextVertex) {
        while (currentCfgNode != null && currentCfgNode.isSpecialCfgNode())
            currentCfgNode = currentCfgNode.getTrueNode();

		/*
		 * The content in test path is normalized in FunctionInstrumentation
		 * class.
		 */
        if (currentCfgNode != null) {
            String noBreakLineContent = FunctionInstrumentation.replaceBreakLine(currentCfgNode.getContent());

            if (noBreakLineContent.equals(contentNextVertex))
                return currentCfgNode;

            else
                return null;
        } else
            return null;

    }

    @Override
    public String[] getTestpath() {
        return testpath;
    }

    @Override
    public void setTestpath(String[] testpath) {
        this.testpath = testpath;
    }

    @Override
    public ICFG getCfg() {
        return cfg;
    }

    @Override
    public void setCfg(ICFG cfg) {
        this.cfg = cfg;
    }

}
