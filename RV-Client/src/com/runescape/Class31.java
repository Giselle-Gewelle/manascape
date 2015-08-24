package com.runescape;

import com.runescape.io.ByteBuffer;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Class31
{

    public static String method320(int i, ByteBuffer byteStream, int j)
    {
        int k = 0;
        int l = -1;
        for(int i1 = 0; i1 < j; i1++)
        {
            int j1 = byteStream.getUnsignedByte();
            int k1 = j1 >> 4 & 0xf;
            if(l == -1)
            {
                if(k1 < 13)
                    aCharArray588[k++] = aCharArray590[k1];
                else
                    l = k1;
            } else
            {
                aCharArray588[k++] = aCharArray590[((l << 4) + k1) - 195];
                l = -1;
            }
            k1 = j1 & 0xf;
            if(l == -1)
            {
                if(k1 < 13)
                    aCharArray588[k++] = aCharArray590[k1];
                else
                    l = k1;
            } else
            {
                aCharArray588[k++] = aCharArray590[((l << 4) + k1) - 195];
                l = -1;
            }
        }

        boolean flag = true;
        for(int l1 = 0; l1 < k; l1++)
        {
            char c = aCharArray588[l1];
            if(flag && c >= 'a' && c <= 'z')
            {
                aCharArray588[l1] += '\uFFE0';
                flag = false;
            }
            if(c == '.' || c == '!' || c == '?')
                flag = true;
        }

        if(i != 0)
            anInt587 = -409;
        return new String(aCharArray588, 0, k);
    }

    public static void method321(String s, int i, ByteBuffer byteStream)
    {
        if(s.length() > 80)
            s = s.substring(0, 80);
        s = s.toLowerCase();
        if(i <= 0)
        {
            for(int j = 1; j > 0; j++);
        }
        int k = -1;
        for(int l = 0; l < s.length(); l++)
        {
            char c = s.charAt(l);
            int i1 = 0;
            for(int j1 = 0; j1 < aCharArray590.length; j1++)
            {
                if(c != aCharArray590[j1])
                    continue;
                i1 = j1;
                break;
            }

            if(i1 > 12)
                i1 += 195;
            if(k == -1)
            {
                if(i1 < 13)
                    k = i1;
                else
                    byteStream.putByte(i1);
            } else
            if(i1 < 13)
            {
                byteStream.putByte((k << 4) + i1);
                k = -1;
            } else
            {
                byteStream.putByte((k << 4) + (i1 >> 4));
                k = i1 & 0xf;
            }
        }

        if(k != -1)
            byteStream.putByte(k << 4);
    }

    public static String method322(byte byte0, String s)
    {
        aByteStream_589.position = 0;
        method321(s, 569, aByteStream_589);
        int i = aByteStream_589.position;
        if(byte0 != 0)
        {
            for(int j = 1; j > 0; j++);
        }
        aByteStream_589.position = 0;
        String s1 = method320(0, aByteStream_589, i);
        return s1;
    }

    public static int anInt587 = 20411;
    public static char aCharArray588[] = new char[100];
    public static ByteBuffer aByteStream_589 = new ByteBuffer(new byte[100]);
    public static char aCharArray590[] = {
        ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 
        'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 
        'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', 
        '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', 
        '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', 
        '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', 
        ']'
    };

}
