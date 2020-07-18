package com.fit.cfg;

import java.util.ArrayList;
import java.util.List;

import com.fit.cfg.object.BeginFlagCfgNode;
import com.fit.cfg.object.ConditionCfgNode;
import com.fit.cfg.object.EndFlagCfgNode;
import com.fit.cfg.object.FlagCfgNode;
import com.fit.cfg.object.ICfgNode;
import com.fit.cfg.object.NormalCfgNode;
import com.fit.cfg.overviewgraph.IOverviewCFGMaxLevelComputation;
import com.fit.cfg.overviewgraph.OverviewCFGMaxLevelComputation;
import com.fit.cfg.testpath.FullTestpath;
import com.fit.cfg.testpath.FullTestpaths;
import com.fit.cfg.testpath.IFullTestpath;
import com.fit.cfg.testpath.PossibleTestpathGeneration;
import com.fit.testdatagen.coverage.CFGUpdater;
import com.fit.tree.object.IFunctionNode;

public class CFG implements ICFG {
	private PossibleTestpathGeneration possibleTestpaths;
	/**
	 * The corresponding function node of the current CFG
	 */
	private IFunctionNode functionNode;

	/**
	 * A list of CFG nodes of the current CFG
	 */
	private List<ICfgNode> statements = new ArrayList<>();

	public CFG(List<ICfgNode> stms) {
		statements = stms;
	}

	@Override
	public List<ICfgNode> getAllNodes() {
		return statements;
	}

	@Override
	public int computeNumofUnvisitedStatements() {
		int count = 0;
		for (ICfgNode stm : getAllNodes())
			if (!stm.isVisited() && !stm.isSpecialCfgNode())
				count++;
		return count;
	}

	@Override
	public int computeNumofVisitedStatements() {
		int count = 0;
		for (ICfgNode stm : getAllNodes())
			if (stm.isVisited() && !stm.isSpecialCfgNode())
				count++;
		return count;
	}

	@Override
	public ICfgNode getBeginNode() {
		for (ICfgNode stm : getAllNodes())
			if (stm instanceof FlagCfgNode && stm.getContent().equals(BeginFlagCfgNode.BEGIN_FLAG))
				return stm;
		return null;
	}

	@Override
	public int computeNumofVisitedBranches() {
		int count = 0;
		for (ICfgNode stm : getAllNodes())
			if (stm.isCondition() && stm.isVisited()) {

				if (stm.getTrueNode() != null) {
					ICfgNode tmp = ignoreFlagStm(stm.getTrueNode());
					if (tmp.isVisited())
						count++;
				}

				if (stm.getFalseNode() != null) {
					ICfgNode tmp = ignoreFlagStm(stm.getFalseNode());
					if (tmp.isVisited())
						count++;
				}
			}
		return count;
	}

	@Override
	public int computeNumOfBranches() {
		int count = 0;
		for (ICfgNode stm : getAllNodes())
			if (stm.isCondition())
				count += 2;
		return count;

	}

	private ICfgNode ignoreFlagStm(ICfgNode stm) {
		while (stm != null && stm.isSpecialCfgNode())
			stm = stm.getTrueNode();
		return stm;
	}

	@Override
	public void resetVisitedState() {
		for (ICfgNode stm : getAllNodes())
			stm.setVisit(false);
	}

	@Override
	public void setIdforAllNodes() {
		int id = 0;
		for (ICfgNode stm : getAllNodes())
			stm.setId(id++);
	}

	@Override
	public int computeNumofStatements() {
		int count = 0;
		for (ICfgNode stm : getAllNodes())
			if (!stm.isSpecialCfgNode())
				count += 1;
		return count;

	}

	@Override
	public List<ICfgNode> getUnvisitedStatements() {
		List<ICfgNode> unvisitedStm = new ArrayList<>();
		for (ICfgNode stm : getAllNodes())
			if (!stm.isVisited() && !stm.isSpecialCfgNode())
				unvisitedStm.add(stm);
		return unvisitedStm;
	}

	@Override
	public List<Branch> getUnvisitedBranches() {
		List<Branch> unvisitedBranches = new ArrayList<>();

		for (ICfgNode stm : getAllNodes())
			if (stm instanceof ConditionCfgNode) {
				ConditionCfgNode conCfg = (ConditionCfgNode) stm;
				if (stm.getTrueNode() != null) {
					ICfgNode tmp = ignoreFlagStm(stm.getTrueNode());
					if (!conCfg.isVisitedTrueBranch()) {
						Branch b = new Branch(stm, tmp);
						unvisitedBranches.add(b);
					}
				}

				if (stm.getFalseNode() != null) {
					ICfgNode tmp = ignoreFlagStm(stm.getFalseNode());
					if (!conCfg.isVisitedFalseBranch()) {
						Branch b = new Branch(stm, tmp);
						unvisitedBranches.add(b);
					}
				}
			}
		return unvisitedBranches;
	}

	@Override
	public List<ICfgNode> getVisitedStatements() {
		List<ICfgNode> visitedStm = new ArrayList<>();
		for (ICfgNode stm : getAllNodes())
			if (stm.isVisited() && !stm.isSpecialCfgNode())
				visitedStm.add(stm);
		return visitedStm;
	}

	@Override
	public int getMaxLevelToDisplayOverviewCFG() {
		final int DISPLAY_ALL_IN_ONE_BLOCK = 1;

		if (functionNode != null) {
			IOverviewCFGMaxLevelComputation overviewCfgComputer = new OverviewCFGMaxLevelComputation(functionNode);
			overviewCfgComputer.computeMaxLevel();
			return overviewCfgComputer.getMaxLevel();
		} else
			return DISPLAY_ALL_IN_ONE_BLOCK;
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
	public String toString() {
		String output = "";
		for (ICfgNode stm : getAllNodes()) {
			output += "[" + stm.getId() + "][" + stm.getClass().getSimpleName() + "] " + stm.getContent() + "\t["
					+ stm.isVisited() + "]";

			if (stm.isCondition()) {
				output += "\n\tTrue: [" + stm.getTrueNode().getId() + "]["
						+ stm.getTrueNode().getClass().getSimpleName() + "] " + stm.getTrueNode().getContent() + "\t["
						+ stm.getTrueNode().isVisited() + "]";

				output += "\n\tFalse: [" + stm.getFalseNode().getId() + "]["
						+ stm.getFalseNode().getClass().getSimpleName() + "] " + stm.getFalseNode().getContent() + "\t["
						+ stm.getFalseNode().isVisited() + "]";

			} else if (!(stm instanceof EndFlagCfgNode)) {
				output += "\n\t [" + stm.getTrueNode().getId() + "][" + stm.getTrueNode().getClass().getSimpleName()
						+ "] " + stm.getTrueNode().getContent() + "\t[" + stm.getTrueNode().isVisited() + "]";

			}
			output += "\n";
		}
		return output;
	}

	@Override
	public FullTestpaths getTestpathsContainingUncoveredStatements(FullTestpaths inputTestpaths) {
		FullTestpaths uncoveredTestpath = new FullTestpaths();
		List<ICfgNode> unvisitedNodes = getUnvisitedStatements();

		List<Integer> unvisitedIds = new ArrayList<>();
		for (ICfgNode unvisitedNode : unvisitedNodes)
			unvisitedIds.add(unvisitedNode.getId());

		for (IFullTestpath tp : inputTestpaths) {

			boolean isNotExecuted = false;
			for (ICfgNode cfgNode : tp.getAllCfgNodes())
				if (cfgNode.isNormalNode())
					if (unvisitedIds.contains(cfgNode.getId())) {
						isNotExecuted = true;
						break;
					}
			if (isNotExecuted)
				uncoveredTestpath.add(tp);
		}

		return uncoveredTestpath;
	}

	@Override
	public FullTestpaths getTestpathsContainingUncoveredBranches(FullTestpaths inputTestpaths) {
		FullTestpaths uncoveredTestpath = new FullTestpaths();
		List<Branch> unvisitedBranches = getUnvisitedBranches();

		for (IFullTestpath tp : inputTestpaths) {

			boolean isNotExecuted = false;
			// Get shorten test path
			FullTestpath tmp = new FullTestpath();
			for (ICfgNode cfgNode : tp.getAllCfgNodes())
				if (cfgNode instanceof NormalCfgNode)
					tmp.add(cfgNode);

			// Get all unvisited branches
			if (tmp.size() == 1)
				isNotExecuted = true;
			else
				for (int i = 0; i < tmp.size() - 1; i++) {
					ICfgNode current = tmp.get(i);
					ICfgNode next = tmp.get(i + 1);
					Branch b = new Branch(current, next);

					for (Branch unvisitedBranch : unvisitedBranches)
						if (unvisitedBranch.equals(b)) {
							isNotExecuted = true;
							break;
						}
				}

			if (isNotExecuted)
				uncoveredTestpath.add(tp);
		}

		return uncoveredTestpath;
	}

	@Override
	public FullTestpaths generateAllPossibleTestpaths(int maximumIterationForEachLoop) {
		if (possibleTestpaths == null) {
			boolean resetCfg = false;
			possibleTestpaths = new PossibleTestpathGeneration(this, maximumIterationForEachLoop, resetCfg);
			possibleTestpaths.generateTestpaths();
		}
		return possibleTestpaths.getPossibleTestpaths();
	}

	@Override
	@Deprecated
	public void updateVisitedNodes(String testpath) {
		new CFGUpdater(testpath, this).updateVisitedNodes();
	}

	@Override
	public PossibleTestpathGeneration getPossibleTestpaths() {
		return possibleTestpaths;
	}

	@Override
	public void setPossibleTestpaths(PossibleTestpathGeneration possibleTestpaths) {
		this.possibleTestpaths = possibleTestpaths;
	}

	@Override
	public ICfgNode findById(int id) {
		for (ICfgNode node : getAllNodes())
			if (node.getId() == id)
				return node;
		return null;
	}

	@Override
	public ICfgNode findFirstCfgNodeByContent(String content) {
		for (ICfgNode node : getAllNodes())
			if (node.getContent().equals(content))
				return node;
		return null;
	}

	@Override
	public int getMaxId() {
		int maxId = 0;
		for (ICfgNode cfgNode : getAllNodes())
			if (maxId < cfgNode.getId())
				maxId = cfgNode.getId();
		return maxId;
	}
}
