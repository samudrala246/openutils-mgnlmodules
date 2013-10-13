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

package net.sourceforge.openutils.mgnlmedia.media.crop;
 
import info.magnolia.context.MgnlContext;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class PzcServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = -1763781795315878360L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/plain");
        String command = req.getParameter("command");
        final int zoom = NumberUtils.toInt(req.getParameter("zoom"));
        final int x = NumberUtils.toInt(req.getParameter("x"));
        final int y = NumberUtils.toInt(req.getParameter("y"));
        final String id = req.getParameter("id");
        String handle = req.getParameter("handle");
        String repository = req.getParameter("repository");
        if (StringUtils.isBlank(repository))
        {
            repository = RepositoryConstants.WEBSITE;
        }

        try
        {
            Session hm = MgnlContext.getJCRSession(repository);
            final Node node = hm.getNode(handle);
            if ("delete".equals(command))
            {
                if (node != null && node.hasProperty(id))
                {
                    node.getProperty(id).remove();
                }
                hm.save();
                resp.getWriter().println("true");
            }
            else
            {
                final String systemRepository = repository;
                MgnlContext.doInSystemContext(new MgnlContext.Op<Void, RepositoryException>()
                {

                    public Void exec() throws RepositoryException
                    {
                        Session session = MgnlContext.getJCRSession(systemRepository);
                        try
                        {
                            Node systemNode = session.getNode(node.getPath());
                            systemNode.setProperty(id, new StringBuffer()
                                .append(zoom)
                                .append("|")
                                .append(x)
                                .append("|")
                                .append(y)
                                .toString());
                            systemNode.getSession().save();
                        }
                        catch (RepositoryException ex)
                        {
                        }
                        return null;
                    }
                },
                    true);
                resp.getWriter().println("true");
            }
        }
        catch (RepositoryException ex)
        {
            resp.getWriter().println("false");
        }

        resp.flushBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req, resp);
    }

}
