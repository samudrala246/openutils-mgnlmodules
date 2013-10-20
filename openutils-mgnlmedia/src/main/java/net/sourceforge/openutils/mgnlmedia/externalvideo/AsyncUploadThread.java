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
