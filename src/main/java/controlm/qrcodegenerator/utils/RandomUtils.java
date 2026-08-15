package controlm.qrcodegenerator.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtils {
    private final Random random = new Random();

    public int randomInt(int min, int max) {
        return random.nextInt(min, max);
    }

    public int randomInt(int max) {
        return random.nextInt(max);
    }
}
