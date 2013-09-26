package net.sourceforge.openutils.mgnlmedia.externalvideo;

/**
 * @author molaschi
 * @version $Id: $
 */
public class FtpAccount
{

    private String username;

    private String password;

    private String url;

    private boolean passive;

    /**
     * Returns the username.
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Returns the password.
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returns the url.
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url.
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Returns the passive.
     * @return the passive
     */
    public boolean isPassive()
    {
        return passive;
    }

    /**
     * Sets the passive.
     * @param passive the passive to set
     */
    public void setPassive(boolean passive)
    {
        this.passive = passive;
    }

}
