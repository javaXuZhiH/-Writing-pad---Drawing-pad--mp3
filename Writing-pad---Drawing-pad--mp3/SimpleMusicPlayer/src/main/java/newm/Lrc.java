package newm;
import java.util.Map;
import java.util.TreeMap;

public class Lrc {
 
    // 唱片集
    private String album;
    // 演唱者
    private String artist;
    // 歌词作者
    private String author;
    // 此LRC文件的创建者
    private String by;
    // 创建此LRC文件的播放器或编辑器
    private String re;
    // 歌词标题
    private String title;
    // 程序的版本
    private String ve;
    // 歌词正文
    private Map<Double, String> lyric;
 
    public Lrc() {
        lyric = new TreeMap<Double, String>();
 
    }
 
   
    public void setAlbum(String album) {
        this.album = album;
    }
 
    public String getArtist() {
        return artist;
    }
 
    public void setArtist(String artist) {
        this.artist = artist;
    }
 
    public void setAuthor(String author) {
        this.author = author;
    }
 
 
    public void setBy(String by) {
        this.by = by;
    }

    public void setRe(String re) {
        this.re = re;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }

 
    public void setVe(String ve) {
        this.ve = ve;
    }
 
    public Map<Double, String> getLyric() {
        return lyric;
    }
 
    public void setLyric(Map<Double, String> lyric) {
        this.lyric = lyric;
    }
}