package gtm.opt.evaluation;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.textsim.textrt.preproc.SinglethreadTextRtPreprocessor;
import org.textsim.textrt.preproc.TextRtPreprocessor;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.util.token.DefaultTokenFilter;

public class AbstractTest {
    
    protected static TextRtPreprocessor preprocessor;
    protected static List<TextInstance> instances;

    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        preprocessor = new SinglethreadTextRtPreprocessor(
                Constant.UNI_FILE, Constant.STOP_FILE_ENGLISH_SMART, null, new PennTreeBankTokenizer(), new DefaultTokenFilter());
        instances = preprocessor.preprocess(new File[]{
                new File(TestConstant.TestDoc1), new File(TestConstant.TestDoc2)});
    }
    
    protected void testRun(DocRtMeasure measure, int preprocType, MatrixBuilder builder)
    {
        measure.rel(instances.get(0), instances.get(1), preprocType, builder);
    }

    protected void testRun(DocRtMeasure measure)
    {
        testRun(measure, DocRtMeasure.BALANCED, new MatrixBuilder(){
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return wordrt;
            }
        });
    }
}
