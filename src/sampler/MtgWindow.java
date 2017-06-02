package sampler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class MtgWindow extends JFrame{
	private static final long serialVersionUID = -44669440408101826L;
	
	boolean pulling;
	boolean ready;
	
	double scale = 0.3;
	JButton sbu;
	JTextArea inp;
	JLabel wisl;
	JTextPane wis;
	JLabel csil;
	JTextPane csi;
	JButton gets;
	BufferStrategy b;
	JLabel pnl;
	JTextPane pn;
	JButton pb;
	JButton clr;
	JLabel pch;
	Insets insets;
	
	public MtgWindow(){
		setTitle("MTG Resample");
		setSize(800,480);
		setVisible(true);
		setPreferredSize(getSize());
		
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
		insets = getInsets();
		createBufferStrategy(2);
		b = getBufferStrategy();
		sbu = new JButton();
		add(sbu);
		sbu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try{ resample = ImageIO.read(new File(inp.getText()));} catch (IOException e) {
					e.printStackTrace();
				}
				resample(resample,Integer.parseInt(csi.getText()),(int)(Integer.parseInt(csi.getText())*(double)full[0].getHeight()/full[0].getWidth()),Integer.parseInt(wis.getText()));
			}
		});
		sbu.setIcon((Icon)def);
		inp = new JTextArea();
		inp.setEditable(true);
		inp.setLineWrap(true);
		inp.setAutoscrolls(true);
		add(inp);
		wisl = new JLabel("Cards Across");
		add(wisl);
		wis = new JTextPane();
		wis.setText("100");
		add(wis);
		csil = new JLabel("Card width");
		add(csil);
		csi = new JTextPane();
		csi.setText("30");
		add(csi);
		pnl = new JLabel("Pull amount");
		add(pnl);
		pn = new JTextPane();
		pn.setText("100");
		add(pn);
		pb = new JButton("Pull");
		pb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				exe.execute(new PullLoop());
			}
		});
		add(pb);
		clr = new JButton("Clear");
		clr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		add(clr);
		pch = new JLabel("Pool size:");
		add(pch);
		exe.execute(new PaintLoop());
	}
	
			pulling = true;
			ready = false;
			int times = Integer.parseInt(pn.getText());
			for(int i=0;i<times;i++){
				boolean p = pull();
				if(p){
					System.out.println("Loaded "+i+" out of "+poolsize);
				}else{
					System.out.println("Failed to load at "+i);
					i--;
				}
			}
			setup();
			pulling = false;
			ready = true;
	
	public void gopaint(){
		Graphics g = b.getDrawGraphics();
		insets = getInsets();
		//rr.setBounds(insets.left+200,0,ww.getWidth(),ww.getHeight());
		sbu.setBounds(insets.left+10,insets.top+10,180,30);
		sbu.setEnabled(ready);
		if(ready){
			sbu.setText("Sample!");
		}else{
			sbu.setText("Not Ready!");
		}
		inp.setBounds(insets.left+10,insets.top+50,180,80);
		wisl.setBounds(insets.left+10,insets.top+140,180,30);
		wis.setBounds(insets.left+100,insets.top+140,90,30);
		csil.setBounds(insets.left+10,insets.top+180,180,30);
		csi.setBounds(insets.left+100,insets.top+180,90,30);
		pnl.setBounds(insets.left+10,insets.top+220,90,30);
		pn.setBounds(insets.left+100,insets.top+220,90,30);
		pb.setBounds(insets.left+10,insets.top+260,180,30);
		pb.setEnabled(!pulling);
		clr.setBounds(insets.left+10,insets.top+300,180,30);
		pch.setBounds(insets.left+10,insets.top+340,180,30);
		pch.setText("Pool size: "+poolsize);
		//
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		setBackground(Color.WHITE);
		try{
			g2.drawImage(MTG.desample,0,0,(int)(ppp[0].length*ppp[0][0].getWidth()*scale),(int)(MTG.ppp.length*MTG.ppp[0][0].getHeight()*scale),null);
			revalidate();
			setPreferredSize(new Dimension((int)(ppp[0].length*ppp[0][0].getWidth()*scale),(int)(MTG.ppp.length*MTG.ppp[0][0].getHeight()*scale)));
		}catch(Exception ex){}
	}

}
