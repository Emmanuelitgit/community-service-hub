package com.community_service_hub.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class ImageUtil {

    /**
     * @description This method is used to compress iamge or any file before saving to db to reduce the size.
     * @param data
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 11th May 2025
     */
    public static byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            log.info("fail to compress image:->>>>{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return outputStream.toByteArray();
    }

    /**
     * @description This method is used to decompress image or any file after retrieving it from db.
     * @param data
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 11th May 2025
     */
    public static byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
            log.info("fail to decompress image:->>>>{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return outputStream.toByteArray();
    }
}