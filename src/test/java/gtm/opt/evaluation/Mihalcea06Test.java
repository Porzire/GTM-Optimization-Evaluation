package gtm.opt.evaluation;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.textsim.wordrt.proc.DefaultWordRtProcessor;

public class Mihalcea06Test
        extends AbstractTest
{
    private DocRtMeasure measure;

    @Before
    public void before()
            throws Exception
    {
        measure = new Mihalcea06(new DefaultWordRtProcessor(new File(Constant.TRI_FILE)));
    }

    @Test
    public void testRun()
    {
        testRun(measure);
    }
}
