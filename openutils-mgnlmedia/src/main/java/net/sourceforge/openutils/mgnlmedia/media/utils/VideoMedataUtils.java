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

package net.sourceforge.openutils.mgnlmedia.media.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

import com.coremedia.iso.IsoBufferWrapper;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoFileConvenienceHelper;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.TrackHeaderBox;


/**
 * <p>
 * Parses Video metadata in order to extract size and duration.
 * </p>
 * <p>
 * Code based on FLVMetaData class by SANTHOSH REDDY MANDADI
 * http://java-servlet-jsp-web.blogspot.com/2009/06/java-program-to-fetch-flv-metadata.html
 * </p>
 * @author fgiust
 * @version $Id$
 */
public final class VideoMedataUtils
{

    /**
     * Contains informations about a FLV file.
     */
    public static class VideoMetaData
    {

        private long duration;

        private long width;

        private long height;

        private double audioDataRate;

        private double videoDataRate;

        private long fileSize;

        private String createdDate;

        private String mimeType;

        private double frameRate;

        public void setCreatedDate(String createdDate)
        {
            this.createdDate = createdDate;
        }

        public String getCreatedDate()
        {
            return createdDate;
        }

        public void setMimeType(String mimeType)
        {
            this.mimeType = mimeType;
        }

        public String getMimeType()
        {
            return mimeType;
        }

        public void setWidth(long width)
        {
            this.width = width;
        }

        public double getWidth()
        {
            return width;
        }

        public void setHeight(long height)
        {
            this.height = height;
        }

        public long getHeight()
        {
            return height;
        }

        public void setAudioDataRate(double audioDataRate)
        {
            this.audioDataRate = audioDataRate;
        }

        public double getAudioDataRate()
        {
            return audioDataRate;
        }

        public void setVideoDataRate(double videoDataRate)
        {
            this.videoDataRate = videoDataRate;
        }

        public double getVideoDataRate()
        {
            return videoDataRate;
        }

        public void setFileSize(long fileSize)
        {
            this.fileSize = fileSize;
        }

        public long getFileSize()
        {
            return fileSize;
        }

        public void setFrameRate(double frameRate)
        {
            this.frameRate = frameRate;
        }

        public double getFrameRate()
        {
            return frameRate;
        }

        public void setDuration(long duration)
        {
            this.duration = duration;
        }

        public long getDuration()
        {
            return duration;
        }

    }

    public static VideoMetaData parsefromStream(String extension, InputStream stream) throws IOException
    {
        if ("flv".equals(extension))
        {
            return parseFLVfromStream(stream);
        }
        if ("mp4".equals(extension) || "m4v".equals(extension))
        {
            return parseMP4fromStream(stream);
        }
        return null;
    }

    public static VideoMetaData parseFLVfromStream(InputStream stream) throws IOException
    {
        return getMetaData(stream);
    }

    public static VideoMetaData parseMP4fromStream(InputStream stream) throws IOException
    {
        VideoMetaData meta = new VideoMetaData();

        File tempFile = File.createTempFile("medatadaextraction", ".mp4");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
        IOUtils.copy(stream, out);
        out.close();

        try
        {
            IsoBufferWrapper buf = new IsoBufferWrapper(tempFile);
            IsoFile isoFile = new IsoFile(buf);
            isoFile.parse();

            MovieBox box = (MovieBox) IsoFileConvenienceHelper.get(isoFile, MovieBox.TYPE);
            meta.setDuration(box.getMovieHeaderBox().getDuration() / box.getMovieHeaderBox().getTimescale());
            meta.setFrameRate(box.getMovieHeaderBox().getRate());

            for (long trackid : box.getTrackNumbers())
            {
                TrackHeaderBox thb = box.getTrackMetaData(trackid).getTrackBox().getTrackHeaderBox();
                if (thb.getWidth() > 0)
                {
                    meta.setWidth((long) thb.getWidth());
                    meta.setHeight((long) thb.getHeight());
                    break;
                }
            }
        }
        finally
        {
            tempFile.delete();
        }

        return meta;
    }

    public static VideoMetaData parseFLVfromUrl(URL url) throws IOException
    {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(5000);

        // Getting the remote input stream
        InputStream fis = connection.getInputStream();
        try
        {
            return parseFLVfromStream(fis);
        }
        finally
        {
            IOUtils.closeQuietly(fis);
        }

    }

    /**
     * Extract the metadata for the flv and sets them in the properties. If the property has 0.0 or null, then the
     * information is not available on the target FLV.
     * @throws IOException
     * @throws Exception if something goes wrong.
     */
    private static VideoMetaData getMetaData(InputStream fis) throws IOException
    {
        VideoMetaData meta = new VideoMetaData();
        try
        {
            // Creating the bytes array to read the first 400 bytes data from input stream
            byte[] bytes = new byte[400];
            // Reading the data from the input stream
            fis.read(bytes);

            meta = getMetaData(bytes);

        }
        finally
        {
            IOUtils.closeQuietly(fis);
        }

        return meta;
    }

    /**
     * @param meta
     * @param bytes
     */
    protected static VideoMetaData getMetaData(byte[] bytes)
    {

        VideoMetaData meta = new VideoMetaData();

        // Fetching the properties. If the output shows -1 or null then consider that the FLV doesn't have that
        // info on metadata
        meta.setDuration(getLong(bytes, "duration"));
        meta.setWidth(getLong(bytes, "width"));
        meta.setHeight(getLong(bytes, "height"));
        meta.setAudioDataRate(getDouble(bytes, "audiodatarate"));
        meta.setVideoDataRate(getDouble(bytes, "videodatarate"));
        meta.setFileSize(getLong(bytes, "filesize"));
        meta.setCreatedDate(getString(bytes, "creationdate"));
        meta.setMimeType(getString(bytes, "mimetype"));
        meta.setFrameRate(getDouble(bytes, "framerate"));

        return meta;
    }

    private static double getDouble(byte[] bytes, String property)
    {
        // Checking whether the property exists on the metadata
        int offset = indexOf(bytes, property.getBytes());
        if (offset != -1)
        {
            // Calculating the value from the bytes received from getBytes method
            return ByteBuffer.wrap(getBytes(bytes, offset + property.length() + 1, 8)).getDouble();
        }
        else
        {
            // Returning -1 to notify the info not available
            return -1;
        }
    }

    private static long getLong(byte[] bytes, String property)
    {
        // Checking whether the property exists on the metadata
        int offset = indexOf(bytes, property.getBytes());
        if (offset != -1)
        {
            // Calculating the value from the bytes received from getBytes method
            return (long) ByteBuffer.wrap(getBytes(bytes, offset + property.length() + 1, 8)).getDouble();
        }
        else
        {
            // Returning -1 to notify the info not available
            return -1;
        }
    }

    private static String getString(byte[] bytes, String property)
    {
        // Checking whether the property exists on the metadata
        int offset = indexOf(bytes, property.getBytes());
        if (offset != -1)
        {
            // Constructing the string from the bytes received from getBytes method
            return new String(getBytes(bytes, offset + property.length() + 3, 24));
        }
        else
        {
            // Returning null to notify the info not available
            return null;
        }
    }

    private static byte[] getBytes(byte[] bytes, int offset, int length)
    {
        // Fetching the required number of bytes from the source and returning
        byte[] requiredBytes = new byte[length];
        for (int i = offset, j = 0; j < length; i++, j++)
        {
            requiredBytes[j] = bytes[i];
        }
        return requiredBytes;
    }

    static int indexOf(byte[] source, byte[] target)
    {
        byte first = target[0];
        int max = source.length - target.length;

        for (int i = 0; i <= max; i++)
        {
            /* Look for first character. */
            if (source[i] != first)
            {
                while (++i <= max && source[i] != first)
                {
                    ;
                }
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max)
            {
                int j = i + 1;
                int end = j + target.length - 1;
                for (int k = 1; j < end && source[j] == target[k]; j++, k++)
                {
                    ;
                }

                if (j == end)
                {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }

}
