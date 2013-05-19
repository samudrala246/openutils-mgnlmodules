package it.openutils.mgnlutils.setup;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import it.openutils.mgnlutils.el.NodeElResolver;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author fgiust
 * @version $Id$
 */
public class MgnlUtilsModule implements ModuleLifecycle
{

    @Inject
    private ServletContext servletContext;

    private Logger log = LoggerFactory.getLogger(MgnlUtilsModule.class);

    /**
     * {@inheritDoc}
     */
    public void start(ModuleLifecycleContext moduleLifecycleContext)
    {
        log.warn("Adding EL resolver for javax.jcr.Node");
        JspFactory.getDefaultFactory().getJspApplicationContext(servletContext).addELResolver(new NodeElResolver());
    }

    /**
     * {@inheritDoc}
     */
    public void stop(ModuleLifecycleContext moduleLifecycleContext)
    {
        // TODO Auto-generated method stub

    }

}
