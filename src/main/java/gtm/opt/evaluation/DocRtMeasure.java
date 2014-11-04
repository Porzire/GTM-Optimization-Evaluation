package gtm.opt.evaluation;

import org.textsim.textrt.proc.singlethread.TextInstance;

interface DocRtMeasure
{
    public static final int ORIGINAL    = 0;
    public static final int REMOVE_ZERO = 1;
    public static final int BALANCED    = 2;

    /**
     * Measure the relatedness between two given document content strings.
     * 
     * @param  doc1         The TextInstance of one document.
     * @param  doc2         The TextInstance of another document.
     * @param  preprocType  The preprocessing type, must be defined in the sublcass.
     * @return The document relatedness.
     */
    public double rel(TextInstance doc1, TextInstance doc2, int preprocType, MatrixBuilder builder);
}
