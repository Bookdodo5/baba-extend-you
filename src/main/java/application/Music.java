package application;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Music {
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
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void playLoop(String fileLocation) {
        try {
            // Load from classpath
            URL musicPath = Music.class.getClassLoader().getResource(fileLocation);

            if (musicPath == null) {
                System.out.println("Music File does not exist: " + fileLocation);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            loopClip = AudioSystem.getClip();
            loopClip.open(audioInput);

            // Add listener to detect when music ends
            loopClip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        // Create a new thread to handle the delay and restart
                        new Thread(() -> {
                            try {
                                // 3 second delay
                                Thread.sleep(3000);

                                // Restart from beginning
                                loopClip.setFramePosition(0);
                                loopClip.start();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            });

            loopClip.start();

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
}