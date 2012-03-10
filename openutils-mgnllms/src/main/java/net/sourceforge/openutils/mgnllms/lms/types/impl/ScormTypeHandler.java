/**
 *
 * E-learning Module for Magnolia CMS (http://www.openmindlab.com/lab/products/lms.html)
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

package net.sourceforge.openutils.mgnllms.lms.types.impl;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import net.sf.json.JSONSerializer;
import net.sourceforge.openutils.mgnllms.lms.exceptions.CourseException;
import net.sourceforge.openutils.mgnllms.module.LmsTypesManager;
import net.sourceforge.openutils.mgnllms.scorm.model.CommonDataItemOrganization;
import net.sourceforge.openutils.mgnllms.scorm.model.Item;
import net.sourceforge.openutils.mgnllms.scorm.model.Manifest;
import net.sourceforge.openutils.mgnllms.scorm.model.Organization;
import net.sourceforge.openutils.mgnllms.scorm.utils.JaxbUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 */
public class ScormTypeHandler extends BaseTypeHandler
{

    public static String key = "scorm";

    private String courseName = "";

    private Manifest manifest;

    public static final String DATAMODEL_NODEDATA = "data_model";

    public static final String ADLDATA_NODEDATA = "adl_data";

    public static final String ACTIVITIES_NODEDATA = "activities";

    public static final String USER_REPORT_TEMPLATE = "user-report";

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(ScormTypeHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Content typeDefinitionNode)
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Content node)
    {
        NodeData file = node.getNodeData(key);
        String fileName = file.getAttribute(FileProperties.PROPERTY_FILENAME)
            + "."
            + file.getAttribute(FileProperties.EXTENSION);

        return LmsTypesManager.getInstance().getURIMappingPrefix() + node.getHandle() + "/" + key + "/" + fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl(Content node, Map<String, String> options)
    {
        return getUrl(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename(Content node)
    {
        NodeData file = node.getNodeData("zip");
        return file.getAttribute(FileProperties.PROPERTY_FILENAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullFilename(Content node)
    {
        NodeData file = node.getNodeData("zip");
        String ext = file.getAttribute(FileProperties.PROPERTY_EXTENSION);
        return getFilename(node) + "." + ext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPostSave(Content node, MultipartForm form, HttpServletRequest request)
    {
        try
        {
            byte[] imsmanifest = (byte[]) request.getAttribute("imsmanifest");
            if (imsmanifest != null)
            {
                if (node.hasNodeData("imsmanifest"))
                {
                    node.deleteNodeData("imsmanifest");
                }
                NodeData nd = NodeDataUtil.getOrCreate(node, "imsmanifest", PropertyType.BINARY);
                ByteArrayInputStream bais = new ByteArrayInputStream(imsmanifest);
                nd.setValue(bais);
                request.removeAttribute("imsmanifest");
                nd.setAttribute(FileProperties.PROPERTY_EXTENSION, "xml");
                nd.setAttribute(FileProperties.PROPERTY_SIZE, String.valueOf(imsmanifest.length));
                nd.setAttribute(FileProperties.PROPERTY_FILENAME, "imsmanifest");
                nd.setAttribute(FileProperties.PROPERTY_MIMETYPE, "text/xml");
                nd.setAttribute(FileProperties.PROPERTY_CONTENTTYPE, "text/xml");
                nd.setAttribute(FileProperties.PROPERTY_LASTMODIFIED, Calendar.getInstance());

                bais.reset();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copy(bais, baos);
                NodeDataUtil.getOrCreateAndSet(node, "manifestStr", new String(baos.toByteArray()));
                IOUtils.closeQuietly(bais);

                for (Organization organization : this.manifest.getOrganizations().getOrganization())
                {
                    getCmiFromManifest(node, organization);
                }
            }

            if (!node.hasNodeData("title"))
            {
                Manifest manifest = (Manifest) request.getAttribute("manifest");

                this.courseName = getCourseNameFromManifest(manifest);
                if (this.courseName != null)
                {
                    NodeDataUtil.getOrCreateAndSet(node, "title", this.courseName);
                }
            }

            node.getMetaData().setTemplate(USER_REPORT_TEMPLATE);

            node.save();
        }
        catch (RepositoryException ex)
        {
            log.error(ex.getMessage(), ex);
            return false;
        }
        catch (IOException ex)
        {
            log.error(ex.getMessage(), ex);
            return false;
        }
        catch (CourseException e)
        {
            log.error(e.getMessage(), e);
            return false;
        }

        return super.onPostSave(node, form, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNewNodeName(Content parent, MultipartForm form, HttpServletRequest request)
    {
        File temp = null;
        InputStream zipStream = form.getDocument("zip").getStream();
        try
        {
            temp = File.createTempFile("zipscorm", ".zip");
            FileOutputStream fos = new FileOutputStream(temp);
            IOUtils.copy(zipStream, fos);

            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(zipStream);

            ZipFile zip = new ZipFile(temp);
            Enumeration< ? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();

                String path = entry.getName();

                if ("imsmanifest.xml".equals(path))
                {

                    InputStream inputStream = zip.getInputStream(entry);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(inputStream, baos);

                    IOUtils.closeQuietly(inputStream);

                    request.setAttribute("imsmanifest", baos.toByteArray());

                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    try
                    {

                        this.manifest = JaxbUtils.unmarshal(Manifest.class, bais);
                        request.setAttribute("manifest", this.manifest);

                        // TODO: parsing activity per salvataggio dei valori base di cmi

                        if (StringUtils.isNotBlank(request.getParameter("title")))
                        {
                            return request.getParameter("title");
                        }

                        String name = getCourseNameFromManifest(this.manifest);
                        if (name == null)
                        {
                            name = form.getDocument("zip").getFileName();
                        }
                        return name;
                    }
                    finally
                    {
                        IOUtils.closeQuietly(bais);
                    }
                }
            }
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
        catch (JAXBException e)
        {
            log.error(e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(zipStream);
            if (temp != null)
            {
                temp.delete();
            }
        }
        return super.getNewNodeName(parent, form, request);
    }

    private String getCourseNameFromManifest(Manifest manifest)
    {
        if (manifest.getOrganizations().getOrganization() != null
            && manifest.getOrganizations().getOrganization().size() > 0
            && manifest.getOrganizations().getOrganization().get(0).getTitle() != null)
        {
            return manifest.getOrganizations().getOrganization().get(0).getTitle();
        }
        if (manifest.getOrganizations().getOrganization().size() > 0)
        {
            return manifest.getOrganizations().getOrganization().get(0).getIdentifier();
        }
        return UUID.randomUUID().toString();
    }

    private void getCmiFromManifest(Content node, CommonDataItemOrganization item) throws CourseException
    {
        if (item.getItem().isEmpty())
        {
            Map<String, String> tempObject = new HashMap<String, String>();
            if (item.getCompletionThreshold() != null && item.getCompletionThreshold().isCompletedByMeasure())
            {
                tempObject
                    .put("completion_threshold", item.getCompletionThreshold().getMinProgressMeasure().toString());
            }

            if (item.getDataFromLMS() != null)
            {
                tempObject.put("launch_data", item.getDataFromLMS());
            }

            if (item.getSequencing() != null
                && item.getSequencing().getLimitConditions() != null
                && item.getSequencing().getLimitConditions().getAttemptAbsoluteDurationLimit() != null)
            {
                tempObject.put("max_time_allowed", item
                    .getSequencing()
                    .getLimitConditions()
                    .getAttemptAbsoluteDurationLimit()
                    .toString());
            }

            if (item.getSequencing() != null
                && item.getSequencing().getObjectives() != null
                && item.getSequencing().getObjectives().getPrimaryObjective().isSatisfiedByMeasure())
            {
                tempObject.put("scaled_passing_score", (item
                    .getSequencing()
                    .getObjectives()
                    .getPrimaryObjective()
                    .getMinNormalizedMeasure() != null) ? item
                    .getSequencing()
                    .getObjectives()
                    .getPrimaryObjective()
                    .getMinNormalizedMeasure()
                    .toString() : "1.0");
            }

            if (item.getTimeLimitAction() != null)
            {
                tempObject.put("time_limit_action", item.getTimeLimitAction().value());
            }
            tempObject.put("_version", "1.0");

            Content itemContent;
            try
            {
                String json = JSONSerializer.toJSON(tempObject).toString();
                Content activities = ContentUtil.getOrCreateContent(node, ACTIVITIES_NODEDATA, ItemType.CONTENTNODE);
                itemContent = activities.createContent(
                    Path.getValidatedLabel(item.getIdentifier()),
                    ItemType.CONTENTNODE);
                NodeDataUtil.getOrCreateAndSet(itemContent, DATAMODEL_NODEDATA, json.substring(1, json.length() - 1));
                NodeDataUtil.getOrCreateAndSet(itemContent, "title", item.getTitle());
            }
            catch (RepositoryException e)
            {
                throw new CourseException(e);
            }

            if (item.getData() != null)
            {
                try
                {
                    Content adldata = itemContent.createContent(ADLDATA_NODEDATA, ItemType.CONTENTNODE);
                    for (net.sourceforge.openutils.mgnllms.scorm.model.cp.Map map : item.getData().getMap())
                    {
                        Content mapContent = adldata.createContent(map.getTargetID(), ItemType.CONTENTNODE);
                        NodeDataUtil.getOrCreateAndSet(mapContent, "readSharedData", map.isReadSharedData());
                        NodeDataUtil.getOrCreateAndSet(mapContent, "writeSharedData", map.isWriteSharedData());
                    }
                    node.save();
                }
                catch (RepositoryException e)
                {
                    throw new CourseException(e);
                }
            }
        }
        else
        {
            for (Item childItem : item.getItem())
            {
                getCmiFromManifest(node, childItem);
            }
        }
    }
}
