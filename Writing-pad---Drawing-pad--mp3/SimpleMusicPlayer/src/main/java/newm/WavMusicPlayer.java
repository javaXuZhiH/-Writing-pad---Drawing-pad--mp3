
package newm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.GainChangeEvent;
import javax.media.GainChangeListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;

import javax.media.Time;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class WavMusicPlayer extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel panel;
	JPanel panel1,panel3;
	JScrollPane panel2;
	JSlider voiceSlider;
	JMenuBar menubar;
	JMenu menu1;
	JMenuItem itemOpen, addlrc,item2, item1, item3, item4, item5;
	JPopupMenu popupmenu, popupmenu1;
	JButton playbutton, Stopbutton, lastbutton, nextbutton;
	JLabel label1, label2, label3, label4;
	JList<String> PlayList;
	JTextField lrcText; 
	//JLabel lrcText;
	int lastAudioLevel = 40;
	int isplayComplete = 0;
	DefaultListModel<String> listContent;
	String filename, playingSongname; // 文件名
	File fildnames[];
	JComboBox playModeBox;
	int playmode=0;
	int ms=0,ns=0;
	HashMap<String, String> mapString;// time+name->path
	HashMap<String, String> mapStringTime;//time+name->name
	HashMap<String, String> mapStringName;//name->time+name
	HashMap<String, String> mapStringLrc;//Lrcname->lrcpath
	String playStringpath;
	boolean isplaying = false;
	boolean ispause = false;
	Player player;

	Component mediaControl; // 视频播放控制组件
	//暂停时间
	//Time pausedTime = null;
	Timer time;
	LrcParser lrcparser=new LrcParser();
	Map<Double, String> lrc;
	


	void Init() {
		panel = new JPanel();
		panel1 = new JPanel();
		panel3=new JPanel();
		label1 = new JLabel("音 乐 播 放 器");
		label1.setFont(new Font("楷体",Font.BOLD,17));
		label1.setHorizontalAlignment(SwingConstants.CENTER);	
		label3 = new JLabel("播放列表");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		menubar = new JMenuBar();
		menu1 = new JMenu("添加 ");
		itemOpen = new JMenuItem("歌曲");
		itemOpen.addActionListener(this);
		addlrc=new JMenuItem("LRC歌词");
		addlrc.addActionListener(this);
		menu1.add(itemOpen);
//		menu1.add(addlrc);
		menubar.add(menu1);
		setJMenuBar(menubar);
		popupmenu = new JPopupMenu();
		popupmenu1 = new JPopupMenu();
		item1 = new JMenuItem("播放");
		item3 = new JMenuItem("移除");
		item4 = new JMenuItem("全部清除");
		item5 = new JMenuItem("全部清除");
		MouseListener popupListener = new popupListener();
		item1.addMouseListener(popupListener);
		item3.addMouseListener(popupListener);
		item4.addMouseListener(popupListener);
		item5.addMouseListener(popupListener);
		popupmenu.add(item1);
		popupmenu.add(item3);
		popupmenu.add(item4);
		popupmenu1.add(item5);
		playbutton = new JButton("播放");
		Stopbutton = new JButton("停止");
		lastbutton = new JButton("上一首");
		nextbutton = new JButton("下一首");
		playbutton.addActionListener(this);
		Stopbutton.addActionListener(this);
		lastbutton.addActionListener(this);
		nextbutton.addActionListener(this);
		panel1.add(lastbutton);
		panel1.add(playbutton);
		panel1.add(Stopbutton);
		panel1.add(nextbutton);
		final String modeString[]= {"顺序播放"};
		playModeBox=new JComboBox(modeString);
		playModeBox.addActionListener(this);
		panel1.add(playModeBox);
		voiceSlider = new JSlider();
		voiceSlider.setMinimum(0);
		voiceSlider.setMaximum(80);
		voiceSlider.setValue(40);
		voiceSlider.setPreferredSize(new Dimension(250,30));
		voiceSlider.setBorder(BorderFactory.createTitledBorder("声音"));
//		panel1.add(voiceSlider);
		//lrcText=new JLabel("暂无歌词");
		lrcText=new JTextField();
		lrcText.setPreferredSize(new Dimension(260,30));
		lrcText.setHorizontalAlignment(JTextField.CENTER);
		lrcText.setBackground(new Color(252, 249, 21, 239));
		lrcText.setFont(new Font("楷体",Font.BOLD,15));
		lrcText.setBorder(BorderFactory.createLineBorder(new Color(0,0,252)));
		lrcText.setVisible(false);
		panel1.add(lrcText);
		Color c = new Color(236, 142, 252);
		panel1.setBackground(c);
		panel.setBackground(Color.green);
		panel.setLayout(new BorderLayout());
		panel.add(panel1, BorderLayout.CENTER);
		panel.add(label1, BorderLayout.NORTH);
		add(panel);
		listContent = new DefaultListModel<String>();
		PlayList = new JList<String>(listContent);
		PlayList.setBackground(c);
		PlayList.setFont(new Font("楷体", Font.PLAIN, 20));
		mapString = new HashMap<String, String>();
		mapStringTime = new HashMap<String, String>();
		mapStringName = new HashMap<String, String>();
		panel2 = new JScrollPane(PlayList);
		panel3.setBackground(Color.green);
		panel3.setLayout( new BorderLayout());
		panel3.add(panel2, BorderLayout.CENTER);
		panel3.add(label3, BorderLayout.NORTH);
		add(panel3);
		mapStringLrc=new HashMap<String, String>();
		PlayList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					System.out.println("鼠标点击了一下");
				}
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) { // 双击时处理
					if (listContent.isEmpty() == false) {
						playbutton.setText("暂停");
						label1.setText("正在播放  :   " + mapStringTime.get(PlayList.getSelectedValue().toString()));
						if (ispause || isplaying)
							player.close();
						if(mapStringLrc.get(playingSongname)!=null) time.stop();
						//lrcText.setText("暂无歌词");
						lrcText.setVisible(false);
						playingSongname = mapStringTime.get(PlayList.getSelectedValue().toString());
						audioplay();
						isplaying = true;
						player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
					if (PlayList.isSelectionEmpty() == false) {
						popupmenu.show(PlayList, e.getX(), e.getY());
						System.out.println("鼠标右击,出现小菜单");
					} else if (listContent.isEmpty() == false && PlayList.isSelectionEmpty()) {
						popupmenu1.show(PlayList, e.getX(), e.getY());
						System.out.println("鼠标右击,出现清除");
					}
				}

			}
		});
		voiceSlider.addChangeListener(new ChangeListener() {


			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				lastAudioLevel = voiceSlider.getValue();
				int volune = voiceSlider.getValue();
				System.out.println(volune);
				// System.out.println(player.getGainControl().getLevel());
				// player.getGainControl().setDB(0);
				if (ispause || isplaying) // player!=null
				{
					player.getGainControl().setLevel((float) 0.01 * volune);
				} else {
					System.out.println("没有播放时调节的音量大小*******" + lastAudioLevel);
				}
			}
		});

	}
	
	// 以下完成事件的处理,多个按钮
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == itemOpen) // 文件打开，可以多选
		{
			FileDialog fd = new FileDialog(this, "打开媒体文件", FileDialog.LOAD);
			fd.setMultipleMode(true);
			fd.setVisible(true);
			String fileDirpath=fd.getDirectory();//得到当前目录
			System.out.println(fileDirpath);
			System.out.println(Arrays.toString(fd.getFiles()));
			fildnames = fd.getFiles();
			for (int i = 0; i < fildnames.length; i++) {
				if (fildnames[i].toString().endsWith(".wav")) {
					int index = fildnames[i].getName().lastIndexOf('.');
					String tep = fildnames[i].getName().substring(0, index);
					String flag = mapStringName.get(tep);
					if (flag == null || listContent.isEmpty() || (flag != null && listContent.indexOf(flag) == -1)) {
						String time = "xx:xx";
						int min = 0, sec = 0;
						try {
							sec = ReadWavTime(fildnames[i].toString()) % 60;
							min = ReadWavTime(fildnames[i].toString()) / 60;

							if (sec / 10 == 0) {
								time = min + ":0" + sec;
								mapStringTime.put("[" + time + "]" + tep, tep);
							} else {
								time = min + ":" + sec;
								mapStringTime.put("[" + time + "]" + tep, tep);
							}

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						mapStringName.put(tep, "[" + time + "]" + tep);
						listContent.addElement("[" + time + "]" + tep);
						mapString.put("[" + time + "]" + tep, fildnames[i].toString());
					}
				}else if(fildnames[i].toString().endsWith(".lrc")) {
					if(fildnames[i].toString().endsWith(".lrc")) {
						int index = fildnames[i].getName().lastIndexOf('.');
						String tep = fildnames[i].getName().substring(0, index);
						mapStringLrc.put(tep,fildnames[i].toString() );
					}
					
				}
			}
		
		}
		if(e.getSource()==addlrc)
		{
			FileDialog fd=new FileDialog(this, "打开LRC", FileDialog.LOAD);
			fd.setMultipleMode(true);
			fd.setVisible(true);
			fildnames = fd.getFiles();
			for(int i=0;i<fildnames.length;i++) 
			{
				if(fildnames[i].toString().endsWith(".lrc")) {
					int index = fildnames[i].getName().lastIndexOf('.');
					String tep = fildnames[i].getName().substring(0, index);
					mapStringLrc.put(tep,fildnames[i].toString() );
				}
			}
			if(mapStringLrc.get(playingSongname)!=null) {
				 lrc=lrcparser.LRC(mapStringLrc.get(playingSongname)).getLyric();
				 LrcTime();
				 if(ns==0) {
						setSize(new Dimension(353,450));
						ns++;
					}
					else if(ns==1) {
						setSize(new Dimension(353,451));
						ns=0;
					}
			}
	
			
		}
		if (e.getSource() == playbutton) {
			if (playbutton.getText().equals("播放") && listContent.isEmpty() == false) {
				if(playmode!=3) {
					if (PlayList.isSelectionEmpty()) PlayList.setSelectedIndex(0);
				}else {
					if (PlayList.isSelectionEmpty()) {
						Random random=new Random();// 定义随机类
						int nextindex=random.nextInt(listContent.getSize());//右边不包括
						PlayList.setSelectedIndex(nextindex);		
					}	
				}
				playbutton.setText("暂停");
				if (isplaying || ispause)// player!=null
				{
					// playStringpath=mapString.get(PlayList.getSelectedValue());
					if (ispause == true) // 有暂停的,继续播放
					{
						//player.setMediaTime(pausedTime==null?new Time(0):pausedTime);
						//player.setStopTime(pausedTime==null?new Time(0):pausedTime);
						player.start();
						ispause = false;
						if(mapStringLrc.get(playingSongname)!=null)time.start();
						label1.setText("正在播放  :   " + playingSongname);
					}
				} else if (isplaying == false && ispause == false) // player==null
				{
					label1.setText("正在播放  :   " + mapStringTime.get(PlayList.getSelectedValue().toString()));
					playingSongname = mapStringTime.get(PlayList.getSelectedValue().toString());
					audioplay();
				}
				isplaying = true;
				player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
				
			} else if (playbutton.getText().equals("暂停")) {
				pausePlaying();
				playbutton.setText("播放");
				label1.setText("已暂停  :  " + playingSongname);
			}
		}
		if (e.getSource() == Stopbutton)// 停止按钮
		{
			stopPlaying();
		}
		if (e.getSource() == nextbutton) {
			if(playmode!=3)nextSong();
			else nextSongRandom();
		}
		if (e.getSource() == lastbutton) {
			if(playmode!=3) lastSong();
			else lastSongRandom();
		}
		if(e.getSource()==playModeBox) {
			if(playModeBox.getSelectedIndex()==0) {
				playmode=0;
			}else if(playModeBox.getSelectedIndex()==1) {
				playmode=1;
			}else if(playModeBox.getSelectedIndex()==2) {
				playmode=2;
			}else if(playModeBox.getSelectedIndex()==3) {
				playmode=3;
			}
		}

	}

	// 以上是监听事件方法
	public void lastSong() {
		if (listContent.isEmpty() == false) {
			if (isplaying || ispause)// player!=null
			{
				player.close();
				// player=null;
			}
			isplaying = true;
			ispause = false;
			playbutton.setText("暂停");
			int index;
			if (playingSongname == null)
				index = listContent.getSize();
			else
				index = listContent.indexOf(mapStringName.get(playingSongname));
			int lastindex = (index - 1 + listContent.getSize()) % listContent.getSize();
			if(mapStringLrc.get(playingSongname)!=null) {
				time.stop();
				//System.out.println("测试"+playingSongname);
			}
			lrcText.setText("---");
			lrcText.setVisible(false);
			label1.setText("正在播放  :  " + mapStringTime.get(listContent.elementAt(lastindex)));
			playingSongname = mapStringTime.get(listContent.elementAt(lastindex).toString());
			playStringpath = mapString.get(listContent.elementAt(lastindex));
			PlayList.setSelectedIndex(lastindex);
			audioplay();
			player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
			//selectItemScroll(lastindex);
		}
	}
	public void selectItemScroll(int x) {
		Point p=PlayList.indexToLocation(x);
        if(p != null) 
        {
            JScrollBar jScrollBar = panel2.getVerticalScrollBar();//获得垂直滚动条  
            jScrollBar.setValue(p.y);//设置垂直滚动条位置
        }
	}
	public void lastSongRandom() {
		if (listContent.isEmpty() == false) {
			if (isplaying || ispause)// player!=null
			{
				player.close();
			}
			isplaying = true;
			ispause = false;
			playbutton.setText("暂停");
			int index;
			if (playingSongname == null)
				index = listContent.getSize();
			else
				index = listContent.indexOf(mapStringName.get(playingSongname));
			Random random=new Random();// 定义随机类
			int lastindex=random.nextInt(listContent.getSize());//右边不包括
			if(lastindex==index) lastindex=(lastindex-1+listContent.getSize())%listContent.getSize();
			if(mapStringLrc.get(playingSongname)!=null) time.stop();
			lrcText.setText("---");
			lrcText.setVisible(false);
			label1.setText("正在播放  :  " + mapStringTime.get(listContent.elementAt(lastindex)));
			playingSongname = mapStringTime.get(listContent.elementAt(lastindex).toString());
			//selectItemScroll(lastindex);
			playStringpath = mapString.get(listContent.elementAt(lastindex));
			PlayList.setSelectedIndex(lastindex);
			audioplay();
			player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
		}
	}
	public void nextSong() {
		if (listContent.isEmpty() == false) {
			if (isplaying || ispause)// player!=null
			{
				player.close();
				// player=null;
			}
			isplaying = true;
			ispause = false;
			playbutton.setText("暂停");
			int index;
			if (playingSongname == null)
				index = -1;
			else
				index = listContent.indexOf(mapStringName.get(playingSongname));
			if(playmode==2&&index==listContent.getSize()-1)//顺序播放
			{
				stopPlaying();
			}
			else
			{
				int nextindex = (index + 1) % listContent.getSize();
				if(mapStringLrc.get(playingSongname)!=null) time.stop();
				lrcText.setText("---");
				lrcText.setVisible(false);
				label1.setText("正在播放  :  " + mapStringTime.get(listContent.elementAt(nextindex)));
				playingSongname = mapStringTime.get(listContent.elementAt(nextindex));
			
				playStringpath = mapString.get(listContent.elementAt(nextindex));
				PlayList.setSelectedIndex(nextindex);
				audioplay();
				//selectItemScroll(nextindex);
				player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
			}
			
		}
	}
	public void nextSongRandom() {
		if (listContent.isEmpty() == false) {
			if (isplaying || ispause)
			{
				player.close();
			}
			isplaying = true;
			ispause = false;
			playbutton.setText("暂停");
			int index = listContent.indexOf(mapStringName.get(playingSongname));
			Random random=new Random();// 定义随机类
			int nextindex=random.nextInt(listContent.getSize());//右边不包括
			if(nextindex==index) nextindex=(nextindex+1)%listContent.getSize();
			if(mapStringLrc.get(playingSongname)!=null) time.stop();
			lrcText.setText("---");
			lrcText.setVisible(false);
			label1.setText("正在播放  :  " + mapStringTime.get(listContent.elementAt(nextindex)));
			playingSongname = mapStringTime.get(listContent.elementAt(nextindex));
			playStringpath = mapString.get(listContent.elementAt(nextindex));
			PlayList.setSelectedIndex(nextindex);
			//selectItemScroll(nextindex);
			audioplay();
			player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
		}
	}
	// 下面是右击小菜单的事件监听
	public class popupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getSource() == item1)// 播放,不管当前是否正在播放，全部取消，播放点击的歌曲
			{
				playbutton.setText("暂停");
				label1.setText("正在播放  :  " + mapStringTime.get(PlayList.getSelectedValue().toString()));
				if(mapStringLrc.get(playingSongname)!=null) time.stop();
				lrcText.setText("---");
				lrcText.setVisible(false);
				playingSongname = mapStringTime.get(PlayList.getSelectedValue().toString());
				if (ispause || isplaying) {
					player.close();
				}	
				audioplay();
				player.getGainControl().setLevel((float) (lastAudioLevel * 0.01));
				System.out.print("右击播放");
				isplaying = true;
				ispause = false;
			}
			if (e.getSource() == item3)// 移除
			{
				if (playingSongname != null)// 当前有歌曲播放或者暂停
				{
					if (PlayList.getSelectedValue().toString().compareTo(mapStringName.get(playingSongname)) == 0)// 停止播放，移除此歌曲
					{
						if (ispause || isplaying) {
							player.close();
						}
						if(mapStringLrc.get(playingSongname)!=null) time.stop();
						lrcText.setText("---");
						lrcText.setVisible(false);
						// player=null;
						playbutton.setText("播放");
						label1.setText("简 易 音 乐 播 放 器");
						isplaying = false;
						ispause = false;
						playingSongname = null;
						mediaControl.setVisible(false);
					}
					
					//移除的不是播放暂停的歌曲，直接移除
					mapString.remove(PlayList.getSelectedValue().toString());
					listContent.removeElement(PlayList.getSelectedValue().toString());
				} else {// 无播放
					
					mapString.remove(PlayList.getSelectedValue().toString());
					listContent.removeElement(PlayList.getSelectedValue().toString());
				}
				if(mapStringLrc.get(playingSongname)!=null) time.stop();
				lrcText.setText("---");	
				lrcText.setVisible(false);
			}
			if (e.getSource() == item4)// 歌曲项，右击全部清除
			{
				if (playingSongname != null) {
					player.close();

				}
				if(mapStringLrc.get(playingSongname)!=null) time.stop();
				lrcText.setText("---");
				lrcText.setVisible(false);
				// player=null;
				playbutton.setText("播放");
				label1.setText("简 易 音 乐 播 放 器");
				isplaying = false;
				ispause = false;
				playingSongname = null;
				listContent.removeAllElements();
				mediaControl.setVisible(false);
				
			}
			if (e.getSource() == item5)// 空白右击全部清除
			{
				if(mapStringLrc.get(playingSongname)!=null) time.stop();
				lrcText.setText("---");
				lrcText.setVisible(false);
				listContent.removeAllElements();
				mediaControl.setVisible(false);
			}
		}
	}

	public void audioplay() {
			if (mediaControl != null) {
				panel.remove(mediaControl); // 如果对象mediaControl非空则移去
				//player.close();
			}
		try {
		
			playStringpath = mapString.get(PlayList.getSelectedValue());
			player = Manager.createRealizedPlayer(new MediaLocator("file:" + playStringpath));
			getMediaComponents();
			player.addControllerListener(new PlayerControllerListener() );
			player.getGainControl().addGainChangeListener(new GainChangeListener() {

				public void gainChange(GainChangeEvent e) {
					// TODO Auto-generated method stub
					voiceSlider.setValue((int)(e.getLevel()*100));
				}
				
			});
			player.start();
		} catch (Exception e1) {
			System.out.println(e1);
			System.out.println("audioPlay 报错报错报错");
		}
		selectItemScroll(listContent.indexOf(mapStringName.get(playingSongname)));
		if(mapStringLrc.get(playingSongname)!=null) {
			lrc=lrcparser.LRC(mapStringLrc.get(playingSongname)).getLyric();
			LrcTime();
		}else {
			lrcText.setText("---");
			lrcText.setVisible(false);
		}
	}



	public void pausePlaying() // 暂停播放
	{
		if (isplaying == true) {
			if(mapStringLrc.get(playingSongname)!=null)time.stop();
			player.stop();// 暂停
			// player.setMediaTime(new Time(0));重新开始
			isplaying = false;
			ispause = true;
		}
	}
	public void stopPlaying() {
		if (isplaying || ispause)// player!=null
		{
			if(mapStringLrc.get(playingSongname)!=null)time.stop();
			lrcText.setText("---");
			lrcText.setVisible(false);
			player.close();
			// player!=null
			isplaying = ispause = false;
			playbutton.setText("播放");
			label1.setText("简 易 音 乐 播 放 器");
			playingSongname = null;
			PlayList.clearSelection();
			mediaControl.setVisible(false);
		}
	}
	public void LrcTime() {
		ActionListener lrctimelistener=new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				double currenttime=player.getMediaTime().getSeconds();
				BigDecimal bg = new BigDecimal(currenttime);
	            double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	            if(lrc.get(f1)!=null) {
					lrcText.setText(lrc.get(f1));
				}
					
			}
		};
		lrcText.setVisible(true);
		time=new Timer(0,lrctimelistener);
		time.addActionListener(lrctimelistener);
		time.start();
	}
	public void getMediaComponents(){
		mediaControl = player.getControlPanelComponent(); // 取得播放控制组件
		if (mediaControl != null) {
			//setBounds(320, 120, 353, 450);
			panel.add(mediaControl,BorderLayout.SOUTH);
			if(ms==0) {
				setSize(new Dimension(353,451));
				ms++;
			}
			else if(ms==1) {
				setSize(new Dimension(353,450));
				ms=0;
			}
		}
	}
	public int ReadWavTime(String str) throws IOException {
		RandomAccessFile rdf = new RandomAccessFile(str, "r");
		int q, i;
		q = ToInt(Read(rdf, 4, 4));
		i = ToInt(Read(rdf, 28, 4));
		rdf.close();
		return q / i;
	}

	public  int ToInt(byte[] b) {
		return ((b[3] & 0xff) << 24) + ((b[2] & 0xff) << 16) + ((b[1] & 0xff) << 8) + (b[0] & 0xff);
	}

	public  byte[] Read(RandomAccessFile rdf, int pos, int length) throws IOException {
		rdf.seek(pos);
		byte result[] = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = rdf.readByte();
		}
		return result;
	}

	public class PlayerControllerListener implements ControllerListener{
			public void controllerUpdate(ControllerEvent event) {
				if (event.getClass().getName().equals("javax.media.StartEvent")) {
					//System.out.println("开始了");
					label1.setText("正在播放  :  " + mapStringTime.get(PlayList.getSelectedValue().toString()));
					isplaying = true;
					playbutton.setText("暂停");
				}
				if (event.getClass().getName().equals("javax.media.EndOfMediaEvent")) {
					lrcText.setText("---");
					lrcText.setVisible(false);
					if(mapStringLrc.get(playingSongname)!=null) time.stop();
					player.setMediaTime(new Time(0));
					playbutton.setText("播放");
					isplaying=false;
					if(playmode==0) {
						ispause=false;
						label1.setText("播放完毕  :  "+playingSongname);
					}
					else if(playmode==1) {
						
						player.start();
						if(mapStringLrc.get(playingSongname)!=null) LrcTime();
					}
					else if(playmode==2) nextSong();
					else if(playmode==3) nextSongRandom();
					
				}
				if (event.getClass().getName().equals("javax.media.StopByRequestEvent")) {
				
						
						label1.setText("已暂停  :  " + playingSongname);
						//pausedTime = player.getMediaTime();
						ispause = true;
						playbutton.setText("播放");
				}
			}
	}
	public WavMusicPlayer() {
		setTitle(" Lalilali player");
		setLayout(new GridLayout(2, 1));
		Init();
		setBounds(320, 120, 353, 450);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String args[]) {
		WavMusicPlayer MainInterface = new WavMusicPlayer();
	}
}