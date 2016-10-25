package sampler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RandMtg {
	
	Window ww;
	LinkedList<BufferedImage> cards = new LinkedList<BufferedImage>();
	LinkedList<Color> colors = new LinkedList<Color>();
	int times = 500;
	BufferedImage def;
	BufferedImage resample;
	BufferedImage desample;
	BufferedImage[][] ppp;
	int[][][] lit;
	
	public void getPools(String path){
		File folder = new File(path);
		File[] files = folder.listFiles();
		cards.clear();
		colors.clear();
		for(int i=0;i<files.length;i++){
			try{
				BufferedImage ti = ImageIO.read(files[i]);
				cards.add(ti);
				colors.add(getAvg(ti));
				System.out.println("Got "+i+" out of "+files.length);
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
	
	public Color getAvg(BufferedImage b){
		LinkedList<Color> cs = new LinkedList<Color>();
		for(int y=0;y<b.getHeight();y+=3){
			for(int x=0;x<b.getWidth();x+=3){
				cs.add(new Color(b.getRGB(x, y)));
			}
		}
		int[] t = new int[3];
		for(int i=0;i<cs.size();i++){
			t[0] += cs.get(i).getRed();
			t[1] += cs.get(i).getGreen();
			t[2] += cs.get(i).getBlue();
		}
		return new Color(t[0]/cs.size(),t[1]/cs.size(),t[2]/cs.size());
	}
	
	public BufferedImage scale(BufferedImage b, int iw, int ih){
		BufferedImage bb = new BufferedImage(iw,ih,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bb.createGraphics();
		g.drawImage(b, 0, 0, bb.getWidth(), bb.getHeight(),null);
		return bb;
	}
	
	public BufferedImage getImgur(){
		BufferedImage fff = null;
		try{ fff = ImageIO.read(getClass().getResource("removed.png"));} catch (IOException e) {}
		BufferedImage temp = null;
		String hchars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		while(true){
			String f4 = "";
			for(int i=0;i<(int)(Math.random()*(7-4+1)+4);i++){
				f4 += hchars.charAt((int)(Math.random()*hchars.length()));
			}
			try{
				temp = ImageIO.read(new URL("http://i.imgur.com/"+f4+".jpg"));
				if(!same(temp,fff) && temp != null){
					break;
				}
			}catch(Exception ex){}
		}
		return temp;
	}
	//COPYRIGHT 2016 EMMETT GLASER
	
	public void foundln(int www, int hhh, int ind, int x, int y){
		System.out.println("Found! - "+ind+" - "+Math.round(((double)(y*www)+(double)(x%www))/(double)(hhh*www)*100)+"%");
	}
	
	public void resample(BufferedImage s, LinkedList<BufferedImage> pool, LinkedList<Color> cpool, int picw, int pich, double scl, boolean rep){
		LinkedList<BufferedImage> angel = new LinkedList<BufferedImage>();
		for(int i=0;i<cards.size();i++){
			angel.add(scale(cards.get(i),picw,pich));
		}
		cards.clear();
		for(int i=0;i<angel.size();i++){
			cards.add(angel.get(i));
		}
		int ccount  =0;
		int ls = 27;
		lit = new int[ls][ls][ls]; //BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB
		double lratio = (double)ls/(double)256;
		for(int i =0;i<cpool.size();i++){
			Color cc = cpool.get(i);
			if(lit[(int)(cc.getRed()*lratio)][(int)(cc.getGreen()*lratio)][(int)(cc.getBlue()*lratio)]==0){
				ccount++;
			}
			lit[(int)(cc.getRed()*lratio)][(int)(cc.getGreen()*lratio)][(int)(cc.getBlue()*lratio)] = i+1;
		}
		int[][][] tlit = new int[ls][ls][ls];
		while(ccount<ls*ls*ls){
			for(int r =0;r<ls;r++){
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
										if(r+z>-1&&r+z<ls&&g+y>-1&&g+y<ls&&b+x>-1&&b+x<ls&&lit[r+z][g+y][b+x]==0&&tlit[r+z][g+y][b+x]==0){
											tlit[r+z][g+y][b+x] = sel;
											ccount++;
											System.out.println(ccount+" | "+r+" "+g+" "+b);
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
			}
		}
		
		int hhh = (int)((double)s.getHeight()/(double)pich*scl);
		int www = (int)((double)s.getWidth()/(double)picw*scl);
		
		BufferedImage sampp = new BufferedImage(www,hhh,BufferedImage.TYPE_INT_RGB);
		ppp = new BufferedImage[hhh][www];
		desample = new BufferedImage(www*picw,hhh*pich,BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<hhh;y++){
			for(int x=0;x<www;x++){
				double wiw = ((double)s.getWidth()/(double)www);
				double hih = ((double)s.getHeight()/(double)hhh);
				sampp.setRGB(x, y, getAvg(crop(s,(int)(x*wiw),(int)(y*hih),(int)(x*wiw+wiw),(int)(y*hih+hih))).getRGB());
				
				Color tc = new Color(sampp.getRGB(x, y));
				int vari = lit[(int)(tc.getRed()*lratio)][(int)(tc.getGreen()*lratio)][(int)(tc.getBlue()*lratio)]-1;
				BufferedImage tb = pool.get(vari);
				ppp[y][x] = tb;
				Graphics g = desample.createGraphics();
				g.drawImage(pool.get(vari), x*tb.getWidth(), y*tb.getHeight(), tb.getWidth(), tb.getHeight(), null);
				if(!rep){
					pool.remove(vari);
					cpool.remove(vari);
				}
				foundln(www, hhh, vari, x, y);
				/*
				overloop: while(true){
					//LinkedList<Integer> vari = new LinkedList<Integer>();
					int[] vari = {999,0};
					for(int i=0;i<cpool.size();i++){
						int ttemp=0;
						ttemp += Math.abs(cpool.get(i).getRed()-tc.getRed());
						ttemp += Math.abs(cpool.get(i).getGreen()-tc.getGreen());
						ttemp += Math.abs(cpool.get(i).getBlue()-tc.getBlue());
						if(ttemp<vari[0]){
							vari[0] = ttemp;
							vari[1] = i;
						}
					}
					if(true){
						BufferedImage tb = pool.get(vari[1]);
						ppp[y][x] = tb;
						Graphics g = desample.createGraphics();
						g.drawImage(pool.get(vari[1]), x*tb.getWidth(), y*tb.getHeight(), tb.getWidth(), tb.getHeight(), null);
						if(!rep){
							pool.remove(vari[1]);
							cpool.remove(vari[1]);
						}
						foundln(www, hhh, vari[1], x, y);
						break overloop;
						Integer[] ctemp = {tc.getRGB(), vari[1]};
					}
				}*/
			}
		}
		System.out.println("SAMPLED");
		File f = new File(genHex(5)+".png");
		try {
			ImageIO.write(desample, "PNG", f);
		}catch (IOException ex){}
		System.out.println("DONE");
	}
	
	public RandMtg(){ ///Y\\\                                                           YYYYYYYASSSSSSSSSSSSS MAA0IN
		ww = new Window();
		try{ def = ImageIO.read(getClass().getResource("Image.jpg"));} catch (IOException e) {}
		//try{ resample = ImageIO.read(getClass().getResource("backk.jpg"));} catch (IOException e) {}
		try{ resample = ImageIO.read(new File("C:\\Users\\Emmett\\Desktop\\source - texture\\chameleon.jpg"));} catch (IOException e) {
			e.printStackTrace();
		}
		//resample = getImgur();
		//
		def = crop(def,18,36,207,173);
		//
		boolean pull = false; //TO PULL OR NOT TO PULL
		times = 1;
		if(pull){
			for(int i=0;i<times;i++){
				BufferedImage now;
				while(true){
					now = getCard((int)(Math.random()*420617+1));
					if(!same(now, def)){
						getAvg(now);
						break;
					}
				}
				//now = samp(now,2);
				cards.add(now);
				colors.add(getAvg(now));
				System.out.println("got "+(i+1)+" out of "+times);
				String id = genHex(5);
				try {
					//File f = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"\\dump\\"+id+".png");
					File f = new File(System.getProperty("user.dir")+"\\dump\\"+id+".png");
					//C:\\Users\\Emmett\\workspace\\guessgame
					ImageIO.write(now, "PNG", f);
				}catch (IOException ex){
					ex.printStackTrace();
				}
			}
		}
		getPools(System.getProperty("user.dir")+"\\dump");
		resample(resample,cards,colors,30,21,7,true);//RESAMPLEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
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
			image = crop(image,18,36,207,173);
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
	
	public BufferedImage samp(BufferedImage b, int d){
		BufferedImage samp = new BufferedImage(b.getWidth(),b.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<b.getHeight();y+=d){
			for(int x=0;x<b.getWidth();x+=d){
				samp.setRGB(x, y, b.getRGB(x,y));
			}
		}
		return samp;
	}
	
	public class Window extends JFrame{
		
		double scale = 0.3;
		
		public Window(){
			setSize(800,480);
			setLayout(new GridLayout());
			addMouseWheelListener(new MouseWheelListener(){
				public void mouseWheelMoved(MouseWheelEvent e) {
					scale *= (double)((e.getWheelRotation()*-1)+22)/(double)22;
				}
			});
			addWindowListener(new WindowListener(){
				public void windowActivated(WindowEvent arg0) {
				}
				public void windowClosed(WindowEvent e) {
				}
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				public void windowDeactivated(WindowEvent e) {
				}
				public void windowDeiconified(WindowEvent e) {
				}
				public void windowIconified(WindowEvent e) {
				}
				public void windowOpened(WindowEvent e) {
				}	
			});
			//add(new JScrollPane(new LTest()));
			add(new JScrollPane(new RTest()));
			setVisible(true);
		}
		
		public class RTest extends JPanel{
			
			Canvas canvas;
			
			public RTest(){
				setLayout(new GridLayout());
				canvas = new Canvas();
				add(canvas);
			}
			
			public class Canvas extends JPanel{
				public void paintComponent(Graphics g){
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					super.paintComponent(g);
					setBackground(Color.WHITE);
					try{
						double w = ppp[0][0].getWidth(this)*scale;
						double h = ppp[0][0].getHeight(this)*scale;
						g2.drawImage(resample,0,0,(int)(ppp[0].length*ppp[0][0].getWidth()*scale),(int)(ppp.length*ppp[0][0].getHeight()*scale),this);
						g2.drawImage(desample,0,0,(int)(ppp[0].length*ppp[0][0].getWidth()*scale),(int)(ppp.length*ppp[0][0].getHeight()*scale),this);
						//for(int y=0;y<lit[0].length;y++){
						//	for(int x=0;x<lit[0][0].length;x++){
						//		double u = 256/27;
						//		g2.setColor(new Color(0,(int)(y*u),(int)(x*u)));
						//		g2.fillRect((int)(x*15), (int)(y*15), (int)Math.ceil(15), (int)Math.ceil(15));
						//	}
						//}
						revalidate();
						setPreferredSize(new Dimension((int)(ppp[0].length*ppp[0][0].getWidth()*scale),(int)(ppp.length*ppp[0][0].getHeight()*scale)));
					}catch(Exception ex){}
					repaint();
				}
			}
		}
	}

	public static void main(String[] args){
		new RandMtg();
	}
}
