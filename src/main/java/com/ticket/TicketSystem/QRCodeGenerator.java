/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Carron Muleya
 */
public class QRCodeGenerator {

     public static byte[] generateQRCode2(String content, int size) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);

        // Convert BitMatrix to BufferedImage
        BufferedImage bufferedImage = BitMatrixToBufferedImageWithScaling(bitMatrix,size);

        // Write the image to a file
//        ImageIO.write(bufferedImage, "png", new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        // Return the image bytes
        return baos.toByteArray();
    }
    public static byte[] generateQRCode(String content, int size) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);

        // Convert BitMatrix to BufferedImage
        BufferedImage bufferedImage = BitMatrixToBufferedImage(bitMatrix);

        // Write the image to a file
//        ImageIO.write(bufferedImage, "png", new File(filePath));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        // Return the image bytes
        return baos.toByteArray();
    }

    private static BufferedImage BitMatrixToBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        
        return image;
    }
     private static BufferedImage BitMatrixToBufferedImageWithScaling(BitMatrix matrix, int size) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Draw the QR code into the BufferedImage
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        // Apply scaling transformation to reduce padding
        AffineTransform at = AffineTransform.getScaleInstance((double) size / width, (double) size / height);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        image = atop.filter(image, null);

        return image;
    }

}

