package gtm.opt.evaluation;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * The implementation of this class is based on MaxentTagger in StanfordNLP.
 * 
 * @author Jie Mei
 */
public class POSTagger
{
    /**
     * @see <a href="https://gate.ac.uk/wiki/twitter-postagger.html">https://gate.ac.uk/wiki/twitter-postagger.html</a>
     */
    private static final String MODEL_FILE = "model/gate-EN-twitter.model";
    private static String TOKENIZE_OPTIONS = "ptb3Escaping=false";
    
    /**
     * The underlying POS tagger.
     */
    private static MaxentTagger tagger;
    static {
        tagger = new MaxentTagger(MODEL_FILE);
    }

    public static List<TaggedWord> tag(String sentence)
    {
        return tagger.tagSentence(new PTBTokenizer<Word>(new StringReader(sentence), new WordTokenFactory(), TOKENIZE_OPTIONS).tokenize());
    }
}
