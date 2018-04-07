package math;

import util.BufferReader;

public class Triangle
{
    public int a;
    public int b;
    public int c;

    public Triangle(BufferReader buf)
    {
        super();

        a = buf.nextUByte();
        b = buf.nextUByte();
        c = buf.nextUByte();
    }

    public String getFormatedString()
    {
        return a + ", " + b + ", " + c + ",";
    }

    public String getRawString(Vertex vertices[])
    {
        return "[A:" + vertices[a] + "   B:" + vertices[b] + "   C:" + vertices[c] + "]";
    }

    @Override
    public String toString()
    {
        return "[" + a + ", " + b + ", " + c + "]";
    }

}
