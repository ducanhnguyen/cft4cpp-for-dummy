package com.fit.testdatagen;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.fit.cfg.CFGGenerationSubCondition;
import com.fit.cfg.ICFG;
import com.fit.cfg.object.EndFlagCfgNode;
import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.testpath.IStaticSolutionGeneration;
import com.fit.cfg.testpath.ITestpath;
import com.fit.cfg.testpath.StaticSolutionGeneration;
import com.fit.config.Bound;
import com.fit.config.FunctionConfig;
import com.fit.config.IFunctionConfig;
import com.fit.config.ISettingv2;
import com.fit.config.Paths;
import com.fit.exception.GUINotifyException;
import com.fit.gui.testreport.object.ITestedFunctionReport;
import com.fit.gui.testreport.object.ProjectReport;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.testdatagen.fast.TestpathFinder;
import com.fit.testdatagen.fast.random.BasicTypeRandom;
import com.fit.testdatagen.se.IPathConstraints;
import com.fit.testdatagen.se.ISymbolicExecution;
import com.fit.testdatagen.se.Parameter;
import com.fit.testdatagen.se.PathConstraint;
import com.fit.testdatagen.se.PathConstraints;
import com.fit.testdatagen.se.SymbolicExecution;
import com.fit.testdatagen.se.memory.ISymbolicVariable;
import com.fit.testdatagen.structuregen.ChangedTokens;
import com.fit.testdatagen.testdatainit.VariableTypes;
import com.fit.tree.object.FunctionNode;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.tree.object.IVariableNode;
import com.fit.tree.object.StructureNode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;
import com.ibm.icu.util.Calendar;

/**
 * 
 * Use fast function execution (compile the testing project once)
 * 
 * @author DucAnh
 */
public class FastTestdataGeneration extends MarsTestdataGeneration2 {
	final static Logger logger = Logger.getLogger(FastTestdataGeneration.class);

	public static void main(String[] args) throws Exception {
		File clone = Utils.copy(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(clone);
		IFunctionNode function = (IFunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Tritype(int,int,int)").get(0);

		FunctionConfig functionConfig = new FunctionConfig();
		functionConfig.setCharacterBound(new Bound(30, 120));
		functionConfig.setIntegerBound(new Bound(10, 200));
		functionConfig.setSizeOfArray(5);
		functionConfig.setMaximumInterationsForEachLoop(3);
		functionConfig.setSolvingStrategy(ISettingv2.SUPPORT_SOLVING_STRATEGIES[0]);
		function.setFunctionConfig(functionConfig);

		ProjectReport.getInstance().addFunction((FunctionNode) function);

		FastTestdataGeneration gen = new FastTestdataGeneration((IFunctionNode) function,
				ProjectReport.getInstance().getSourcecodeFiles().get(0).getTestedFunctionReports().get(0));
		gen.generateTestdata();
	}

	public FastTestdataGeneration(IFunctionNode function, ITestedFunctionReport iTestedFunctionReport) {
		super(function, iTestedFunctionReport);
	}

	@Override
	protected void generateTestdata(IFunctionNode originalFunction, ITestedFunctionReport fnReport) throws Exception {
		logger.debug("Test data generation strategy: Fast Mars");
		Date startTime = Calendar.getInstance().getTime();

		// Find the path of .exe
		getExePath(Utils.getRoot(originalFunction));
		if (new File(Paths.CURRENT_PROJECT.EXE_PATH).exists())
			new File(Paths.CURRENT_PROJECT.EXE_PATH).delete();

		// The testing function should be normalized into a unique format.
		// Ex: "int test(int a){a=a>0?1:2;}"---normalize---->
		// "int test(int a){if (a>0) a=1; else a=2;}".
		ICFG normalizedCfg = null;
		ChangedTokens changedTokens = new ChangedTokens();
		{
			FunctionNormalizer fnNorm = originalFunction.normalizedASTtoInstrument();
			changedTokens = fnNorm.getTokens();

			IFunctionNode normalizedFunction = (IFunctionNode) originalFunction.clone();
			normalizedFunction.setAST(fnNorm.getNormalizedAST());
			// logger.debug("Normalized function: \n" +
			// normalizedFunction.getAST().getRawSignature());

			normalizedCfg = new CFGGenerationSubCondition(normalizedFunction,
					CFGGenerationSubCondition.SEPARATE_FOR_INTO_SEVERAL_NODES).generateCFG();
		}

		//
		if (normalizedCfg != null) {
			normalizedCfg.resetVisitedState();
			normalizedCfg.setIdforAllNodes();

			logger.info("Num of statements in normalized CFG: " + normalizedCfg.getUnvisitedStatements().size());
			logger.info("Num of branches in normalized CFG: " + normalizedCfg.getUnvisitedBranches().size());

			// Initialize data at random
			String testdata = ""; // Ex: a=1;b=2
			{
				Map<String, String> initialization = constructRandomInput(fn.getArguments(), fn.getFunctionConfig(),
						"");
				for (String key : initialization.keySet())
					testdata += key + "=" + initialization.get(key) + ";";
				logger.debug("Random test data= " + testdata);
			}

			// Generate test data
			int iteration = 1;
			float currentCoverage = 0.0f;
			List<IPathConstraints> candidateNegatedPCs = new ArrayList<>();
			List<IPathConstraints> solvingNegatedPCs = new ArrayList<>();

			boolean isEmptyCandidate = false;
			boolean failTestdataExecution = false;
			// boolean isIncompleteTestpath = false;
			boolean dontReachExpectedCoverage = false;
			boolean isZeroCoverage = false;
			boolean canNotGenerateTestdataAnyMore = false;
			boolean reachLimitIteration = false;
			boolean reachMaximumCoverage = false;
			boolean hasNextTestdata = false;
			do {
				// ----------------
				// STEP 1.
				// ----------------
				hasNextTestdata = reachMaximumCoverage = failTestdataExecution = isEmptyCandidate = dontReachExpectedCoverage = isZeroCoverage = canNotGenerateTestdataAnyMore = reachLimitIteration = false;
				// isIncompleteTestpath = false;
				logger.debug("\n");
				logger.debug("\n");
				logger.debug("\n");
				logger.debug("---------------------ITERATION-------------------------");
				ITestdataExecution testdataExecution = executeFunction(testdata, changedTokens, fnReport, normalizedCfg,
						originalFunction);

				// If the current test data does not cause any errors
				if (testdataExecution != null) {
					// Find the path constraints of the given test data
					Parameter paramaters = new Parameter();
					for (INode n : ((FunctionNode) originalFunction).getArguments())
						paramaters.add(n);
					for (INode n : ((FunctionNode) originalFunction).getReducedExternalVariables())
						paramaters.add(n);

					// Find the corresponding test path in the CFG
					ITestpath tp = new TestpathFinder(
							testdataExecution.getTestpath().split(ITestpath.SEPARATE_BETWEEN_NODES), normalizedCfg)
									.getTestpath();
					ICfgNode lastNode = tp.getAllCfgNodes().get(tp.getAllCfgNodes().size() - 1);

					logger.debug("Testpath in normalized CFG = " + tp.getAllCfgNodes().toString());
					// if (lastNode instanceof EndFlagCfgNode || lastNode.getTrueNode() instanceof
					// EndFlagCfgNode
					// || lastNode.getFalseNode() instanceof EndFlagCfgNode) {
					currentCoverage = fnReport.getCurrentCoverage();

					logger.debug("Performing symbolic execution on this test path");
					ISymbolicExecution se = new SymbolicExecution(tp, paramaters, originalFunction);
					logger.debug("Done. Constraints: \n"
							+ se.getConstraints().toString().replace(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE, "")
									.replace(ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES, ".")
									.replace(ISymbolicVariable.ARRAY_CLOSING, "]")
									.replace(ISymbolicVariable.ARRAY_OPENING, "["));

					// Store all constraints which can visit unvisited branches
					// TODO: negate unvisited branches
					for (int i = 0; i <= se.getConstraints().size() - 1; i++)
						// If condition i is created from decisions
						if (((PathConstraints) se.getConstraints()).get(i)
								.getType() == PathConstraint.CREATE_FROM_DECISION) {
							IPathConstraints negated = se.getConstraints().negateConditionAt(i);
							if (negated != null && !candidateNegatedPCs.contains(negated)
									&& !solvingNegatedPCs.contains(negated))
								candidateNegatedPCs.add(negated);
						}
					logger.debug("Negated path constraints list size = " + candidateNegatedPCs.size());

					if (candidateNegatedPCs.size() > 0) {
						// Select a path constraints in the constraints list
						do {
							int next = new Random().nextInt(candidateNegatedPCs.size());
							IPathConstraints negatedPC = candidateNegatedPCs.get(next);

							logger.debug("Negated Constraints: \n" + negatedPC.toString()
									.replace(ISymbolicVariable.PREFIX_SYMBOLIC_VALUE, "")
									.replace(ISymbolicVariable.SEPARATOR_BETWEEN_STRUCTURE_NAME_AND_ITS_ATTRIBUTES, ".")
									.replace(ISymbolicVariable.ARRAY_CLOSING, "]")
									.replace(ISymbolicVariable.ARRAY_OPENING, "["));

							logger.debug("Solving negated path constraints...");
							testdata = new StaticSolutionGeneration().solve(negatedPC, originalFunction);
							solvingNegatedPCs.add(negatedPC);
							candidateNegatedPCs.remove(next);
						} while (testdata.equals(IStaticSolutionGeneration.NO_SOLUTION)
								&& candidateNegatedPCs.size() > 0);

						if (!testdata.equals(IStaticSolutionGeneration.NO_SOLUTION))
							hasNextTestdata = true;

						logger.debug("Done solving. Next test data = " + testdata);
					} else
						isEmptyCandidate = true;
					// } else {
					// logger.debug("Incomplete test path. Generate another test data...");
					// isIncompleteTestpath = true;
					// }
				} else
					failTestdataExecution = true;

				reachMaximumCoverage = currentCoverage == 100f;
				dontReachExpectedCoverage = currentCoverage < 100f;
				isZeroCoverage = currentCoverage == 0f;

				if (hasNextTestdata && !reachMaximumCoverage) {
					// continue running
				} else if (!isZeroCoverage && dontReachExpectedCoverage && isEmptyCandidate)
					canNotGenerateTestdataAnyMore = true;
				else if (reachMaximumCoverage)
					canNotGenerateTestdataAnyMore = true;

				reachLimitIteration = ++iteration > MAX_ITERATIONS;
				// ----------------
				// STEP 2
				// ----------------
				if (!hasNextTestdata && failTestdataExecution || /* isIncompleteTestpath|| */ isZeroCoverage) {
					testdata = "";
					// Generate other test data
					Map<String, String> initialization = constructRandomInput(fn.getArguments(), fn.getFunctionConfig(),
							"");

					for (String key : initialization.keySet())
						testdata += key + "=" + initialization.get(key) + ";";
					logger.debug("Current test data causes errors. Generate random test data= " + testdata);
				}

			} while (!reachLimitIteration && !canNotGenerateTestdataAnyMore);

			// Calculate the running time
			{
				Date end = Calendar.getInstance().getTime();
				long runningTime = end.getTime() - startTime.getTime();
				runningTime = runningTime / 1000;// seconds

				fnReport.getCoverage().setTime(runningTime);
				fnReport.getCoverage().setNumofSolverCalls(numOfSolverCalls);
				logger.debug("Generate test data done");
				logger.debug("\n\n\n\n\n\n\n\n\n\n");
			}
		} else
			throw new GUINotifyException("Dont support for parsing this function");

	}

	/**
	 * Ex: Consider this function:
	 * 
	 * <pre>
	 *  int struct_test1(SinhVien sv){
	 *		char* s = sv.other[0].eeee;
	 *		if (sv.age > 0){
	 *			if (s[0] == 'a')
	 *				return 0;
	 *			else
	 *				return 1;
	 *		}else{
	 *			return 2;		
	 *		}
	 *	}
	 * </pre>
	 * 
	 * The above function has only one argument and it has been configured. <br/>
	 * Example of output: sv.age=306;sv.name=NULL;sv.other[0].eeee=NULL;
	 * 
	 * @param arguments
	 * @param functionConfig
	 * @param prefixName
	 * @return
	 */
	protected Map<String, String> constructRandomInput(List<IVariableNode> arguments, IFunctionConfig functionConfig,
			String prefixName) {
		Map<String, String> input = new TreeMap<>();
		for (IVariableNode argument : arguments) {
			String type = argument.getRawType();

			// Number
			if (VariableTypes.isBool(type)) {
				// 0 - false; 1 - true
				input.put(prefixName + argument.getName(), BasicTypeRandom.generateInt(0, 1) + "");
			} else if (VariableTypes.isNumBasic(type)) {
				if (VariableTypes.isNumBasicFloat(type)) {
					input.put(prefixName + argument.getName(),
							BasicTypeRandom.generateFloat(functionConfig.getIntegerBound().getLower(),
									functionConfig.getIntegerBound().getUpper()) + "");
				} else {
					input.put(prefixName + argument.getName(),
							BasicTypeRandom.generateInt(functionConfig.getIntegerBound().getLower(),
									functionConfig.getIntegerBound().getUpper()) + "");
				}

			} else if (VariableTypes.isNumOneDimension(type)) {
				for (int i = 0; i < functionConfig.getSizeOfArray(); i++)
					if (VariableTypes.isNumOneDimensionFloat(type)) {
						input.put(prefixName + argument.getName() + "[" + i + "]",
								BasicTypeRandom.generateFloat(functionConfig.getIntegerBound().getLower(),
										functionConfig.getIntegerBound().getUpper()) + "");
					} else {
						input.put(prefixName + argument.getName() + "[" + i + "]",
								BasicTypeRandom.generateInt(functionConfig.getIntegerBound().getLower(),
										functionConfig.getIntegerBound().getUpper()) + "");
					}

			} else if (VariableTypes.isNumOneLevel(type)) {
				if (assignPointerToNull()) {
					input.put(prefixName + argument.getName(), "NULL");
				} else {
					for (int i = 0; i < functionConfig.getSizeOfArray(); i++)
						input.put(prefixName + argument.getName() + "[" + i + "]",
								BasicTypeRandom.generateInt(functionConfig.getCharacterBound().getLower(),
										functionConfig.getCharacterBound().getUpper()) + "");
				}
			}
			// Character
			else if (VariableTypes.isChBasic(type)) {
				input.put(prefixName + argument.getName(),
						BasicTypeRandom.generateInt(functionConfig.getCharacterBound().getLower(),
								functionConfig.getCharacterBound().getUpper()) + "");

			} else if (VariableTypes.isChOneDimension(type)) {
				for (int i = 0; i < functionConfig.getSizeOfArray(); i++)
					input.put(prefixName + argument.getName() + "[" + i + "]",
							BasicTypeRandom.generateInt(functionConfig.getCharacterBound().getLower(),
									functionConfig.getCharacterBound().getUpper()) + "" + "");

			} else if (VariableTypes.isChOneLevel(type)) {
				if (assignPointerToNull()) {
					input.put(prefixName + argument.getName(), "NULL");
				} else {
					for (int i = 0; i < functionConfig.getSizeOfArray(); i++)
						input.put(prefixName + argument.getName() + "[" + i + "]",
								BasicTypeRandom.generateInt(functionConfig.getCharacterBound().getLower(),
										functionConfig.getCharacterBound().getUpper()) + "" + "");
				}
			}
			// Structure
			else if (VariableTypes.isStructureSimple(type)) {
				INode correspondingNode = argument.resolveCoreType();
				if (correspondingNode != null && correspondingNode instanceof StructureNode) {
					input.putAll(constructRandomInput(((StructureNode) correspondingNode).getAttributes(),
							functionConfig, prefixName + argument.getName() + "."));
				}

			} else if (VariableTypes.isStructureOneDimension(type)) {
				INode correspondingNode = argument.resolveCoreType();

				if (correspondingNode != null && correspondingNode instanceof StructureNode)
					for (int i = 0; i < functionConfig.getSizeOfArray(); i++) {
						input.putAll(constructRandomInput(((StructureNode) correspondingNode).getAttributes(),
								functionConfig, prefixName + argument.getName() + "[" + i + "]" + "."));
					}

			} else if (VariableTypes.isStructureOneLevel(type)) {
				if (assignPointerToNull()) {
					input.put(prefixName + argument.getName(), "NULL");
				} else {
					INode correspondingNode = argument.resolveCoreType();

					if (correspondingNode != null && correspondingNode instanceof StructureNode) {
						List<IVariableNode> attributes = ((StructureNode) correspondingNode).getAttributes();

						// Consider the linked list case (e.g., "class A{A* next}"), we assign value of
						// "next" to NULL. Besides, we assume the size of the structure pointer is
						// equivalent to 0.
						for (int i = attributes.size() - 1; i >= 0; i--)
							if (attributes.get(i).getReducedRawType().equals(argument.getReducedRawType())) {
								input.put(prefixName + argument.getName() + "[0]." + attributes.get(i).getName(),
										"NULL");
								attributes.remove(i);
							}

						//
						input.putAll(constructRandomInput(attributes, functionConfig,
								prefixName + argument.getName() + "[0]."));
					}
				}
			}
		}
		return input;
	}

	protected boolean assignPointerToNull() {
		return new Random().nextInt(2/* default */) == 1;
	}

	public static final int MAX_ITERATIONS = 100; // default
}
