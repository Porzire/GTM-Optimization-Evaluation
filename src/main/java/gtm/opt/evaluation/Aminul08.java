package gtm.opt.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
public class Aminul08
        extends AbstractImportanceAwareTextRtProcessor
        implements DocRtMeasure
{
    public Aminul08(WordRtProcessor wordRtProcessor)
            throws IOException
    {
        super(wordRtProcessor);
    }

    @Override
    public double relImpl(TextInstance text1, TextInstance text2, int numOfSame, int textSize1, int textSize2, MatrixBuilder builder)
    {
        // Sort the entries in the decreasing order with the value.
        double[][] entries = buildMatrix(text1.getCont(), text2.getCont(), builder);
        List<Double> selected = new ArrayList<Double>();
        if (entries.length != 0) {
            List<Entry> entryList = new ArrayList<Entry>(entries.length * entries[0].length);
            for (int i = 0; i < entries.length; i++)
                for (int j = 0; j < entries[i].length; j++)
                    entryList.add(new Entry(entries[i][j], i, j));
            Collections.sort(entryList);
            // Select the entries.
            HashSet<Integer> rmRow = new HashSet<Integer>();
            HashSet<Integer> rmCol = new HashSet<Integer>();
            int remaining = Math.min(entries.length, entries[0].length);
            int index = 0;
            Entry curr = null;
            while (remaining-- > 0) {
                while((curr = entryList.get(index++)) != null) {
                    if (!rmRow.contains(curr.pos[0]) && !rmCol.contains(curr.pos[1])) {
                        selected.add(curr.val);
                        rmRow.add(curr.pos[0]);
                        rmCol.add(curr.pos[1]);
                        break;
                    }
                }
            }
        }
        
        // Compute the document relatedness.
        double sum = 0;
        for (double val : selected)
            sum += val;
        return (numOfSame + sum) * (textSize1 + textSize2) / (textSize1 * textSize2);
    }
    
    private static class Entry
            implements Comparable<Entry>
    {
        public final double val;
        public final int[] pos;

        public Entry(double val, int index1, int index2)
        {
            this.val = val;
            pos = new int[]{index1, index2};
        }

        @Override
        public int compareTo(Entry other) {
            int res = 0;
            if (val > other.val) res = -1;
            else if (val < other.val) res = 1;
            return res;
        }
    }
}
