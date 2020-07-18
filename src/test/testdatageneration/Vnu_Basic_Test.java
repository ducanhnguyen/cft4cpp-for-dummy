package test.testdatageneration;

import com.fit.config.IFunctionConfig;
import com.fit.config.Paths;
import com.fit.testdatagen.htmlreport.FuntionTestReportGUI;
import org.junit.Assert;
import org.junit.Test;

import javax.sound.sampled.LineUnavailableException;

public class Vnu_Basic_Test extends AbstractJUnitTest {
    @Test
    public void test1() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest1(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test2() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest2(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test3() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest3(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test4() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest4(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test5() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest5(int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test6() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest6(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test7() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest7(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test8() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest8(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test9() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest9(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test10() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest10(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test11() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest11(int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test12() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest12(int)", new EO(2, 50.0f),
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test13() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest12(int)", new EO(2, 80.0f),
                IFunctionConfig.STATEMENT_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test14() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest13(int)", new EO(2, 50.0f),
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test15() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest13(int)", new EO(2, 80.0f),
                IFunctionConfig.STATEMENT_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test16() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest14(char,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test17() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest15(bool)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test18() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest16(bool)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test19() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest17(int,int,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test20() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest18(bool,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test21() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest18(bool,int)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test22() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest20(bool,bool)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }

    @Test
    public void test23() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.SYMBOLIC_EXECUTION_TEST, "basicTest21(bool,bool)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }
}
