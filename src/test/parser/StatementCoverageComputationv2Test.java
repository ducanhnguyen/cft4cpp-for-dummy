package test.parser;

import com.fit.cfg.CFGGenerationforBranchvsStatementCoverage;
import com.fit.cfg.ICFGGeneration;
import com.fit.config.Paths;
import com.fit.normalizer.FunctionNormalizer;
import com.fit.parser.projectparser.ProjectParser;
import com.fit.testdatagen.coverage.StatementCoverageComputation;
import com.fit.tree.object.IFunctionNode;
import com.fit.tree.object.INode;
import com.fit.utils.Utils;
import com.fit.utils.search.FunctionNodeCondition;
import com.fit.utils.search.Search;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StatementCoverageComputationv2Test {

	ProjectParser parser;

	@Before
	public void ini() throws Exception {
		File p = new File(Paths.STATEMENT_COVERAGE_COMPUTATION_TEST);
		parser = new ProjectParser(p);
	}

	@Test
	public void test1() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test1(int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("int sum = 0;=>int i = 0;=>i < a=>sum++;=>sum += i;=>i++=>i < a=>return sum;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test2() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test1(int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("int sum = 0;=>int i = 0;=>i < a=>return sum;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0 / 7 * 4, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test3() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test(int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("return a--;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test4() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test2(int,int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a!=b=>a = a - b;=>return a;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0 / 6 * 3, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test5() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test2(int,int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a!=b=>int x = test(a);=>a>0=>return a;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0 / 6 * 4, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test6() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test2(int,int)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a!=b=>int x = test(a);=>a>0=>a++;=>return a;");
		testpaths.add("a!=b=>a = a - b;=>return a;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test7() throws Exception {
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "xoa_dau_cach_thua(char[])").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("s[0] == ' '=>StrDel(s, 0, 1);=>s[0] == ' '=>int i = 0;=>i<strlen(s)");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0 / 8 * 4, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test8() throws Exception {
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "xoa_dau_cach_thua(char[])").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("s[0] == ' '=>int i = 0;=>i<strlen(s)=>s[i]==' '=>i++;=>i<strlen(s)");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0 / 8 * 5, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test9() throws Exception {
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "test3(People)").get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("p.getAge() > 0=>return 0;");
		testpaths.add("p.getAge() > 0=>return 1;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test10() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "basicTest2(int,int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("a = a - b;=>a>0=>return a;");
		testpaths.add("a = a - b;=>a>0=>return b;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test11() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);

		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "bsort(int*,int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>int i, temp, nb;=>char fini;=>fini = 0;=>nb = 0;=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>{=>fini = 0;=>temp = table[i];=>table[i] = table[i + 1];=>table[i + 1] = temp;=>}=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>{=>fini = 1;=>i=0 ;=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>{=>table[i] < table[i+1]=>}=>i++=>i<l-1=>nb++;=>}=>fini==0 && (nb < l-1)=>}");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test12() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "add_digits(int)")
				.get(0);
		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(
				(IFunctionNode) function, ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add(
				"{=>static int sum = 0;=>n == 0=>sum = n%10 + add_digits(n/10);=>{=>static int sum = 0;=>n == 0=>{=>return 0;=>return sum;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test13() throws Exception {
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
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(100.0, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test14() throws Exception {
		File p = new File(Paths.SYMBOLIC_EXECUTION_TEST);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "check_anagram(char[],char[])").get(0);
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
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(87.5f, coverage.getCoverage() * 100, 1);
	}

	@Test
	public void test15() throws Exception {
		File p = new File(Paths.TSDV_R1_4);
		ProjectParser parser = new ProjectParser(p);
		INode function = Search.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "bmi(float,float)")
				.get(0);
		FunctionNormalizer fnNorm = ((IFunctionNode) function).normalizedASTtoInstrument();
		String normalizedCoverage = fnNorm.getNormalizedSourcecode();
		IFunctionNode clone = (IFunctionNode) function.clone();
		clone.setAST(Utils.getFunctionsinAST(normalizedCoverage.toCharArray()).get(0));

		CFGGenerationforBranchvsStatementCoverage cfgGen = new CFGGenerationforBranchvsStatementCoverage(clone,
				ICFGGeneration.SEPARATE_FOR_INTO_SEVERAL_NODES);

		List<String> testpaths = new ArrayList<>();
		testpaths.add("{=>double c;=>c = (b_w / (he * he / 10000));=>{=>c < 19=>{=>{=>{=>c >= 30=>{=>return 3;");
		StatementCoverageComputation coverage = new StatementCoverageComputation(testpaths, cfgGen.generateCFG());
		Assert.assertEquals(5.0 / 11 * 100, coverage.getCoverage() * 100, 1);
	}
}
