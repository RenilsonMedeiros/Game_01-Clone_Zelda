package com.gcstudios.main;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	
	public static final Sound musicBackground = new Sound("res/zelda1.wav");
	public static final Sound musicHurt = new Sound("res/hurt.wav");
	public static final String music = "uva";
	
	private AudioInputStream audioInput;
	private Clip clip;
	
	public Sound(String path) {
		try {
			File musicPath = new File(path);
			
			if(musicPath.exists()) {
				this.audioInput = AudioSystem.getAudioInputStream(musicPath);
				this.clip = AudioSystem.getClip();
			} else System.out.println("Arquivo de música não encontrado");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void loop() {
		try {
			clip.open(audioInput);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void play() {
		try {
			if(!clip.isOpen()) clip.open(audioInput);
			clip.start();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
