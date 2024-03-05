/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Carron Muleya
 */
public class QRCodeGenerator {
    public static byte[] generateQRCode(String data,int width,int height) throws WriterException{
        BitMatrix matrix=new MultiformatWriter().encode(data,BarcodeFormat.QR_CODE,width,height);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        new  MatrixToImageWriter().writeToStream(matrix,"PNG",outputStream);
        return outputStream.toByteArray();
    }
}
