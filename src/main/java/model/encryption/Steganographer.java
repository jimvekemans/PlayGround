package model.encryption;

import java.awt.image.BufferedImage;

public final class Steganographer {

    public static String readFromImage(BufferedImage image) {
        StringBuilder bitContainer = new StringBuilder(), result = new StringBuilder();
        int imgHeight = image.getHeight(), imgWidth = image.getWidth();
        for (int i = 0; i < imgHeight; i++)
            for (int j = 0; j < imgWidth; j++) {
                int rgb = image.getRGB(j, i);
                bitContainer.append(((rgb & 0x00010000) == 0x00010000) ? 1 : 0);
                bitContainer.append(((rgb & 0x00000100) == 0x00000100) ? 1 : 0);
                bitContainer.append(((rgb & 0x00000001) == 0x00000001) ? 1 : 0);
            }
        String binaryString = bitContainer.toString();
        for (int i = 0; i <= binaryString.length() - 8; i += 8) {
            if (binaryString.substring(i, i + 8).equalsIgnoreCase("00000000")) break;
            result.append((char) Integer.parseInt(binaryString.substring(i, i + 8), 2));
        }
        return result.toString();
    }

    public static BufferedImage embedMessageIntoImage(String message, BufferedImage image) {
        final int imgHeight = image.getHeight(), imgWidth = image.getWidth();

        byte[] msgBytes = message.getBytes();
        if (msgBytes.length * (3.0 / 8.0) >= imgHeight * imgWidth)
            throw new IllegalArgumentException("Message wont fit inside image.");
        byte[] paddedMsgBytes = new byte[imgHeight * imgWidth * 3 / 8];
        System.arraycopy(msgBytes, 0, paddedMsgBytes, 0, msgBytes.length);

        for (int RGBcounter = 0, byteCounter = 0; (RGBcounter + 7) < (imgHeight) * imgWidth; ) {
            int[] rgb = new int[8];
            for (int i = 0; i < 8; i++)
                rgb[i] = image.getRGB(RGBcounter % imgWidth, RGBcounter / imgWidth) & 0xFFFEFEFE;

            int first = paddedMsgBytes[byteCounter++];
            int second = paddedMsgBytes[byteCounter++];
            int third = paddedMsgBytes[byteCounter++];

            rgb[0] += ((first & 0x80) << 9) + ((first & 0x40) << 2) + ((first & 0x20) >> 5);
            rgb[1] += ((first & 0x10) << 12) + ((first & 0x08) << 5) + ((first & 0x04) >> 2);
            rgb[2] += ((first & 0x02) << 15) + ((first & 0x01) << 8) + ((second & 0x80) >> 7);
            rgb[3] += ((second & 0x40) << 10) + ((second & 0x20) << 3) + ((second & 0x10) >> 4);
            rgb[4] += ((second & 0x08) << 13) + ((second & 0x04) << 6) + ((second & 0x02) >> 1);
            rgb[5] += ((second & 0x01) << 16) + ((third & 0x80) << 1) + ((third & 0x40) >> 6);
            rgb[6] += ((third & 0x20) << 11) + ((third & 0x10) << 4) + ((third & 0x08) >> 3);
            rgb[7] += ((third & 0x04) << 14) + ((third & 0x02) << 7) + (third & 0x01);

            for (int i = 0; i < 8; i++)
                image.setRGB(RGBcounter % imgWidth, RGBcounter++ / imgWidth, rgb[i]);
        }
        return image;
    }
}