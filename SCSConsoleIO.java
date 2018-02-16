public class SCSConsoleIO implements ISCSIO
{
    public void lineOut(String out)
    {
        System.out.println(out);
    }

    public void clear()
    {}

    public String stringIn(String caption)
    {
        return System.console().readLine();
    }

    public int intIn(String caption)
    {
        lineOut(caption);
        String _in = System.console().readLine();
        int _value = -1;
        boolean _good = false;
        while(!_good){
            try{
                _value = Integer.parseInt(_in);
                _good = true;
            }//end try
            catch(NumberFormatException nfe){
                lineOut(caption);
                _in = System.console().readLine();
            }//end catch
        }//end while

        return _value;
    }
}