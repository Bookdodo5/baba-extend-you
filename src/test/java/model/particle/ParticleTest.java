package model.particle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ParticleTest {
    private Particle particle;

    @BeforeEach
    void setUp() {
        particle = new Particle(5.0, 3.0, 1.0, 0.0, ParticleType.PUFF, Color.WHITE);
    }

    @Test
    void testInitialPosition() {
        assertEquals(5.0, particle.getX(), 0.001);
        assertEquals(3.0, particle.getY(), 0.001);
    }

    @Test
    void testGetImage() {
        assertNotNull(particle.getImage());
    }

    @Test
    void testUpdate() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                particle.update();
                assertEquals(5.1, particle.getX(), 0.001);
                assertEquals(3.0, particle.getY(), 0.001);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
