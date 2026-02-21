package application;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class Music {
    private static Clip currentClip;
    private static Clip loopClip; // Store reference for loop control

    public static void play(String fileLocation) {
        try {
            // Load from classpath instead of file system
            URL musicPath = Music.class.getClassLoader().getResource(fileLocation);

            if (musicPath == null) {
                System.out.println("Music File does not exist: " + fileLocation);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    if (currentClip == clip) {
                        currentClip = null;
                    }
                }
            });
            currentClip = clip;
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void playLoop(String fileLocation) {
        try {
            URL musicPath = Music.class.getClassLoader().getResource(fileLocation);

            if (musicPath == null) {
                System.out.println("Music File does not exist: " + fileLocation);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            loopClip = AudioSystem.getClip();
            loopClip.open(audioInput);
            loopClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.out.println("Error playing music loop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Optional: Add a method to stop the loop
    public static void stopLoop() {
        if (loopClip != null && loopClip.isRunning()) {
            loopClip.stop();
            loopClip.close();
        }
    }

    public static void stop() {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
        }
        if (loopClip != null) {
            if (loopClip.isRunning()) {
                loopClip.stop();
            }
            loopClip.close();
            loopClip = null;
        }
    }
}
