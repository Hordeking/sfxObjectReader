package starfox.struc;

import util.BufferReader;

public class SFXObjectHeader
{
    public static final int HEADER_BASE_LENGHT = 23;

    public int id;
    public int id1;
    public int id2;
    public int id3;
    public int id4;

    public int bank;
    public int vertexAddress;
    public int faceAddress;

    public byte[] data;

    public SFXObjectHeader(BufferReader buf, int objectCount)
    {
        super();

        // store header raw data
        data = new byte[HEADER_BASE_LENGHT + (5 * objectCount)];

        for (int i = 0; i < data.length; i++)
            data[i] = buf.buffer[buf.offset + i];

        vertexAddress = buf.nextUShort();
        bank = buf.nextUByte();
        faceAddress = buf.nextUShort();

        if (bank != 0)
        {
            vertexAddress += (0x8000 * (bank - 1)) + 0x200;
            faceAddress += (0x8000 * (bank - 1)) + 0x200;
        }

        if (objectCount == 0)
        {
            System.out.println("Error parsing object...");
            id = -1;
        }
        else
        {
            int idOffset = 15 + (objectCount * 5);

            id1 = ((data[idOffset + 1] & 0xFF) << 8) | (data[idOffset + 0] & 0xFF);
            id2 = ((data[idOffset + 3] & 0xFF) << 8) | (data[idOffset + 2] & 0xFF);
            id3 = ((data[idOffset + 5] & 0xFF) << 8) | (data[idOffset + 4] & 0xFF);
            id4 = ((data[idOffset + 7] & 0xFF) << 8) | (data[idOffset + 6] & 0xFF);

            if ((id1 == id2) || (id1 == id3) || (id1 == id4))
                id = id1;
            else if ((id2 == id3) || (id2 == id4))
                id = id2;
            else
                id = id4;
        }
    }

    public String getRawString()
    {
        String str = "";

        for (int i = 0; i < data.length; i++)
            str = str + String.format("%02X", data[i] & 0xFF) + " ";

        return str;
    }

    @Override
    public String toString()
    {
        return getRawString();
    }
}
