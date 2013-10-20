/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2013, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
