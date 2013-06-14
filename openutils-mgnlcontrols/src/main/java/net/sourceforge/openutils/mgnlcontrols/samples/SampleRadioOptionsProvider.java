package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.dialog.DialogRadioGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleRadioOptionsProvider implements DialogRadioGroup.RadioOptionsProvider
{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SampleRadioOptionsProvider.class);

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getRadioOptions(DialogControl dialogControl)
    {
        Map<String, String> options = new LinkedHashMap<String, String>();
        try
        {
            Node parent = MgnlContext.getJCRSession("website").getRootNode();
            for (Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:page").iterator(); iter.hasNext();)
            {
                Node node = iter.next();
                options.put(node.getIdentifier(), node.getName());
            }
        }
        catch (LoginException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        catch (RepositoryException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        return options;
    }

}
