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

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.gui.control.File;
import info.magnolia.cms.gui.dialog.DialogFile;
import info.magnolia.cms.gui.misc.Spacer;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.i18n.MessagesUtil;

import java.io.IOException;
import java.io.Writer;

import net.sourceforge.openutils.mgnlmedia.media.tags.el.MediaEl;

import org.apache.commons.lang.StringUtils;


/**
 * Overrides DialogFile and force no preview but the download link.<br/>
 * Renders the list of valid file extensions (specified by "extensions" property) and validates uploaded file against
 * it.
 * @author molaschi
 * @version $Id: $
 */
public class DialogFileNoPreview extends DialogFile
{

    private final String i18nBasename = "net.sourceforge.openutils.mgnlmedia.media.lang.messages";

    private Messages messages;

    public void initImageExtensions()
    {
        // no images, so no preview will be generated
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtml(Writer out) throws IOException
    {
        this.getImageExtensions().clear();
        this.setConfig("preview", false);
        super.drawHtml(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawHtmlPre(Writer out) throws IOException
    {
        super.drawHtmlPre(out);
        String extensions = this.getConfigValue("extensions");
        if (StringUtils.isNotBlank(extensions))
        {
            StringBuffer sb = new StringBuffer();
            sb
                .append("<span class=\"mgnlDialogDescription\">")
                .append(getMessage("dialog.filenopreview.extensions"))
                .append(" ")
                .append(extensions)
                .append("</span>")
                .append(Spacer.getHtml(0, 0));
            out.write(sb.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validate()
    {
        boolean valid = super.validate();
        if (valid)
        {
            MultipartForm form = (MultipartForm) getRequest().getAttribute(MultipartForm.REQUEST_ATTRIBUTE_NAME);
            if (form != null)
            {
                Document doc = form.getDocument(getName());
                if (doc != null)
                {
                    String extensions = this.getConfigValue("extensions");
                    if (StringUtils.isNotBlank(extensions))
                    {
                        String[] allowedExtensions = StringUtils.split(extensions, ',');
                        for (String allowedExtension : allowedExtensions)
                        {
                            if (StringUtils.trimToEmpty(allowedExtension).equalsIgnoreCase(doc.getExtension()))
                            {
                                return true;
                            }

                        }

                        setValidationMessage(getMessage("dialog.filenopreview.error.extension"));
                        return false;
                    }
                }
                // String extensions = doc.getExtension();
            }
        }
        return valid;
    }

    @Override
    protected Messages getMessages()
    {
        if (messages == null)
        {
            // if this is the root
            if (this.getParent() == null)
            {
                messages = MessagesManager.getMessages();
            }
            else
            {
                // try to get it from the control nearest to the root
                messages = super.getMessages();
            }
            // if this control defines a bundle (basename in the terms of jstl)
            String basename = this.getConfigValue("i18nBasename", i18nBasename);
            if (StringUtils.isNotEmpty(basename))
            {
                // extend the chain with this bundle
                messages = MessagesUtil.chain(basename, messages);
            }
        }
        return messages;
    }

    @Override
    protected void writeInnerHtml(Writer out, final boolean showImage, File control, StringBuffer htmlControlFileName,
        String link) throws IOException
    {
        out.write(htmlControlFileName.toString());
        if (!showImage)
        {
            String iconPath = MediaEl.urlres(control.getWebsiteNode(), "16x16");

            out.write(Spacer.getHtml(0, 0));

            out.write("<a href=");
            out.write(link);
            out
                .write(" target=\"_blank\" style=\"color: #000; text-decoration: none; padding-left: 20px; background-image: url(");
            out.write(this.getRequest().getContextPath() + iconPath);
            out.write("); background-position: left; background-repeat: no-repeat\">");

            out.write(control.getFileName() + "." + control.getExtension() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Get the uri of the file (used to show images)
     * @param control
     * @return
     */
    @Override
    protected String getFileURI(File control)
    {
        return "/mediaObject" + control.getHandle();
    }
}
