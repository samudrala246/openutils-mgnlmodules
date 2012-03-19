package it.openutils.magnoliastripes.samples;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleStripesActionBean implements ActionBean
{

    private ActionBeanContext context;

    private String text;

    /**
     * {@inheritDoc}
     */
    public void setContext(ActionBeanContext context)
    {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    public ActionBeanContext getContext()
    {
        return context;
    }

    /**
     * Returns the text.
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    @DefaultHandler
    public Resolution show()
    {
        return new ForwardResolution("/templates/samples-stripes/paragraph.jsp");
    }
}
