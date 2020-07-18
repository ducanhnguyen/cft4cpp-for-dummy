package test.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fit.cfg.CFGGenerationforBranchvsStatementCoverage;
import com.fit.cfg.ICFGGeneration;
import com.fit.config.Paths;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.testdatagen.coverage.BranchCoverageComputation;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;

public class BranchCoverageComputationv2Test {

	@Test
	public void test1() throws Exception {
		File p = new File(Paths.STATEMENT_COVERAGE_COMPUTATION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test2(int,int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a!=b=>int x = test(a);=>a>0=>return a;");
		testpaths.add("a!=b=>a = a - b;=>return a;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(75.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test2() throws Exception {
		File p = new File(Paths.BTL);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "TS::StrIns(char[],int,char[])").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("{=>memmove(s+strlen(s1)+pos,s+pos,strlen(s)-pos+1);=>strncpy(s+pos,s1,strlen(s1));=>}");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test3() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "pointerTest2(const int*,int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a[b]==a  [   b-1]=>return 1;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(50.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test4() throws Exception {
		File p = new File(Paths.SAMPLE01);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "StackLinkedList::destroyList()")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		/**
		 * Statement "front != NULL" is executed one time, but compiler tries to run it
		 * two times.
		 */
		testpaths.add(
				"front != NULL=>Node *temp = front;=>front = front->N;=>delete temp;=>front != NULL=>front != NULL=>}");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test5() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "basicTest2(int,int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a = a - b;=>a>0=>return a;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(50.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test6() throws Exception {
		File p = new File(Paths.SAMPLE01);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "StackLinkedList::push(Node*)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("front == NULL=>front = n;=>}");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(50.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test7() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "pointerTest8(int*,int*)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("p1 = p2+1;=>*(p1) == *(p2+2)=>return 1;");
		testpaths.add("p1 = p2+1;=>*(p1) == *(p2+2)=>return 0;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test8() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "pointerTest15(char*)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("char* s;=>s = eeee;=>s[0] == 'a'=>return 1;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(50.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test9() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "pointerTest7(int*,int*)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("*(p1) == *(p2+1)=>return 0;");
		testpaths.add("*(p1) == *(p2+1)=>p1 = p2;=>*(p1) == *(p2+1)=>return 1;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(75.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test10() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);

		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "bsort(int*,int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>int i, temp, nb;=>char fini;=>fini = 0;=>nb = 0;=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>}");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test11() throws Exception {
		File p = new File(Paths.TSDV_R1_2);
		ProjectParser parser = new ProjectParser(p);

		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "Divide(int,int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("{=>y == 0=>return x / y;");
		testpaths.add("{=>y == 0=>{=>throw std::exception();");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());

		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test12() throws Exception {
		File p = new File(Paths.TSDV_R1_4);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "trigamma(double,int*)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>double a = 0.0001;=>double b = 5.0;=>double b2 =  0.1666666667;=>double b4 = -0.03333333333;=>double b6 =  0.02380952381;=>double b8 = -0.03333333333;=>double value;=>double y;=>double z;=>x <= 0.0=>{=>*ifault = 1;=>value = 0.0;=>return value;");

		testpaths.add(
				"{=>double a = 0.0001;=>double b = 5.0;=>double b2 =  0.1666666667;=>double b4 = -0.03333333333;=>double b6 =  0.02380952381;=>double b8 = -0.03333333333;=>double value;=>double y;=>double z;=>x <= 0.0=>*ifault = 0;=>z = x;=>x <= a=>{=>value = 1.0 / x / x;=>return value;");

		testpaths.add(
				"{=>double a = 0.0001;=>double b = 5.0;=>double b2 =  0.1666666667;=>double b4 = -0.03333333333;=>double b6 =  0.02380952381;=>double b8 = -0.03333333333;=>double value;=>double y;=>double z;=>x <= 0.0=>*ifault = 0;=>z = x;=>x <= a=>value = 0.0;=>z < b=>{=>value = value + 1.0 / z / z;=>z = z + 1.0;=>}=>z < b=>{=>value = value + 1.0 / z / z;=>z = z + 1.0;=>}=>z < b=>y = 1.0 / z / z;=>value = value + 0.5 *       y + ( 1.0     + y * ( b2     + y * ( b4     + y * ( b6     + y *   b8 )))) / z;=>return value;");

		testpaths.add(
				"{=>double a = 0.0001;=>double b = 5.0;=>double b2 =  0.1666666667;=>double b4 = -0.03333333333;=>double b6 =  0.02380952381;=>double b8 = -0.03333333333;=>double value;=>double y;=>double z;=>x <= 0.0=>*ifault = 0;=>z = x;=>x <= a=>value = 0.0;=>z < b=>{=>value = value + 1.0 / z / z;=>z = z + 1.0;=>}=>z < b=>y = 1.0 / z / z;=>value = value + 0.5 *       y + ( 1.0     + y * ( b2     + y * ( b4     + y * ( b6     + y *   b8 )))) / z;=>return value;");

		testpaths.add(
				"{=>double a = 0.0001;=>double b = 5.0;=>double b2 =  0.1666666667;=>double b4 = -0.03333333333;=>double b6 =  0.02380952381;=>double b8 = -0.03333333333;=>double value;=>double y;=>double z;=>x <= 0.0=>*ifault = 0;=>z = x;=>x <= a=>value = 0.0;=>z < b=>y = 1.0 / z / z;=>value = value + 0.5 *       y + ( 1.0     + y * ( b2     + y * ( b4     + y * ( b6     + y *   b8 )))) / z;=>return value;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test13() throws Exception {
		File p = new File(Paths.SAMPLE01);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "StackLinkedList::push(Node*)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("{=>front == NULL=>{=>front = n;=>}=>}");

		testpaths.add("{=>front == NULL=>{=>cout <<\"1\";=>front->P = n;=>n->N = front;=>front = n;=>}=>}");

		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test14() throws Exception {
		File p = new File(Paths.TSDV_R1_4);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "i4_log_10(int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>int i_abs;=>int ten_pow;=>int value;=>i == 0=>{=>value = 0;=>ten_pow = 10;=>i < 0=>{=>i_abs = -i;=>}=>ten_pow <= i_abs=>{=>value = value + 1;=>ten_pow = ten_pow * 10;=>}=>ten_pow <= i_abs=>}=>return value;");
		testpaths.add("{=>int i_abs;=>int ten_pow;=>int value;=>i == 0=>{=>value = 0;=>}=>return value;");
		testpaths.add(
				"{=>int i_abs;=>int ten_pow;=>int value;=>i == 0=>{=>value = 0;=>ten_pow = 10;=>i < 0=>{=>i_abs = i;=>}=>ten_pow <= i_abs=>{=>value = value + 1;=>ten_pow = ten_pow * 10;=>}=>ten_pow <= i_abs=>{=>value = value + 1;=>ten_pow = ten_pow * 10;=>}=>ten_pow <= i_abs=>}=>return value;");

		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test15() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "check_anagram(char[],char[])").get(0);
		System.out.println(((IFunctionNode) function).getAST().getRawSignature());
		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>c = 0;=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>{=>second[b[c]-'a']++;=>c++;=>}=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;");
		testpaths.add(
				"int first[26], second[26], c = 0;=>first[20] = 0;=>second[26]=0;=>a[c] != 0=>{=>first[a[c]-'a']++;=>c++;=>}=>a[c] != 0=>c = 0;=>b[c] != 0=>c = 0;=>c < 26=>{=>first[c] != second[c]=>{=>return 0;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(75f, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test16() throws Exception {
		File p = new File(Paths.TSDV_R1_4);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "bmi(float,float)")
				.get(0);
		System.out.println(((IFunctionNode) function).getAST().getRawSignature());
		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("{=>double c;=>c = (b_w / (he * he / 10000));=>{=>c < 19=>{=>{=>{=>c >= 30=>{=>return 3;");
		BranchCoverageComputation coverage = new BranchCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(5.0 / 11 * 100, coverage.getCoverage() * 100, 1);
	}
}
