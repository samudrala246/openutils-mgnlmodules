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

package net.sourceforge.openutils.mgnlmedia.media.pages;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.module.admininterface.TemplatedMVCHandler;
import info.magnolia.objectfactory.Components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaConfigurationManager;
import net.sourceforge.openutils.mgnlmedia.media.configuration.MediaTypeConfiguration;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Make getMsgs method public
 * @author molaschi
 * @version $Id$
 */
public class MessagesTemplatedMVCHandler extends TemplatedMVCHandler
{

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected TemplateMethodModel getStatic = new GetStaticMethodModel();

    /**
     * @param name
     * @param request
     * @param response
     */
    public MessagesTemplatedMVCHandler(String name, HttpServletRequest request, HttpServletResponse response)
    {
        super(name, request, response);
    }

    private Messages mediaTypesExtendedMsgs;

    /**
     * {@inheritDoc}
     */
    @Override
    public Messages getMsgs()
    {

        if (mediaTypesExtendedMsgs == null)
        {
            Map<String, MediaTypeConfiguration> types = Components
                .getComponent(MediaConfigurationManager.class)
                .getTypes();
            List<String> basenames = new ArrayList<String>();
            basenames.add(getI18nBasename());
            for (MediaTypeConfiguration typeConfig : types.values())
            {
                String basename = typeConfig.getI18nBasename();
                if (!StringUtils.isEmpty(basename) && !basenames.contains(basename))
                {
                    basenames.add(basename);
                }
            }
            mediaTypesExtendedMsgs = MessagesUtil.chain(basenames.toArray(new String[0]));
            super.setMsgs(mediaTypesExtendedMsgs);
        }
        return super.getMsgs();
    }

    /**
     * Override the standard method in order to use request.getParameterMap instead of
     * requestFormUtils.getParamenterMap() since the latter doesn't work with virtual URI
     */
    @SuppressWarnings({"unchecked", "deprecation" })
    @Override
    protected void populateFromRequest(Object bean)
    {
        Map<String, Object> parameters = new HashMap<String, Object>();

        // FIX: use request.getParameterMap instead of requestFormUtils.getParamenterMap()
        parameters.putAll(request.getParameterMap());
        // handle uploaded files too
        parameters.putAll(new info.magnolia.cms.util.RequestFormUtil(this.getRequest()).getDocuments());

        try
        {
            BeanUtils.populate(bean, parameters);
        }
        catch (Exception e)
        {
            log.error("can't set properties on the handler", e);
        }
    }

    /**
     * Returns the getStatic.
     * @return the getStatic
     */
    public TemplateMethodModel getGetStatic()
    {
        return getStatic;
    }

    public static class GetStaticMethodModel implements TemplateMethodModel
    {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings({"cast" })
        public Object exec(List arguments) throws TemplateModelException
        {
            if (arguments != null && arguments.size() > 0)
            {
                BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
                TemplateHashModel staticModel = wrapper.getStaticModels();
                return (TemplateHashModel) staticModel.get((String) arguments.get(0));
            }
            return null;
        }
    }

}