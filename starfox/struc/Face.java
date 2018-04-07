package starfox.struc;

import math.Normal;
import util.BufferReader;

public class Face
{
    public int triangleInd;
    public int verticesIndex[];
    public Normal normal;
    public int color;

    public Face(int vertexCount, BufferReader buf)
    {
        super();

        triangleInd = buf.nextUByte();
        color = buf.nextUByte();
        normal = new Normal(buf);
        verticesIndex = new int[vertexCount];

        for (int i = 0; i < vertexCount; i++)
            verticesIndex[i] = buf.nextUByte();

        if (vertexCount > 8)
            System.err.println("Warning : face with more than 8 vertex (" + vertexCount + ")");
    }

    public int getVertexCount()
    {
        return verticesIndex.length;
    }

    public String getFormatedVertexString()
    {
        String res = "";

        for (int ind : verticesIndex)
            res += ind + ", ";

        return res;
    }

    public String getFormatedNormalString()
    {
        return normal.getFormatedString();
    }

    public String getFormatedExtraString()
    {
        return "triangle: " + triangleInd + "  color: " + String.format("%02X", color);
    }

    @Override
    public String toString()
    {
        return "triangle: " + triangleInd + "  color: " + String.format("%02X", color) + "  vertexCount: "
                + verticesIndex.length + "  normal: " + normal + super.toString();
    }

}
