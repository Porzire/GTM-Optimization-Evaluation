package gtm.opt.evaluation;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.wordrt.proc.WordRtProcessor;

public class Mihalcea06
        extends AbstractImportanceAwareTextRtProcessor
        implements DocRtMeasure
{
    public Mihalcea06(WordRtProcessor wordRtProcessor)
            throws IOException
    {
        super(wordRtProcessor);
    }

    @Override
    double relImpl(TextInstance text1, TextInstance text2, int numOfSame, int textSize1, int textSize2, MatrixBuilder builder)
    {
        TIntIntHashMap t1Cont = text1.getCont();
        @SuppressWarnings("unused")
        TObjectIntHashMap<String> t1Rej = text1.getRej();
        TIntIntHashMap t2Cont = text2.getCont();
        @SuppressWarnings("unused")
        TObjectIntHashMap<String> t2Rej = text2.getRej();

        // Select the maximum value for each row and column respectively.
        double[][] entries = buildMatrix(t1Cont, t2Cont, builder);
        if (entries.length == 0 || entries[0].length == 0) {
            return 0;
        }
        List<Double> rowMax = new ArrayList<Double>();
        for (int i = 0; i < entries.length; i++)
            rowMax.add(max(entries[i]));
        List<Double> colMax = new ArrayList<Double>();
        double[] col = new double[entries.length];
        for (int i = 0; i < entries[0].length; i++) {
            for (int j = 0; j < entries.length; j++)
                col[j] = entries[j][i];
            colMax.add(max(col));
        }

        // Compute the document relatedness.
        return (sum(rowMax) / rowMax.size() + sum(colMax) / colMax.size()) / 2;
    }
    
    private double sum(List<Double> vals)
    {
        double sum = 0;
        for (double val : vals)
            sum += val;
        return sum;
    }
    
    private double max(double... vals)
    {
        if (vals.length == 0)
            throw new RuntimeException();
        double max = vals[0];
        for (int i = 1; i < vals.length; i++)
            if (vals[i] > max)
                max = vals[i];
        return max;
    }
}
