package starfox.struc;

import java.util.ArrayList;
import java.util.List;

import util.BufferReader;

public class FaceGroup
{
    public int address;
    public Face faces[];

    public FaceGroup(BufferReader buf)
    {
        super();

        // store address
        address = buf.offset;

        List<Face> faceList = new ArrayList<>();

        int vertexCount = buf.nextUByte();

        while ((vertexCount != 0xFF) && (vertexCount != 0xFE))
        {
            if (vertexCount == 0xFE)
                System.err.println("Warning 0xFE end");

            faceList.add(new Face(vertexCount, buf));
            // get next face
            vertexCount = buf.nextUByte();
        }

        faces = faceList.toArray(new Face[faceList.size()]);
    }

    public int getFaceCount()
    {
        return faces.length;
    }

    public String getFormatedVertexString()
    {
        String result = "";

        for (Face f : faces)
            result += f.getFormatedVertexString() + "\n";

        return result;
    }

    public String getFormatedNormalString()
    {
        String result = "";

        for (Face f : faces)
            result += f.getFormatedNormalString() + "\n";

        return result;
    }

    public String getFormatedExtraString()
    {
        String result = "";

        for (Face f : faces)
            result += f.getFormatedExtraString() + "\n";

        return result;
    }

    @Override
    public String toString()
    {
        return String.format("%05X", address) + "  face: " + faces.length;
    }
}
