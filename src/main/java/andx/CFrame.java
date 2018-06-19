package andx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 3/29/2017.
 */

class CFrame {
    private List<File> org_images,org_videos,org_gifs,org_audios,org_scripts,images,videos,gifs,audios,scripts;
    private String org_text,text;
    public CFrame(){
        images=videos=gifs=audios=scripts=new ArrayList<>();
        org_images=org_videos=org_audios=org_gifs=org_scripts=new ArrayList<>();
    }

    public List<File> getOrg_images() {
        return org_images;
    }

    public void setOrg_images(List<File> org_images) {
        this.org_images = org_images;
    }

    public List<File> getOrg_videos() {
        return org_videos;
    }

    public void setOrg_videos(List<File> org_videos) {
        this.org_videos = org_videos;
    }

    public List<File> getOrg_gifs() {
        return org_gifs;
    }

    public void setOrg_gifs(List<File> org_gifs) {
        this.org_gifs = org_gifs;
    }

    public List<File> getOrg_audios() {
        return org_audios;
    }

    public void setOrg_audios(List<File> org_audios) {
        this.org_audios = org_audios;
    }

    public List<File> getOrg_scripts() {
        return org_scripts;
    }

    public void setOrg_scripts(List<File> org_scripts) {
        this.org_scripts = org_scripts;
    }

    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }

    public List<File> getVideos() {
        return videos;
    }

    public void setVideos(List<File> videos) {
        this.videos = videos;
    }

    public List<File> getGifs() {
        return gifs;
    }

    public void setGifs(List<File> gifs) {
        this.gifs = gifs;
    }

    public List<File> getAudios() {
        return audios;
    }

    public void setAudios(List<File> audios) {
        this.audios = audios;
    }

    public List<File> getScripts() {
        return scripts;
    }

    public void setScripts(List<File> scripts) {
        this.scripts = scripts;
    }

    public String getOrg_text() {
        return org_text;
    }

    public void setOrg_text(String org_text) {
        this.org_text = org_text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
