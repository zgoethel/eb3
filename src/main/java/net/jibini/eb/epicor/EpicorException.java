package net.jibini.eb.epicor;

/**
 * This unchecked exception is thrown any time an Epicor-related operation fails
 * because of a failure due to Epicor or communication with Epicor.
 *
 * @author Zach Goethel
 */
public class EpicorException extends IllegalStateException
{
    public EpicorException(String message)
    {
        super(message);
    }

    public EpicorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
