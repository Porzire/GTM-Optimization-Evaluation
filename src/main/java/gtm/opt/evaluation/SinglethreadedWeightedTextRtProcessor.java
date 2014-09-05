package gtm.opt.evaluation;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

import java.io.IOException;
import java.util.Map;

import org.textsim.textrt.proc.singlethread.SinglethreadTextRtProcessor;
import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.wordrt.proc.WordRtProcessor;

/**
 * This class provides similarity processor which allows word weights and stop
 * words.
 * 
 * @author Jie Mei
 * 
 * @see WordRtProcessor
 */
public class SinglethreadedWeightedTextRtProcessor
        extends SinglethreadTextRtProcessor
{
    TObjectIntHashMap<String> idMap;
    // Weight map has two components:
    // existing unigram looking up (int->double)
    TIntDoubleHashMap weightContMap;
    // and rejected unigram looking up (string->double)
    TObjectDoubleHashMap<String> weightRejMap;
    
    /**
     * Construct a weighted similarity processor with a word relatedness process
     * and an word to numeric identifier map for looking up. All words use
     * default weight 1.
     *
     * @param idMap            A word to numeric identifier map.
     * @param wordRtProcessor  A word similarity processor.
     * @throws IOException     If an I/O  error occurs.
     */
    public SinglethreadedWeightedTextRtProcessor(
            TObjectIntHashMap<String> idMap,
            WordRtProcessor wordRtProcessor)
            throws IOException
    {
        super(wordRtProcessor);
        this.idMap = idMap;
        weightContMap = new TIntDoubleHashMap();
    }

    /**
     * Construct a weighted similarity processor with a word relatedness process,
     * a weight map and an word to numeric identifier map for looking up.
     *
     * @param idMap            A word to numeric identifier map.
     * @param wordRtProcessor  A word similarity processor.
     * @param weights          A word to weight hash map.
     * @throws IOException     If an I/O  error occurs.
     */
    public SinglethreadedWeightedTextRtProcessor(
            TObjectIntHashMap<String> idMap,
            Map<String, Float> weights,
            WordRtProcessor wordRtProcessor)
            throws IOException
    {
        this(idMap, wordRtProcessor);
        setWeights(weights);
    }
    
    /**
     * Set word weigth for future similarity computation.
     * 
     * @param weights  A word to weight hash map.
     */
    public void setWeights(Map<String, Float> weights)
    {
        weightContMap = new TIntDoubleHashMap();
        weightRejMap = new TObjectDoubleHashMap<String>();
        for (String word : weights.keySet()) {
            if (idMap.contains(word))
                weightContMap.put(idMap.get(word), weights.get(word));
            else
                weightRejMap.put(word, weights.get(word));
        }
    }

    @Override
    public double computeTextRT(TextInstance text1, TextInstance text2, float w1, float w2)
    {
        TIntIntHashMap t1Cont = text1.getCont();
        TObjectIntHashMap<String> t1Rej = text1.getRej();
        TIntIntHashMap t2Cont = text2.getCont();
        TObjectIntHashMap<String> t2Rej = text2.getRej();
        int numofSame = removeSame(t1Cont, t1Rej, t2Cont, t2Rej,
                weightContMap, weightRejMap);
        double sumofMean = (text1.getTotalcount() <= text2.getTotalcount()
                ? computeSumOfSignificantMean(t1Cont, t1Rej, t2Cont, t2Rej)
                : computeSumOfSignificantMean(t2Cont, t2Rej, t1Cont, t1Rej));
        double numerator = (w1 * numofSame + w2 * sumofMean) * (text1.getTotalcount() + text2.getTotalcount());
        double denomenator = 2 * text1.getTotalcount() * text2.getTotalcount();
        return numerator / denomenator;
    }

    /**
     * Remove the duplicate words from the both input texts and return its
     * weighted sum for the removed words.
     * 
     * @param  t1Cont  The words in the text 1 with numeric identifer available.
     * @param  t1Rej   The words in the text 1 without number identifer.
     * @param  t2Cont  The words in the text 2 with numeric identifer available.
     * @param  t2Rej   The words in the text 1 without number identifer.
     * @param  weightContMap
     *                Map for all weighted words with numeric identifer.
     * @param  weightRejMap
     *                Map for all weighted words without numeric identifer.
     * @return The weighted sum of all the removed words in either text.
     */
    private int removeSame(
            TIntIntHashMap t1Cont, TObjectIntHashMap<String> t1Rej,
            TIntIntHashMap t2Cont, TObjectIntHashMap<String> t2Rej,
            TIntDoubleHashMap weightContMap,
            TObjectDoubleHashMap<String> weightRejMap)
    {
        int numofSame = 0;
        // Compare the content map.
		int[] idKey2 = t2Cont.keySet().toArray();
		for (int i = 0; i < idKey2.length; i++) {
			if (t1Cont.containsKey(idKey2[i])) {
                // The count in text A and B respectively.
				int countIn1 = t1Cont.get(idKey2[i]);
				int countIn2 = t2Cont.get(idKey2[i]);
                // Weight the current token.
                float weight = (float)weightContMap.get(idKey2[i]);
				if (countIn1 < countIn2) {
					t1Cont.remove(idKey2[i]);
					t2Cont.put(idKey2[i],countIn2-countIn1);
					numofSame += countIn1 * weight;
				} else if (countIn1 > countIn2) {
					t1Cont.put(idKey2[i], countIn1 - countIn2);
					numofSame += countIn2 * weight;
					t2Cont.remove(idKey2[i]);
				} else {
					t1Cont.remove(idKey2[i]);
					t2Cont.remove(idKey2[i]);
					numofSame += countIn1 * weight;
				}
			}
		}
        // Compare the rejected map.
		String[] strKey2 = t1Rej.keys(new String[t2Rej.size()]);
		for (int i = 0; i < strKey2.length; i++) {
			if (t1Rej.containsKey(strKey2[i])) {
                // The count in text A and B respectively.
				int countIn1=t1Rej.get(strKey2[i]);
				int countIn2=t2Rej.get(strKey2[i]);
                // Weight the current token.
                float weight = (float)weightRejMap.get(idKey2[i]);
				if(countIn1 < countIn2) {
					t1Rej.remove(strKey2[i]);
					t2Rej.put(strKey2[i], countIn2-countIn1);
					numofSame += countIn1 * weight;
				} else if (countIn1 > countIn2) {
					t2Rej.remove(strKey2[i]);
					t1Rej.put(strKey2[i], countIn1-countIn2);
					numofSame += countIn2 * weight;
				} else {
					t1Rej.remove(strKey2[i]);
					t2Rej.put(strKey2[i], countIn2-countIn1);
					numofSame += countIn1 * weight;
				}
			}
		}
        return numofSame;
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
            TIntIntHashMap t2Cont, TObjectIntHashMap<String> t2Rej)
    {
        if (RTCollection == null)
            throw new NullPointerException("Null table exception"); 

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
        // Total occurrences of all the rejWords.
        int numOfRejWords = 0;
        int[] rejNums = t2Rej.values();
        for (int i = 0; i < rejNums.length; i++)
            numOfRejWords += rejNums[i];

        double sumofmean = 0;
        // For each distinct word in the matrix. Note that each iteration
        // consider t1Cont.get(contKey1[i]) rows in the matrix.
        for (int i = 0; i < contKey1.length; i++) {
            // Relatedness value of each element in the row.
            double[] line = new double[contKey2.length];
            // Calculate mean and standard deviation of the row.
            double sumofallrt = 0; 
            int numOfContWords = 0;
            for (int g = 0; g < contKey2.length; g++) {
                // Add weight to the relatedness value.
                int id1 = contKey1[i];
                int id2 = contKey2[i];
                double rt = RTCollection.sim(id1, id2)
                        * (weightContMap.contains(id1) ? weightContMap.get(id1) : 1)
                        * (weightContMap.contains(id2) ? weightContMap.get(id2) : 1);
                int wc = t2Cont.get(contKey2[g]);
                numOfContWords += wc;
                line[g]= rt;
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
            // Search for the significant matchings.
            double sumofrt = 0;
            int numofrt = 0;
            double sum = stddev + meanofline;
            for (int g = 0; g < line.length; g++) {   
                if (line[g] >= sum) {
                    int tmp = contKey2[g];
                    sumofrt += line[g] * t2Cont.get(tmp);
                    numofrt += t2Cont.get(tmp);
                }
            }
            double meanofrt = (numofrt == 0 ? 0 : sumofrt / numofrt);
            sumofmean += meanofrt * t1Cont.get(contKey1[i]);
        }
        return sumofmean;
    }
}
