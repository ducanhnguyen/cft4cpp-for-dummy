package test.testdatageneration;

import com.fit.config.IFunctionConfig;
import com.fit.config.Paths;
import com.fit.testdatagen.htmlreport.FuntionTestReportGUI;
import org.junit.Assert;
import org.junit.Test;

import javax.sound.sampled.LineUnavailableException;

public class UnsupportedDataTypesTest extends AbstractJUnitTest {

    @Test
    public void test01() throws LineUnavailableException {
        Assert.assertEquals(true, generateTestdata(Paths.UNSUPPORTED_DATA_TYPES, "pointerTest01(double*)", null,
                IFunctionConfig.BRANCH_COVERAGE, new FuntionTestReportGUI()));
    }
}
