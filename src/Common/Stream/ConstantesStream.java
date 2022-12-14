package Common.Stream;

public class ConstantesStream {
    public static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    public static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade
    public static int VIDEO_LENGTH = 500; //length of the video in frames
    public static String VideoFileName = "src/out/production/ProgEx/movie.Mjpeg";


    public static String VideoFileNameCORE = "/home/core/Desktop/ESR/src/out/production/ProgEx/movie.Mjpeg";

    /**
     * If we want to drop packets, when we resume the video, we will get to the current time in stream;
     * If we don't want to, when we resume the video, it will continue the reproduction.
     *
     */
    public static boolean dropPacketsWhenPause = true;

    public static boolean showStream = true;

    public static int maxSizeBuffer = 10;


    public  static int streamPort = 9020;

}
