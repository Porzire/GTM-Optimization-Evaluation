package gtm.opt.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.textsim.textrt.preproc.SinglethreadTextRtPreprocessor;
import org.textsim.textrt.preproc.TextRtPreprocessor;
import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.textrt.proc.singlethread.TextRtProcessor;
import org.textsim.util.token.DefaultTokenFilter;
import org.textsim.wordrt.proc.DefaultWordRtProcessor;
import org.textsim.wordrt.proc.WordRtProcessor;

public class Main
{
    private static String dataDir = "/home/default/projects/etc/GTM-Webpage/server/data/ngram/";
    private static String uniPath = dataDir + "googleWeb.uni";
    private static String triPath = dataDir + "googleWeb.tri";
    @SuppressWarnings("unused")
    private static String swPath1  = "resource/english_33.txt";
    private static String swPath2  = "resource/english_stop_words_smart.txt";

    public static final int INTERAL  = 0;
    public static final int PAIRWISE = 1;

    // Singleton in single thread.
    private static TextRtPreprocessor preprocessor = new SinglethreadTextRtPreprocessor(
            uniPath, swPath2, null, new PennTreeBankTokenizer(), new DefaultTokenFilter());
    
    public static MatrixBuilder option0 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return wordrt;
            }
    };
    public static MatrixBuilder option1 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return (wordrt + importance1 + importance2) / 3;
            }
    };
    public static MatrixBuilder option2 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return (wordrt + (importance1 > importance2 ? importance1 : importance2)) / 2;
            }
    };
    public static MatrixBuilder option3 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return wordrt * importance1 * importance2;
            }
    };
    public static MatrixBuilder option4 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return (wordrt + (importance1 < importance2 ? importance1 : importance2)) / 2;
            }
    };
    public static MatrixBuilder option5 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return (wordrt + (importance1 + importance2) / 2) / 2;
            }
    };
    public static MatrixBuilder option6 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return wordrt * (importance1 > importance2 ? importance1 : importance2);
            }
    };
    public static MatrixBuilder option7 = new MatrixBuilder() {
            @Override
            public double getEntry(double wordrt, double importance1, double importance2) {
                return wordrt * (importance1 < importance2 ? importance1 : importance2);
            }
    };

    public static void main(String[] args)
            throws IOException
    {
        /*
        File[] datasets = new File("data/input").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.charAt(name.length() - 1) == '*')
                    return false;
                else
                    return true;
            }
        });
        for (File dir : datasets) {
            String inputDir = "data/input/" + dir.getName();
            String outputDir = "data/output/" + dir.getName();
            test(inputDir, outputDir, PAIRWISE);
        }
        */
        // test("data/input/50-docs*", "data/output/50-docs/", INTERAL);
        // test("data/input/30-pairs", "data/output/30-pairs/", PAIRWISE);
        // test("data/input/131-pairs", "data/output/131-pairs/", PAIRWISE);

        test("data/SemEval2012/STS.input.MSRpar/"          , "data/SemEval2012/STS.output.MSRpar/"          , PAIRWISE);
        test("data/SemEval2012/STS.input.MSRvid/"          , "data/SemEval2012/STS.output.MSRvid/"          , PAIRWISE);
        test("data/SemEval2012/STS.input.SMTeuroparl/"     , "data/SemEval2012/STS.output.SMTeuroparl/"     , PAIRWISE);
        test("data/SemEval2012/STS.input.surprise.OnWN/"   , "data/SemEval2012/STS.output.surprise.OnWN/"   , PAIRWISE);
        test("data/SemEval2012/STS.input.surprise.SMTnews/", "data/SemEval2012/STS.output.surprise.SMTnews/", PAIRWISE);

        test("data/SemEval2013/STS.input.FNWN/"            , "data/SemEval2013/STS.output.FNWN/"            , PAIRWISE);
        test("data/SemEval2013/STS.input.headlines/"       , "data/SemEval2013/STS.output.headlines/"       , PAIRWISE);
        test("data/SemEval2013/STS.input.OnWN/"            , "data/SemEval2013/STS.output.OnWN/"            , PAIRWISE);
        test("data/SemEval2013/STS.input.SMT/"             , "data/SemEval2013/STS.output.SMT/"             , PAIRWISE);
    }
    
    private static void test(String inputDir, String outputDir, int type)
            throws IOException
    {
        System.out.print("Testing " + inputDir + ": ");

        @SuppressWarnings("deprecation")
        List<TextInstance> instances = preprocessor.createTextInstances(new File(inputDir).listFiles());
        Collections.sort(instances, new Comparator<TextInstance>() {
            @Override
            public int compare(TextInstance t1, TextInstance t2) {
                return t1.getFileName().compareTo(t2.getFileName());
            }
        });

        System.out.println(instances.size());

        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        
        WordRtProcessor processor = new DefaultWordRtProcessor(new File(triPath));
        
        // Test DocRtMeasure with all the test cases and store the output csv files in the subfolder.
        // testcase(new SinglethreadTextRtProcessor(processor), instances, outputDir + "/original-GTM.csv", type);
        testMeasure(new Aminul12(processor), instances, outputDir, type);
        testMeasure(new Aminul08(processor), instances, outputDir, type);
        testMeasure(new Mihalcea06(processor), instances, outputDir, type);
    }
    
    private static void testMeasure(DocRtMeasure measure, List<TextInstance> instances, String outputDir, int type)
            throws IOException
    {
        String measureName = measure.getClass().getSimpleName();
        File outDir = new File(outputDir, measureName);
        if (!outDir.exists())
            outDir.mkdir();

        testcase(measure, instances, outDir.getPath() + "/U_original.csv", DocRtMeasure.ORIGINAL,    option0, type);
        testcase(measure, instances, outDir.getPath() + "/U_option1.csv",  DocRtMeasure.ORIGINAL,    option1, type);
        testcase(measure, instances, outDir.getPath() + "/U_option2.csv",  DocRtMeasure.ORIGINAL,    option2, type);
        testcase(measure, instances, outDir.getPath() + "/U_option3.csv",  DocRtMeasure.ORIGINAL,    option3, type);
        testcase(measure, instances, outDir.getPath() + "/U_option4.csv",  DocRtMeasure.ORIGINAL,    option4, type);
        testcase(measure, instances, outDir.getPath() + "/U_option5.csv",  DocRtMeasure.ORIGINAL,    option5, type);
        testcase(measure, instances, outDir.getPath() + "/U_option6.csv",  DocRtMeasure.ORIGINAL,    option6, type);
        testcase(measure, instances, outDir.getPath() + "/U_option7.csv",  DocRtMeasure.ORIGINAL,    option7, type);
        testcase(measure, instances, outDir.getPath() + "/B_original.csv", DocRtMeasure.BALANCED,    option0, type);
        testcase(measure, instances, outDir.getPath() + "/B_option1.csv",  DocRtMeasure.BALANCED,    option1, type);
        testcase(measure, instances, outDir.getPath() + "/B_option2.csv",  DocRtMeasure.BALANCED,    option2, type);
        testcase(measure, instances, outDir.getPath() + "/B_option3.csv",  DocRtMeasure.BALANCED,    option3, type);
        testcase(measure, instances, outDir.getPath() + "/B_option4.csv",  DocRtMeasure.BALANCED,    option4, type);
        testcase(measure, instances, outDir.getPath() + "/B_option5.csv",  DocRtMeasure.BALANCED,    option5, type);
        testcase(measure, instances, outDir.getPath() + "/B_option6.csv",  DocRtMeasure.BALANCED,    option6, type);
        testcase(measure, instances, outDir.getPath() + "/B_option7.csv",  DocRtMeasure.BALANCED,    option7, type);
        testcase(measure, instances, outDir.getPath() + "/R_original.csv", DocRtMeasure.REMOVE_ZERO, option0, type);
        testcase(measure, instances, outDir.getPath() + "/R_option1.csv",  DocRtMeasure.REMOVE_ZERO, option1, type);
        testcase(measure, instances, outDir.getPath() + "/R_option2.csv",  DocRtMeasure.REMOVE_ZERO, option2, type);
        testcase(measure, instances, outDir.getPath() + "/R_option3.csv",  DocRtMeasure.REMOVE_ZERO, option3, type);
        testcase(measure, instances, outDir.getPath() + "/R_option4.csv",  DocRtMeasure.REMOVE_ZERO, option4, type);
        testcase(measure, instances, outDir.getPath() + "/R_option5.csv",  DocRtMeasure.REMOVE_ZERO, option5, type);
        testcase(measure, instances, outDir.getPath() + "/R_option6.csv",  DocRtMeasure.REMOVE_ZERO, option6, type);
        testcase(measure, instances, outDir.getPath() + "/R_option7.csv",  DocRtMeasure.REMOVE_ZERO, option7, type);
    }
    
    /*
     * Each test case tests through the documentset with a specific setting.
     */
    private static void testcase(DocRtMeasure measure, List<TextInstance> instances, String filename, int preprocType, MatrixBuilder option, int type)
            throws IOException
    {
        try (
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        ){
            if (type == INTERAL) {
                for (int i = 0; i < instances.size(); i++)
                    for (int j = i; j < instances.size(); j++)
                        if (i == j)
                            bw.write(",\n");
                        else
                            bw.write(measure.rel(instances.get(i), instances.get(j), preprocType, option) + ",\n");
            } else if (type == PAIRWISE) {
                for (int i = 0; i < instances.size(); i+=2)
                    bw.write(measure.rel(instances.get(i), instances.get(i+1), preprocType, option) + ",\n");
            }
        }
    }
    @SuppressWarnings("unused")
    private static void testcase(TextRtProcessor processor, List<TextInstance> instances, String filename, int type)
            throws IOException
    {
        try (
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        ){
            if (type == INTERAL) {
                for (int i = 0; i < instances.size(); i++)
                    for (int j = i; j < instances.size(); j++)
                        if (i == j)
                            bw.write(",\n");
                        else
                            bw.write(processor.sim(instances.get(i), instances.get(j)) + ",\n");
            } else if (type == PAIRWISE) {
                for (int i = 0; i < instances.size(); i+=2)
                    bw.write(processor.sim(instances.get(i), instances.get(i+1)) + ",\n");
            }
        }
    }
}
