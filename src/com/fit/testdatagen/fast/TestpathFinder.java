package com.fit.testdatagen.fast;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.fit.cfg.CFGGenerationSubCondition;
import com.fit.cfg.ICFG;
import com.fit.cfg.ICFGGeneration;
import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.testpath.ITestpath;
import com.fit.config.Paths;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.testdatagen.coverage.CoverageComputation;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;

/**
 * Find a test path in CFG by its content
 * 
 * @author ducanhnguyen
 *
 */
public class TestpathFinder extends CoverageComputation {

	private Set<Object> visitedStatements = new HashSet<>();

	public TestpathFinder(String[] testpath, ICFG cfg) {
		findTestpath(testpath, cfg);
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Merge2(int[],int[],int[],int,int)")
				.get(0);

		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		//
		CFGGenerationSubCondition cfgGen = new CFGGenerationSubCondition(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);
		//
		String testpath = "{=>int i = 0, j = 0, k = 0 ;=>i < l1=>j < l2=>{=>t1[i] < t2[j]=>{=>t3[k] = t1[i];=>i++;=>}=>k++;=>}=>i < l1=>j < l2=>{=>t1[i] < t2[j]=>{=>t3[k] = t1[i];=>i++;=>}=>k++;=>}=>i < l1=>j < l2=>{=>t1[i] < t2[j]=>{=>t3[k] = t2[j];=>j++;=>}=>k++;=>}=>i < l1=>j < l2";
		TestpathFinder testpathFinder = new TestpathFinder(testpath.split("=>"), cfgGen.generateCFG());
		System.out.println(cfgGen.generateCFG());
		System.out.println("Test path in CFG= " + testpathFinder.getTestpath().getFullPath());
		ITestpath tp = testpathFinder.getTestpath();
		ICfgNode lastNode = tp.cast().get(tp.cast().size() - 1);
		int i = 0;
		i++;
	}

	@Override
	public void findTestpath(String[] tp, ICFG cfg) {
		int iterTestpath = 0;
		ICfgNode currentCfgNode = cfg.getBeginNode();

		while (currentCfgNode != null && iterTestpath < tp.length) {
			testpath.cast().add(currentCfgNode);
			iterTestpath = getNextNormalPosition(tp, iterTestpath);

			if (!currentCfgNode.isSpecialCfgNode())
				visitedStatements.add(currentCfgNode.getId());

			if (iterTestpath == CoverageComputation.UNDEFINED_POSITION)
				break;
			String nextContent = tp[iterTestpath];

			String nextContentLevel2 = null;
			if (iterTestpath + 1 < tp.length)
				nextContentLevel2 = tp[iterTestpath + 1];

			currentCfgNode.setVisit(true);

			ICfgNode next = findNextCfgNode(currentCfgNode, nextContent, nextContentLevel2);

			if (next == null) {
				if (currentCfgNode.getContent().equals(nextContent)) {
					/*
					 * Sometimes, a statement is duplicated many times continuously (e.g., condition
					 * of while-do block)
					 */
					iterTestpath++;

					if (!currentCfgNode.isSpecialCfgNode()) {
						visitedStatements.add(currentCfgNode.getId());
					}

					visitedStatements.size();
				} else {
					// recursive call, exit
					break;
				}
			} else {
				iterTestpath++;

				if (!currentCfgNode.isSpecialCfgNode()) {
					visitedStatements.add(currentCfgNode.getId());
				}

				if (!next.isSpecialCfgNode()) {
					visitedStatements.add(next.getId());
				}

				visitedStatements.size();

				currentCfgNode = next;
				next.setVisit(true);
			}
		}

		if (testpath.cast().get(testpath.cast().size() - 1).getId() != currentCfgNode.getId())
			testpath.cast().add(currentCfgNode);
	}

}
