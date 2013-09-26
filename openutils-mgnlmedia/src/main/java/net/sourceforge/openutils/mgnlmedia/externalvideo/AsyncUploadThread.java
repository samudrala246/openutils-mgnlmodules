package net.sourceforge.openutils.mgnlmedia.externalvideo;

import java.io.IOException;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public class AsyncUploadThread extends Thread
{

    private AsyncUploadExternalVideoProvider externalVideoProvider;

    private String mediaUUID;

    public AsyncUploadThread(AsyncUploadExternalVideoProvider externalVideoProvider, String mediaUUID)
    {
        this.externalVideoProvider = externalVideoProvider;
        this.mediaUUID = mediaUUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        ExternalVideoUtil.setStatus(mediaUUID, AsyncUploadExternalVideoProvider.STATUS_UPLOADING);
        Exception lastException = null;
        for (int nTry = 0; nTry < externalVideoProvider.getNumTries(); nTry++)
        {
            try
            {
                externalVideoProvider.uploadVideo(mediaUUID);
                ExternalVideoUtil.setStatus(mediaUUID, AsyncUploadExternalVideoProvider.STATUS_UPLOADED);
                externalVideoProvider.uploadedVideo(mediaUUID);
                return;
            }
            catch (IOException e)
            {
                lastException = e;
            }
            catch (RepositoryException e)
            {
                lastException = e;
            }
        }
        ExternalVideoUtil.setError(mediaUUID, null, lastException != null ? lastException.getMessage() : StringUtils.EMPTY);
    }

}
