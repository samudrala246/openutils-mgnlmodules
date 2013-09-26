package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.MultipartForm;

import java.util.Map;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;


/**
 * @author molaschi
 * @version $Id: $
 */
public interface ExternalVideoProvider
{

    void processVideo(Node media);

    boolean isAsyncUpload();

    String getUrl(Node media, Map<String, String> options);

    String getNewNodeName(MultipartForm form, HttpServletRequest request);

    String getName();

    String getUploadFileName(Node media);

    String getMediaUUIDFromFileName(String filename);

    void stop();

    String getThumbnailUrl(Node media);

    String getPreviewUrl(Node media);
    
    String getFilename(Node media);
}
