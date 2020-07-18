package com.fit.testdatagen;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.fit.cfg.ICFG;
import com.fit.cfg.testpath.FullTestpaths;
import com.fit.cfg.testpath.IFullTestpath;
import com.fit.cfg.testpath.IStaticSolutionGeneration;
import com.fit.config.IFunctionConfig;
import com.fit.config.Paths;
import com.fit.exception.GUINotifyException;
import com.fit.gui.testreport.object.ITestedFunctionReport;
import com.fit.gui.testreport.object.ProjectReport;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.normalizer.StaticSolutionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.testdatagen.structuregen.ChangedTokens;
import com.fit.tree.object.FunctionNode;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;
import com.ibm.icu.util.Calendar;

/**
 * This is <b>Mars</b>-test data generation. <br/>
 * <br/>
 * Given a function with its configuration, <br/>
 * for (int loop: loops) do // loops = {1, 0, 2, 3, ...}</li>
 * <ul>
 * <li>Find the possible test path</li>
 * <li>Find may-have possible test path by using <b>k-cut</b></li>
 * <li>Generate test data based on the above may-have possible test path</li>
 * </ul>
 *
 * @author DucAnh
 */
@Deprecated
public class MarsTestdataGeneration extends AbstractTestdataGeneration
		implements ITestdataGeneration, IVenusTestdataGenerationStrategy {
	final static Logger logger = Logger.getLogger(MarsTestdataGeneration.class);

	public static void main(String[] args) throws Exception {
		Utils.initializeEnvironment();
		ProjectParser parser = new ProjectParser(new File(Paths.SYMBOLIC_EXECUTION_TEST));
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "merge(int[],int,int[],int,int[])")
				.get(0);

		ProjectReport.getInstance().addFunction((FunctionNode) function);

		new MarsTestdataGeneration((IFunctionNode) function,
				ProjectReport.getInstance().getSourcecodeFiles().get(0).getTestedFunctionReports().get(0))
						.generateTestdata();
	}

	public MarsTestdataGeneration(IFunctionNode function, ITestedFunctionReport iTestedFunctionReport) {
		super(function, iTestedFunctionReport);
	}

	@Override
	protected void generateTestdata(IFunctionNode originalFunction, ITestedFunctionReport fnReport) throws Exception {
		logger.debug("Test data generation strategy: Mars");
		Date startTime = Calendar.getInstance().getTime();

		// Normalize function to find static solution.
		FunctionNormalizer fnNorm = originalFunction.normalizeFunctionToFindStaticTestcase();
		ChangedTokens changedTokens = fnNorm.getTokens();

		IFunctionNode normalizedFunction = (IFunctionNode) originalFunction.clone();
		normalizedFunction.setAST(fnNorm.getNormalizedAST());
		logger.debug("Normalized function: \n" + normalizedFunction.getAST().getRawSignature());

		// Generate CFG based on the normalized function
		ICFG normalizedCfg = normalizedFunction.generateCFGToFindStaticSolution();

		if (normalizedCfg != null) {
			normalizedCfg.resetVisitedState();
			normalizedCfg.setIdforAllNodes();

			// Generate test data
			if (Utils.containsLoopBlock(normalizedFunction)) {
				analyzeLoopBlock(originalFunction, normalizedFunction, fnReport, normalizedCfg, changedTokens);

			} else if (Utils.containsIfBlock(normalizedFunction)) {
				analyzeIfBlock(originalFunction, normalizedFunction, fnReport, normalizedCfg, changedTokens);

			} else {
				// The function does not have any condition, we only run it one
				// time
				// with random inputs
				logger.debug("ONY ONE ITERATION ");
				logger.debug("|=| Run under a random inputs...");
				String staticSolution = IStaticSolutionGeneration.NO_SOLUTION;
				executeFunction(staticSolution, changedTokens, fnReport, normalizedCfg, originalFunction);
			}

			// Calculate the running time
			Date end = Calendar.getInstance().getTime();
			long runningTime = end.getTime() - startTime.getTime();
			runningTime = runningTime / 1000;

			fnReport.getCoverage().setTime(runningTime);
			fnReport.getCoverage().setNumofSolverCalls(numOfSolverCalls);
			logger.debug("Generate test data done");
		} else
			throw new GUINotifyException("Dotn support for parsing this function");
	}

	/**
	 * Generate test data for function only contains if-else
	 */
	void analyzeIfBlock(IFunctionNode originalFunction, IFunctionNode normalizedFunction,
			ITestedFunctionReport fnReport, ICFG normalizedCfg, ChangedTokens changedTokens) throws Exception {
		logger.debug("Tested function contain if block");
		IFunctionConfig functionConfig = originalFunction.getFunctionConfig();

		// use maxLoop as cutting-value
		final int kCut = 10;
		logger.info("kCut=" + kCut);
		FullTestpaths possibleTestpaths = findPossibleTestpaths(normalizedCfg, kCut);
		logger.debug("Possible test paths size = " + possibleTestpaths.size());

		FullTestpaths mayHaveSolutionTestpaths = findMayHaveTestpaths(possibleTestpaths, kCut / 2);
		logger.debug("May-have solution test paths size = " + mayHaveSolutionTestpaths.size());

		FullTestpaths prioritizedTestpaths = priorityTestpaths(functionConfig, normalizedCfg, mayHaveSolutionTestpaths);
		logger.debug("Prioritized test paths size = " + prioritizedTestpaths.size());

		FullTestpaths uncoveredTestpaths = getUncoveredTestpaths(prioritizedTestpaths, functionConfig, normalizedCfg);
		logger.debug("Uncovered testpaths size = " + uncoveredTestpaths.size());

		generateTestdata(functionConfig, uncoveredTestpaths, normalizedCfg, changedTokens, 0, originalFunction);
	}

	/**
	 * Generate test data for function containing control block
	 */
	void analyzeLoopBlock(IFunctionNode originalFunction, IFunctionNode normalizedFunction,
			ITestedFunctionReport fnReport, ICFG normalizedCfg, ChangedTokens changedTokens) throws Exception {
		logger.debug("Tested function contain loop");
		IFunctionConfig functionConfig = originalFunction.getFunctionConfig();
		int maxLoop = functionConfig.getMaximumInterationsForEachLoop();

		/*
		 * Prioritize the number of loop iteration
		 */
		int[] maxLoops = new int[maxLoop + 10];
		maxLoops[0] = 1;
		maxLoops[1] = 0;
		for (int i = 2; i <= maxLoop; i++)
			maxLoops[i] = i;

		int numExecution = 0;
		for (int i = 0; i <= maxLoop; i++)

			if (fnReport.computeCoverage() < 100f) {

				fnReport.setState(i + 1 + "/" + (maxLoop + 1));
				logger.debug("Current max loop" + maxLoops[i]);

				FullTestpaths possibleTestpaths = findPossibleTestpaths(normalizedCfg, maxLoops[i]);

				int kCut = maxLoops[i] / 2;
				logger.info("kCut=" + kCut);
				FullTestpaths mayHaveSolutionTestpaths = findMayHaveTestpaths(possibleTestpaths, kCut);

				FullTestpaths prioritizedTestpaths = priorityTestpaths(functionConfig, normalizedCfg,
						mayHaveSolutionTestpaths);

				FullTestpaths uncoveredTestpaths = getUncoveredTestpaths(prioritizedTestpaths, functionConfig,
						normalizedCfg);

				generateTestdata(functionConfig, uncoveredTestpaths, normalizedCfg, changedTokens, numExecution,
						originalFunction);
			}
	}

	/**
	 * Find may-have solution test paths
	 *
	 * @param normalizedCfg
	 * @param maxLoop
	 * @return
	 */
	FullTestpaths findPossibleTestpaths(ICFG normalizedCfg, int maxLoop) {
		normalizedCfg.setPossibleTestpaths(null);
		FullTestpaths possibleTestpaths = normalizedCfg.generateAllPossibleTestpaths(maxLoop);
		return possibleTestpaths;
	}

	FullTestpaths findMayHaveTestpaths(FullTestpaths testpaths, int kCut) {
		for (int con = 0; con <= kCut; con++)
			testpaths = testpaths.removeNoSolutionTestpathsAt(con);
		return testpaths;
	}

	/**
	 * Get test paths containing unvisited statements/branches in a specified order
	 * (e.g., increasing order, decreasing order)
	 *
	 * @param functionConfig
	 * @param normalizedCfg
	 * @param testpaths
	 * @return
	 */
	FullTestpaths priorityTestpaths(IFunctionConfig functionConfig, ICFG normalizedCfg, FullTestpaths testpaths) {
		return testpaths.arrangeByNumVisitedStatementsinDecreasingOrder(normalizedCfg);
	}

	FullTestpaths getUncoveredTestpaths(FullTestpaths testpaths, IFunctionConfig functionConfig, ICFG normalizedCfg) {
		switch (functionConfig.getTypeofCoverage()) {
		case IFunctionConfig.BRANCH_COVERAGE:
			testpaths = testpaths.getTestpathsContainingUncoveredBranches(normalizedCfg);
			break;
		case IFunctionConfig.SUBCONDITION:
			testpaths = testpaths.getTestpathsContainingUncoveredBranches(normalizedCfg);
			break;
		case IFunctionConfig.STATEMENT_COVERAGE:
			testpaths = testpaths.getTestpathsContainingUncoveredStatements(normalizedCfg);
			break;
		}
		return testpaths;
	}

	synchronized void generateTestdata(IFunctionConfig functionConfig, FullTestpaths testpaths, ICFG normalizedCfg,
			ChangedTokens changedTokens, int numExecution, IFunctionNode originalFunction) throws Exception {
		FullTestpaths visitedTestpaths = new FullTestpaths();

		while (testpaths.size() > 0 && fnReport.computeCoverage() < 100) {
			/*
			 * Get the highest priority test path
			 */
			IFullTestpath bestTestpath = null;
			do {
				bestTestpath = testpaths.get(0);
				testpaths.remove(0);
			} while (visitedTestpaths.contains(bestTestpath));

			if (bestTestpath != null) {
				visitedTestpaths.add(bestTestpath);
				logger.debug("Test path = " + bestTestpath.toString());
				IStaticSolutionGeneration staticSolutionGen = bestTestpath.generateTestdata();
				String staticSolution = staticSolutionGen.getStaticSolution();

				if (staticSolution.equals(IStaticSolutionGeneration.NO_SOLUTION))
					logger.debug("|=| No solution. Choose another test path...");

				else if (staticSolution.equals(IStaticSolutionGeneration.EVERY_SOLUTION)) {
					logger.debug("|=| Run under a random inputs...");
					executeFunction(IStaticSolutionGeneration.NO_SOLUTION, changedTokens, fnReport, normalizedCfg,
							originalFunction);

				} else {
					logger.debug("|=| Found a solution...");
					logger.debug("iteration at " + numExecution++);
					/*
					 * Normalize static solution before executing
					 */
					StaticSolutionNormalizer staticNorm = new StaticSolutionNormalizer();
					staticNorm.setOriginalSourcecode(staticSolution);
					staticNorm.setTokens(changedTokens);
					staticNorm.normalize();
					staticSolution = staticNorm.getNormalizedSourcecode();
					logger.debug("Normalized static solution=" + staticSolution + "...");

					executeFunction(staticSolution, changedTokens, fnReport, normalizedCfg, originalFunction);
				}
			}
		}
	}
}
