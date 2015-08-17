package com.runescape;

import com.runescape.io.ByteBuffer;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Class41
{

    public Class41(ByteBuffer byteStream, int i)
    {
        anInt697 = byteStream.getUnsignedByte();
        if(i != 0)
            throw new NullPointerException();
        anIntArray698 = new int[anInt697];
        anIntArrayArray699 = new int[anInt697][];
        for(int j = 0; j < anInt697; j++)
            anIntArray698[j] = byteStream.getUnsignedByte();

        for(int k = 0; k < anInt697; k++)
        {
            int l = byteStream.getUnsignedByte();
            anIntArrayArray699[k] = new int[l];
            for(int i1 = 0; i1 < l; i1++)
                anIntArrayArray699[k][i1] = byteStream.getUnsignedByte();

        }

    }

    public int anInt697;
    public int anIntArray698[];
    public int anIntArrayArray699[][];
}
