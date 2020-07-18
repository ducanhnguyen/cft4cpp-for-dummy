package com.fit.cfg;

import java.util.List;

import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.testpath.FullTestpaths;
import com.fit.cfg.testpath.PossibleTestpathGeneration;
import com.fit.tree.object.IFunctionNode;

/**
 * Represent control flow graph
 *
 * @author DucAnh
 */
public interface ICFG {
	/**
	 * Find a cfg node having the specified content
	 *
	 * @param content
	 * @return
	 */
	ICfgNode findFirstCfgNodeByContent(String content);

	/**
	 * Find a cfg node having the specified id
	 *
	 * @param id
	 * @return
	 */
	ICfgNode findById(int id);

	/**
	 * Get number of branches in CFG
	 *
	 * @return
	 */
	int computeNumOfBranches();

	/**
	 * Get number of statements in CFG
	 *
	 * @return
	 */
	int computeNumofStatements();

	/**
	 * Get the beginning node of CFG
	 *
	 * @return
	 */
	ICfgNode getBeginNode();

	/**
	 * Get all nodes in CFG
	 *
	 * @return
	 */
	List<ICfgNode> getAllNodes();

	/**
	 * Get the number of unsited statements in CFG
	 *
	 * @return
	 */
	int computeNumofUnvisitedStatements();

	/**
	 * Get the number of visited branches
	 *
	 * @return
	 */
	int computeNumofVisitedBranches();

	/**
	 * Get the number of visited statements
	 *
	 * @return
	 */
	int computeNumofVisitedStatements();

	/**
	 * Each node in CFG may be visited or unvisited. This function used to reset the
	 * state of all nodes to unvisited
	 */
	void resetVisitedState();

	/**
	 * Each node in CFG is represented by an ID
	 */
	void setIdforAllNodes();

	/**
	 * Get all unvisited statements
	 *
	 * @return
	 */
	List<ICfgNode> getUnvisitedStatements();

	List<Branch> getUnvisitedBranches();

	/**
	 * Get all visited statements
	 *
	 * @return
	 */
	List<ICfgNode> getVisitedStatements();

	/**
	 * Get maximum number of level to display in the overview CFG
	 *
	 * @return
	 */
	int getMaxLevelToDisplayOverviewCFG();

	/**
	 * Set the function node corresponding to the current CFG
	 *
	 * @return
	 */
	IFunctionNode getFunctionNode();

	/**
	 * Get the function node corresponding to the current CFG
	 *
	 * @param functionNode
	 */
	void setFunctionNode(IFunctionNode functionNode);

	FullTestpaths getTestpathsContainingUncoveredStatements(FullTestpaths inputTestpaths);

	FullTestpaths getTestpathsContainingUncoveredBranches(FullTestpaths inputTestpaths);

	/**
	 * Generate all possible test paths
	 *
	 * @param functionConfig
	 *            maximum iterations for each loop
	 * @return
	 */
	FullTestpaths generateAllPossibleTestpaths(int maximumIterationForEachLoop);

	/**
	 * Update the visited nodes in the current cfg
	 *
	 * @param testpath
	 */
	@Deprecated
	void updateVisitedNodes(String testpath);

	PossibleTestpathGeneration getPossibleTestpaths();

	void setPossibleTestpaths(PossibleTestpathGeneration possibleTestpaths);

	int getMaxId();
}
