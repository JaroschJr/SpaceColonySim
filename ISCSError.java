/**
 * Interface defining an object which has the
 * ability to facilitate dealing with errors
 * and exceptions which occur.
 */
public interface ISCSError{
	public void handleException(Exception thisException, String sExtraInfo, boolean bFatal);
}
