package gtm.opt.evaluation;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.textsim.textrt.proc.singlethread.TextInstance;
import org.textsim.wordrt.proc.WordRtProcessor;

/**
 * This class provides similarity processor which combine word similarity and
 * importance of the words in text similarity computation.
 * 
 * @author Jie Mei
 * 
 * @see WordRtProcessor
 */
abstract class AbstractImportanceAwareTextRtProcessor
        implements DocRtMeasure
{
    WordRtProcessor processor;

    abstract double relImpl(TextInstance text1, TextInstance text2, int numOfSame, int textSize1, int textSize2, MatrixBuilder builder);

    /**
     * Compute the realtedness between two given text instances.
     */
    public double rel(TextInstance text1, TextInstance text2, int preprocType, MatrixBuilder builder)
    {
        // Ensure any change will not affect the original instance.
        TextInstance t1 = text1.deepClone();
        TextInstance t2 = text2.deepClone();
        int textSize1 = computeSize(t1);
        int textSize2 = computeSize(t2);

        // Compute the number of identical tokens.
        TIntIntHashMap t1Cont = t1.getCont();
        TObjectIntHashMap<String> t1Rej = t1.getRej();
        TIntIntHashMap t2Cont = t2.getCont();
        TObjectIntHashMap<String> t2Rej = t2.getRej();
        int numOfSame = removeSame(t1Cont, t1Rej, t2Cont, t2Rej);

        // Balance the input.
        if (preprocType == BALANCED) {

            int size1 = computeSize(t1Cont);
            int size2 = computeSize(t2Cont);
            // Trim the longer one short.
            TIntIntHashMap toTrim = (size1 > size2 ? t1Cont : t2Cont);
            int toSize = (size1 < size2 ? size1 : size2);
            double[][] matrix = buildMatrix(toTrim, toTrim, new MatrixBuilder(){
                @Override
                public double getEntry(double wordrt, double importance1, double importance2) {
                    return importance1;
                }
            });
            List<Word> words = new ArrayList<Word>();
            int[] keys = toTrim.keys();
            for (int i = 0; i < matrix.length; i++)
                words.add(new Word(keys[i], toTrim.get(keys[i]), matrix[i][0]));
            // Create new content map.
            TIntIntHashMap newCont = new TIntIntHashMap();
            int count = 0;
            int index = 0;
            while (count < toSize) {
                Word curr = words.get(index++);
                if (curr.count + count <= toSize) {
                    newCont.put(curr.key, curr.count);
                    count += curr.count;
                } else {
                    newCont.put(curr.key, toSize - count);
                    count = toSize;
                }
            }
            t1 = new TextInstance("", (size1 < size2 ? t1 : t2).getCont(), new TObjectIntHashMap<String>());
            t2 = new TextInstance("", newCont, new TObjectIntHashMap<String>());

        // Remove the zero values
        } else if (preprocType == REMOVE_ZERO) {

            t1 = new TextInstance("", t1.getCont(), new TObjectIntHashMap<String>());
            t2 = new TextInstance("", t2.getCont(), new TObjectIntHashMap<String>());
        }

        // If any text size is 0 after preprocess, return 1.
        if (computeSize(t1) == 0 || computeSize(t2) == 0)
            return 1;
        return relImpl(t1, t2, numOfSame, textSize1, textSize2, builder);
    }

    /**
     * Construct a importance aware similarity processor with a word relatedness
     * process and an word to numeric identifier map for looking up.
     *
     * @param  wordRtProcessor  A word similarity processor.
     * @throws IOException      If an I/O  error occurs.
     */
    public AbstractImportanceAwareTextRtProcessor(WordRtProcessor wordRtProcessor)
            throws IOException
    {
        processor = wordRtProcessor;
    }
    

    /* 
     * Compute similarity value for the t1Cont*t2Cont matrix.
     */
    protected double[][] computeWordRt(TIntIntHashMap t1Cont, TIntIntHashMap t2Cont)
    {
        int[] contKey1 = t1Cont.keys();
        int[] contKey2 = t2Cont.keys();
        double[][] sim = new double[contKey1.length][contKey2.length];
        for (int i = 0; i < contKey1.length; i++)
            for (int j = 0; j < contKey2.length; j++)
                sim[i][j] = processor.sim(contKey1[i], contKey2[j]);
        return sim;
    }
    
    /*
     *  Compute the importance for t1Cont and t2Cont.
     */
    protected double[][] computeImportance(double[][] wordRt)
    {
        double[] imp1 = new double[wordRt.length];
        for (int i = 0; i < wordRt.length; i++)
            imp1[i] = meanAddDev(wordRt[i]);
        double[] imp2 = new double[wordRt[0].length];
        for (int i = 0; i < imp2.length; i++) {
            double[] t1Sim = new double[wordRt.length];
            for (int j = 0; j < wordRt.length; j++)
                t1Sim[j] = wordRt[j][i];
            imp2[i] = meanAddDev(t1Sim);
        }
        return  new double[][] {imp1, imp2};
    }
    
    /*
     * Build matrix with the value computed with given MatrixBuilder.
     */
    protected double[][] buildMatrix(TIntIntHashMap t1Cont, TIntIntHashMap t2Cont, MatrixBuilder builder)
    {
        double[][] sim = computeWordRt(t1Cont, t2Cont);
        if (sim.length != 0) {
            double[][] imp = computeImportance(sim);
            // Update the similarity to the function of word similarity and
            // importance.
            for (int i = 0; i < sim.length; i++)
                for (int j = 0; j < sim[0].length; j++)
                    sim[i][j] = builder.getEntry(sim[i][j], imp[0][i], imp[1][j]);
        }
        return sim;
    }

    /*
     * Compute the sum of mean and standard deviation of the given array.
     */
    private double meanAddDev(double[] vals)
    {
        // Compute the mean and standard deviation for the similarities.
        double sum = 0;
        for (int i = 0; i < vals.length; i++)
            sum += vals[i];
        double mean = sum / vals.length;
        double dev = 0;
        for (int i = 0; i < vals.length; i++)
            dev += Math.pow(vals[i] - mean, 2);
        dev = (double) Math.sqrt(dev / vals.length);
        // Use the sum of mean and standard deviation as the score.
        return mean + dev;
    }
    
    /**
     * Remove the duplicate words from the both input texts and return its
     * sum for the removed words.
     * 
     * @param  t1Cont  The words in the text 1 with numeric identifer available.
     * @param  t1Rej   The words in the text 1 without number identifer.
     * @param  t2Cont  The words in the text 2 with numeric identifer available.
     * @param  t2Rej   The words in the text 1 without number identifer.
     * @return The sum of all the removed words in either text.
     */
    protected int removeSame(
            TIntIntHashMap t1Cont, TObjectIntHashMap<String> t1Rej,
            TIntIntHashMap t2Cont, TObjectIntHashMap<String> t2Rej)
    {
        int same = 0;
        // Compare the content map.
		int[] idKey2 = t2Cont.keySet().toArray();
		for (int i = 0; i < idKey2.length; i++) {
            int id = idKey2[i];
			if (t1Cont.containsKey(id)) {
                // The count in text A and B respectively.
				int count1 = t1Cont.get(id);
				int count2 = t2Cont.get(id);
                // Rmove the same repeat count from A and B, and add the number
                // of duplication to same.
				if (count1 < count2) {
					t1Cont.remove(id);
					t2Cont.put(id,count2-count1);
					same += count1;
				} else if (count1 > count2) {
					t1Cont.put(id, count1 - count2);
					same += count2;
					t2Cont.remove(id);
				} else {
					t1Cont.remove(id);
					t2Cont.remove(id);
					same += count1;
				}
			}
		}
        // Compare the rejected map.
		String[] strKey2 = t1Rej.keys(new String[t2Rej.size()]);
		for (int i = 0; i < strKey2.length; i++) {
            String token = strKey2[i];
			if (t1Rej.containsKey(token)) {
                // The count in text A and B respectively.
				int count1=t1Rej.get(token);
				int count2=t2Rej.get(token);
                // Rmove the same repeat count from A and B, and add the number
                // of duplication to same.
				if(count1 < count2) {
					t1Rej.remove(token);
					t2Rej.put(token, count2-count1);
					same += count1;
				} else if (count1 > count2) {
					t2Rej.remove(token);
					t1Rej.put(token, count1-count2);
					same += count2;
				} else {
					t1Rej.remove(token);
					t2Rej.put(token, count2-count1);
					same += count1;
				}
			}
		}
        return same;
    }
    
    /*
     * Compute the size of given object.
     */
    protected int computeSize(TextInstance t)
    {
        int size = 0;
        for (int count : t.getCont().values())
            size += count;
        for (int count : t.getRej().values())
            size += count;
        return size;
    }
    protected int computeSize(TIntIntHashMap map)
    {
        int size = 0;
        for (int count : map.values())
            size += count;
        return size;
    }
    
    private static class Word
            implements Comparable<Word>
    {
        public final int key;
        public final int count;
        public final double importance;

        public Word(int key, int count, double importance)
        {
            this.key = key;
            this.count = count;
            this.importance = importance;
        }

        @Override
        public int compareTo(Word word)
        {
            return importance > word.importance ? -1 : 1;
        }
        
        public String toString() {
            return importance + " ";
        }
    }
    
    protected void print(TextInstance t)
    {
        
        System.out.println("cont {");
        TIntIntHashMap contMap = t.getCont();
        for (int key : contMap.keys())
            System.out.println(key + ":" + contMap.get(key));
        System.out.println("}");
        System.out.println("rej {");
        TObjectIntHashMap<String> rejMap = t.getRej();
        for (String key : rejMap.keySet())
            System.out.println(key + ":" + rejMap.get(key));
        System.out.println("}");
        System.out.println();
    }
}
