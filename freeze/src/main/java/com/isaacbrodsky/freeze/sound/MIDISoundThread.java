/**
 * 
 */
package com.isaacbrodsky.freeze.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

/**
 * @author isaac
 * 
 */
public class MIDISoundThread extends Thread implements SoundEngine {
	private static final int DEFAULT_OCTAVE = 5;

	private String tune;
	private int pos;
	private Synthesizer synth;
	private MidiChannel mainOut, percOut;

	public MIDISoundThread() {
		synth = null;

		setDaemon(true);
		setName("FZ Sound Thread");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.sound.SoundEngine#init()
	 */
	@Override
	public boolean init() {
		MidiDevice.Info[] inf = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < inf.length; i++) {
			System.out.println(i + ": " + inf[i]);

			MidiDevice m = null;
			try {
				m = MidiSystem.getMidiDevice(inf[i]);
				m.open();
				if (m instanceof Synthesizer) {
					Synthesizer syn = (Synthesizer) m;

					syn.loadAllInstruments(syn.getDefaultSoundbank());

					synth = syn;
					mainOut = synth.getChannels()[0];
					percOut = synth.getChannels()[9];
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.sound.SoundEngine#close()
	 */
	@Override
	public void close() {
		if (synth != null)
			synth.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if (synth == null)
			return;

		int note, duration, octive;

		char c = tune.charAt(pos);
		c = Character.toUpperCase(c);

		if (c >= 'A' && c <= 'F') {

		} else if (c >= '0' && c <= '9') {

		} else if (c == '+' || c == '-') {

		}
	}

	/**
	 * @param channel
	 * @param notes
	 * @param velocities
	 * @param durations
	 */
	public static void playChannel(MidiChannel channel, int[] notes,
			int[] velocities, int[] durations) {
		for (int i = 0; i < notes.length; i++) {
			channel.noteOn(notes[i], velocities[i]);
			try {
				Thread.sleep(durations[i]);
			} catch (InterruptedException e) {
			}
		}
		for (int i = 0; i < notes.length; i++)
			channel.noteOff(notes[i]);
	}

	/**
	 * @param channel
	 * @param note
	 * @param velo
	 * @param dur
	 */
	public static void playNote(MidiChannel channel, int note, int velo, int dur) {
		channel.noteOn(note, velo);
		try {
			Thread.sleep(dur);
		} catch (InterruptedException e) {
		}
		channel.noteOff(note);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.sound.SoundEngine#getTune()
	 */
	@Override
	public String getTune() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.sound.SoundEngine#getTunePos()
	 */
	@Override
	public int getTunePos() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.sound.SoundEngine#setTune(java.lang.String)
	 */
	@Override
	public void setTune(String t) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.sound.SoundEngine#setTunePos(int)
	 */
	@Override
	public void setTunePos(int p) {

	}
}
