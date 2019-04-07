package model;

import model.encryption.Steganographer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(
                    Paths.get("./src/main/resources/encrypted.png").toFile()
            );
            System.out.println(Steganographer.readFromImage(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
