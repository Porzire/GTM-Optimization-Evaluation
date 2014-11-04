package gtm.opt.evaluation;

public interface MatrixBuilder
{
    /**
     * Get the value of each entry in the matrix.
     * This method defines the algorithm for computing matrix entries in the
     * concrete subclasses. This method is called internally in matrix
     * construction.
     * 
     * @param wordrt       The word relatedness value between given two words.
     * @param importance1  The importance value of the first word.
     * @param importance2  The importance value of the second word.
     * @return The entry value in the matrix.
     */
     public double getEntry(double wordrt, double importance1, double importance2);
}
