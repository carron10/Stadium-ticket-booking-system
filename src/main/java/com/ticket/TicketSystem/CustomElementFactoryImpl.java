/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ticket.TicketSystem;

import com.lowagie.text.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

/**
 *
 * @author Carron Muleya
 */
@Service
public class CustomElementFactoryImpl implements ReplacedElementFactory {

   @Autowired
   ImageService imgservice;

    @Override
    public ReplacedElement createReplacedElement(LayoutContext lc, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        org.w3c.dom.Element e = box.getElement();
        String nodeName = e.getNodeName();
        if (nodeName.equals("img")) {
            String imagePath = e.getAttribute("src");
            try {
//                Resource resource = resourceLoader.getResource("classpath:static/" + new PathResource(imagePath).getFilename());
                InputStream input = imgservice.getImageAsStream(imagePath);
//                String encodedPath = "static/" + URLEncoder.encode(imagePath, "UTF-8");
//
//                InputStream input = getClass().getClassLoader().getResourceAsStream(encodedPath);

                byte[] bytes = IOUtils.toByteArray(input);
                Image image = Image.getInstance(bytes);
                FSImage fsImage = new ITextFSImage(image);
                if (cssWidth != -1 || cssHeight != -1) {
                    fsImage.scale(cssWidth, cssHeight);
                } else {
                    fsImage.scale(2000, 1000);
                }
                return new ITextImageElement(fsImage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void reset() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void remove(Element elmnt) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener fl) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
