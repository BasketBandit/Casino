package com.basketbandit.io.audio;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LavaPlayer extends AudioEventAdapter {
    private static final Logger log = LoggerFactory.getLogger(LavaPlayer.class);
    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = manager.createPlayer();
    private final LinkedList<AudioTrack> queue = new LinkedList<>();
    private final Deque<AudioTrack> history = new ArrayDeque<>(); // Originally wanted to use java.util.Stack but despite being LIFO the .foreach doesn't respect that ordering whereas java.util.Deque does.
    private AudioTrack pausedTrack;
    private boolean loop = false;

    public LavaPlayer() {
        this.player.addListener(this);
        this.player.setVolume(10);
        AudioSourceManagers.registerLocalSource(this.manager);
        new AudioSendHandler(manager, player).start();
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public void load(String url) {
        manager.loadItem(url, new FunctionalResultHandler(track -> {
            track.setUserData(new HashMap<>() {{
                put("position", toTime(0));
                put("positionPercent", 0.0f);
                put("duration", toTime(track.getDuration()));
            }});
            log.info("Queued track: {}", track.getInfo().title);
            queue.offer(track);
            if(player.getPlayingTrack() == null) {
                nextTrack();
            }
        }, playlist -> {
            playlist.getTracks().forEach(audioTrack -> {
                audioTrack.setUserData(new HashMap<>() {{
                    put("position", toTime(0));
                    put("positionPercent", 0.0f);
                    put("duration", toTime(audioTrack.getDuration()));
                }});
            });
            log.info("Queued {} tracks from playlist '{}'.", playlist.getTracks().size(), playlist.getName());
            queue.addAll(playlist.getTracks());
            if(player.getPlayingTrack() == null) {
                nextTrack();
            }
        }, null, null));
    }

    public List<AudioTrack> getQueue() {
        return queue;
    }

    public Deque<AudioTrack> getHistory() {
        return history;
    }

    public void setLoop(boolean state) {
        this.loop = state;
    }

    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    @SuppressWarnings("unchecked")
    public AudioTrack getActiveTrack() {
        AudioTrack track = (player.getPlayingTrack() != null) ? player.getPlayingTrack() : (pausedTrack != null && player.isPaused()) ? pausedTrack : null;
        if(track != null) {
            HashMap<String, String> data = new HashMap<>(){{
                put("position", toTime(track.getPosition()));
                put("positionPercent", "" + Math.round(((track.getPosition()+.0)/(track.getDuration()+.0)*100) * 100) / 100f);
            }}; // this is used to update current position/percentage
            ((HashMap<String, String>) track.getUserData()).putAll(data);
            return track;
        }
        return null;
    }

    public void nextTrack() {
        if(!player.isPaused()) {
            AudioTrack track = queue.poll();
            player.startTrack(track, false);
        }
    }

    public void move(int index, int destination) {
        queue.add(destination, queue.remove(index));
    }

    public void shuffle() {
        Collections.shuffle(queue);
    }

    public void remove(int index) {
        queue.remove(Math.max(0, Math.min((queue.size()-1), index)));
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        if(player.getPlayingTrack() != null) {
            AudioTrack track = player.getPlayingTrack().makeClone();
            track.setPosition(player.getPlayingTrack().getPosition());
            pausedTrack = track;
            player.stopTrack();
        }
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        if(pausedTrack != null) {
            player.playTrack(pausedTrack);
            pausedTrack = null;
            return;
        }
        nextTrack();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Playing track: {}", track.getInfo().title);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext) {
            pausedTrack = null;
            history.push(track);
            if(loop) {
                queue.push(track.makeClone());
            }
            nextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Exception when playing track: {}", exception.getMessage());
        nextTrack();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        log.error("Track '{}' stuck, skipping.", track.getInfo().title);
        nextTrack();
    }

    public static String toTime(long ms) {
        long second = (ms / 1000) % 60;
        long minute = (ms / (1000 * 60));
        return String.format("%02d:%02d", minute, second);
    }
}
