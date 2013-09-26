package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.context.MgnlContext;

import java.io.IOException;

import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;

import org.apache.commons.lang.math.NumberUtils;


/**
 * @author molaschi
 * @version $Id: $
 */
public abstract class FtpAsyncUploadExternalVideoProvider extends AsyncUploadExternalVideoProvider
{

    private FtpAccount account = new FtpAccount();

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadVideo(final String mediaUUID) throws IOException, RepositoryException
    {
        Content media = MgnlContext
            .getSystemContext()
            .getHierarchyManager(MediaModule.REPO)
            .getContentByUUID(mediaUUID);
        NodeData file = media.getNodeData(BaseVideoTypeHandler.ORGINAL_NODEDATA_NAME);
        long size = NumberUtils.toLong(file.getAttribute(FileProperties.PROPERTY_SIZE), -1);
        FtpUtil.upload(
            account,
            media.getNodeData(BaseVideoTypeHandler.ORGINAL_NODEDATA_NAME).getStream(),
            size,
            this.getUploadFileName(media.getJCRNode()),
            new FtpUtil.UploadProgress()
            {

                public void updateProgress(long totalBytesTransferred)
                {
                    ExternalVideoUtil.setProperty(
                        mediaUUID,
                        AsyncUploadExternalVideoProvider.ND_PROGRESS,
                        totalBytesTransferred);
                }
            });
    }

    /**
     * Returns the account.
     * @return the account
     */
    public FtpAccount getAccount()
    {
        return account;
    }

    /**
     * Sets the account.
     * @param account the account to set
     */
    public void setAccount(FtpAccount account)
    {
        this.account = account;
    }

}
