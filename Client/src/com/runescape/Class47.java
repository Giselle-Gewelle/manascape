package com.runescape;

import com.runescape.cache.CacheArchive;
import com.runescape.io.ByteBuffer;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Class47
{

    public static Class47 method423(int i)
    {
        for(int j = 0; j < 20; j++)
            if(aClass47Array782[j].anInt773 == i)
                return aClass47Array782[j];

        anInt777 = (anInt777 + 1) % 20;
        Class47 class47 = aClass47Array782[anInt777];
        aByteStream_767.position = anIntArray758[i];
        class47.anInt773 = i;
        class47.method429();
        class47.method430(aByte800, aByteStream_767);
        return class47;
    }

    public Class47 method424(int i)
    {
        if(i != 0)
            anInt788 = 445;
        int j = -1;
        if(anInt778 != -1)
        {
            Class49 class49 = Class49.aClass49Array824[anInt778];
            int k = class49.anInt826;
            int l = class49.anInt827;
            int i1 = class49.anInt828;
            int j1 = Client.anIntArray1214[i1 - l];
            j = aClient770.anIntArray1039[k] >> l & j1;
        } else
        if(anInt781 != -1)
            j = aClient770.anIntArray1039[anInt781];
        if(j < 0 || j >= anIntArray805.length || anIntArray805[j] == -1)
            return null;
        else
            return method423(anIntArray805[j]);
    }

    public void method425(Class32_Sub1 class32_sub1, int i)
    {
        if(anIntArray763 == null)
            return;
        for(int j = 0; j < anIntArray763.length; j++)
            class32_sub1.method337(anIntArray763[j] & 0xffff, 0, aByte793);

        if(i >= 0)
        {
            for(int k = 1; k > 0; k++);
        }
    }

    public static void method426(CacheArchive class2)
    {
        aByteStream_767 = new ByteBuffer(class2.getDataForName("loc.dat"));
        ByteBuffer byteStream = new ByteBuffer(class2.getDataForName("loc.idx"));
        anInt808 = byteStream.getUnsignedShort();
        anIntArray758 = new int[anInt808];
        int i = 2;
        for(int j = 0; j < anInt808; j++)
        {
            anIntArray758[j] = i;
            i += byteStream.getUnsignedShort();
        }

        aClass47Array782 = new Class47[20];
        for(int k = 0; k < 20; k++)
            aClass47Array782[k] = new Class47();

    }

    public Model method427(int i, int j, int k, int l)
    {
        Model class50_sub1_sub4_sub4 = null;
        long l1;
        if(anIntArray789 == null)
        {
            if(l != 10)
                return null;
            l1 = (long)((anInt773 << 6) + i) + ((long)(j + 1) << 32);
            Model class50_sub1_sub4_sub4_1 = (Model)aClass33_762.get(l1);
            if(class50_sub1_sub4_sub4_1 != null)
                return class50_sub1_sub4_sub4_1;
            if(anIntArray763 == null)
                return null;
            boolean flag1 = aBoolean798 ^ (i > 3);
            int k1 = anIntArray763.length;
            for(int i2 = 0; i2 < k1; i2++)
            {
                int l2 = anIntArray763[i2];
                if(flag1)
                    l2 += 0x10000;
                class50_sub1_sub4_sub4 = (Model)aClass33_779.get(l2);
                if(class50_sub1_sub4_sub4 == null)
                {
                    class50_sub1_sub4_sub4 = Model.getModel(l2 & 0xffff);
                    if(class50_sub1_sub4_sub4 == null)
                        return null;
                    if(flag1)
                        class50_sub1_sub4_sub4.method592(0);
                    aClass33_779.put(class50_sub1_sub4_sub4, l2, 5);
                }
                if(k1 > 1)
                    aClass50_Sub1_Sub4_Sub4Array771[i2] = class50_sub1_sub4_sub4;
            }

            if(k1 > 1)
                class50_sub1_sub4_sub4 = new Model(k1, aClass50_Sub1_Sub4_Sub4Array771);
        } else
        {
            int i1 = -1;
            for(int j1 = 0; j1 < anIntArray789.length; j1++)
            {
                if(anIntArray789[j1] != l)
                    continue;
                i1 = j1;
                break;
            }

            if(i1 == -1)
                return null;
            l1 = (long)((anInt773 << 6) + (i1 << 3) + i) + ((long)(j + 1) << 32);
            Model class50_sub1_sub4_sub4_2 = (Model)aClass33_762.get(l1);
            if(class50_sub1_sub4_sub4_2 != null)
                return class50_sub1_sub4_sub4_2;
            int j2 = anIntArray763[i1];
            boolean flag3 = aBoolean798 ^ (i > 3);
            if(flag3)
                j2 += 0x10000;
            class50_sub1_sub4_sub4 = (Model)aClass33_779.get(j2);
            if(class50_sub1_sub4_sub4 == null)
            {
                class50_sub1_sub4_sub4 = Model.getModel(j2 & 0xffff);
                if(class50_sub1_sub4_sub4 == null)
                    return null;
                if(flag3)
                    class50_sub1_sub4_sub4.method592(0);
                aClass33_779.put(class50_sub1_sub4_sub4, j2, 5);
            }
        }
        boolean flag;
        if(anInt780 != 128 || anInt760 != 128 || anInt796 != 128)
            flag = true;
        else
            flag = false;
        boolean flag2;
        if(anInt761 != 0 || anInt785 != 0 || anInt766 != 0)
            flag2 = true;
        else
            flag2 = false;
        Model class50_sub1_sub4_sub4_3 = new Model(i == 0 && j == -1 && !flag && !flag2, false, anIntArray799 == null, class50_sub1_sub4_sub4, Class21.method239(j));
        if(k != 0)
            anInt768 = 487;
        if(j != -1)
        {
            class50_sub1_sub4_sub4_3.createBones();
            class50_sub1_sub4_sub4_3.method585(j);
            class50_sub1_sub4_sub4_3.anIntArrayArray1679 = null;
            class50_sub1_sub4_sub4_3.anIntArrayArray1678 = null;
        }
        while(i-- > 0) 
            class50_sub1_sub4_sub4_3.method588(true);
        if(anIntArray799 != null)
        {
            for(int k2 = 0; k2 < anIntArray799.length; k2++)
                class50_sub1_sub4_sub4_3.recolour(anIntArray799[k2], anIntArray792[k2]);

        }
        if(flag)
            class50_sub1_sub4_sub4_3.method593(anInt760, anInt796, 9, anInt780);
        if(flag2)
            class50_sub1_sub4_sub4_3.method590(anInt761, anInt766, false, anInt785);
        class50_sub1_sub4_sub4_3.light(64 + aByte784, 768 + aByte787 * 5, -50, -10, -50, !aBoolean804);
        if(anInt794 == 1)
            class50_sub1_sub4_sub4_3.anInt1675 = ((Class50_Sub1_Sub4) (class50_sub1_sub4_sub4_3)).modelHeight;
        aClass33_762.put(class50_sub1_sub4_sub4_3, l1, 5);
        return class50_sub1_sub4_sub4_3;
    }

    public boolean method428(int i)
    {
        if(anIntArray763 == null)
            return true;
        boolean flag = true;
        while(i >= 0) 
            anInt768 = 347;
        for(int j = 0; j < anIntArray763.length; j++)
            flag &= Model.isCached(anIntArray763[j] & 0xffff);

        return flag;
    }

    public void method429()
    {
        anIntArray763 = null;
        anIntArray789 = null;
        aString776 = "null";
        aByteArray783 = null;
        anIntArray799 = null;
        anIntArray792 = null;
        anInt801 = 1;
        anInt775 = 1;
        aBoolean810 = true;
        aBoolean809 = true;
        aBoolean759 = false;
        aBoolean769 = false;
        aBoolean804 = false;
        aBoolean797 = false;
        anInt803 = -1;
        anInt802 = 16;
        aByte784 = 0;
        aByte787 = 0;
        aStringArray790 = null;
        anInt806 = -1;
        anInt795 = -1;
        aBoolean798 = false;
        aBoolean807 = true;
        anInt780 = 128;
        anInt760 = 128;
        anInt796 = 128;
        anInt764 = 0;
        anInt761 = 0;
        anInt785 = 0;
        anInt766 = 0;
        aBoolean765 = false;
        aBoolean791 = false;
        anInt794 = -1;
        anInt778 = -1;
        anInt781 = -1;
        anIntArray805 = null;
    }

    public void method430(byte byte0, ByteBuffer byteStream)
    {
        int i = -1;
        if(byte0 != 6)
            throw new NullPointerException();
label0:
        do
        {
            int j;
            do
            {
                j = byteStream.getUnsignedByte();
                if(j == 0)
                    break label0;
                if(j == 1)
                {
                    int k = byteStream.getUnsignedByte();
                    if(k > 0)
                        if(anIntArray763 == null || aBoolean772)
                        {
                            anIntArray789 = new int[k];
                            anIntArray763 = new int[k];
                            for(int k1 = 0; k1 < k; k1++)
                            {
                                anIntArray763[k1] = byteStream.getUnsignedShort();
                                anIntArray789[k1] = byteStream.getUnsignedByte();
                            }

                        } else
                        {
                            byteStream.position += k * 3;
                        }
                } else
                if(j == 2)
                    aString776 = byteStream.getRS2String();
                else
                if(j == 3)
                    aByteArray783 = byteStream.getBytes();
                else
                if(j == 5)
                {
                    int l = byteStream.getUnsignedByte();
                    if(l > 0)
                        if(anIntArray763 == null || aBoolean772)
                        {
                            anIntArray789 = null;
                            anIntArray763 = new int[l];
                            for(int l1 = 0; l1 < l; l1++)
                                anIntArray763[l1] = byteStream.getUnsignedShort();

                        } else
                        {
                            byteStream.position += l * 2;
                        }
                } else
                if(j == 14)
                    anInt801 = byteStream.getUnsignedByte();
                else
                if(j == 15)
                    anInt775 = byteStream.getUnsignedByte();
                else
                if(j == 17)
                    aBoolean810 = false;
                else
                if(j == 18)
                    aBoolean809 = false;
                else
                if(j == 19)
                {
                    i = byteStream.getUnsignedByte();
                    if(i == 1)
                        aBoolean759 = true;
                } else
                if(j == 21)
                    aBoolean769 = true;
                else
                if(j == 22)
                    aBoolean804 = true;
                else
                if(j == 23)
                    aBoolean797 = true;
                else
                if(j == 24)
                {
                    anInt803 = byteStream.getUnsignedShort();
                    if(anInt803 == 65535)
                        anInt803 = -1;
                } else
                if(j == 28)
                    anInt802 = byteStream.getUnsignedByte();
                else
                if(j == 29)
                    aByte784 = byteStream.getByte();
                else
                if(j == 39)
                    aByte787 = byteStream.getByte();
                else
                if(j >= 30 && j < 39)
                {
                    if(aStringArray790 == null)
                        aStringArray790 = new String[5];
                    aStringArray790[j - 30] = byteStream.getRS2String();
                    if(aStringArray790[j - 30].equalsIgnoreCase("hidden"))
                        aStringArray790[j - 30] = null;
                } else
                if(j == 40)
                {
                    int i1 = byteStream.getUnsignedByte();
                    anIntArray799 = new int[i1];
                    anIntArray792 = new int[i1];
                    for(int i2 = 0; i2 < i1; i2++)
                    {
                        anIntArray799[i2] = byteStream.getUnsignedShort();
                        anIntArray792[i2] = byteStream.getUnsignedShort();
                    }

                } else
                if(j == 60)
                    anInt806 = byteStream.getUnsignedShort();
                else
                if(j == 62)
                    aBoolean798 = true;
                else
                if(j == 64)
                    aBoolean807 = false;
                else
                if(j == 65)
                    anInt780 = byteStream.getUnsignedShort();
                else
                if(j == 66)
                    anInt760 = byteStream.getUnsignedShort();
                else
                if(j == 67)
                    anInt796 = byteStream.getUnsignedShort();
                else
                if(j == 68)
                    anInt795 = byteStream.getUnsignedShort();
                else
                if(j == 69)
                    anInt764 = byteStream.getUnsignedByte();
                else
                if(j == 70)
                    anInt761 = byteStream.getShort();
                else
                if(j == 71)
                    anInt785 = byteStream.getShort();
                else
                if(j == 72)
                    anInt766 = byteStream.getShort();
                else
                if(j == 73)
                    aBoolean765 = true;
                else
                if(j == 74)
                {
                    aBoolean791 = true;
                } else
                {
                    if(j != 75)
                        continue;
                    anInt794 = byteStream.getUnsignedByte();
                }
                continue label0;
            } while(j != 77);
            anInt778 = byteStream.getUnsignedShort();
            if(anInt778 == 65535)
                anInt778 = -1;
            anInt781 = byteStream.getUnsignedShort();
            if(anInt781 == 65535)
                anInt781 = -1;
            int j1 = byteStream.getUnsignedByte();
            anIntArray805 = new int[j1 + 1];
            for(int j2 = 0; j2 <= j1; j2++)
            {
                anIntArray805[j2] = byteStream.getUnsignedShort();
                if(anIntArray805[j2] == 65535)
                    anIntArray805[j2] = -1;
            }

        } while(true);
        if(i == -1)
        {
            aBoolean759 = false;
            if(anIntArray763 != null && (anIntArray789 == null || anIntArray789[0] == 10))
                aBoolean759 = true;
            if(aStringArray790 != null)
                aBoolean759 = true;
        }
        if(aBoolean791)
        {
            aBoolean810 = false;
            aBoolean809 = false;
        }
        if(anInt794 == -1)
            anInt794 = aBoolean810 ? 1 : 0;
    }

    public Model method431(int i, int j, int k, int l, int i1, int j1, int k1)
    {
        Model class50_sub1_sub4_sub4 = method427(j, k1, 0, i);
        if(class50_sub1_sub4_sub4 == null)
            return null;
        if(aBoolean769 || aBoolean804)
            class50_sub1_sub4_sub4 = new Model(aBoolean769, aBoolean804, 0, class50_sub1_sub4_sub4);
        if(aBoolean769)
        {
            int l1 = (k + l + i1 + j1) / 4;
            for(int i2 = 0; i2 < class50_sub1_sub4_sub4.anInt1648; i2++)
            {
                int j2 = class50_sub1_sub4_sub4.anIntArray1649[i2];
                int k2 = class50_sub1_sub4_sub4.anIntArray1651[i2];
                int l2 = k + ((l - k) * (j2 + 64)) / 128;
                int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
                int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
                class50_sub1_sub4_sub4.anIntArray1650[i2] += j3 - l1;
            }

            class50_sub1_sub4_sub4.method582(6);
        }
        return class50_sub1_sub4_sub4;
    }

    public boolean method432(int i, int j)
    {
        if(i != 26261)
            aBoolean786 = !aBoolean786;
        if(anIntArray789 == null)
        {
            if(anIntArray763 == null)
                return true;
            if(j != 10)
                return true;
            boolean flag = true;
            for(int l = 0; l < anIntArray763.length; l++)
                flag &= Model.isCached(anIntArray763[l] & 0xffff);

            return flag;
        }
        for(int k = 0; k < anIntArray789.length; k++)
            if(anIntArray789[k] == j)
                return Model.isCached(anIntArray763[k] & 0xffff);

        return true;
    }

    public static void method433(boolean flag)
    {
        aClass33_779 = null;
        aClass33_762 = null;
        anIntArray758 = null;
        if(flag)
        {
            for(int i = 1; i > 0; i++);
        }
        aClass47Array782 = null;
        aByteStream_767 = null;
    }

    public Class47()
    {
        anInt768 = -992;
        anInt773 = -1;
        aBoolean774 = true;
        aString776 = "null";
        aBoolean786 = true;
        aByte793 = -113;
    }

    public static int anIntArray758[];
    public boolean aBoolean759;
    public int anInt760;
    public int anInt761;
    public static MemCache aClass33_762 = new MemCache(40);
    public int anIntArray763[];
    public int anInt764;
    public boolean aBoolean765;
    public int anInt766;
    public static ByteBuffer aByteStream_767;
    public int anInt768;
    public boolean aBoolean769;
    public static Client aClient770;
    public static Model aClass50_Sub1_Sub4_Sub4Array771[] = new Model[4];
    public static boolean aBoolean772;
    public int anInt773;
    public boolean aBoolean774;
    public int anInt775;
    public String aString776;
    public static int anInt777;
    public int anInt778;
    public static MemCache aClass33_779 = new MemCache(500);
    public int anInt780;
    public int anInt781;
    public static Class47 aClass47Array782[];
    public byte aByteArray783[];
    public byte aByte784;
    public int anInt785;
    public boolean aBoolean786;
    public byte aByte787;
    public int anInt788;
    public int anIntArray789[];
    public String aStringArray790[];
    public boolean aBoolean791;
    public int anIntArray792[];
    public byte aByte793;
    public int anInt794;
    public int anInt795;
    public int anInt796;
    public boolean aBoolean797;
    public boolean aBoolean798;
    public int anIntArray799[];
    public static byte aByte800 = 6;
    public int anInt801;
    public int anInt802;
    public int anInt803;
    public boolean aBoolean804;
    public int anIntArray805[];
    public int anInt806;
    public boolean aBoolean807;
    public static int anInt808;
    public boolean aBoolean809;
    public boolean aBoolean810;

}
