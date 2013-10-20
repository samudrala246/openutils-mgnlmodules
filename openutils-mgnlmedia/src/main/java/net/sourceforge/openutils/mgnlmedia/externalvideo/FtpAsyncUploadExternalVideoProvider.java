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
