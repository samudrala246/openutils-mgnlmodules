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

package net.sourceforge.openutils.mgnlmedia.media.dialog;

import info.magnolia.cms.beans.config.MIMEMapping;
import info.magnolia.cms.gui.control.File;
import info.magnolia.cms.gui.dialog.DialogFile;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.gui.misc.Spacer;
import info.magnolia.objectfactory.Components;

import java.io.IOException;
import java.io.Writer;

import javax.jcr.PropertyType;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;

import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 * @version Id:
 */
@SuppressWarnings("deprecation")
public class DialogFileAudio extends DialogFile
{

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initImageExtensions()
    {
        String extensions = this.getConfigValue("extensions");
        for (String extension : StringUtils.split(extensions, ","))
        {
            this.getImageExtensions().add(extension);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtml(Writer out) throws IOException
    {
        File control = getFileControl();
        control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING)); //$NON-NLS-1$
        control.setSaveInfo(false); // set manualy below
        control.setCssClass(CssConstants.CSSCLASS_FILE);
        control.setCssClassFileName(CssConstants.CSSCLASS_EDIT);
        control.setCssStyles("width", this.getConfigValue("width", "100%")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        this.drawHtmlPre(out);

        String width = this.getConfigValue("width", "100%"); //$NON-NLS-1$ //$NON-NLS-2$

        final boolean preview = Boolean.valueOf(getConfigValue("preview", "true")).booleanValue();
        final boolean extensionIsDisplayableImage = this.getImageExtensions().contains(
            control.getExtension().toLowerCase());
        final boolean showImage = extensionIsDisplayableImage && preview;

        String htmlControlBrowse = control.getHtmlBrowse();
        StringBuffer htmlControlFileName = new StringBuffer();
        htmlControlFileName.append("<span class=\"" //$NON-NLS-1$
            + CssConstants.CSSCLASS_DESCRIPTION
            + "\">" //$NON-NLS-1$
            + getMessage("dialog.file.filename") //$NON-NLS-1$
            + "</span>"); //$NON-NLS-1$
        htmlControlFileName.append(Spacer.getHtml(1, 1));
        htmlControlFileName.append(control.getHtmlFileName() + "<span id=\"" //$NON-NLS-1$
            + this.getName()
            + "_fileNameExtension\">." //$NON-NLS-1$
            + control.getExtension()
            + "</span>"); //$NON-NLS-1$
        String htmlContentEmpty = htmlControlBrowse + Spacer.getHtml(0, 0) + htmlControlFileName;
        out.write("<div id=\"" + this.getName() + "_contentDiv\" style=\"width:100%;\">"); //$NON-NLS-1$ //$NON-NLS-2$
        boolean exists = false;

        if (this.getStorageNode() != null)
        {
            exists = this.getStorageNode().getNodeData(this.getName()).isExist();
        }

        if (!exists)
        {
            out.write(htmlContentEmpty);
            out.write("</div>"); //$NON-NLS-1$
        }
        else
        {
            if (showImage)
            {

                out.write("\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"" + width + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
                out.write("<tr><td class=\"" + CssConstants.CSSCLASS_FILEIMAGE + "\">"); //$NON-NLS-1$ //$NON-NLS-2$

                out.write("\n<div id=\"container");
                out.write(this.getName());
                out
                    .write("\"><a href=\"http://www.macromedia.com/go/getflashplayer\">Get the Flash Player</a> to see this player.</div>");

                out.write("<script type=\"text/javascript\" src=\"");
                out.write(this.getRequest().getContextPath());
                out.write("/.resources/media/js/swfobject.js\"></script>");
                out.write("\n<script type=\"text/javascript\" src=\"");
                out.write(this.getRequest().getContextPath());
                out.write("/.resources/media/js/swfobject.js\"></script>");

                out.write("\n<script type=\"text/javascript\">");
                out.write("\nvar s1 = new SWFObject(\"");
                out.write(this.getRequest().getContextPath());
                out.write("/.resources/media/players/player.swf\",\"ply\",\"320\",\"20\",\"9\",\"#FFFFFF\");");
                out.write("\ns1.addParam(\"flashvars\",\"file=");
                out.write(this.getRequest().getContextPath());
                out.write(Components.getComponent(MediaConfigurationManager.class).getURIMappingPrefix());
                out.write(getFileURI(control));
                out.write("/");
                out.write(control.getFileName());
                out.write(".mp3");
                out.write("\");");
                out.write("\ns1.write(\"container");
                out.write(this.getName());

                out.write("\");\n</script>");

                /*
                 * // flash movie out.write("<object type=\"application/x-shockwave-flash\" data=\"");
                 * out.write(this.getRequest().getContextPath()); out.write("/.resources/media/players/player.swf");
                 * out.write("\" title=\""); out.write(control.getFileName()); out.write("\" ");
                 * out.write("width=\"320\" "); out.write("height=\"30\" "); out.write(">"); out.write("<param
                 * name=\"movie\" value=\""); out.write(this.getRequest().getContextPath());
                 * out.write("/.resources/media/players/player.swf"); out.write("\"/>"); out.write("<param
                 * name=\"flashvars\" value=\"file="); out.write(this.getRequest().getContextPath());
                 * out.write(getFileURI(control)); out.write("\"/>"); out.write("</object>\n");
                 */
                out.write("</td></tr><tr><td>"); //$NON-NLS-1$

            }
            out.write(htmlControlFileName.toString());
            if (!showImage)
            {
                String iconPath = MIMEMapping.getMIMETypeIcon(control.getExtension());

                out.write(Spacer.getHtml(0, 0));
                out.write("<a href=" + this.getRequest().getContextPath() + control.getPath() + " target=\"_blank\">"); //$NON-NLS-1$ //$NON-NLS-2$
                out.write("<img src=\"" //$NON-NLS-1$
                    + this.getRequest().getContextPath()
                    + iconPath
                    + "\" class=\"" //$NON-NLS-1$
                    + CssConstants.CSSCLASS_FILEICON
                    + "\" border=\"0\">"); //$NON-NLS-1$
                out.write(control.getFileName() + "." + control.getExtension() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.write(Spacer.getHtml(12, 12));
            out.write(control.getHtmlRemove("mgnlDialogFileRemove('" + this.getName() + "');")); //$NON-NLS-1$ //$NON-NLS-2$
            if (showImage)
            {
                out.write("</td></tr></table>"); //$NON-NLS-1$
            }
            out.write("</div>\n"); //$NON-NLS-1$
            out.write("<div style=\"position:absolute;top:-500px;left:-500px;visibility:hidden;\">\n<textarea id=\""); //$NON-NLS-1$
            out.write(this.getName());
            out.write("_contentEmpty\">");
            out.write(htmlContentEmpty);

            // @todo should be escaped, but we need to test it
            // out.write(StringEscapeUtils.escapeXml(htmlContentEmpty));
            out.write("</textarea>\n</div>\n"); //$NON-NLS-1$
        }
        control.setSaveInfo(true);
        out.write(control.getHtmlSaveInfo());
        this.drawHtmlPost(out);
    }
}
