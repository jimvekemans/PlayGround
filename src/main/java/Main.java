import model.encryption.Steganographer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        readStuff();
    }

    private static void readStuff() {
        try {
            Path encryptedImgPath = Paths.get("/home/jim/Desktop/PlayGround/src/main/resources/encrypted.png");
            System.out.println("Decoded message: " + Steganographer.readFromImage(ImageIO.read(encryptedImgPath.toFile())));

            BufferedImage testImage = Steganographer.embedMessageIntoImage("What a save! What a save! What a save!", ImageIO.read(encryptedImgPath.toFile()));
            System.out.println("Decoded message: " + Steganographer.readFromImage(testImage));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
