package net.sourceforge.openutils.mgnlmedia.externalvideo;

/**
 * @author molaschi
 * @version $Id: $
 */
public interface TrascodingExternalVideoProvider extends ExternalVideoProvider
{

    public final static String STATUS_TO_TRASCODE = "to_trascode";

    public final static String STATUS_TRASCODING = "trascoding";
    
    public final static String STATUS_TRASCODED = "trascoded";

    void startTrascoding(String mediaUUID);

    void updateTrascodingProgress(String mediaUUID, double progress);

    void trascoded(String mediaUUID);
}
