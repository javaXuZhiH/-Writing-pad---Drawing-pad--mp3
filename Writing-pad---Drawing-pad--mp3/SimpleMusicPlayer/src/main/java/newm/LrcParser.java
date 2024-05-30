package newm;
import newm.Lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class LrcParser {
	static DecimalFormat df = new DecimalFormat("#.00");
    public static Lrc parse(List<String> lineList)
    {	
        Lrc lrc = new Lrc();//实例化TreeMap
        for (String line : lineList) {
            line = line.toLowerCase();
            // 唱片集
            if (line.startsWith("[al:")) {
                lrc.setAlbum(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[ar:")) {
                // 演唱者
                lrc.setArtist(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[au:")) {
                // 歌词作者
                lrc.setAuthor(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[by:")) {
                // LRC制作者
                lrc.setBy(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[re:")) {
                // 此LRC文件的创建者或编辑器
                lrc.setRe(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[ti:")) {
                // 歌词标题
                lrc.setTitle(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[ve:")) {
                // 程序的版本
                lrc.setVe(line.substring(4, line.length() - 1));
            } else if (line.startsWith("[ver:")) {
                // 程序的版本
                lrc.setVe(line.substring(5, line.length() - 1));
            } else if(line.equals("")) {
            	
            }else {
                int delimiterIndex = line.indexOf("]");
                String time = line.substring(1, delimiterIndex);
                double min,sec,sum;
                min=(time.charAt(0)-'0')*10+(time.charAt(1)-'0');//分中
                sec=(time.charAt(3)-'0')*10+(time.charAt(4)-'0');
                if(time.length()>=8) sec+=(time.charAt(6)-'0')*0.1+(time.charAt(7)-'0')*0.01;
                sum=min*60+sec;
                String lrcContent = line.substring(delimiterIndex + 1);
                if(lrcContent.length()>=3&&lrcContent.charAt(0)==' '&&lrcContent.charAt(1)==' '&&lrcContent.charAt(2)==' ') lrcContent="-------";
                lrc.getLyric().put(Double.parseDouble(df.format(sum)), lrcContent);
              
            }
        }
        return lrc;
    }
    public Lrc LRC(String f) {
    	InputStreamReader read;
    	 ArrayList<String> readList =new ArrayList<String>();
		try {
			read = new InputStreamReader(new FileInputStream(f),"GBK");
			BufferedReader reader=new BufferedReader(read); 
	    	String temp=null;
	    	
	    	while((temp=reader.readLine())!=null &&!"".equals(temp)){  
				readList.add(temp);  
	        }
	        read.close(); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Lrc lrc = parse(readList);
		return lrc; 	
    }
}