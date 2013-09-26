package net.sourceforge.openutils.mgnlmedia.externalvideo;

/**
 * @author molaschi
 * @version $Id: $
 */
public abstract class BaseExternalVideoProvider implements ExternalVideoProvider
{

    private String name;

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

}
