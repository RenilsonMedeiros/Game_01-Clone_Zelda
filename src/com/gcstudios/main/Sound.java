package com.gcstudios.main;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {

	public static class Clips{
		public Clip[] clips;
		private int p;
		private int count;
		
		public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
			if(buffer == null)
				return;
			
			clips = new Clip[count];
			this.count = count;
			
			for(int i = 0; i < count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		public void play() {
			if(clips == null) return;
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			p++;
			if(p>=count) p = 0;
		}
		
		public void loop() {
			if(clips == null) return;
			clips[p].loop(300);
		}
		
		public void stop() {
			if(clips == null) return;
			clips[p].stop();
		}

	}
	
	public static final Clips background = load("/zelda1.wav", 1);
	public static final Clips hurt = load("/hurt.wav", 1);
	public static final Clips hurtPlayer = load("/hurtplayer.wav", 1);
	public static final Clips jumpPlayer = load("/jumpplayer.wav", 1);
	public static final Clips shoot = load("/shoot.wav", 1);
	public static final Clips pickup = load("/pickup.wav", 1);
	public static final Clips wallWrecked = load("/wallwrecked.wav", 1);
	public static final Clips select = load("/select.wav", 1);
	public static final Clips begin = load("/begin.wav", 1);
	public static final Clips gameOver = load("/gameover.wav", 1);
	public static final Clips gameOverMusic = load("/gameovermusic.wav", 1);
	
	private static Clips load(String name,int count) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
			
			byte[] buffer = new byte[1024];
			int read = 0;
			while((read = dis.read(buffer)) >= 0) {
				baos.write(buffer,0,read);
			}
			dis.close();
			byte[] data = baos.toByteArray();
			return new Clips(data,count);
		}catch(Exception e) {
			try {
				return new Clips(null,0);
			}catch(Exception ee) {
				return null;
			}
		}
	}
	
}