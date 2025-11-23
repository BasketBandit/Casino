package com.basketbandit.io.audio;

import javax.sound.sampled.*;
import java.io.InputStream;

public class SoundEffect {
    private AudioFormat format;
    private byte[] audioData;

    public SoundEffect(String filePath) {
        try (InputStream in = SoundEffect.class.getClassLoader().getResourceAsStream(filePath);
             AudioInputStream stream = AudioSystem.getAudioInputStream(in)) {
            format = stream.getFormat();
            audioData = stream.readAllBytes();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        new Thread(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                // Stream audio data to the line
                line.write(audioData, 0, audioData.length);

                line.drain();
                line.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start(); // Each play runs in its own thread
    }

    public void play(float volume) {
        new Thread(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);

                // Check if volume control is supported
                if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(volume);
                }

                line.start();
                line.write(audioData, 0, audioData.length);
                line.drain();
                line.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
