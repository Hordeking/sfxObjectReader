package starfox.struc;

import util.BufferReader;

public class SFXObjectId implements Comparable<SFXObjectId>
{
    static final int MAX_OBJECT = 5;

    public int objectCount;
    public String name;
    public SFXObjectHeader header;
    public SFXObject object;

    public SFXObjectId(byte buf[], int offset)
    {
        super();

        name = "";

        objectCount = findNumObject(buf, offset + 20);
        header = new SFXObjectHeader(new BufferReader(buf, offset), objectCount);

        if (header.bank != 0)
            object = new SFXObject(buf, header.vertexAddress, header.faceAddress);
        else
            object = null;
    }

    int findNumObject(byte buf[], int offset)
    {
        for (int i = 0; i < MAX_OBJECT; i++)
            if (testTail(buf, offset + (i * 5)))
                return i + 1;

        return 0;
    }

    boolean testTail(byte buf[], int offset)
    {
        int i1, i2, i3, i4;

        i1 = ((buf[offset + 1] & 0xFF) << 8) | (buf[offset + 0] & 0xFF);
        i2 = ((buf[offset + 3] & 0xFF) << 8) | (buf[offset + 2] & 0xFF);
        i3 = ((buf[offset + 5] & 0xFF) << 8) | (buf[offset + 4] & 0xFF);
        i4 = ((buf[offset + 7] & 0xFF) << 8) | (buf[offset + 6] & 0xFF);

        boolean b1 = (i1 > 0x8000) && (i2 > 0x8000) && (i3 > 0x8000) && (i4 > 0x8000);
        boolean b2 = (i1 == i2) || (i1 == i3) || (i1 == i4) || (i2 == i3) || (i2 == i4) || (i3 == i4);

        return b1 && b2;
    }

    @Override
    public int compareTo(SFXObjectId o)
    {
        return Integer.compare(header.id, o.header.id);
        // return Integer.compare(num, o.num);
    }

    @Override
    public String toString()
    {
        // return String.format("%02X", num) + "  " + Integer.toHexString(id) + "  " + name;
        return String.format("%04X", header.id) + "  " + name;
    }
}
