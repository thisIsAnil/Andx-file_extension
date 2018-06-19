package andx;



import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import static andx.AndxDecoderCore.write;

/**
 * Created by INFIi on 3/17/2017.
 */

public class AndxDecompressor {
    private static LZ4Factory factory = LZ4Factory.fastestInstance();
    private static LZ4SafeDecompressor decompressor = factory.safeDecompressor();

    private static FileInputStream fis;
    private static FileOutputStream fos;

    public static void decompressFile(File input, String output) throws Exception{
        File out=new File(output);
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
       }
    private static void copy(long len) throws Exception{
        byte[] data=new byte[(int)len];
        fis.read(data,0,data.length);
        write("Read data from file");
        byte[] decompress=decompress(data,data.length);
        write("Executed de compressor");
        fos.write(decompress,0,decompress.length);

    }

    public static byte[] decompress(byte[] finalCompressedArray, int compressedLength) {
        byte[] restored = new byte[compressedLength*3];
        int decompressLen = decompressor.decompress(finalCompressedArray, 0,compressedLength,restored,0);
        byte[] finalDeCompressedArray = Arrays.copyOf(restored, decompressLen);
        return finalDeCompressedArray;
    }
}
