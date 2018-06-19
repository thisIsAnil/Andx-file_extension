package andx;

import java.nio.charset.Charset;

/**
 * Created by INFIi on 3/1/2017.
 */

public class Utils {
    final  static long size=0;
    static final String HEADER="PCX-1";
    static final String VERSION="0.9";
    static final String LAYOUT_FLAGS_CODE="LFC";
    static final String ENCODING= "UTF-8     ";
    static final String PROTECTION_PASSWORD="00000000";

    //Frame count SOS len EOS
    //FRame structure
    static final String START_OF_FRAME="SOF";

    static final String START_OF_SIZE="SOS";
    static final String SIZE=String.valueOf(size);
    static final String END_OF_SIZE="EOS";
    //local layout flag 32 bit
    static final int LOCAL_LAYOUT_ID_DEFAULT=0;                 //16 bit value max 2^16 layout support
    static final boolean USE_LOCAL_LAYOUT_BG_COLOR=false;        //1 bit
    static final boolean USE_LOCAL_LAYOUT_DEFAULT=false;        //1 bit
    static final boolean USE_LOCAL_TEXT_SIZE_DEFAULT=false;     //1 bit
    static final boolean USE_LOCAL_TEXT_COLOR=false;            //1 bit
    static final boolean USE_LOCAL_FONT_SIZE=false;             //1 bit
    static final boolean USE_LOCAL_TEXT_ANIMATION=false;        //1 bit
    static final boolean USE_LOCAL_IMAGE_ANIMATION=false;       //1 bit
    static final boolean USE_LOCAL_WATERMARK=false;             //1 bit
    static final byte RESERVED_LLFC_BITS=(byte)0;             // 8 bits


    static final String START_OF_FRAME_DATA="SFD";
    static final String COMPRESSION_ALGO_CODE="0";
    static final String START_OF_TEXT_DATA="STD";          //64 bits
    static final String END_OF_TEXT_DATA="ETD";            //64 bits
    static final String START_OF_DICTIONARY="SOD";
    static final String END_OF_DICTIONARY="EOD";
    static final String START_OF_IMAGE_DATA="SID";
    static final String START_OF_IMAGE="SOI";
    static final String END_OF_IMAGE="EOI";
    static final String END_OF_IMAGE_DATA="EID";
    static final String START_OF_GIF_DATA="SGD";
    static final String START_OF_GIF="SOG";
    static final String END_OF_GIF="EOG";
    static final String END_OF_GIF_DATA="EGD";
    static final String START_OF_VIDEO_DATA="SVD";
    static final String START_OF_VIDEO="SOV";
    static final String END_OF_VIDEO="EOV";
    static final String END_OF_VIDEO_DATA="EVD";
    static final String START_OF_AUDIO_DATA="SAD";
    static final String START_OF_AUDIO="SOA";
    static final String END_OF_AUDIO="EOA";
    static final String END_OF_AUDIO_DATA="EAD";
    static final String START_OF_SCRIPT_DATA="SCD";
    static final String START_OF_SCRIPT="SCR";
    static final String START_OF_PATH="SCP";
    static final String END_OF_PATH="ECP";
    static final String END_OF_SCRIPT="ECR";
    static final String END_OF_SCRIPT_DATA="ECD";
    static final String END_OF_FRAME_DATA="EFD";

    static final String START_OF_METADATA="SOM";
    static final int LOCAL_BG_COLOR=0xffffffff;     //32 bit color value
    static final int LOCAL_TEXT_SIZE=24;            //32 bit text size
    static final int LOCAL_TEXT_COLOR=0xff000000;   //32 bit
    static final String LOCAL_START_OF_FONT_NAME="LFN";
    static final String LOCAL_END_OF_FONT_NAME="LEF";
    //Text Animation bits same as local



    static final String END_OF_METADATA="EOM";
    static final String END_OF_FRAME="EOF";



    public static byte[] getHeader(String encoding){

        byte[] bytes=(Utils.HEADER+Utils.VERSION+Utils.ENCODING+Utils.PROTECTION_PASSWORD).getBytes();
        return bytes;
    }
    public static byte[] getFrameHeader(long size,String encoding){

        byte[] bytes=(Utils.START_OF_FRAME+Utils.START_OF_SIZE+size+Utils.END_OF_SIZE).getBytes(Charset.forName(encoding));
        return bytes;
    }
    public  static long frameMetaDataLength(String encoding){
        return START_OF_FRAME_DATA.getBytes(Charset.forName(encoding)).length +8
                +START_OF_TEXT_DATA.getBytes(Charset.forName(encoding)).length+END_OF_TEXT_DATA.getBytes(Charset.forName(encoding)).length
                +Utils.START_OF_IMAGE_DATA.getBytes(Charset.forName(encoding)).length+Utils.END_OF_IMAGE_DATA.getBytes(Charset.forName(encoding)).length
                + Utils.START_OF_VIDEO_DATA.getBytes(Charset.forName(encoding)).length+Utils.END_OF_VIDEO_DATA.getBytes(Charset.forName(encoding)).length
                + Utils.START_OF_GIF_DATA.getBytes(Charset.forName(encoding)).length+Utils.END_OF_GIF_DATA.getBytes(Charset.forName(encoding)).length
                + Utils.END_OF_AUDIO_DATA.getBytes(Charset.forName(encoding)).length+Utils.START_OF_AUDIO_DATA.getBytes(Charset.forName(encoding)).length+(SIZE.getBytes().length*8)
                + Utils.END_OF_FRAME_DATA.getBytes(Charset.forName(encoding)).length+Utils.END_OF_FRAME_DATA.getBytes(Charset.forName(encoding)).length;

    }
    public static String getFrameDataHeader(int compressionCode){
        return Utils.START_OF_FRAME_DATA+Utils.COMPRESSION_ALGO_CODE+compressionCode;
    }


}
