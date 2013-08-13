/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.sun.webkit.graphics;

import java.util.HashMap;
import java.util.Map;

final class RenderMediaControls {
    /**
     * Media control part constants (types for the paintControl method)
     */
    private static final int PLAY_BUTTON                     = 1;
    private static final int PAUSE_BUTTON                    = 2;
    private static final int DISABLED_PLAY_BUTTON            = 3;

    private static final int MUTE_BUTTON                     = 4;
    private static final int UNMUTE_BUTTON                   = 5;
    private static final int DISABLED_MUTE_BUTTON            = 6;

    //private static final int FULLSCREEN_BUTTON               = 7;

    // background for control panel (except buttons)
    //private static final int BACKGROUND                      = 8;

    // Time slider track is rendered by paintTimeSliderTrack
    private static final int TIME_SLIDER_TRACK               = 9;
    private static final int TIME_SLIDER_THUMB               = 10;

    private static final int VOLUME_CONTAINER                = 11;
    // Volume slider track is rendered by paintVolumeTrack
    private static final int VOLUME_TRACK                    = 12;
    private static final int VOLUME_THUMB                    = 13;

    //private static final int CURRENT_TIME                    = 14;
    //private static final int REMAINING_TIME                  = 15;

    private static String getControlName(int control) {
        switch (control) {
            case PLAY_BUTTON: return "PLAY_BUTTON";
            case PAUSE_BUTTON: return "PAUSE_BUTTON";
            case DISABLED_PLAY_BUTTON: return "DISABLED_PLAY_BUTTON";

            case MUTE_BUTTON: return "MUTE_BUTTON";
            case UNMUTE_BUTTON: return "UNMUTE_BUTTON";
            case DISABLED_MUTE_BUTTON: return "DISABLED_MUTE_BUTTON";

            //case FULLSCREEN_BUTTON: return "FULLSCREEN_BUTTON";

            //case BACKGROUND: return "BACKGROUND";

            case TIME_SLIDER_TRACK: return "TIME_SLIDER_TRACK";
            case TIME_SLIDER_THUMB: return "TIME_SLIDER_THUMB";

            case VOLUME_CONTAINER: return "VOLUME_CONTAINER";
            case VOLUME_TRACK: return "VOLUME_TRACK";
            case VOLUME_THUMB: return "VOLUME_THUMB";

            //case CURRENT_TIME: return "CURRENT_TIME";
            //case REMAINING_TIME: return "REMAINING_TIME";
        }
        return "{UNKNOWN CONTROL " + control + "}";
    }


    private RenderMediaControls() {}    // disable instantiations

    static void paintControl(WCGraphicsContext gc,
            int type, int x, int y, int w, int h) {
        if (log) {
            log("paintControl, type=" + type + "(" + getControlName(type) + ")"
                    + ", x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
        }
        switch (type) {
            case PLAY_BUTTON:
                paintControlImage("mediaPlay", gc, x, y, w, h);
                break;
            case PAUSE_BUTTON:
                paintControlImage("mediaPause", gc, x, y, w, h);
                break;
            case DISABLED_PLAY_BUTTON:
                paintControlImage("mediaPlayDisabled", gc, x, y, w, h);
                break;
            case MUTE_BUTTON:
                paintControlImage("mediaMute", gc, x, y, w, h);
                break;
            case UNMUTE_BUTTON:
                paintControlImage("mediaUnmute", gc, x, y, w, h);
                break;
            case DISABLED_MUTE_BUTTON:
                paintControlImage("mediaMuteDisabled", gc, x, y, w, h);
                break;
            case TIME_SLIDER_THUMB:
                paintControlImage("mediaTimeThumb", gc, x, y, w, h);
                break;
            case VOLUME_CONTAINER:
                break;
            case VOLUME_THUMB:
                paintControlImage("mediaVolumeThumb", gc, x, y, w, h);
                break;
            default:
                if (log) log("ERROR: paintControl, unknown type: " + type);
                break;
        }
    }

    private static final int TimeSliderTrackUnbufferedColor =
            rgba(0xec, 0x87, 0x7d);
    private static final int TimeSliderTrackBufferedColor =
            rgba(0xf9, 0x1a, 0x02);
    private static final int TimeSliderTrackThickness = 3;

    static void paintTimeSliderTrack(WCGraphicsContext gc,
            float duration, float curTime, float[] bufferedPairs,
            int x, int y, int w, int h) {
        if (log) {
            String bufStr = "{";
            for (int i=0; i<bufferedPairs.length; i+=2) {
                if (i>0) {
                    bufStr += ", ";
                }
                bufStr += "[" + bufferedPairs[i] + "-" + bufferedPairs[i+1]+"]";
            }
            bufStr +="}";
            log("paintTimeSliderTrack, duration=" + duration
                    + ", curTime=" + curTime
                    + ", buffered=" + bufStr
                    + ", x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
        }

        // make the track height TimeSliderTrackThickness pixels
        y += (h - TimeSliderTrackThickness)/2;
        h = TimeSliderTrackThickness;
        // descrease width by width of the thumb (increasing x by half of the value)
        int thumbWidth = (fwkGetSliderThumbSize(SLIDER_TYPE_TIME) >> 16) & 0xFFFF;
        w -= thumbWidth;
        x += thumbWidth/2;

        if (duration < 0) {
            // the media is not available
        } else {
            float timeToPixel = (1f / duration) * w;
            float start = 0f;
            for (int i=0; i<bufferedPairs.length; i+=2) {
                // unbuffered
                if (log) {
                    log("..[unbuffered]: " + (x + timeToPixel*start)
                            + "-" + (x+timeToPixel * bufferedPairs[i]));
                }
                gc.fillRect(x + timeToPixel*start, y,
                        timeToPixel * (bufferedPairs[i] - start), h,
                        TimeSliderTrackUnbufferedColor);
                // buffered
                if (log) {
                    log("..[  buffered]: " + (x + timeToPixel*bufferedPairs[i])
                            + "-" + (x+ timeToPixel * (bufferedPairs[i+1] - bufferedPairs[i])));
                }
                gc.fillRect(x + timeToPixel*bufferedPairs[i], y,
                        timeToPixel * (bufferedPairs[i+1] - bufferedPairs[i]), h,
                        TimeSliderTrackBufferedColor);
                start = bufferedPairs[i+1];
            }
            if (start < duration) {
                //last unbuffered
                if (log) {
                    log("..[unbuffered]: " + (x + timeToPixel*start)
                            + "-" + (x+timeToPixel * duration));
                }
                gc.fillRect(x + timeToPixel*start, y,
                        timeToPixel * (duration - start), h,
                        TimeSliderTrackUnbufferedColor);
            }

        }
    }

    private static final int VolumeTrackColor = rgba(0xd0, 0xd0, 0xd0, 0x80);
    private static final int VolumeTrackThickness = 1;

    static void paintVolumeTrack(WCGraphicsContext gc,
            float curVolume, boolean muted,
            int x, int y, int w, int h) {
        if (log) {
            log("paintVolumeTrack, curVolume=" + curVolume
                    + ", muted=" + (muted ? "true" : "false")
                    + ", x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
        }

        // (w+1): to shift the track 1 line righter when (w - thickness) % 2 == 1
        x += ((w + 1) - VolumeTrackThickness)/2;
        w = VolumeTrackThickness;
        // descrease height by height of the thumb (increasing y by half of the value)
        int thumbWidth = fwkGetSliderThumbSize(SLIDER_TYPE_TIME) & 0xFFFF;
        h -= thumbWidth;
        y += thumbWidth/2;


        gc.fillRect(x, y, w, h, VolumeTrackColor);
    }

    private static final int SLIDER_TYPE_TIME = 0;
    private static final int SLIDER_TYPE_VOLUME = 1;
    /**
     * returns ((width << 16) | height)
     */
    private static int fwkGetSliderThumbSize(int type) {
        WCImage image = null;
        switch (type) {
            case SLIDER_TYPE_TIME:
                image = getControlImage("mediaTimeThumb");
                break;
            case SLIDER_TYPE_VOLUME:
                image = getControlImage("mediaVolumeThumb");
                break;
        }
        if (image != null) {
            return (image.getWidth() << 16) | image.getHeight();
        }
        return 0;
    }

    private static final Map<String, WCImage> controlImages
            = new HashMap<String, WCImage>();

    private static WCImage getControlImage(String resName) {
        WCImage image = controlImages.get(resName);
        if (image == null) {
            WCImageDecoder decoder =
                    WCGraphicsManager.getGraphicsManager().getImageDecoder();
            decoder.loadFromResource(resName);
            WCImageFrame frame = decoder.getFrame(0, null);
            if (frame != null) {
                image = frame.getFrame();
                controlImages.put(resName, image);
            }
        }
        return image;
    }

    private static void paintControlImage(String resName,
            WCGraphicsContext gc, int x, int y, int w, int h) {
        WCImage image = getControlImage(resName);
        if (image != null) {
            // don't stretch the image, just center it
            x += (w - image.getWidth()) / 2;
            w = image.getWidth();
            y += (h - image.getHeight()) / 2;
            h = image.getHeight();
            gc.drawImage(image,
                    x, y, w, h,
                    0f, 0f, image.getWidth(), image.getHeight());
        } else {
            if (log) log("  paintControlImage(" + resName + "), image is NULL");
        }
    }

    private static int rgba(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    private static int rgba(int r, int g, int b) {
        return rgba(r, g, b, 0xFF);
    }

    private final static boolean log = false;
    private static void log(String s) {
        System.out.println(s);
        System.out.flush();
    }
}
