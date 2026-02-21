package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class Audio {
    private static Clip musicClip;
    private static String currentMusicFile;
    private static final List<Clip> sfxClips = new ArrayList<>();
    private static final Map<String, URL> resourceCache = new HashMap<>();

    private static URL getResourceUrl(String fileLocation) {
        return resourceCache.computeIfAbsent(fileLocation,
            location -> Audio.class.getClassLoader().getResource(location));
    }

    public static void playSfx(String fileLocation) {
        try {
            System.out.println("Playing " + fileLocation);
            URL sfxPath = getResourceUrl(fileLocation);

            if (sfxPath == null) {
                System.out.println("Sfx File does not exist: " + fileLocation);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(sfxPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            sfxClips.add(clip);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    sfxClips.remove(clip);
                }
            });
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing sfx: " + e.getMessage());
        }
    }

    public static void playMusic(String fileLocation) {
        try {
            if (fileLocation.equals(currentMusicFile)) {
                System.out.println("Music file same as current");
                return;
            }

            URL musicPath = getResourceUrl(fileLocation);

            if (musicPath == null) {
                System.out.println("Music File does not exist: " + fileLocation);
                return;
            }

            if (musicClip != null) {
                System.out.println("Close music");
                musicClip.close();
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInput);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusicFile = fileLocation;

        } catch (Exception e) {
            System.out.println("Error playing music loop: " + e.getMessage());
        }
    }

    public static void pauseMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    public static void resumeMusic() {
        if (musicClip != null && !musicClip.isRunning()) {
            musicClip.start();
        }
    }
}
