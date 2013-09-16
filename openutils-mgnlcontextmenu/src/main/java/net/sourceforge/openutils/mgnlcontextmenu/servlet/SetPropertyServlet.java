package net.sourceforge.openutils.mgnlcontextmenu.servlet;

import info.magnolia.cms.security.Permission;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SetPropertyServlet extends HttpServlet
{

    private Logger log = LoggerFactory.getLogger(SetPropertyServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String path = req.getParameter("path");
        String name = req.getParameter("name");
        String value = req.getParameter("value");
        try
        {
            Node node = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).getNode(path);
            if (NodeUtil.isGranted(node, Permission.SET))
            {
                node.setProperty(name, value);
                MgnlContext.getJCRSession(RepositoryConstants.WEBSITE).save();
            }
        }
        catch (RepositoryException e)
        {
            log.error(e.getMessage(), e);
        }
    }
}
