package andx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import static andx.AndxDecoderCore.write;

/**
 * Created by INFIi on 3/17/2017.
 */

public class AndxCompressor {
    private static int decompressedLength;
    private static LZ4Factory factory = LZ4Factory.fastestInstance();
    private static LZ4Compressor compressor = factory.fastCompressor();
    private static FileInputStream fis;
    private static FileOutputStream fos;

    public static void compressFile(File input,String output){
        File out=new File(output);
        try {
            if (!out.exists()) out.createNewFile();
        fis=new FileInputStream(input);
            fos=new FileOutputStream(out);
            int idx=0;
            long len=input.length();
            long max=1000000000;
            if(len<max) {
                copy(len);
                fis.close();
                fos.close();

            }else{
                int loop=(int)(len/max);
                int offset=(int)(len%max);
                for(int i=0;i<loop;i++){
                    copy(max);
                }
                copy(offset);
                fis.close();fos.close();

            }
        }catch (Exception e){
            write(e.getMessage()+"\nError While Creating new file");}
    }
    private static void copy(long len) throws Exception{
        byte[] data=new byte[(int)len];
        fis.read(data,0,data.length);
        byte[] compress=compress(data,data.length);
        fos.write(compress,0,compress.length);

    }
    public static byte[] compress(byte[] src, int srcLen) {
        decompressedLength = srcLen;
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressLen = compressor.compress(src, 0, decompressedLength, compressed, 0, maxCompressedLength);
        byte[] finalCompressedArray = Arrays.copyOf(compressed, compressLen);
        return finalCompressedArray;
    }

}
