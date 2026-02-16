package application;

import java.io.File;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Music {
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
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }
}