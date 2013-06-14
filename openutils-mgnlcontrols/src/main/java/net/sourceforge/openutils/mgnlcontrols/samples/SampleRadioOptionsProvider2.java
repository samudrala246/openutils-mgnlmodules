package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.jcr.util.NodeUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlcontrols.dialog.DialogDependentSelectListAndRadioGroup;
import net.sourceforge.openutils.mgnlcontrols.dialog.DialogRadioGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author diego
 * @version $Id: $
 */
public class SampleRadioOptionsProvider2 implements DialogRadioGroup.RadioOptionsProvider
{

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SampleRadioOptionsProvider2.class);

    /**
     * {@inheritDoc}
     */
    public DialogRadioGroup.RadioOptions getRadioOptions(int itemsPerPage, int pageNumberStartingFromOne,
        DialogControl dialogControl)
    {
        DialogRadioGroup.RadioOptions options = new DialogRadioGroup.RadioOptions();
        options.setItems(new LinkedHashMap<String, String>());
        options.setMore(false);
        List<String> treePathValues = ((DialogDependentSelectListAndRadioGroup) dialogControl)
            .getDependentSelectList()
            .getTreePathValues();
        if (!treePathValues.isEmpty())
        {
            try
            {
                Node parent = NodeUtil.getNodeByIdentifier("config", treePathValues.get(treePathValues.size() - 1));
                Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:contentNode").iterator();
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
                }
                options.setMore(iter.hasNext());
            }
            catch (RepositoryException e)
            {
                // TODO Auto-generated catch block
                log.error(e.getMessage(), e);
            }
        }
        return options;
    }

}
