package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;

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
    public DialogRadioGroup.RadioOptions getRadioOptions(int itemsPerPage, int pageNumberStartingFromOne,
        DialogControl dialogControl)
    {
        DialogRadioGroup.RadioOptions options = new DialogRadioGroup.RadioOptions();
        options.setItems(new LinkedHashMap<String, String>());
        options.setMore(false);
        try
        {
            Node parent = MgnlContext.getJCRSession("website").getRootNode();
            Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:page").iterator();
            int start = itemsPerPage > 0 ? Math.max(pageNumberStartingFromOne - 1, 0) * itemsPerPage : 0;
            int i = 0;
            while (i < start && iter.hasNext())
            {
                iter.next();
                i++;
            }
            int end = itemsPerPage > 0 ? start + itemsPerPage : 0;
            while ((end == 0 || i < end) && iter.hasNext())
            {
                Node node = iter.next();
                options.getItems().put(node.getIdentifier(), node.getName());
                i++;
            }
            options.setMore(iter.hasNext());
        }
        catch (RepositoryException e)
        {
            // TODO Auto-generated catch block
            log.error(e.getMessage(), e);
        }
        return options;
    }

}
