/**
 *
 * Mobile Module for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnlmobile.html)
 * Copyright(C) 2010-2012, Openmind S.r.l. http://www.openmindonline.it
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

package net.sourceforge.openutils.mgnlmobile.tags;

import info.magnolia.cms.gui.control.Button;
import net.sourceforge.openutils.mgnlmobile.preview.DevicePreview;
import net.sourceforge.openutils.mgnlmobile.preview.MobilePreviewManager;

import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class MobilePreviewButton extends Button
{

    private static final String JS_INCLUDED = "mobileJsPreviewIncluded";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtml()
    {
        StringBuffer sb = new StringBuffer();
        if (this.getRequest().getAttribute(JS_INCLUDED) == null)
        {
            sb
                .append("<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/mootools/1.2.2/mootools-yui-compressed.js\"><!-- --></script>");
            sb.append("<script type=\"text/javascript\" src=\""
                + this.getRequest().getContextPath()
                + "/.resources/mgnlmobile/js/SqueezeBox.js\"><!-- --></script>");
            sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + this.getRequest().getContextPath()
                + "/.resources/mgnlmobile/css/SqueezeBox.css\" />");
            sb.append("<script type=\"text/javascript\" src=\""
                + this.getRequest().getContextPath()
                + "/.resources/mgnlmobile/js/button.js\"><!-- --></script>");
            this.getRequest().setAttribute(JS_INCLUDED, true);
            // sb.append("<div id=\"MB_PREVIEW_OL\" style=\"background: #999;position:absolute;top:0px;left:0px;width:100%;height:100%;z-index:10000;display:none\"></div>");
        }
        sb
            .append("<span class=\"mgnlControlButton\" style=\" cursor: pointer; padding-left:24px !important;  background: transparent url("
                + this.getRequest().getContextPath()
                + "/.resources/mgnlmobile/images/24_iphone.png) no-repeat top left;\" onclick=\"selectPreview()\">Mobile Preview</span>");
// sb.append("<select onchange=\"openPreview(this.options[this.selectedIndex].value)\"><option>---</option>");
// for (DevicePreview device : MobilePreviewManager.getInstance().getDevicesList())
// {
// sb
// .append("<option value=\"")
// .append(device.getDeviceId())
// .append(",")
// .append(device.getPreviewWidth())
// .append(",")
// .append(device.getPreviewHeight())
// .append("\">")
// .append(device.getDeviceId())
// .append("</option>");
// }
// sb.append("</select>");
        sb.append("<div id=\"selectPreview\" style=\"display:none\" >");
        sb
            .append("<h3 style=\"margin:0px; line-height:1.1em;padding-left:30px;background: transparent no-repeat top left url("
                + this.getRequest().getContextPath()
                + "/.resources/mgnlmobile/images/24_iphone.png)\">");
        sb.append("Select device to preview:");
        sb.append("</h3>");
        sb.append("<ul>");
        for (DevicePreview device : MobilePreviewManager.getInstance().getDevicesList())
        {
            String description = StringUtils.isNotBlank(device.getDeviceDescription())
                ? device.getDeviceDescription()
                : device.getDeviceId();
            sb.append("<li>").append("<a href=\"javascript:void(0)\" onclick=\"openPreview('").append(
                device.getDeviceId()).append(",").append(device.getPreviewWidth()).append(",").append(
                device.getPreviewHeight()).append("'); \">").append(description).append("</a></li>");
        }
        sb.append("</ul>");
        sb.append("<a href=\"javascript:void(0)\" class=\"closeButton\" onclick=\"redirectToHome()\" >");
        sb.append("Close preview mode");
        sb.append("</a>");
        sb.append("</div>");

        return sb.toString();
    }
}
