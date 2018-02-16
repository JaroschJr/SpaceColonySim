/**
 * Interface for defining an object which
 * facilitates communication with the user
 * in a generic way.
 */
public interface ISCSIO
{
    /**
     * Sends a string to the output stream.
     * @param out The string to output.
     */
    public void lineOut(String out);

    /**
     * Clears the output stream to make a
     * clean slate.
     */
    public void clear();

    /**
     * Asks the user for input in the
     * form of a string.
     * @param caption The caption to show the user.
     * @return Returns the user's input.
     */
    public String stringIn(String caption);

    /**
     * Asks the user for input in the
     * form of an integer.
     * @param caption The caption to show the user.
     * @return Returns the user's input.
     */
    public int intIn(String caption);
}