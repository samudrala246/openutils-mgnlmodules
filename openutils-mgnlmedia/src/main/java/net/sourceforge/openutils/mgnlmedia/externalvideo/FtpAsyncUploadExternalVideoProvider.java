package net.sourceforge.openutils.mgnlmedia.externalvideo;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.context.SystemContext;
import info.magnolia.objectfactory.Components;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.lifecycle.MediaModule;


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
        Node media = Components
            .getComponent(SystemContext.class)
            .getJCRSession(MediaModule.REPO)
            .getNodeByIdentifier(mediaUUID);
        Node file = media.getNode(BaseVideoTypeHandler.ORGINAL_NODEDATA_NAME);

        long size = -1;
        if (file.hasProperty(FileProperties.PROPERTY_SIZE))
        {
            size = file.getProperty(FileProperties.PROPERTY_SIZE).getLong();
        }

        // TODO check binary nodedata handling
        FtpUtil.upload(
            account,
            file.getProperty("jcr:data").getValue().getBinary().getStream(),
            size,
            this.getUploadFileName(media),
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
