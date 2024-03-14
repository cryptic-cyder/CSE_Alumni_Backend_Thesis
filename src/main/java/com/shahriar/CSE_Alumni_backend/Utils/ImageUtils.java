package com.shahriar.CSE_Alumni_backend.Utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {

    public static byte[] compressImage(byte[] data){

        // The Deflater class in Java is used for compressing data using the DEFLATE compression algorithm.
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION); //for best type compression
        deflater.setInput(data);
        deflater.finish();

        /*
        * An output stream is a sequence of data that a program sends or writes.
        * It allows the program to write data to a destination, such as a file,
        * network connection, or another program.
        * In Java, the OutputStream class is used to write data to a destination.
        * */
        // This is a stream where data can be written, and it will be stored in an internal byte array.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] temp = new byte[4*1024];

        /*
        *  Imagine you have a big bag of balloons, and your goal is to squeeze the air out of them and put the
        *  squished balloons into a smaller bag. This process is like compressing data.
        *  The big bag of balloons is like your original data that you want to compress.
        *  The small bag is where you'll put the compressed data.
        In the code:

        deflater.deflate(temp):
        => It's like squeezing the air out of a balloon and putting it into the temporary bag (temp array).
        * baos.write(temp, 0, size):
        =>It's like taking the squished balloons from the temporary bag and
             putting them into a smaller bag (baos stream).
        The loop continues until you've squeezed all the balloons (compressed all the data) from
        * the big bag to the small bag.
        * */
        while(!deflater.finished()){
            int size = deflater.deflate(temp);
            baos.write(temp, 0 ,size);
        }

        return baos.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) throws DataFormatException {

        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] temp = new byte[4*1024];

        while(!inflater.finished()){
            int size = inflater.inflate(temp);
            baos.write(temp, 0, size);
        }

        return baos.toByteArray();
    }

}
