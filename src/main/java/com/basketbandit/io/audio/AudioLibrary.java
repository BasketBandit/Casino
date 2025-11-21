package com.basketbandit.io.audio;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioLibrary {
    private static final HashMap<String, URL> audio = new HashMap<>(Map.ofEntries(
            Map.entry("smooth", Objects.requireNonNull(AudioLibrary.class.getClassLoader().getResource("assets/audio/smooth-jazz.mp3")))
    ));
    private static final HashMap<String, URL> effects = new HashMap<>(Map.ofEntries(
            //Map.entry("pop1", Objects.requireNonNull(AudioLibrary.class.getClassLoader().getResource("assets/audio/effects/pop1.wav"))),
            //Map.entry("pop2", Objects.requireNonNull(AudioLibrary.class.getClassLoader().getResource("assets/audio/effects/pop2.wav")))
    ));

    public AudioLibrary() {
    }

    public static URL audioURL(String name) {
        return audio.get(name);
    }

    public static URL effectURL(String name) {
        return effects.get(name);
    }

    public static String audioFile(String name) {
        return audio.get(name).getFile();
    }

    public static String effectFile(String name) {
        return effects.get(name).getFile();
    }
}
