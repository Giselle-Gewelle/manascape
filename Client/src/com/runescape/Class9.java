package com.runescape;

public class Class9
{

    public Class9(boolean flag)
    {
        aBoolean170 = true;
        aClass50_Sub1_171 = new NodeSub();
        aClass50_Sub1_171.aClass50_Sub1_1381 = aClass50_Sub1_171;
        aClass50_Sub1_171.aClass50_Sub1_1382 = aClass50_Sub1_171;
        if(!flag)
        {
            for(int i = 1; i > 0; i++);
        }
    }

    public void method185(NodeSub class50_sub1)
    {
        if(class50_sub1.aClass50_Sub1_1382 != null)
            class50_sub1.method443();
        class50_sub1.aClass50_Sub1_1382 = aClass50_Sub1_171.aClass50_Sub1_1382;
        class50_sub1.aClass50_Sub1_1381 = aClass50_Sub1_171;
        class50_sub1.aClass50_Sub1_1382.aClass50_Sub1_1381 = class50_sub1;
        class50_sub1.aClass50_Sub1_1381.aClass50_Sub1_1382 = class50_sub1;
    }

    public NodeSub method186()
    {
        NodeSub class50_sub1 = aClass50_Sub1_171.aClass50_Sub1_1381;
        if(class50_sub1 == aClass50_Sub1_171)
        {
            return null;
        } else
        {
            class50_sub1.method443();
            return class50_sub1;
        }
    }

    public NodeSub method187()
    {
        NodeSub class50_sub1 = aClass50_Sub1_171.aClass50_Sub1_1381;
        if(class50_sub1 == aClass50_Sub1_171)
        {
            aClass50_Sub1_172 = null;
            return null;
        } else
        {
            aClass50_Sub1_172 = class50_sub1.aClass50_Sub1_1381;
            return class50_sub1;
        }
    }

    public NodeSub method188(int i)
    {
        NodeSub class50_sub1 = aClass50_Sub1_172;
        if(class50_sub1 == aClass50_Sub1_171)
        {
            aClass50_Sub1_172 = null;
            return null;
        }
        aClass50_Sub1_172 = class50_sub1.aClass50_Sub1_1381;
        if(i < 1 || i > 1)
            aBoolean170 = !aBoolean170;
        return class50_sub1;
    }

    public int method189()
    {
        int i = 0;
        for(NodeSub class50_sub1 = aClass50_Sub1_171.aClass50_Sub1_1381; class50_sub1 != aClass50_Sub1_171; class50_sub1 = class50_sub1.aClass50_Sub1_1381)
            i++;

        return i;
    }

    public boolean aBoolean170;
    public NodeSub aClass50_Sub1_171;
    public NodeSub aClass50_Sub1_172;
}
