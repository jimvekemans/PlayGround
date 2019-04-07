package model.encryption;

import java.awt.image.BufferedImage;

public class Steganographer {
    public static String readFromImage(BufferedImage image) {
        StringBuilder bitContainer = new StringBuilder();
        int imgHeight = image.getHeight();
        int imgWidth = image.getWidth();
        for (int i = 0; i < imgHeight; i++) {
            for (int j = 0; j < imgWidth; j++) {
                int rgb = image.getRGB(j, i);
                // getRGB geeft een 32-bit waarde maar we hebben enkel de least-significant-bytes van R,G,B nodig
                int leastSignBits = (rgb & 0x00010101);
                // nu hebben we een integer met allemaal nullen behalve op de posities 0000.0001.0000.0001.0000.0001
                // we kiezen telkens 1 bit uit en plaatsen die bij in de 'bitcontainer'
                bitContainer.append(((leastSignBits & 0x00010000) == 0x00010000) ? 1 : 0);
                bitContainer.append(((leastSignBits & 0x00000100) == 0x00000100) ? 1 : 0);
                bitContainer.append(((leastSignBits & 0x00000001) == 0x00000001) ? 1 : 0);
            }
        }
        String binaryString = bitContainer.toString();
        StringBuilder result = new StringBuilder();
        // nu moeten we de string met eentjes en nullen overzetten naar bytes en die bytes casten naar een char.
        // dit gaat enkel als er geen encodering ingesteld is, want anders is een char 2 bytes lang. Geen zorg voor nu
        for (int i = 0; i <= binaryString.length() - 8; i += 8) {
            result.append((char) Integer.parseInt(binaryString.substring(i, i + 8), 2));
        }
        return result.toString();
    }
}
