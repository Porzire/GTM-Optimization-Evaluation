package gtm.opt.evaluation;

import org.textsim.textrt.preproc.tokenizer.PennTreeBankTokenizer;

public class TokenizationTest {
    public static void main(String[] args) {
        String input = "(The) national executive of the strife-torn Democrats last night appointed little-known West Australian senator Brian Greig as interim leader - a shock move likely to provoke further conflict between the party's senators and its organisation. In a move to reassert control over the party's seven senators, the national executive last night rejected Aden Ridgeway's bid to become interim leader, in favour of Senator Greig, a supporter of deposed leader Natasha Stott Despoja and an outspoken gay rights activist.";
        PennTreeBankTokenizer ptbt = new PennTreeBankTokenizer();
        ptbt.tokenize(input);
        while (ptbt.hasMoreTokens()) {
            System.out.print(ptbt.nextToken() + ' ');
        }
    }
}