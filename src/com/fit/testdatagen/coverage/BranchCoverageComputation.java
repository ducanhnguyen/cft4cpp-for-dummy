package com.fit.testdatagen.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fit.cfg.CFGGenerationSubCondition;
import com.fit.cfg.ICFG;
import com.fit.cfg.ICFGGeneration;
import com.fit.cfg.object.ConditionCfgNode;
import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.object.ScopeCfgNode;
import com.fit.cfg.testpath.ITestpath;
import com.fit.config.Paths;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;

public class BranchCoverageComputation extends CoverageComputation {
	// Store visited branches
	private Set<String> visitedBranches = new HashSet<>();

	public BranchCoverageComputation(List<String> testpaths, ICFG cfg) {
		if (testpaths.size() > 0) {
			cfg.resetVisitedState();
			cfg.setIdforAllNodes();

			for (String item : testpaths) {
				String[] testpath = item.split(ITestpath.SEPARATE_BETWEEN_NODES);
				try {
					findTestpath(testpath, cfg);
				} catch (Exception e) {
					System.out.print("problem in computing coverge...");
					e.printStackTrace();
				}
			}
			//
			int cAllBranches = cfg.computeNumOfBranches();
			if (cAllBranches == 0)
				coverage = CoverageComputation.MAXIMIZE_COVERAGE;
			else
				coverage = (float) (visitedBranches.size() * 1.0 / cAllBranches);

			// cfg.resetVisitedState();
		} else {
			coverage = 0.0f;
			// cfg.resetVisitedState();
		}
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_4));

		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "bmi(float,float)")
				.get(0);

		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationSubCondition cfgGen = new CFGGenerationSubCondition(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);
		ICFG cfg = cfgGen.generateCFG();

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>double c;=>c = (b_w / (he * he / 10000));=>{=>c < 19=>{=>c >= 19 && c < 25=>{=>c >= 25 && c < 30=>{=>c >= 30=>{=>return 3;");
		CoverageComputation coverage = new BranchCoverageComputation(testpaths, cfg);
		System.out.print(coverage.getCoverage() + "%");
		System.out.println(cfg.getUnvisitedBranches());
	}

	@Override
	public void findTestpath(String[] testpath, ICFG cfg) {
		int counter = 0;
		ICfgNode currentCfgNode = cfg.getBeginNode();

		while (currentCfgNode != null && counter < testpath.length) {
			int lastCounter = counter;
			counter = getNextNormalPosition(testpath, counter);

			if (counter == -999 || counter == StatementCoverageComputation.UNDEFINED_POSITION)
				if (currentCfgNode.isCondition()) {
					visitedBranches.add(currentCfgNode.getId() + ",End-Cfg-Node");

					if (currentCfgNode.getTrueNode().getContent().equals(testpath[lastCounter]))
						((ConditionCfgNode) currentCfgNode).setVisitedTrueBranch(true);
					else if (currentCfgNode.getFalseNode().getContent().equals(testpath[lastCounter]))
						((ConditionCfgNode) currentCfgNode).setVisitedFalseBranch(true);
				} else
					break;

			if (counter < testpath.length && counter >= 0) {
				String nextContent = testpath[counter];

				String nextContentLevel2 = null;
				if (counter + 1 < testpath.length)
					nextContentLevel2 = testpath[counter + 1];

				currentCfgNode.setVisit(true);

				ICfgNode next = findNextCfgNode(currentCfgNode, nextContent, nextContentLevel2);

				if (next == null)
					if (currentCfgNode.getContent().equals(nextContent)) {
						/*
						 * Sometimes, a statement is duplicated many times continuously (e.g., condition
						 * of while-do block)
						 */
						counter++;

						if (currentCfgNode.isCondition()) {
							visitedBranches.add(currentCfgNode.getId() + ",End-Cfg-Node");

							if (currentCfgNode.getTrueNode().getContent().equals(testpath[lastCounter]))
								((ConditionCfgNode) currentCfgNode).setVisitedTrueBranch(true);
							else if (currentCfgNode.getFalseNode().getContent().equals(testpath[lastCounter]))
								((ConditionCfgNode) currentCfgNode).setVisitedFalseBranch(true);
						}
					} else
						currentCfgNode = null;
				else {
					counter++;

					if (currentCfgNode.isCondition())
						if (currentCfgNode.getTrueNode().getId() == next.getId()
								|| (currentCfgNode.getTrueNode() instanceof ScopeCfgNode
										&& currentCfgNode.getTrueNode().getTrueNode().getId() == next.getId())) {
							visitedBranches.add(currentCfgNode.getId() + "T," + next.getId());
							((ConditionCfgNode) currentCfgNode).setVisitedTrueBranch(true);
						} else {
							visitedBranches.add(currentCfgNode.getId() + "F," + next.getId());
							((ConditionCfgNode) currentCfgNode).setVisitedFalseBranch(true);
						}

					currentCfgNode = next;
					next.setVisit(true);
				}
			} else
				break;
		}
	}
}
