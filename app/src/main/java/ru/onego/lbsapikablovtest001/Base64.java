package ru.onego.lbsapikablovtest001;

/**
 * Created by Серега on 30.07.2017.
 */

import java.io.ByteArrayOutputStream;

public final class Base64 {
    final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    public static String encode (byte source[]) {
        int len = source.length;
        char[] out = new char[((len+2) / 3) * 4];
        for (int i = 0, index = 0; i < len; i += 3, index += 4) {
            boolean trip = false;
            boolean quad = false;

            int val = (0xFF & source[i]) << 8;
            if ((i+1) < len) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad ? (val & 0x3F) : 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip ? (val & 0x3F) : 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }

    public static byte[] decode (String s) {
        return baosFromBase64(s).toByteArray();
    }

    private static ByteArrayOutputStream baosFromBase64 (String s) {
        int padding = 0;
        int ibuf = 1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        for (int i = 0; i < s.length(); i++) {
            int nextChar = s.charAt(i);
            //if( nextChar == -1 )
            //    throw new EndOfXMLException();
            int base64 = -1;
            if (nextChar > 'A'-1 && nextChar < 'Z'+1) {
                base64 = nextChar-'A';
            } else if (nextChar > 'a'-1 && nextChar < 'z'+1) {
                base64 = nextChar+26-'a';
            } else if (nextChar > '0'-1 && nextChar < '9'+1) {
                base64 = nextChar+52-'0';
            } else if (nextChar == '+') {
                base64 = 62;
            } else if (nextChar == '/') {
                base64 = 63;
            } else if (nextChar == '=') {
                base64 = 0;
                padding++;
            } else if (nextChar == '<') {
                break;
            }
            if (base64 >= 0) {
                ibuf = (ibuf << 6)+base64;
            }
            if (ibuf >= 0x01000000) {
                baos.write((ibuf >> 16) & 0xff);                   //00xx0000 0,1,2 =
                if (padding < 2) {
                    baos.write((ibuf >> 8) & 0xff);     //0000xx00 0,1 =
                }
                if (padding == 0) {
                    baos.write(ibuf & 0xff);         //000000xx 0 =
                }
                //len+=3;
                ibuf = 1;
            }
        }
        try {
            baos.close();
        } catch (Exception ignored) {
        }
        return baos;
    }
}
