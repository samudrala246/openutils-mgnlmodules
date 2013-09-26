package net.sourceforge.openutils.mgnlmedia.externalvideo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author molaschi
 * @version $Id: $
 */
public class FtpUtil
{

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(FtpUtil.class);

    public static void upload(FtpAccount account, InputStream inputStream, long size, final String fileName,
        final UploadProgress progress) throws IOException
    {
        FTPClient f = new FTPClient();
        log.debug("Connection to {}", account.getUrl());
        f.connect(account.getUrl());
        log.debug("Login with {}", account.getUsername());
        if (f.login(account.getUsername(), account.getPassword()))
        {

            String paths[] = StringUtils.split(fileName, "/");
            String path = "/";
            for (int i = 0; i < paths.length - 1; i++)
            {
                path += paths[i] + "/";
                if (f.listNames(path) == null)
                {
                    log.debug("Creating folder {}", path);
                    f.mkd(path);
                }
            }
            if (!"/".equals(path))
            {
                log.debug("Moving to folder {}", path);
                f.cwd(path);
            }

            String relativeFileName = paths[paths.length - 1];

            if (account.isPassive())
            {
                log.debug("Entering passive mode");
                f.enterLocalPassiveMode();
            }

            log.debug("Storing file {} in {}", new Object[]{relativeFileName, path });
            f.setFileType(FTP.BINARY_FILE_TYPE);
            if (progress != null && size > 0)
            {
                OutputStream os = f.storeFileStream(relativeFileName);
                org.apache.commons.net.io.Util.copyStream(
                    inputStream,
                    os,
                    f.getBufferSize(),
                    size,
                    new org.apache.commons.net.io.CopyStreamAdapter()
                    {

                        @Override
                        public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize)
                        {
                            log.debug("File {} upload: {} / {}", new Object[]{
                                fileName,
                                totalBytesTransferred,
                                streamSize });
                            progress.updateProgress((int) (100 * totalBytesTransferred / streamSize));
                        }
                    });
                os.flush();
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(inputStream);

                f.completePendingCommand();
            }
            else
            {
                f.storeFile(relativeFileName, inputStream);
            }
            log.debug("Done, disconnecting");
        }
        f.disconnect();
    }

    public interface UploadProgress
    {

        void updateProgress(long totalBytesTransferred);
    }

}
