package andx;



import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 3/15/2017.
 */

class DecodedFrame {
    List<File> images,videos,gifs,audios,scripts;
    String text;
    int compressionCode;
    long frameLength;
    long textLength,imagesSize,videosSize,gifsSize,audiosSize,scriptsSize;
    long imagesCount,gifsCount,videoCount,audioCount, scriptsCount;


    public DecodedFrame(){
        images=videos=gifs=audios=scripts=new ArrayList<>();
    }

    public List<File> getAudios() {
        return audios;
    }

    public List<File> getImages() {
        return images;
    }

    public List<File> getVideos() {
        return videos;
    }

    public List<File> getGifs() {
        return gifs;
    }

    public long getImagesCount() {
        return imagesCount;
    }

    public long getVideoCount() {
        return videoCount;
    }

    public long getGifsCount() {
        return gifsCount;
    }

    public long getAudioCount() {
        return audioCount;
    }

    public List<File> getScripts() {
        return scripts;
    }

    public long getScriptsCount() {
        return scriptsCount;
    }

    public String getText() {
        return text;
    }
}
