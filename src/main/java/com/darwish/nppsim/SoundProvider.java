package com.darwish.nppsim;

import java.io.InputStream;
import java.util.HashSet;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundProvider {
    HashSet<Clip> currentlyPlaying = new HashSet<>();
    Clip ALARM_1, ALARM_2;

    public SoundProvider() {
        try {
            ALARM_1 = loadAsset("alarm1.wav");
            ALARM_1.setLoopPoints(0, ALARM_1.getFrameLength() - 1000);
            ALARM_2 = loadAsset("alarm2.wav");
            ALARM_2.setLoopPoints(0, ALARM_2.getFrameLength() - 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void playContinuously(Clip clip) {
        if (currentlyPlaying.contains(clip)) {
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        currentlyPlaying.add(clip);
    }
    
    public void stop(Clip clip) {
        if (!currentlyPlaying.contains(clip)) {
            return;
        }
        clip.stop();
        currentlyPlaying.remove(clip);
    }
    
    public void stopAll() {
        currentlyPlaying.forEach(playingClip -> {
            playingClip.stop();
        });
        currentlyPlaying.clear();
    }
    
    public void pause() {
        currentlyPlaying.forEach(playingClip -> {
            playingClip.stop();
        });
    }
    
    public void resume() {
        currentlyPlaying.forEach(playingClip -> {
            playingClip.loop(Clip.LOOP_CONTINUOUSLY);
        });
    }
    
    private Clip loadAsset(String asset) throws Exception {
        Clip loadedAsset;
        try {
            InputStream input = getClass().getResourceAsStream("/res/" + asset);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(input);
            loadedAsset = AudioSystem.getClip();
            loadedAsset.open(audioStream);
            audioStream.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("AudioAssetException");
        }
        return loadedAsset;
    }
}
