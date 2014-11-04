package gtm.opt.evaluation;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.wordrt.proc.WordRtProcessor;

public class Aminul12
        extends AbstractImportanceAwareTextRtProcessor
        implements DocRtMeasure
{
    public Aminul12(WordRtProcessor wordRtProcessor)
            throws IOException
    {
        super(wordRtProcessor);
    }
    
    @Override
    public double relImpl(TextInstance text1, TextInstance text2, int numOfSame, int textSize1, int textSize2, MatrixBuilder builder)
    {
        TIntIntHashMap t1Cont = text1.getCont();
        TObjectIntHashMap<String> t1Rej = text1.getRej();
        TIntIntHashMap t2Cont = text2.getCont();
        TObjectIntHashMap<String> t2Rej = text2.getRej();

        double sumOfMean = (text1.getTotalcount() <= text2.getTotalcount()
                ? computeSumOfSignificantMean(t1Cont, t1Rej, t2Cont, t2Rej, builder)
                : computeSumOfSignificantMean(t2Cont, t2Rej, t1Cont, t1Rej, builder));
        double numerator = (numOfSame + sumOfMean) * (textSize1 + textSize2);
        double denomenator = 2 * textSize1 * textSize2;
        return numerator / denomenator;
    }

    /**
     * Compute mean of all the significant matchings ({@code uT(w1, w2)})
     * between two texts.
     * 
     * @param  t1Cont  The words in the text 1 with numeric identifer available.
     * @param  t1Rej   The words in the text 1 without number identifer.
     * @param  t2Cont  The words in the text 2 with numeric identifer available.
     * @param  t2Rej   The words in the text 1 without number identifer.
     * @return The mean of all the significant matchings.
     */
    protected double computeSumOfSignificantMean(
            TIntIntHashMap t1Cont, TObjectIntHashMap<String> t1Rej,
            TIntIntHashMap t2Cont, TObjectIntHashMap<String> t2Rej,
            MatrixBuilder builder)
    {
        int[] contKey1 = t1Cont.keys();
        int[] contKey2 = t2Cont.keys();
        
        /* Build a matrix in the following structure:
         *
         *                     t2
         *                t2Cont     t2Rej
         *            +------------+------+
         *            |            |      |
         *     t1Cont |            |      |
         *            |            |      |
         *  t1        |            |      |
         *            +------------+------+
         *      t1Rej |            |      |
         *            |            |      |
         *            +------------+------+
         */
        
        double[][] sim = buildMatrix(t1Cont, t2Cont, builder);

        // Total occurrences of all the rejWords.
        int numOfRejWords = 0;
        int[] rejNums = t2Rej.values();
        for (int i = 0; i < rejNums.length; i++)
            numOfRejWords += rejNums[i];

        double sumofmean = 0;
        // For each distinct word in the matrix. Note that each iteration
        // consider t1Cont.get(contKey1[i]) rows in the matrix.
        for (int i = 0; i < contKey1.length; i++) {

            // Calculate relatedness value of each element in the row.
            double[] line = new double[contKey2.length];
            // Calculate mean and standard deviation of the row.
            double sumofallrt = 0; 
            int numOfContWords = 0;
            for (int j = 0; j < contKey2.length; j++) {
                double rt = sim[i][j];
                int wc = t2Cont.get(contKey2[j]);
                numOfContWords += wc;
                line[j]= rt;
                sumofallrt += rt * wc;    
            }

            // Compute mean of the relatedness of the line elements (rejected
            // words has 0 similarity with other words).
            double meanofline = sumofallrt / (numOfContWords + numOfRejWords);
            // Compute standard deviation of the relatedness of the line elements.
            double dev = 0;
            for (int g = 0; g < line.length; g++)
                dev += Math.pow(line[g] - meanofline, 2) * t2Cont.get(contKey2[g]);
            double stddev = Math.sqrt(
                    (dev + Math.pow(meanofline, 2) * numOfRejWords)
                    / (numOfRejWords + numOfContWords));
            double sum = stddev + meanofline;

            // Search for the significant matchings.
            double sumofrt = 0;
            int numofrt = 0;
            for (int g = 0; g < line.length; g++) {   
                if (line[g] >= sum) {
                    int count = t2Cont.get(contKey2[g]);
                    sumofrt += line[g] * count;
                    numofrt += count;
                }
            }
            double meanofrt = (numofrt == 0 ? 0 : sumofrt / numofrt);
            sumofmean += meanofrt * t1Cont.get(contKey1[i]);
        }
        return sumofmean;
    }
}
