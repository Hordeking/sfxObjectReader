package math;

import util.BufferReader;

public class Vertex
{
    public boolean sh;
    public double x;
    public double y;
    public double z;

    public Vertex(BufferReader buf, boolean sh)
    {
        super();

        this.sh = sh;

        if (sh)
        {
            x = buf.nextShort();
            y = buf.nextShort();
            z = buf.nextShort();
        }
        else
        {
            x = buf.nextByte();
            y = buf.nextByte();
            z = buf.nextByte();
        }
    }

    public Vertex(Vertex v, boolean xflip, boolean yflip, boolean zflip)
    {
        super();

        sh = v.sh;

        if (xflip)
            x = -v.x;
        else
            x = v.x;
        if (yflip)
            y = -v.y;
        else
            y = v.y;
        if (zflip)
            z = -v.z;
        else
            z = v.z;
    }

    public String getFormatedString()
    {
        //return x + "\t" + y + "\t" + z;
        return z + "\t" + (-x) + "\t" + (-y);
    }

    public String getRawString()
    {
        String res;

        if (sh)
            res = "[short   ";
        else
            res = "[byte   ";

        return res + "X:" + x + " Y:" + y + " Z:" + z + "]";
    }

    @Override
    public String toString()
    {
        return getRawString();
    }
}
