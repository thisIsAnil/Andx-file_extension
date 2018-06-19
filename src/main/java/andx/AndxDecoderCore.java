package andx;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 3/14/2017.
 */

public class AndxDecoderCore {

    FileInputStream fis;
    String input;
    String encoding;
    String password;
    int compressionCode;
    DecodedFrame decodedFrame;
    long frameCount;
    File cache;
    public AndxDecoderCore(String input, String tempFile, File cacheDir) throws Exception{
        this.input= tempFile;
        this.cache =cacheDir;
        write("Input File is:"+input+"\nTmp File"+this.input);
        AndxDecompressor.decompressFile(new File(input),this.input);
        write("Decompressed");
        fis=new FileInputStream(this.input);
        write("Loaded file");
        decodedFrame=new DecodedFrame();

        byte[] extension=new byte[5];
        fis.read(extension,0,5);
        write("Reading extension:"+new String(extension,Charset.defaultCharset()));

        if(!new String(extension,Charset.defaultCharset()).equals(Utils.HEADER)){
            throw new UnsupportedEncodingException("Unsupported file format");
        }
        fis.skip(3);
        byte[] enc=new byte[10];                        //encoding length 10bytes
        fis.read(enc,0,enc.length);
        encoding=new String(enc,Charset.defaultCharset()).replaceAll("\\s","");     //remove padded whitespaces
        write("Encoding is;"+encoding);
        fis.skip(8);                                    //skip password for now TODO: get the encrypted password and decrypt it from jni call using conceal.
        fis.skip(3);
        frameCount=getSize();
        write("FrameCount:"+frameCount);
        fis.skip(9);                                    //skip EOS,start of frame and frame size next bytes are frame length until we encounter EOS
    }

    public long getFrameCount() {
        return frameCount;
    }

    public String getEncoding() {
        return encoding;
    }

    public DecodedFrame getNextFrame(DecodedFrame decodedFrame) throws Exception{

        decodedFrame=getFrameMetaData(decodedFrame);
        decodedFrame=decodeText(decodedFrame);
        decodedFrame=decodeImage(decodedFrame);
        decodedFrame=decodeGif(decodedFrame);
        decodedFrame=decodeVideo(decodedFrame);
        decodedFrame=decodeAudio(decodedFrame);
        decodedFrame=decodeScipts(decodedFrame);
        return decodedFrame;

    }
    public void closeFile(){
        try{
            fis.close();
            File f=new File(input);
            f.delete();
        }catch (Exception e){}
    }
    public DecodedFrame getFrame(int n) throws Exception{
            DecodedFrame decodedFrame=new DecodedFrame();
            moveToFrame(n);
            return getNextFrame(decodedFrame);
    }
    public void moveToFrame(int n) throws Exception{

        fis=new FileInputStream(input);

        byte[] extension=new byte[5];
        fis.read(extension,0,extension.length);

        if(!new String(extension).equals(Utils.HEADER)){
            throw new UnsupportedEncodingException("Unsupported file format.Not a PCX File");
        }
        fis.skip(3);
        skip(10);
        fis.skip(8);                                    //skip password for now
        fis.skip(3);
        frameCount=getSize();
        write("SkipTo :"+n);
        write("Calculated frameCount"+frameCount);
        fis.skip(6);                                    //skip comprCode,start of frame and frame size next bytes are frame length until we encounter EOS

        for(int i=0;i<n;i++) {
            skip(getSize());
            skip(6);
        }


    }
    public List<String> getTextFromAllFrames() throws Exception{
        List<String> texts=new ArrayList<>();
        for(int i=0;i<frameCount;i++){
            moveToFrame(i);
            DecodedFrame decodedFrame=new DecodedFrame();
            decodedFrame=getFrameMetaData(decodedFrame);
            decodedFrame=decodeText(decodedFrame);
            texts.add(decodedFrame.text);
            write("Decoded Text:"+decodedFrame.text);
        }
        return texts;
    }
    private DecodedFrame decodeText(DecodedFrame decodedFrame) throws Exception{
        decodedFrame.textLength=getSize();
        if(decodedFrame.textLength==0){
            decodedFrame.text="";
            return decodedFrame;
        }
        write("Text Length"+(int)decodedFrame.textLength);
        byte[] textBytes=new byte[(int)decodedFrame.textLength];
        fis.read(textBytes,0,textBytes.length);
        String s=new String(textBytes,Charset.forName(encoding));
        decodedFrame.text=s;
        write("Text is"+s);
        skip(9);

        return decodedFrame;
    }
    private DecodedFrame getFrameMetaData(DecodedFrame decodedFrame) throws Exception{
        decodedFrame.frameLength=getSize();
        write("Frame Length"+decodedFrame.frameLength);
        skip(3);
        byte[] bytes=new byte[2];
        fis.read(bytes,0,2);
        decodedFrame.compressionCode=bytes[0];
        write("Compression Code"+new String(bytes,Charset.defaultCharset()));
        skip(6);
        return decodedFrame;

    }

    private DecodedFrame decodeImage(DecodedFrame decodedFrame) throws Exception{
        String s=getSizeString();
        if(s.equals("0")){
            decodedFrame.imagesCount=0;
            decodedFrame.imagesSize=0;
            decodedFrame.images=new ArrayList<>();
            skip(12);
            return decodedFrame;
        }
        String[] longs=s.split("//_//");
        if(longs.length<2)throw new Exception("Could not get length and count of image");
        decodedFrame.imagesSize=Long.parseLong(longs[0]);
        decodedFrame.imagesCount=Long.parseLong(longs[1]);
        write("ImageSize:"+decodedFrame.imagesSize+"\nImage count:"+decodedFrame.imagesCount);
        skip(6);
        for(int i=0;i<decodedFrame.imagesCount;i++) {
            File f = createFileFromBytes(cache.getAbsolutePath()+"/image"+i+".jpg");
            if (f != null) decodedFrame.images.add(f);
            skip(9);
        }
        skip(3);
        return decodedFrame;

    }
    private DecodedFrame decodeGif(DecodedFrame decodedFrame) throws Exception{
        String s=getSizeString();
        if(s.equals("0")){
            decodedFrame.gifsCount=0;
            decodedFrame.gifsSize=0;
            decodedFrame.gifs=new ArrayList<>();
            skip(12);
            return decodedFrame;
        }
        String[] longs=s.split("//_//");
        if(longs.length<2)throw new Exception("Could not get length and count of image");
        decodedFrame.gifsSize=Long.parseLong(longs[0]);
        decodedFrame.gifsCount=Long.parseLong(longs[1]);
        skip(6);
        for(int i=0;i<decodedFrame.gifsCount;i++) {
            File f = createFileFromBytes(cache.getAbsolutePath()+"/gif"+i+".gif");
            if (f != null) decodedFrame.gifs.add(f);
            skip(9);
        }
        skip(3);
        return decodedFrame;

    }
    private DecodedFrame decodeVideo(DecodedFrame decodedFrame) throws Exception{
        String s=getSizeString();
        if(s.equals("0")){
            decodedFrame.videoCount=0;
            decodedFrame.videosSize=0;
            decodedFrame.videos=new ArrayList<>();
            skip(12);
            return decodedFrame;
        }
        String[] longs=s.split("//_//");
        if(longs.length<2)throw new Exception("Could not get length and count of image");
        decodedFrame.videosSize=Long.parseLong(longs[0]);
        decodedFrame.videoCount=Long.parseLong(longs[1]);
        skip(6);
        for(int i=0;i<decodedFrame.videoCount;i++) {
            File f = createFileFromBytes(cache.getAbsolutePath()+"/video"+i+".mp4");
            if (f != null) decodedFrame.videos.add(f);
            skip(9);
        }
        skip(3);
        return decodedFrame;

    }
    private DecodedFrame decodeAudio(DecodedFrame decodedFrame) throws Exception{
        String s=getSizeString();
        if(s.equals("0")){
            decodedFrame.audioCount=0;
            decodedFrame.audiosSize=0;
            decodedFrame.audios=new ArrayList<>();
            skip(12);
            return decodedFrame;
        }
        String[] longs=s.split("//_//");
        if(longs.length<2)throw new Exception("Could not get length and count of image");
        decodedFrame.audiosSize=Long.parseLong(longs[0]);
        decodedFrame.audioCount=Long.parseLong(longs[1]);
        skip(6);
        for(int i=0;i<decodedFrame.audioCount;i++) {
            File f = createFileFromBytes(cache.getAbsolutePath()+"/audio"+i+".mp3");
            if (f != null) decodedFrame.audios.add(f);
            skip(9);
        }
        skip(3);
        return decodedFrame;

    }
    private DecodedFrame decodeScipts(DecodedFrame decodedFrame) throws Exception {
        String s=getSizeString();
        if(s.equals("0")){
            decodedFrame.scriptsCount=0;
            decodedFrame.scriptsSize=0;
            decodedFrame.scripts=new ArrayList<>();
            skip(12);
            return decodedFrame;
        }
        String[] longs=s.split("//_//");
        if(longs.length<2)throw new Exception("Could not get length and count of script");
        decodedFrame.scriptsSize=Long.parseLong(longs[0]);
        decodedFrame.scriptsCount=Long.parseLong(longs[1]);
        skip(6);
        for(int i=0;i<decodedFrame.scriptsCount;i++) {
            String type=getSizeString();
            File f = createFileFromBytes(cache.getAbsolutePath()+"/"+type);
            if (f != null) decodedFrame.scripts.add(f);
            skip(9);
        }
        skip(3);
        return decodedFrame;

    }



    private File createFileFromBytes(String output) throws Exception{

        long size=getSize();
        if(size==0)return null;
        int inc=1;
        List<byte[]> fileData=new ArrayList<>();
        int max=100000000;
        int it=(int)size/(max);
        int rem=(int)size%(max);
        for(int i=0;i<it;i++){
            byte[] bytes=new byte[max];
            fis.read(bytes,0,bytes.length);
            fileData.add(bytes);
        }
        if(rem!=0){
            byte[] bytes=new byte[rem];
            fis.read(bytes,0,bytes.length);
            fileData.add(bytes);
        }
        File img=new File(output);
        if(img.exists())img.delete();
        img.createNewFile();
        FileOutputStream fos=new FileOutputStream(img);
        for(byte[] b:fileData){
            fos.write(b,0,b.length);
        }
        return img;

    }
    private void skip(long n){
        if(n<1)return;
        long s;
        try{
            s=fis.skip(n);
        }catch (Exception e){
            s=-1;
        }
        if(s==-1||s!=n){
            for(int i=0;i<n;i++){
                try {
                    fis.read();
                }catch (Exception e){

                }
            }
        }
    }
    private String getSizeString() throws Exception {
        byte[] bytes=new byte[1];
        List<Byte> valueBytes=new ArrayList<>();
        bytes=new byte[1];
        fis.read(bytes,0,1);

        if(bytes[0]==(byte)'E'){
            skip(2);
            return "0";
        }
        else valueBytes.add(bytes[0]);

        write(new String(bytes,Charset.defaultCharset()));

        while (bytes[0]!=(byte)'E'){
            bytes=new byte[1];
            fis.read(bytes,0,1);
            if(bytes[0]!=(byte)'E') {
                valueBytes.add(bytes[0]);
                write(new String(new byte[]{valueBytes.get(valueBytes.size()-1)}, Charset.defaultCharset()));
            }
        }
        fis.skip(2);
        byte[] valB=new byte[valueBytes.size()];
        for(int i=0;i<valueBytes.size();i++){
            valB[i]=valueBytes.get(i);
        }
        String re=new String(valB, Charset.defaultCharset());
        write("Long val:"+re);

        return re;

    }

    public static void write(String s){
        System.out.println(s);
    }
    private long getSize() throws Exception{
        return Long.parseLong(getSizeString());
    }
}
