package sampler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

public class MTG {
	
	public static final int CROPX1 = 19;
	public static final int CROPY1 = 37;
	public static final int CROPX2 = 205;
	public static final int CROPY2 = 172;
	
	MtgWindow ww;
	
	public static final int NOACTIVITY = 0;
	public static final int PULLACTIVITY = 1;
	public static final int SETUPACTIVITY = 2;
	public static final int SAMPLEACTIVITY = 3;
	public static final int CLEARACTIVITY = 4;
	public static final int ERRORACTIVITY = 5;
	public static final int SAVEACTIVITY = 6;
	public static int activity;
	
	static int newacross;
	static int newcardwidth;
	static double newscale;
	
	static int basecardheight;
	static int basecardwidth;
	
	static int poolsize;
	
	private int cindex;
	static BufferedImage[] cards;
	
	private int findex;
	static BufferedImage[] full;
	
	static int oindex;
	static Color[] colors;
	
	static int pulload;
	static int pullamm;
	
	static BufferedImage def;
	static BufferedImage resample;
	static BufferedImage desample;
	static int[][][] lit;
	double lratio;
	
	private ExecutorService exe;
	
	private String uni = System.getProperty("user.home") + "/Desktop/dump";
	
	public BufferedImage buffer(Image i){
		BufferedImage b = new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = b.createGraphics();
		g.drawImage(i, 0, 0, null);
		return b;	
	}
	
	public void setup(){
		desample = null;
		cindex = 0;
		oindex = 0;
		findex = 0;
		getPools();
		if(poolsize>0){
			getLit();
			BufferedImage tsss = full[0];
			basecardheight = tsss.getHeight();
			basecardwidth = tsss.getWidth();
			tsss = null;
		}
	}
	
	public void getLit(){
 		int ccount  =0;
 		int ls = 16;
 		int total = ls*ls*ls;
 		lit = new int[ls][ls][ls]; 
 		lratio = ls/256.0;
 		for(int i =0;i<poolsize;i++){
 			Color cc = colors[i];
 			int offr=(int)(cc.getRed()*lratio);
 			int offg=(int)(cc.getGreen()*lratio);
 			int offb=(int)(cc.getBlue()*lratio);
 			if(lit[offr][offg][offb]==0){
 				ccount++;
 				lit[offr][offg][offb] = i+1;
 			}
 		}
 		int numpos =6;
 		int[][] pos = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
 		int[][][] tlit = new int[ls][ls][ls];
 		while(ccount<total){
 			for(int r =0;r<ls;r++){
 				for(int g =0;g<ls;g++){
 					for(int b =0;b<ls;b++){
 						tlit[r][g][b] = lit[r][g][b];//the templit becomes a clone of the actual lit
 					}
 				}
 			}
 			for(int r =0;r<ls;r++){//go through everything
 				for(int g =0;g<ls;g++){
 					for(int b =0;b<ls;b++){
 						
 						if(lit[r][g][b] != 0){//if on the current spot in the actual lit is filled in
 							int sel = lit[r][g][b];//the image to be drawn is that
 							
 							for(int p=0;p<numpos;p++){
 							//for(int z=-1;z<=1;z++){//for a 1*1 area around that square
 							//	for(int y=-1;y<=1;y++){
 							//		for(int x=-1;x<=1;x++){
 										
 										int rz = r+pos[p][0];
 										int gy = g+pos[p][1];
 										int bx = b+pos[p][2];
 										
 										if(rz>-1&&rz<ls&&gy>-1&&gy<ls&&bx>-1&&bx<ls&&lit[rz][gy][bx]==0&&tlit[rz][gy][bx]==0){//if it's in bounds, if theres nothing there on the temp lit or the actual lit //&&tlit[rz][gy][bx]==0
 											tlit[rz][gy][bx] = sel;//place
 											ccount++;
 										}
 										
 									//}
 								//}
 							//}
 							}
 							
 						}
 						
 					}
 				}
 			}
 			for(int r =0;r<ls;r++){//the actual lit becomes a copy of the temporary lit
 				for(int g =0;g<ls;g++){
 					for(int b =0;b<ls;b++){
 						lit[r][g][b] = tlit[r][g][b];
 					}
 				}
 			}
 		}
 	}
	
	/*for(int r =0;r<ls;r++){
		for(int g =0;g<ls;g++){
			for(int b =0;b<ls;b++){
				tlit[r][g][b] = lit[r][g][b];
			}
		}
	}
	for(int r =0;r<ls;r++){
		for(int g =0;g<ls;g++){
			for(int b =0;b<ls;b++){
				
				if(lit[r][g][b] != 0){
					int sel = lit[r][g][b];
					
					for(int z=-1;z<=1;z++){
						for(int y=-1;y<=1;y++){
							for(int x=-1;x<=1;x++){
								
								int rz = r+z;
								int gy = g+y;
								int bx = b+x;
								
								if(rz>-1&&rz<ls&&gy>-1&&gy<ls&&bx>-1&&bx<ls&&lit[rz][gy][bx]==0&&tlit[rz][gy][bx]==0){
									tlit[r+z][g+y][b+x] = sel;
									ccount++;
								}
								
							}
						}
					}
					
				}
				
			}
		}
	}
	for(int r =0;r<ls;r++){
		for(int g =0;g<ls;g++){
			for(int b =0;b<ls;b++){
				lit[r][g][b] = tlit[r][g][b];
			}
		}
	}*/
	
	public void addtocolors(Color c){
		colors[oindex] = c;
		oindex++;
	}
	public void addtocards(BufferedImage b){
		cards[cindex] = b;
		cindex++;
	}
	public void addtofull(BufferedImage b){
		full[findex] =b;
		findex++;
	}
	
	public void getPools(){
		pulload = 0;
		File folder = new File(uni);
		File[] files = folder.listFiles();
		poolsize = files.length;
		cards = new BufferedImage[poolsize];
		full = new BufferedImage[poolsize];
		colors = new Color[poolsize];
		for(int i=0;i<poolsize;i++){
			try{
				BufferedImage ti = ImageIO.read(files[i]);
				addtocards(ti);
				addtofull(ti);
				addtocolors(getAvg(ti));
				pulload = i+1;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public BufferedImage crop(BufferedImage b, int x1, int y1, int x2, int y2){
		BufferedImage nn = new BufferedImage(x2-x1,y2-y1,BufferedImage.TYPE_INT_ARGB);
		for(int y=y1;y<y2;y++){
			for(int x=x1;x<x2;x++){
				try{
					nn.setRGB(x-x1, y-y1, b.getRGB(x,y));
				}catch(Exception ex){
					nn.setRGB(x-x1, y-y1, 54645);
				}
			}
		}
		return nn;
	}	
	public BufferedImage crop(BufferedImage b){
		BufferedImage nn = new BufferedImage(CROPX2-CROPX1,CROPY2-CROPY1,BufferedImage.TYPE_INT_ARGB);
		for(int y=CROPY1;y<CROPY2;y++){
			for(int x=CROPX1;x<CROPX2;x++){
				try{
					nn.setRGB(x-CROPX1, y-CROPY1, b.getRGB(x,y));
				}catch(Exception ex){
					nn.setRGB(x-CROPX1, y-CROPY1, 54645);
				}
			}
		}
		return nn;
	}
	
	public Color getAvg(BufferedImage b){
		int bwid = b.getWidth();
		int bhid = b.getHeight();
		int cst = bhid*bwid;
		Color[] cs = new Color[cst];
		int t=0;
		for(int y=0;y<b.getHeight();y++){
			for(int x=0;x<b.getWidth();x++){
				cs[t] = new Color(b.getRGB(x, y));
				t++;
			}
		}
		int[] tots = new int[3];
		for(int i=0;i<cst;i++){
			tots[0] += cs[i].getRed();
			tots[1] += cs[i].getGreen();
			tots[2] += cs[i].getBlue();
		}
		return new Color(tots[0]/cst,tots[1]/cst,tots[2]/cst);
	}
	
	public BufferedImage scale(BufferedImage b, int iw, int ih){
		BufferedImage bb = new BufferedImage(iw,ih,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bb.createGraphics();
		g.drawImage(b, 0, 0, bb.getWidth(), bb.getHeight(),null);
		return bb;
	}
	
	public void scaleall(int picw, int pich){
		BufferedImage[] angel = new BufferedImage[poolsize];
		for(int i=0;i<poolsize;i++){
			angel[i] = scale(full[i],picw,pich);
		}
		cards = angel.clone();
		cindex = 0;
	}
	
	public boolean supersample(){
		
		try{
			resample = ImageIO.read(new File(ww.inp.getText()));
			newacross = Integer.parseInt(ww.wis.getText());
			newcardwidth = Integer.parseInt(ww.csi.getText());
			
			int picw = (int)(newcardwidth);
			int pich = (int)(newcardwidth*((double)basecardheight/basecardwidth));
			
			scaleall(picw,pich);
			
			int origh = resample.getHeight();
			int origw = resample.getWidth();
			
			int hhh = (int)(newacross*((double)origh/origw)*((double)picw/pich));
			int www = newacross;
			
			BufferedImage sampp = new BufferedImage(www,hhh,BufferedImage.TYPE_INT_RGB);
			desample = new BufferedImage(www*picw,hhh*pich,BufferedImage.TYPE_INT_ARGB);
			
			double wiw = ((double)origw/www);
			double hih = ((double)origh/hhh);
			
			Graphics g = desample.createGraphics();
			for(int y=0;y<hhh;y++){
				for(int x=0;x<www;x++){
					sampp.setRGB(x, y, getAvg(crop(resample,(int)(x*wiw),(int)(y*hih),(int)(x*wiw+wiw),(int)(y*hih+hih))).getRGB());
					Color tc = new Color(sampp.getRGB(x, y));
					int vari = lit[(int)(tc.getRed()*lratio)][(int)(tc.getGreen()*lratio)][(int)(tc.getBlue()*lratio)]-1;
					g.drawImage(cards[vari], x*picw, y*pich, picw, pich, null);
					//superwait(1);
				}
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public void supersave(){
		File f;
		int c=0;
		while(true){
			f = new File(System.getProperty("user.home") + "/Desktop/sampled/" + "samp"+c+".png");
			if(!f.exists()){
				break;
			}else{
				c++;
			}
		}
		try {
			ImageIO.write(desample, "PNG", f);
		}catch (IOException ex){}
	}
	
	public void superpull(){
		pullamm = Integer.parseInt(ww.pn.getText());
		pulload = 0;
		for(int i=0;i<pullamm;i++){
			boolean p = pull();
			if(p){
				pulload = (i+1);
			}else{
				i--;
			}
		}
	}
	
	public boolean pull(){
		BufferedImage now;
		int numpass = (int)(Math.random()*420617+43579);
		now = getCard(numpass);
		if(same(now, def)){
			return false;
		}else{
			File f = new File(uni + "\\" + numpass + ".png");
			try {
				ImageIO.write(now, "PNG", f);
			} catch (IOException ex){
				ex.printStackTrace();
			}
			return true;
		}
	}
	
	public void superclear(){
		File f = new File(uni);
		File[] clist = f.listFiles();
		for(int i=0;i<clist.length;i++){
			clist[i].delete();
		}
	}
	
	public MTG(){
		File df = new File(uni);
		if (!df.exists()){
			df.mkdir();
		}
		df = new File(System.getProperty("user.home") + "/Desktop/sampled");
		if (!df.exists()){
			df.mkdir();
		}
		
		ww = new MtgWindow();
		
		try{ def = ImageIO.read(getClass().getResource("Image.jpg"));} catch (IOException e) {}
		def = crop(def);
		
		exe = Executors.newFixedThreadPool(3);
		exe.execute(new PaintLoop());
		activity = SETUPACTIVITY;
		exe.execute(new GameLoop());
	}
	
	public class GameLoop implements Runnable{
		public void run(){
			while(true){
				if(activity==NOACTIVITY){
					Math.random();
				}else if(activity==SETUPACTIVITY){
					setup();
					activity = NOACTIVITY;
				}else if(activity==PULLACTIVITY){
					superpull();
					activity = SETUPACTIVITY;
				}else if(activity==SAMPLEACTIVITY){
					if(supersample()){;
						activity = NOACTIVITY;
					}else{
						activity = ERRORACTIVITY;
					}
				}else if(activity==CLEARACTIVITY){
					superclear();
					activity = SETUPACTIVITY;
				}else if(activity==ERRORACTIVITY){
					superwait(500);
					activity = NOACTIVITY;
				}else if(activity==SAVEACTIVITY){
					supersave();
					activity = NOACTIVITY;
				}
			}
		}
	}
	
	public void superwait(int milis){
		Long goal = System.currentTimeMillis()+milis;
		while(System.currentTimeMillis()<goal){}
	}
	
	public class PaintLoop implements Runnable{
		public void run(){
			while(true){
				ww.gopaint();
			}
		}
	}
	
	public String genHex(int l){
		String hchars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String m = "";
		for(int i=0;i<l;i++){
			m += hchars.charAt((int)(Math.random()*hchars.length()));
		}
		return m;
	}
	
	public BufferedImage getCard(int index){
		BufferedImage image = null;
		try{
			image = ImageIO.read(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+index+"&type=card"));
		}catch(Exception ex){}
		try{
			image = crop(image);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return image;
	}
	
	public boolean same(BufferedImage b0, BufferedImage b1){
		boolean r=true;
		for(int y=0;y<b0.getHeight();y+=60){
			for(int x=0;x<b0.getWidth();x+=60){
				try{
					if(b0.getRGB(x, y) != b1.getRGB(x, y)){
						r=false;
					}
				}catch(Exception e){}
			}
		}
		return r;
	}
	
	//FOR TESTING PURPOSES ONLY
	public BufferedImage samp(BufferedImage b, int d){
		BufferedImage samp = new BufferedImage(b.getWidth(),b.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<b.getHeight();y+=d){
			for(int x=0;x<b.getWidth();x+=d){
				samp.setRGB(x, y, b.getRGB(x,y));
			}
		}
		return samp;
	}

	public static void main(String[] args){
		new MTG();
	}
}
