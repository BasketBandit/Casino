package com.basketbandit.io.audio;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioLibrary {
    private static final HashMap<String, URL> audio = new HashMap<>(Map.ofEntries(
            Map.entry("smooth", Objects.requireNonNull(AudioLibrary.class.getClassLoader().getResource("assets/audio/smooth-jazz.mp3")))
    ));
    private static final HashMap<String, SoundEffect> effects = new HashMap<>(Map.ofEntries(
            Map.entry("deal1", new SoundEffect("assets/audio/effects/deal-card-1.wav")),
            Map.entry("deal2", new SoundEffect("assets/audio/effects/deal-card-2.wav"))
    ));

    public AudioLibrary() {
    }

    public static URL audioURL(String name) {
        return audio.get(name);
    }

    public static SoundEffect effect(String name) {
        return effects.get(name);
    }

    public static String audioFile(String name) {
        return audio.get(name).getFile();
    }

}
