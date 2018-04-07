package math;

import util.BufferReader;

public class Normal
{
    public double x;
    public double y;
    public double z;

    public Normal(BufferReader buf)
    {
        super();

        x = buf.nextByte();
        y = buf.nextByte();
        z = buf.nextByte();
    }

    public String getFormatedString()
    {
        return "{FIX16(" + x + "), FIX16(" + y + "), FIX16(" + z + ")},";
    }

    @Override
    public String toString()
    {
        return "[X:" + x + " Y:" + y + " Z:" + z + "]";
    }
}
