/**
 *
 * SimpleMedia Module for Magnolia CMS (http://www.openmindlab.com/lab/products/media.html)
 * Copyright(C) 2008-2012, Openmind S.r.l. http://www.openmindonline.it
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sourceforge.openutils.mgnlmedia.media.utils.VideoMedataUtils.VideoMetaData;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.io.flv.FlvAtom;
import com.flazr.rtmp.RtmpHeader;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpWriter;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.client.ClientPipelineFactory;
import com.flazr.util.Utils;


/**
 * <p>
 * Utility class to fetch flv headers from a rtmp URL using flazr (http://flazr.com)
 * </p>
 * @author fgiust
 * @version $Id$
 */
public final class RtmpMedatadaFetcher
{

    private RtmpMedatadaFetcher()
    {
        // don't instantiate
    }

    /**
     * Fetch flv metadata from a rtmp URL
     * @param url URL
     * @param timeoutInMs timeout for fetching metadata (milliseconds)
     * @return parsed FLVMetadata
     * @throws IOException
     */
    public static VideoMetaData fetchMetadata(String url, long timeoutInMs) throws IOException
    {

        String validUrl = StringUtils.substringBeforeLast(url, ".flv");

        // sample url: "rtmp://edge01.fms.dutchview.nl/botr/bunny"
        ClientOptions options = new ClientOptions(validUrl, null);

        options.setLoad(1);
        options.getSwfHash();
        options.setLength(1);
        options.setClientVersionToUse(new byte[]{0, 0, 0, 0 });
        MetadataWriter writer = new MetadataWriter();
        options.setWriterToSave(writer);

        final ClientBootstrap bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
        final ChannelFuture future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));

        for (int j = 0; j < (timeoutInMs / 10); j++)
        {
            if (writer.getByteArray().size() >= 400)
            {
                break;
            }
            future.getChannel().getCloseFuture().awaitUninterruptibly(10);
        }

        bootstrap.getFactory().releaseExternalResources();

        return VideoMedataUtils.getMetaData(writer.getByteArray().toByteArray());
    }

    private static ClientBootstrap getBootstrap(final Executor executor, final ClientOptions options)
    {
        final ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
        final ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new ClientPipelineFactory(options));
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        return bootstrap;
    }

    static class MetadataWriter implements RtmpWriter
    {

        /**
         * Logger.
         */
        private Logger log = LoggerFactory.getLogger(RtmpMedatadaFetcher.MetadataWriter.class);

        private final ByteArrayOutputStream bos;

        private final int[] channelTimes = new int[RtmpHeader.MAX_CHANNEL_ID];

        private int primaryChannel = -1;

        private final int seekTime;

        public MetadataWriter()
        {
            bos = new ByteArrayOutputStream();

            this.seekTime = 0;

            try
            {
                bos.write(FlvAtom.flvHeader().toByteBuffer().array());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void close()
        {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         */
        public void write(final RtmpMessage message)
        {
            final RtmpHeader header = message.getHeader();
            if (header.isAggregate())
            {
                final ChannelBuffer in = message.encode();
                while (in.readable())
                {
                    final FlvAtom flvAtom = new FlvAtom(in);
                    final int absoluteTime = flvAtom.getHeader().getTime();
                    channelTimes[primaryChannel] = absoluteTime;
                    write(flvAtom);
                }
            }
            else
            {
                final int channelId = header.getChannelId();
                channelTimes[channelId] = seekTime + header.getTime();
                if (primaryChannel == -1 && (header.isAudio() || header.isVideo()))
                {
                    log.info("first media packet for channel: {}", header);
                    primaryChannel = channelId;
                }
                if (header.getSize() <= 2)
                {
                    return;
                }
                write(new FlvAtom(header.getMessageType(), channelTimes[channelId], message.encode()));
            }
        }

        private void write(final FlvAtom flvAtom)
        {
            try
            {
                bos.write(flvAtom.write().toByteBuffer().array());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        /**
         * Returns the ByteArrayOutputStream.
         * @return the ByteArrayOutputStream
         */
        public ByteArrayOutputStream getByteArray()
        {
            return bos;
        }

    }
}
