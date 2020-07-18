package test.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({BranchCoverageComputationv2Test.class, //
        Cpp11ClassNormalizerTest.class, //
        ExternalVariableDetecterTest.class, //
        FunctionNameNormalizerTest.class, //
        FunctionNodeNameTest.class, //
        GetNameOfExeInDevCppMakeFile.class, //
        MultipleTypeDefDeclareTest.class, //
        OperatorNormalizerTest.class, //
        PossibleTestpathGenerationForLoopTest.class, //
        PrivateToPublicNormalizerTest.class, //
        StatementCoverageComputationv2Test.class, //
        TestpathGenerationTest.class, //
        TreeExpressionGenerationTest.class, //
        TypedefNodeNameTest.class, //
        VariableTypeNormalizerTest.class, //
        CFGGenerationSubConditionTest.class, //
        CFGGenerationTest.class,//
        CFGGenerationForDisplayingTest.class})
public class FullParserTest {

}
