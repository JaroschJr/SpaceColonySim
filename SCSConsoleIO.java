import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SCSConsoleIO implements ISCSIO
{
    private ISCSError _handler;
    private BufferedReader _reader;

    public SCSConsoleIO(ISCSError handler) {
        _handler = handler;
        _reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private String getInput() {
        String result = null;

        try {
            result = _reader.readLine();
        }//end try
        catch (IOException ioe) {

        }//end catch ioe

        return result;
    }

    public void lineOut(String out)
    {
        System.out.println(out);
    }

    public void clear()
    {}

    public String stringIn(String caption)
    {
		lineOut(caption);
        return getInput();
    }

    public int intIn(String caption)
    {
        lineOut(caption);
        String _in = getInput();
        int _value = -1;
        boolean _good = false;
        while(!_good){
            try{
                _value = Integer.parseInt(_in);
                _good = true;
            }//end try
            catch(NumberFormatException nfe){
                lineOut(caption);
                _in = getInput();
            }//end catch
        }//end while

        return _value;
    }
}