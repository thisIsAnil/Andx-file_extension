package andx;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 3/1/2017.
 */

public class AndxEncoderCore {


    private static String TAG="AndxEncoder";
    static final int K64=64*1024;
    private FileOutputStream fos;
    private List<Frame> frames=new ArrayList<>();
    private String encoding,output;
    private byte[] startBytes;
    public AndxEncoderCore(String encoding, String output){
        this.encoding=Charset.isSupported(encoding)?encoding:Charset.defaultCharset().displayName();
        this.output=output;
        try {
            File f=new File(output);
            f.createNewFile();
            fos=new FileOutputStream(output);
            startBytes=Utils.getHeader(encoding);
        }catch (Exception e){
            write(e.getMessage());
        }
    }

    //general format to store any kind of data is data length->data->data.footer where data can text, list of images,videos,gifs etc
    public void addFrameData(String text, List<String> images, List<String> gifs, List<String> videos, List<String> audios, List<Script> scripts, byte compressionCode) throws Exception{
        Frame frame=new Frame();

        frame.compressionCode=compressionCode;
        long size=text==null?(long)0:(long)text.getBytes().length;
        frame.textSize=size;
        if(text!=null){
            frame.text=text;
            write(text+"\n");
        }
        frame.textFooter=Utils.END_OF_TEXT_DATA.getBytes();
        write("TextData Added");
        //adding images to frame
        frame.imagesSize=images==null?0:images.size();
        if(images!=null){
            frame.images=getDataBytesFromFiles(images,Utils.START_OF_IMAGE,Utils.END_OF_IMAGE);
        }
        frame.imageFooter=(Utils.END_OF_IMAGE_DATA).getBytes(Charset.forName(encoding));
        write("ImageData Added");
        //adding gifs data
        frame.gifsSize=gifs==null?0:gifs.size();
        if(gifs!=null){
            frame.gifs=getDataBytesFromFiles(gifs,Utils.START_OF_GIF,Utils.END_OF_GIF);
        }
        frame.gifFooter=(Utils.END_OF_GIF_DATA).getBytes(Charset.defaultCharset());
        write("GifData Added");
        //adding videos to frame
        frame.videosSize=videos==null?0:videos.size();
        if(videos!=null){
            frame.videos=getDataBytesFromFiles(videos,Utils.START_OF_VIDEO,Utils.END_OF_VIDEO);
        }
        frame.videoFooter=(Utils.END_OF_VIDEO_DATA).getBytes(Charset.forName(encoding));
        write("VideoData Added");
        //adding audios to frame
        frame.audiosSize=audios==null?0:audios.size();
        if(audios!=null){
            frame.audios=getDataBytesFromFiles(audios,Utils.START_OF_AUDIO,Utils.END_OF_AUDIO);
        }
        frame.audioFooter=(Utils.END_OF_AUDIO_DATA).getBytes(Charset.forName(encoding));
        write("Audio data added ");
        //adding scripts here scripts can be anyfile which can be viewed by browser eg css js pdf etc.
        frame.scriptsSize=scripts==null?0:scripts.size();
        if(scripts!=null){
            frame.scripts=getDataBytesFromScripts(scripts);
        }
        frame.scriptsFooter=(Utils.END_OF_SCRIPT_DATA).getBytes(Charset.forName(encoding));
        frame.frameDataFooter=(Utils.END_OF_FRAME_DATA).getBytes(Charset.forName(encoding));
        frame.frameFooter=(Utils.END_OF_FRAME).getBytes(Charset.forName(encoding));
        write("Frame Data Written\nAppending Header");

        frame=appendHeaders(frame);
        write("Header Appended");
        frames.add(frame);
    }
    private Frame appendHeaders(Frame frame) throws Exception{
        long[] sizes=getFrameSize(frame);
        write("Calculated all sizes");
        String frameData=Utils.getFrameDataHeader(frame.compressionCode);
        frame.framedataHeader=frameData.getBytes();

        frameData=Utils.START_OF_TEXT_DATA+Utils.START_OF_SIZE+sizes[1]+Utils.END_OF_SIZE;
        frame.textHeader=frameData.getBytes();
        write("Text Header Appended:"+new String(frame.textHeader,Charset.defaultCharset()));

        frame.imageHeader=getHeaderFor(Utils.START_OF_IMAGE_DATA,sizes[2],frame.imagesSize);

        frame.gifHeader=getHeaderFor(Utils.START_OF_GIF_DATA,sizes[3],frame.gifsSize);

        frame.videoHeader=getHeaderFor(Utils.START_OF_VIDEO_DATA,sizes[4],frame.videosSize);

        frame.audioHeader=getHeaderFor(Utils.START_OF_AUDIO_DATA,sizes[5],frame.audiosSize);

        frame.scriptsHeader=getHeaderFor(Utils.START_OF_SCRIPT_DATA,sizes[6],frame.scriptsSize);
        long hlen=computeFrameHeadersLength(frame)+sizes[0];
        frame.frameHeader =Utils.getFrameHeader(hlen,encoding);
        write("Frame Len="+new String(frame.frameHeader,Charset.defaultCharset()));
        return frame;


    }
    private byte[] getHeaderFor(String h_start,long length,long size){
        return (h_start+Utils.START_OF_SIZE+length+"//_//"+size+Utils.END_OF_SIZE).getBytes();
    }
    private long computeFrameHeadersLength(Frame f){
        return f.framedataHeader.length+f.textHeader.length+f.imageHeader.length+f.gifHeader.length+f.videoHeader.length+f.audioHeader.length
                + f.frameDataFooter.length+f.textFooter.length+f.imageFooter.length+f.gifFooter.length+f.videoFooter.length+f.audioFooter.length
                +f.scriptsHeader.length+f.scriptsFooter.length+f.frameFooter.length;
    }
    public void updateFrameText(int frameId,String text){
        Frame f=frames.remove(frameId);
        f.text=text;
        f.textHeader=(Utils.START_OF_TEXT_DATA+Utils.START_OF_SIZE+text.length()+Utils.END_OF_SIZE).getBytes();
        frames.add(frameId,f);
    }
    public void updateFrameImages(int frameId,int id,String images){
        Frame f=frames.remove(frameId);
        f.images.remove(id);
        f.images.add(id,getDataBytesFromFile(images,Utils.START_OF_IMAGE,Utils.END_OF_IMAGE));
        f.imageHeader=getHeaderFor(Utils.START_OF_IMAGE_DATA,computeSizeFromList(f.images),f.imagesSize);
        frames.add(frameId,f);
    }
    public void updateFrameGifs(int frameId,int id,String gifs){
        Frame f=frames.remove(frameId);
        f.gifs.remove(id);
        f.gifs.add(id,getDataBytesFromFile(gifs,Utils.START_OF_IMAGE,Utils.END_OF_IMAGE));
        f.gifHeader=getHeaderFor(Utils.START_OF_IMAGE_DATA,computeSizeFromList(f.gifs),f.imagesSize);
        frames.add(frameId,f);
    }
    public void updateFrameVideos(int frameId,int id,String videos){
        Frame f=frames.remove(frameId);
        f.videos.remove(id);
        f.videos.add(getDataBytesFromFile(videos,Utils.START_OF_VIDEO,Utils.END_OF_VIDEO));
        f.videoHeader=getHeaderFor(Utils.START_OF_VIDEO_DATA,computeSizeFromList(f.videos),f.videosSize);
        frames.add(frameId,f);

    }
    public void updateFrameAudios(int frameId,int id,String audios){
        Frame f=frames.remove(frameId);
        f.audios.remove(id);
        f.audios.add(getDataBytesFromFile(audios,Utils.START_OF_AUDIO,Utils.END_OF_AUDIO));
        f.audioHeader=getHeaderFor(Utils.START_OF_IMAGE_DATA,computeSizeFromList(f.audios),f.audiosSize);
        frames.add(frameId,f);
    }
    public void updateFrameScripts(int frameId,int id,String scripts){
        Frame f=frames.remove(frameId);
        f.scripts.remove(id);
        f.scripts.add(getDataBytesFromFile(scripts,Utils.START_OF_SCRIPT,Utils.END_OF_SCRIPT));
        f.scriptsHeader=getHeaderFor(Utils.START_OF_SCRIPT_DATA,computeSizeFromList(f.scripts),f.scriptsSize);
        frames.add(frameId,f);
    }
    public void closeFile(){
        try{
            fos.close();
        }catch (Exception e){}
    }
    public void saveAllFrames() throws Exception{
        File fs=new File("/andx_files/"+System.nanoTime()+"tmp.dat");
        if(fs.exists())fs.delete();
        fs.createNewFile();
        fos=new FileOutputStream(fs);
        fos.write(startBytes,0,startBytes.length);
        byte[] frameCount=(Utils.START_OF_SIZE+frames.size()+Utils.END_OF_SIZE).getBytes();
        fos.write(frameCount,0,frameCount.length);
        write("Frame Count:"+new String(frameCount,Charset.defaultCharset()));
        for(Frame f:frames)writeFrameToFile(f);
        write("Written all frame.\nsending to compressor");
        AndxCompressor.compressFile(fs,output);
        write("Compressed successfully");
        fs.delete();

    }
    private void writeFrameToFile(Frame frame) throws Exception{
            fos.write(frame.frameHeader,0,frame.frameHeader.length);
            fos.write(frame.framedataHeader,0,frame.framedataHeader.length);
            fos.write(frame.textHeader);
            byte[] texts=frame.text.getBytes(Charset.forName(encoding));
            fos.write(texts);
            write("Text written to file:");
            fos.write(frame.textFooter);
            write("Text Footer Written");
            fos.write(frame.imageHeader);
            write("Image Header Written");
            write("Images Count:"+frame.images.size());
            for(int i=0;i<frame.images.size();i++){
                Databytes db=frame.images.get(i);
                write("Image File"+i+" Contains:"+db.bytes.size()+"byte[]");
                for(int j=0;j<db.bytes.size();j++){
                    fos.write(db.bytes.get(j));
                }

            }
            write("Image Data Written");
            fos.write(frame.imageFooter);
            write("Image Footer written");
            fos.write(frame.gifHeader);
            for(int i=0;i<frame.gifs.size();i++){
                Databytes db=frame.gifs.get(i);
                write("Gif File"+i+" Contains:"+db.bytes.size()+"byte[]");
                for(int j=0;j<db.bytes.size();j++){fos.write(db.bytes.get(j));}
            }
            write("Gif Data Written");
            fos.write(frame.gifFooter);
            fos.write(frame.videoHeader);
            for(int i=0;i<frame.videos.size();i++){
                Databytes db=frame.videos.get(i);
                write("Video File"+i+" Contains:"+db.bytes.size()+"byte[]");
                for(int j=0;j<db.bytes.size();j++){fos.write(db.bytes.get(j));}
            }
            write("Video Data written");
            fos.write(frame.videoFooter);
            fos.write(frame.audioHeader);
            for(int i=0;i<frame.audios.size();i++){
                Databytes db=frame.audios.get(i);
                write("Audio File"+i+" Contains:"+db.bytes.size()+"byte[]");
                for(int j=0;j<db.bytes.size();j++){fos.write(db.bytes.get(j));}
            }
              write("Audio Data Written");
            fos.write(frame.audioFooter);
            fos.write(frame.scriptsHeader);
            for(int i=0;i<frame.scripts.size();i++){
                Databytes db=frame.scripts.get(i);
                write("Script File"+i+"Contains"+db.bytes.size()+"byte[]\n");
                for(int j=0;j<db.bytes.size();j++){
                    fos.write(db.bytes.get(j));
                }
            }
            write("Script Data Written");
            fos.write(frame.scriptsFooter);
            fos.write(frame.frameDataFooter);
            fos.write(frame.frameFooter);
            write("Frame Written Completely");

    }
    private long[] getFrameSize(Frame f){
        long i_size=0;long v_size=0;long g_size=0;long a_size=0;long s_size=0;
        long size=0;
        long t_size=f.textSize;

        i_size=computeSizeFromList(f.images);
        write("Computed images length"+i_size);
        g_size=computeSizeFromList(f.gifs);
        write("Computed gifs length"+g_size);
        a_size=computeSizeFromList(f.audios);
        write("Computed audios length"+a_size);
        v_size=computeSizeFromList(f.videos);
        write("Computed videos length"+v_size);
        s_size=computeSizeFromList(f.scripts);
        size=i_size+g_size+v_size+a_size+t_size+s_size;

        return new long[]{size,t_size,i_size,g_size,v_size,a_size,s_size};
    }
    private long computeSizeFromList(List<Databytes> f){
        long i_size=0;
        if(f==null||f.size()==0)return 0;
        for(int i=0;i<f.size();i++){
            Databytes db=f.get(i);
            i_size+=db.size();
        }
        return i_size;
    }
    private List<Databytes> getDataBytesFromScripts(List<Script> scripts){
        List<Databytes> databytes=new ArrayList<>();
        for(Script s:scripts){
            databytes.add(getDataByteFromScript(s));
        }
        return databytes;
    }
    private Databytes getDataByteFromScript(Script script){
            Databytes databytes = new Databytes();
            long len = 0;
            for (String s : script.script) {
                databytes.bytes.add(s.getBytes(Charset.forName(encoding)));
            }
            byte[] header = (Utils.START_OF_SCRIPT+Utils.START_OF_PATH + script.type + Utils.END_OF_PATH + Utils.START_OF_SIZE + len + Utils.END_OF_SIZE).getBytes(Charset.forName(encoding));
            databytes.bytes.add(0, header);
            byte[] footer=(Utils.END_OF_SCRIPT).getBytes();
            databytes.bytes.add(footer);
            return databytes;

    }
    private List<Databytes> getDataBytesFromFiles(List<String> gifs, String h_start, String h_end){

        List<Databytes> databyte=new ArrayList<>();
        write("Total files:"+gifs.size());
        for(int i=0;i<gifs.size();i++){
                databyte.add(getDataBytesFromFile(gifs.get(i),h_start,h_end));

             }
        return databyte;

    }
    private Databytes getDataBytesFromFile(String url,String h_start,String h_end){
        if(url.startsWith("https")||url.startsWith("http")){
            Databytes databytes=new Databytes();
            List<byte[]> data=new ArrayList<byte[]>();
            data.add(url.getBytes());
            databytes.bytes=data;
            return databytes;
        }
        File b=new File(url);
        List<byte[]> datas=new ArrayList<>();
        byte[] bytes=null;
        byte[] h=(h_start+Utils.START_OF_SIZE+(long)b.length()+Utils.END_OF_SIZE).getBytes();
        datas.add(h);
        try {
            long len=b.length();
            int max=100000000;
            int loop=(int)(len/max);
            int offset=(int)(len%max);
            write("Loop:"+loop+" Offset:"+offset+"  File Size:"+len);
            FileInputStream fis = new FileInputStream(b);
            for(int i=0;i<loop;i++){
                bytes=new byte[max];
                int id=fis.read(bytes,0,max);
                datas.add(bytes);
            }
            if(offset!=0){
                byte[] bytes1=new byte[offset];
                fis.read(bytes1,0,offset);
                datas.add(bytes1);
            }

        }catch (Exception e){
            write(e.getMessage()+"\nFailed for:"+b.getName());
        }finally {
            h=(h_end).getBytes();
            datas.add(h);

        }
        Databytes databyt=new Databytes();
        databyt.bytes=datas;
        return databyt;

    }
    private void write(String s){
        System.out.println(s);
    }
}
