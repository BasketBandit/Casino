package com.basketbandit.io.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.InputStream;

public class SoundEffect {
    private static final Logger log = LoggerFactory.getLogger(SoundEffect.class);
    private AudioFormat format;
    private byte[] audioData;

    public SoundEffect(String filePath) {
        try (InputStream in = SoundEffect.class.getClassLoader().getResourceAsStream(filePath);
             AudioInputStream stream = AudioSystem.getAudioInputStream(in)) {
            format = stream.getFormat();
            audioData = stream.readAllBytes();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void play() {
        Thread.startVirtualThread(() -> {
            try {
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();

                // Stream audio data to the line
                line.write(audioData, 0, audioData.length);

                line.drain();
                line.close();
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    public void play(float volume) {
        Thread.startVirtualThread(() -> {
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
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
