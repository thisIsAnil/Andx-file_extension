package andx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 3/14/2017.
 */

class Frame {
    List<Databytes> images,videos,gifs,audios,scripts;
    String text;
    byte[] frameHeader,framedataHeader,textHeader,imageHeader,videoHeader,gifHeader,audioHeader,scriptsHeader;
    byte[] frameFooter,frameDataFooter,textFooter,imageFooter,videoFooter,gifFooter,audioFooter,scriptsFooter;
    long frameSize,textSize,imagesSize,gifsSize,videosSize,audiosSize,scriptsSize;
    int compressionCode;
    List<Long> imageSizes,gifsSizes,videoSizes,audioSizes;
    public Frame(){
        images=new ArrayList<>();
        videos=new ArrayList<>();
        gifs=new ArrayList<>();
        audios=new ArrayList<>();
        imageSizes=new ArrayList<>();
        gifsSizes=new ArrayList<>();
        videoSizes=new ArrayList<>();
        audioSizes=new ArrayList<>();

        scripts=new ArrayList<>();

    }

}
