package model.encryption;

import java.awt.image.BufferedImage;

public final class Steganographer {

    public static String readFromImage(BufferedImage image) {
        StringBuilder bitContainer = new StringBuilder();
        int imgHeight = image.getHeight(), imgWidth = image.getWidth();
        for (int i = 0; i < imgHeight; i++) {
            for (int j = 0; j < imgWidth; j++) {
                int rgb = image.getRGB(j, i);
                // we kiezen telkens 1 bit uit en plaatsen die bij in de 'bitcontainer'
                bitContainer.append(((rgb & 0x00010000) == 0x00010000) ? 1 : 0);
                bitContainer.append(((rgb & 0x00000100) == 0x00000100) ? 1 : 0);
                bitContainer.append(((rgb & 0x00000001) == 0x00000001) ? 1 : 0);
            }
        }
        String binaryString = bitContainer.toString();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= binaryString.length() - 8; i += 8) {
            result.append((char) Integer.parseInt(binaryString.substring(i, i + 8), 2));
        }
        return result.toString();
    }

    public static BufferedImage embedMessageIntoImage(String message, BufferedImage image) {
        byte[] msgBytes = message.getBytes();
        int imgHeight = image.getHeight(), imgWidth = image.getWidth();
        boolean byteArrayTooLarge = msgBytes.length * (3 / 8.0) >= imgHeight * imgWidth;
        if (byteArrayTooLarge) throw new IllegalArgumentException("Message wont fit inside image.");
        for (int RGBcounter = 0, byteCounter = 0; RGBcounter < imgHeight * imgWidth; ) {
            // We need 8 pixels and 3 bytes to encode 24 bits per iteration
            int[] rgb = new int[8];
            for (int i = 0; i < 8; i++) {
                rgb[i] = image.getRGB((RGBcounter + i) % imgWidth, (RGBcounter + i) / imgHeight) & 0xFFFEFEFE;
            }
            byte byte_1 = msgBytes[byteCounter++];
            byte byte_2 = msgBytes[byteCounter++];
            byte byte_3 = msgBytes[byteCounter++];

            // 0x..../..../..../...?/0000/000./..../..../     -> change LSB of RED
            rgb[0] = rgb[0] & (((int) byte_1 & 0x80) << 9);
            // 0x..../..../..../..../..../..0?/0000/00../     -> change LSB of GREEN
            rgb[0] = rgb[0] & (((int) byte_1 & 0x40) << 2);
            // 0x..../..../..../..../..../..../..../.00?/     -> change LSB of BLUE
            rgb[0] = rgb[0] & (((int) byte_1 & 0x20) >> 5);

            // 0x..../..../..../000?/0000/..../..../..../     -> change LSB of RED
            rgb[1] = rgb[1] & (((int) byte_1 & 0x10) << 12);
            // 0x..../..../..../..../...0/000?/000./..../     -> change LSB of GREEN
            rgb[1] = rgb[1] & (((int) byte_1 & 0x08) << 5);
            // 0x..../..../..../..../..../..../..00/000?/     -> change LSB of BLUE
            rgb[1] = rgb[1] & (((int) byte_1 & 0x04) >> 2);

            // 0x..../..../.000/000?/0.../..../..../..../     -> change LSB of RED
            rgb[2] = rgb[2] & (((int) byte_1 & 0x02) << 15);
            // 0x..../..../..../..../0000/000?/..../..../     -> change LSB of GREEN
            rgb[2] = rgb[2] & (((int) byte_1 & 0x01) << 8);
            // 0x..../..../..../..../..../..../..../...?/     -> change LSB of BLUE
            rgb[2] = rgb[2] & (((int) byte_2 & 0x80) >> 7);

            // 0x..../..../..../..0?/0000/00../..../..../     -> change LSB of RED
            rgb[3] = rgb[3] & (((int) byte_2 & 0x40) << 10);
            // 0x..../..../..../..../..../.00?/0000/0.../     -> change LSB of GREEN
            rgb[3] = rgb[3] & (((int) byte_2 & 0x20) << 3);
            // 0x..../..../..../..../..../..../..../000?/     -> change LSB of BLUE
            rgb[3] = rgb[3] & (((int) byte_2 & 0x10) >> 4);

            // 0x..../..../...0/000?/000./..../..../..../     -> change LSB of RED
            rgb[4] = rgb[4] & (((int) byte_2 & 0x08) << 13);
            // 0x..../..../..../..../..00/000?/00../..../     -> change LSB of GREEN
            rgb[4] = rgb[4] & (((int) byte_2 & 0x04) << 6);
            // 0x..../..../..../..../..../..../.000/000?/     -> change LSB of BLUE
            rgb[4] = rgb[4] & (((int) byte_2 & 0x02) >> 1);

            // 0x..../..../0000/000?/..../..../..../..../     -> change LSB of RED
            rgb[5] = rgb[5] & (((int) byte_2 & 0x01) << 16);
            // 0x..../..../..../..../..../...?/0000/000./     -> change LSB of GREEN
            rgb[5] = rgb[5] & (((int) byte_3 & 0x80) << 1);
            // 0x..../..../..../..../..../..../..../..0?/     -> change LSB of BLUE
            rgb[5] = rgb[5] & (((int) byte_3 & 0x40) >> 6);

            // 0x..../..../..../.00?/0000/0.../..../..../     -> change LSB of RED
            rgb[6] = rgb[6] & (((int) byte_3 & 0x20) << 11);
            // 0x..../..../..../..../..../000?/0000/..../     -> change LSB of GREEN
            rgb[6] = rgb[6] & (((int) byte_3 & 0x10) << 4);
            // 0x..../..../..../..../..../..../...0/000?/     -> change LSB of BLUE
            rgb[6] = rgb[6] & (((int) byte_3 & 0x08) >> 3);

            // 0x..../..../..00/000?/00../..../..../..../     -> change LSB of RED
            rgb[7] = rgb[7] & (((int) byte_3 & 0x04) << 14);
            // 0x..../..../..../..../.000/000?/0.../..../     -> change LSB of GREEN
            rgb[7] = rgb[7] & (((int) byte_3 & 0x02) << 7);
            // 0x..../..../..../..../..../..../0000/000?/     -> change LSB of BLUE
            rgb[7] = rgb[7] & ((int) byte_3 & 0x01);


        }
        return image;
    }
}