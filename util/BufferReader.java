package util;

public class BufferReader
{
    public int offset;
    public byte[] buffer;

    public BufferReader(byte[] buffer, int startOffset)
    {
        super();

        this.buffer = buffer;
        offset = startOffset;
    }

    public BufferReader(BufferReader buf, int offset)
    {
        super();

        this.buffer = buf.buffer;
        this.offset = buf.offset + offset;
    }

    public int getByte(int off)
    {
        return buffer[offset + off];
    }

    public int getByte()
    {
        return getByte(0);
    }

    public int getUByte(int off)
    {
        return buffer[offset + off] & 0xFF;
    }

    public int getUByte()
    {
        return getUByte(0);
    }

    public int getShort(int off)
    {
        int result = getUByte(off + 0);
        result |= getByte(off + 1) << 8;

        return result;
    }

    public int getShort()
    {
        return getShort(0);
    }

    public int getUShort(int off)
    {
        int result = getUByte(off + 0);
        result |= getUByte(off + 1) << 8;

        return result;
    }

    public int getUShort()
    {
        return getUShort(0);
    }

    public int getInt(int off)
    {
        int result = getUShort(off + 0);
        result |= getShort(off + 2) << 16;

        return result;
    }

    public int getInt()
    {
        return getInt(0);
    }

    public long getUInt(int off)
    {
        long result = getUShort(off + 0);
        result |= getUShort(off + 2) << 16;

        return result;
    }

    public long getUInt()
    {
        return getUInt(0);
    }

    public int nextByte()
    {
        int result = getByte();
        offset++;
        return result;
    }

    public int nextUByte()
    {
        int result = getUByte();
        offset++;
        return result;
    }

    public int nextShort()
    {
        int result = getShort();
        offset += 2;
        return result;
    }

    public int nextUShort()
    {
        int result = getUShort();
        offset += 2;
        return result;
    }

    public int nextInt()
    {
        int result = getInt();
        offset += 4;
        return result;
    }

    public long nextUInt()
    {
        long result = getUInt();
        offset += 4;
        return result;
    }

}
