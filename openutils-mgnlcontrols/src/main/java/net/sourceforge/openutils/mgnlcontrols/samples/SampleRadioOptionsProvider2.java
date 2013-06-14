package net.sourceforge.openutils.mgnlcontrols.samples;

import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.jcr.util.NodeUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, String> getRadioOptions(DialogControl dialogControl)
    {
        Map<String, String> options = new LinkedHashMap<String, String>();
        List<String> treePathValues = ((DialogDependentSelectListAndRadioGroup) dialogControl)
            .getDependentSelectList()
            .getTreePathValues();
        if (!treePathValues.isEmpty())
        {
            try
            {
                Node parent = NodeUtil.getNodeByIdentifier("config", treePathValues.get(treePathValues.size() - 1));
                for (Iterator<Node> iter = NodeUtil.getNodes(parent, "mgnl:contentNode").iterator(); iter.hasNext();)
                {
                    Node node = iter.next();
                    options.put(node.getIdentifier(), node.getName());
                }
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
