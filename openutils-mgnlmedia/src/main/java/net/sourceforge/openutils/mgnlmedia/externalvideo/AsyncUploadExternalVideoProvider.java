package net.sourceforge.openutils.mgnlmedia.externalvideo;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public abstract class AsyncUploadExternalVideoProvider extends BaseExternalVideoProvider

{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(AsyncUploadExternalVideoProvider.class);

    public final static String STATUS_TO_UPLOAD = "to_upload";

    public final static String STATUS_UPLOADING = "uploading";

    public final static String STATUS_UPLOADED = "uploaded";

    public static final String ND_PROGRESS = "uploadedBytes";

    public static final String ND_ASYNC = "processingAsyncVideo";

    private int numTries = 3;

    /**
     * {@inheritDoc}
     */
    public void processVideo(Node media)
    {
        try
        {
            ExternalVideoUtil.setProperty(media.getIdentifier(), ND_ASYNC, true);
            // new AsyncUploadThread(this, media.getUUID()).start();
            uploadedVideo(media.getIdentifier());
        }
        catch (RepositoryException re)
        {
            log.error("Error processing video", re);
        }

    }

    public abstract void uploadVideo(String mediaUUID) throws IOException, RepositoryException;

    public abstract void uploadedVideo(String mediaUUID);

    public void endProcess(String mediaUUID)
    {
        ExternalVideoUtil.removeProperty(mediaUUID, ND_ASYNC);
        ExternalVideoUtil.removeProperty(mediaUUID, ExternalVideoUtil.ND_STATUS);
        ExternalVideoUtil.removeProperty(mediaUUID, ExternalVideoUtil.ND_STATUS_CHANGE);
        ExternalVideoUtil.removeProperty(mediaUUID, ExternalVideoUtil.ND_PROGRESS);
    }

    /**
     * Returns the nTries.
     * @return the nTries
     */
    public int getNumTries()
    {
        return numTries;
    }

    /**
     * Sets the nTries.
     * @param nTries the nTries to set
     */
    public void setNumTries(int nTries)
    {
        this.numTries = nTries;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAsyncUpload()
    {
        return true;
    }
}
