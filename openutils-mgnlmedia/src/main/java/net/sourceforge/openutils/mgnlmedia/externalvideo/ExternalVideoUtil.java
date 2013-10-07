package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.Document;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.admininterface.SaveHandlerImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;
import net.sourceforge.openutils.mgnlmedia.media.types.impl.MediaWithPreviewImageTypeHandler;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class ExternalVideoUtil
{

    public static final String ND_STATUS = "status";

    public static final String ND_STATUS_CHANGE = "statusLastModified";

    public static final String ND_ERROR = "error";

    public static final String ND_ERROR_MESSAGE = "errorMessage";

    public static final String ND_PROGRESS = "uploadProgress";

    public static Logger log = LoggerFactory.getLogger(ExternalVideoUtil.class);

    public static boolean setProperty(final String mediaUUID, final String property, final Object value)
    {
        try
        {
            return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RepositoryException>()
            {

                public Boolean exec() throws RepositoryException
                {
                    Node media = NodeUtil.getNodeByIdentifier(MediaModule.REPO, mediaUUID);
                    if (media != null)
                    {
                        PropertyUtil.setProperty(media, property, value);
                        media.save();
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    public static boolean removeProperty(final String mediaUUID, final String property)
    {
        try
        {
            return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RepositoryException>()
            {

                public Boolean exec() throws RepositoryException
                {
                    Node media = NodeUtil.getNodeByIdentifier(MediaModule.REPO, mediaUUID);
                    if (media != null && media.hasProperty(property))
                    {
                        media.getProperty(property).remove();
                        media.save();
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    public static String getStatusProperty(String statusSuffix)
    {
        return StringUtils.join(new String[]{ND_STATUS, statusSuffix != null ? "-" : null, statusSuffix });
    }

    public static String getErrorProperty(String suffix)
    {
        return StringUtils.join(new String[]{ND_ERROR, suffix != null ? "-" : null, suffix });
    }

    public static String getStatus(final Content mediaNode, String statusSuffix)
    {
        return NodeDataUtil.getString(mediaNode, getStatusProperty(statusSuffix));
    }

    public static boolean setStatus(final String mediaUUID, final String statusSuffix, final String status)
    {
        try
        {
            return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RepositoryException>()
            {

                public Boolean exec() throws RepositoryException
                {
                    Content media = ContentUtil.getContentByUUID(MediaModule.REPO, mediaUUID);
                    if (media != null)
                    {
                        String statusProperty = getStatusProperty(statusSuffix);
                        if (!media.hasNodeData(statusProperty)
                            || !StringUtils.equals(NodeDataUtil.getString(media, statusProperty), status))
                        {
                            NodeDataUtil.getOrCreateAndSet(media, statusProperty, status);
                            NodeDataUtil.getOrCreateAndSet(
                                media,
                                statusProperty + "LastModified",
                                Calendar.getInstance());
                            media.save();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    public static boolean hasStatus(final Content mediaNode, final String statusSuffix, final String status)
    {
        String statusProperty = getStatusProperty(statusSuffix);
        return StringUtils.equals(NodeDataUtil.getString(mediaNode, statusProperty), status);
    }

    public static boolean hasStatus(final String mediaUUID, final String statusSuffix, final String status)
    {
        try
        {
            return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RepositoryException>()
            {

                public Boolean exec() throws RepositoryException
                {
                    Content media = ContentUtil.getContentByUUID(MediaModule.REPO, mediaUUID);
                    return media != null && hasStatus(media, statusSuffix, status);
                }
            });
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    public static boolean setStatus(final String mediaUUID, final String status)
    {
        return setStatus(mediaUUID, null, status);
    }

    public static boolean setError(final String mediaUUID, final String suffix, final String message)
    {
        try
        {
            return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, RepositoryException>()
            {

                public Boolean exec() throws RepositoryException
                {
                    Content media = ContentUtil.getContentByUUID(MediaModule.REPO, mediaUUID);
                    if (media != null)
                    {
                        NodeDataUtil.getOrCreateAndSet(media, getErrorProperty(suffix), true);
                        NodeDataUtil.getOrCreateAndSet(
                            media,
                            getErrorProperty(suffix) + "-" + ND_ERROR_MESSAGE,
                            message);
                        media.save();
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (RepositoryException e)
        {
            return false;
        }
    }

    public static boolean copyPreviewImageToRepository(Node media, final String previewUrl) throws IOException
    {
        if (StringUtils.isNotBlank(previewUrl))
        {
            InputStream is = null;
            URL url = new URL(previewUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(5000);
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                is = httpConn.getInputStream();
                File file = File.createTempFile("external-video", "preview");
                FileOutputStream fout = null;
                try
                {
                    fout = new FileOutputStream(file);
                    IOUtils.copy(is, fout);
                }
                finally
                {
                    IOUtils.closeQuietly(fout);
                }
                String contentType = httpConn.getContentType();
                Document doc = new Document(file, contentType);
                try
                {
                    SaveHandlerImpl.saveDocument(
                        ContentUtil.asContent(media),
                        doc,
                        MediaWithPreviewImageTypeHandler.PREVIEW_NODEDATA_NAME,
                        "preview",
                        null);
                    media.save();
                    return true;
                }
                catch (RepositoryException e)
                {
                    log.error(e.getMessage(), e);
                }
                finally
                {
                    doc.delete();
                }
            }
            else
            {
                log.warn("Problem establishing connection with {}: {}", url, httpConn.getResponseCode());
            }
        }
        return false;
    }

    public static boolean copyPreviewImageToRepository(final String mediaUUID, final String previewUrl)
        throws IOException
    {
        return MgnlContext.doInSystemContext(new MgnlContext.Op<Boolean, IOException>()
        {

            public Boolean exec() throws IOException
            {
                Node media;
                try
                {
                    media = NodeUtil.getNodeByIdentifier(MediaModule.REPO, mediaUUID);
                    if (media != null)
                    {
                        return copyPreviewImageToRepository(media, previewUrl);
                    }
                    return false;
                }
                catch (RepositoryException re)
                {
                    throw new IOException(re);
                }

            }
        });

    }
}