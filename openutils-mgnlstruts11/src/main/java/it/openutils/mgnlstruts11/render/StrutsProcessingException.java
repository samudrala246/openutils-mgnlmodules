/**
 *
 * Struts 1.1 module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstruts.html)
 * Copyright(C) ${project.inceptionYear}-2013, Openmind S.r.l. http://www.openmindonline.it
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

package it.openutils.mgnlstruts11.render;

/**
 * @author fgiust
 * @version $Id$
 */
public class StrutsProcessingException extends RuntimeException
{

    /**
     *
     */
    private static final long serialVersionUID = 42L;

    /**
     * @param message
     * @param cause
     */
    public StrutsProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message
     */
    public StrutsProcessingException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public StrutsProcessingException(Throwable cause)
    {
        super(cause);
    }

}
