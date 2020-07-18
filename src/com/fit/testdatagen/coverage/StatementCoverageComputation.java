package com.fit.testdatagen.coverage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fit.cfg.CFGGenerationforBranchvsStatementCoverage;
import com.fit.cfg.ICFG;
import com.fit.cfg.ICFGGeneration;
import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.testpath.ITestpath;
import com.fit.config.Paths;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;

public class StatementCoverageComputation extends CoverageComputation {
	private ITestpath testpath;

	private Set<Object> visitedStatements = new HashSet<>();

	public StatementCoverageComputation(List<String> testpaths, ICFG cfg) {
		cfg.resetVisitedState();
		cfg.setIdforAllNodes();

		for (String testpath : testpaths) {
			String[] stms = testpath.split(ITestpath.SEPARATE_BETWEEN_NODES);
			try {
				findTestpath(stms, cfg);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("problem in coverge...");
			}
		}
		//
		int cStatements = cfg.computeNumofStatements();
		if (cStatements == 0)
			coverage = CoverageComputation.MAXIMIZE_COVERAGE;
		else
			coverage = (float) (visitedStatements.size() * 1.0 / cStatements);

		// cfg.resetVisitedState();
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));

		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "check_anagram(char[],char[])").get(0);

		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		/**
		 * Táº¡o Ä‘á»“ thá»‹ CFG cho hÃ m
		 */
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);
		/**
		 * Ä�Ã¡nh giÃ¡ Ä‘á»™ phá»§
		 */
		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>c = 0;=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;");
		testpaths.add(
				"int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>{=>first[a[c]-'a']++;=>c++;=>}=>a[c] != 0=>c = 0;=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());

		System.out.print(coverage.getCoverage() + "%");
	}

	@Override
	public void findTestpath(String[] testpath, ICFG cfg) {
		int iterTestpath = 0;
		ICfgNode currentCfgNode = cfg.getBeginNode();

		while (currentCfgNode != null && iterTestpath < testpath.length) {
			iterTestpath = getNextNormalPosition(testpath, iterTestpath);

			if (!currentCfgNode.isSpecialCfgNode())
				visitedStatements.add(currentCfgNode.getId());

			if (iterTestpath == -1)
				break;
			String nextContent = testpath[iterTestpath];

			String nextContentLevel2 = null;
			if (iterTestpath + 1 < testpath.length)
				nextContentLevel2 = testpath[iterTestpath + 1];

			currentCfgNode.setVisit(true);

			ICfgNode next = findNextCfgNode(currentCfgNode, nextContent, nextContentLevel2);

			if (next == null) {
				if (currentCfgNode.getContent().equals(nextContent)) {
					/*
					 * Sometimes, a statement is duplicated many times continuously (e.g., condition
					 * of while-do block)
					 */
					iterTestpath++;

					if (!currentCfgNode.isSpecialCfgNode())
						visitedStatements.add(currentCfgNode.getId());

					visitedStatements.size();
				} else {
					int count = 0;
					ICfgNode t = null;
					for (ICfgNode s : cfg.getAllNodes())
						if (s.getContent().equals(nextContent)) {
							count++;
							t = s;
						}
					if (count == 1) {
						if (!t.isSpecialCfgNode())
							visitedStatements.add(t.getId());
						currentCfgNode = t;
					} else
						currentCfgNode = null;

				}
			} else {
				iterTestpath++;

				if (!currentCfgNode.isSpecialCfgNode())
					visitedStatements.add(currentCfgNode.getId());

				if (!next.isSpecialCfgNode())
					visitedStatements.add(next.getId());

				visitedStatements.size();

				currentCfgNode = next;
				next.setVisit(true);
			}
		}
	}
}
