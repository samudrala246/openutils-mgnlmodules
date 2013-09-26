package net.sourceforge.openutils.mgnlmedia.externalvideo;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import net.sourceforge.openutils.mgnlmedia.media.types.impl.MediaWithPreviewImageTypeHandler;
import net.sourceforge.openutils.mgnlmedia.media.utils.MediaMetadataFormatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author dschivo
 */
public abstract class BaseVideoTypeHandler extends MediaWithPreviewImageTypeHandler
{

    /**
     * Logger.
     */
    private Logger log = LoggerFactory.getLogger(BaseVideoTypeHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getMediaInfo(Node media)
    {

        Map<String, String> info = super.getMediaInfo(media);

        try
        {
            long duration = media.getProperty(METADATA_DURATION).getLong();
            if (duration > 0)
            {
                info.put(METADATA_DURATION, MediaMetadataFormatUtils.formatDuration(duration));
            }

            long framerate = media.getProperty(METADATA_FRAMERATE).getLong();
            if (framerate > 0)
            {
                info.put(METADATA_FRAMERATE, Long.toString(framerate));
            }
        }
        catch (RepositoryException re)
        {
            log.error("Error getting external video media infos", re);
        }

        return info;
    }

}
