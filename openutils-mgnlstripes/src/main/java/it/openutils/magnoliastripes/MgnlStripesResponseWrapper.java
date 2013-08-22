/**
 *
 * Stripes module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlstripes.html)
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

package it.openutils.magnoliastripes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * A response wrapper for the Stripes paragraph (replace the standard writer with the Magnolia one).
 * @author fgiust
 * @version $Id: StripesParagraphResponseWrapper.java 10833 2008-09-15 15:39:08Z fgiust $
 */
class MgnlStripesResponseWrapper extends HttpServletResponseWrapper
{

    /**
     * Writer that should be used for output.
     */
    private PrintWriter out;

    /**
     * @param response HttpServletResponse
     * @param out writer guven by Magnolia.
     */
    public MgnlStripesResponseWrapper(HttpServletResponse response, Writer out)
    {
        super(response);
        this.out = new PrintWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter() throws IOException
    {
        return out;
    }

}