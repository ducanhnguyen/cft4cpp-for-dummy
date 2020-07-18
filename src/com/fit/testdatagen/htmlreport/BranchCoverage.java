package com.fit.testdatagen.htmlreport;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fit.cfg.CFGGenerationforBranchvsStatementCoverage;
import com.fit.cfg.ICFG;
import com.fit.cfg.ICFGGeneration;
import com.fit.gui.testreport.object.ITestpathReport;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.testdatagen.coverage.BranchCoverageComputation;
import com.fit.tree.object.IFunctionNode;
import com.fit.utils.Utils;

/**
 * @author DucAnh
 */
public class BranchCoverage extends Coverage {
	final static Logger logger = Logger.getLogger(BranchCoverage.class);

	public BranchCoverage(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	public BranchCoverage(float coverage, List<ITestpathReport> testpaths, IFunctionNode functionNode) {
		super(coverage, testpaths, functionNode);
	}

	public BranchCoverage(float coverage, List<ITestpathReport> testpaths, IFunctionNode functionNode, long time,
			int numStatic) {
		super(coverage, testpaths, functionNode, time, numStatic);
	}

	@Override
	public float computeCoverage() throws Exception {
		float coverageNow = 0;
		/**
		 * Create testpath in string
		 */
		logger.debug("\n");
		logger.debug("Computing branch coverage reaching by " + getTestpaths().size() + " test paths");
		List<String> tpsInStr = new ArrayList<>();
		List<ITestpathReport> tpReportClone = new ArrayList<>(getTestpaths());
		for (ITestpathReport testpathReport : tpReportClone)
			tpsInStr.add(testpathReport.getExecutionTestpath());

		FunctionNormalizer fnNorm = functionNode.normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) functionNode.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		ICFG cfg = cfgGen.generateCFG();
		BranchCoverageComputation branchCoverageComputer = new BranchCoverageComputation(tpsInStr, cfg);
		coverageNow = branchCoverageComputer.getCoverage() * 100;

		logger.debug("Done. 1.Branch coverage = " + coverageNow);
		logger.debug("2.Unvisited branches (size=" + cfg.getUnvisitedBranches().size() + "): "
				+ cfg.getUnvisitedBranches().toString());
		logger.debug("3.Unvisited statements (size=" + cfg.getUnvisitedStatements().size() + "): "
				+ cfg.getUnvisitedStatements().toString() + "\n");

		return coverageNow;
	}
}
