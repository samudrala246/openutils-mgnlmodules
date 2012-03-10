/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmedia.media.dialog;

import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.gui.misc.Sources;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;


/**
 * This dialog allow to popup the dialog as an Ext js layer
 * @author molaschi
 * @version $Id$
 */
public class LayerDialog extends Dialog
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawHtmlPostSubsButtons(Writer out) throws IOException
    {
        Messages msgs = MessagesManager.getMessages();

        out.write("<div class=\"" + CssConstants.CSSCLASS_TABSETSAVEBAR + "\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

        Button save = new Button();
        String saveOnclick = this.getConfigValue("saveOnclick", "mgnlDialogFormSubmit();");
        String saveLabel = this.getConfigValue("saveLabel", msgs.get("buttons.save"));
        if (StringUtils.isNotEmpty(saveOnclick) && StringUtils.isNotEmpty("saveLabel"))
        {
            save.setOnclick(saveOnclick);
            save.setLabel(saveLabel);
            out.write(save.getHtml());
        }
        Button cancel = new Button();
        cancel.setOnclick(this.getConfigValue("cancelOnclick", "parent.closeLayer();")); //$NON-NLS-1$ //$NON-NLS-2$
        cancel.setLabel(this.getConfigValue("cancelLabel", msgs.get("buttons.cancel"))); //$NON-NLS-1$ //$NON-NLS-2$
        out.write(cancel.getHtml());

        out.write("</div>\n"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawHtmlPreSubsHead(Writer out) throws IOException
    {
        out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"); // kupu
        // //$NON-NLS-1$
        out.write("<title>" //$NON-NLS-1$
            + this.getMessage(this.getConfigValue("label", MessagesManager.get("dialog.editTitle"))) //$NON-NLS-1$ //$NON-NLS-2$
            + "</title>\n"); //$NON-NLS-1$
        out.write(new Sources(this.getRequest().getContextPath()).getHtmlJs());
        out.write(new Sources(this.getRequest().getContextPath()).getHtmlCss());
        out.write("<script type=\"text/javascript\">\n"); //$NON-NLS-1$

        // out.write("window.onresize = eventHandlerOnResize;\n"); //$NON-NLS-1$
        out.write("parent.setLayerTitle('"
            + this.getMessage(this.getConfigValue("label", MessagesManager.get("dialog.editTitle")))
            + "');\n");
        out.write("parent.resizeTo(" //$NON-NLS-1$
            + this.getConfigValue("width", DIALOGSIZE_NORMAL_WIDTH) //$NON-NLS-1$
            + "," //$NON-NLS-1$
            + this.getConfigValue("height", DIALOGSIZE_NORMAL_HEIGHT) //$NON-NLS-1$
            + ");\n"); //$NON-NLS-1$
        out.write("</script>\n"); //$NON-NLS-1$

        this.drawJavascriptSources(out);
        this.drawCssSources(out);
    }

}
