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
public class DialogFileImage extends DialogFile
{

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initImageExtensions()
    {
        this.getImageExtensions().add("jpg");
        this.getImageExtensions().add("jpeg");
        this.getImageExtensions().add("gif");
        this.getImageExtensions().add("png");
        this.getImageExtensions().add("bmp");
        this.getImageExtensions().add("ico");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtml(Writer out) throws IOException
    {
        File control = getFileControl();
        control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING));
        control.setSaveInfo(false); // set manualy below
        control.setCssClass(CssConstants.CSSCLASS_FILE);
        control.setCssClassFileName(CssConstants.CSSCLASS_EDIT);
        control.setCssStyles("width", this.getConfigValue("width", "100%")); //$NON-NLS-3$

        this.drawHtmlPre(out);

        String width = this.getConfigValue("width", "100%");

        final boolean preview = Boolean.valueOf(getConfigValue("preview", "true")).booleanValue();
        final boolean extensionIsDisplayableImage = this.getImageExtensions().contains(
            control.getExtension().toLowerCase());
        final boolean showImage = extensionIsDisplayableImage && preview;

        String htmlControlBrowse = control.getHtmlBrowse();
        StringBuffer htmlControlFileName = new StringBuffer();
        htmlControlFileName.append("<span class=\""
            + CssConstants.CSSCLASS_DESCRIPTION
            + "\">"
            + getMessage("dialog.file.filename")
            + "</span>");
        htmlControlFileName.append(Spacer.getHtml(1, 1));
        htmlControlFileName.append(control.getHtmlFileName()
            + "<span id=\""
            + this.getName()
            + "_fileNameExtension\">."
            + control.getExtension()
            + "</span>");
        String htmlContentEmpty = htmlControlBrowse + Spacer.getHtml(0, 0) + htmlControlFileName;
        out.write("<div id=\"" + this.getName() + "_contentDiv\" style=\"width:100%;\">");
        boolean exists = false;

        if (this.getStorageNode() != null)
        {
            exists = this.getStorageNode().getNodeData(this.getName()).isExist();
        }

        if (!exists)
        {
            out.write(htmlContentEmpty);
            out.write("</div>");
        }

        if (exists && showImage)
        {
            out.write("\n<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"" + width + "\"><tr><td>");
        }

        if (exists)
        {
            out.write(htmlControlFileName.toString());

            if (!showImage)
            {
                String iconPath = MIMEMapping.getMIMETypeIcon(control.getExtension());

                out.write(Spacer.getHtml(0, 0));
                out.write("<a href=" + this.getRequest().getContextPath() + control.getPath() + " target=\"_blank\">");
                out.write("<img src=\""
                    + this.getRequest().getContextPath()
                    + iconPath
                    + "\" class=\""
                    + CssConstants.CSSCLASS_FILEICON
                    + "\" border=\"0\">");
                out.write(this.getRequest().getContextPath() + this.getPreviewUrl() + "</a>");
            }
            out.write(Spacer.getHtml(12, 12));
            out.write(control.getHtmlRemove("mgnlDialogFileRemove('" + this.getName() + "');"));

            if (showImage)
            {
                out.write("</td></tr><tr><td class=\"" + CssConstants.CSSCLASS_FILEIMAGE + "\">");

                // standard image

                // todo: image thumbnail template
                // out.write("<img src=\""+ this.getRequest().getContextPath()
                // +THUMB_PATH+"?src="+control.getHandle()+"\"
                // class=\""+CSSCLASS_FILEIMAGE+"\">");
                // tmp workaround: resize in html ...

                int imgwidth = 150;
                int imgheight = 150;

                try
                {
                    imgwidth = Integer.parseInt(control.getImageWidth());
                    imgheight = Integer.parseInt(control.getImageHeight());
                }
                catch (NumberFormatException e)
                {
                    // ignore (is 150)
                }
                int bigger = Math.max(imgwidth, imgheight);
                if (bigger > 150)
                {
                    imgwidth = 150;
                }

                out.write("<p><a href=\"");
                out.write(this.getRequest().getContextPath());
                out.write(MediaConfigurationManager.getInstance().getURIMappingPrefix());
                out.write(this.getFileURI(control));
                out.write("/" + control.getFileName() + "." + control.getExtension());
                out.write("\" target=\"_blank\">");
                out.write("<img border=\"0\" src=\"");
                out.write(this.getRequest().getContextPath());
                out.write(this.getPreviewUrl());
                out.write("\" class=\"");
                out.write(CssConstants.CSSCLASS_FILEIMAGE);
                out.write("\" alt=\"");
                out.write(control.getFileName());
                out.write("\" title=\"");
                out.write(control.getFileName());
                out.write("\" /></a></p>\n");

                if (StringUtils.isNotEmpty(control.getImageWidth()))
                {
                    out.write("<p><em style='white-space:nowrap'>");

                    out.write(control.getImageWidth());

                    out.write(" x ");
                    out.write(control.getImageHeight());

                    out.write("</em></p>\n");
                }

                if (StringUtils.isNotEmpty(control.getImageWidth()))
                {
                    out.write("<p><em style='white-space:nowrap'>");

                    int w = Integer.parseInt(control.getImageWidth());
                    int h = Integer.parseInt(control.getImageHeight());
                    if (w > h)
                    {
                        out.write("landscape");
                    }
                    else if (h > w)
                    {
                        out.write("portrait");
                    }
                    else
                    {
                        out.write("square");
                    }
                    out.write("</em></p>\n");
                }

                out.write("</td></tr></table>");
            }
        }
        out.write("</div>\n");
        out.write("<div style=\"position:absolute;top:-500px;left:-500px;visibility:hidden;\">\n<textarea id=\"");
        out.write(this.getName());
        out.write("_contentEmpty\">");
        out.write(htmlContentEmpty);

        // @todo should be escaped, but we need to test it
        // out.write(StringEscapeUtils.escapeXml(htmlContentEmpty));
        out.write("</textarea>\n</div>\n");

        control.setSaveInfo(true);
        out.write(control.getHtmlSaveInfo());
        this.drawHtmlPost(out);
    }

    private String getPreviewUrl()
    {
        return MediaConfigurationManager.getInstance().getURIMappingPrefix()
            + this.getStorageNode().getHandle()
            + "/resolutions/thumbnail/"
            + this.getStorageNode().getName()
            + ".jpg";
    }
}
