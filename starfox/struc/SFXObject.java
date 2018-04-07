/**
 * 
 */
package starfox.struc;

import java.util.ArrayList;
import java.util.List;

import math.Triangle;
import math.Vertex;
import util.BufferReader;

/**
 * @author Stephane
 */
public class SFXObject
{
    public int vertexAddress;
    public int faceAddress;

    public Vertex vertices[][];
    public Triangle triangles[];
    public BSPTree bspTree;
    public FaceGroup faceGroups[];

    public boolean valid;

    public SFXObject(byte[] data, int vertexAddress, int faceAddress)
    {
        super();

        this.vertexAddress = vertexAddress;
        this.faceAddress = faceAddress;

        vertices = new Vertex[0][0];
        triangles = new Triangle[0];
        bspTree = null;
        faceGroups = new FaceGroup[0];
        valid = true;

        if (buildVertex(new BufferReader(data, vertexAddress)))
            // cannot build face if error during vertex build
            buildFace(new BufferReader(data, faceAddress));
    }

    boolean buildVertex(BufferReader buf)
    {
        if (buf.offset >= buf.buffer.length)
        {
            System.err.println("Vertex data address incorrect : " + Integer.toHexString(buf.offset));
            valid = false;
            return false;
        }

        List<List<Vertex>> vertexAll = new ArrayList<List<Vertex>>();
        List<Vertex> vertexFrame = new ArrayList<Vertex>();

        // use a local buffer per frame
        BufferReader frameBuf = new BufferReader(buf, 0);

        // single frame by default
        int frameCnt = 1;
        int frameInd = 0;

        while (frameInd < frameCnt)
        {
            // add vertex bloc
            vertexFrame.addAll(buildVertexBloc(frameBuf));

            // check if list is terminated
            int listType = frameBuf.nextUByte();
            switch (listType)
            {
                case 0x0C:
                    // end vertex list --> next frame
                    vertexAll.add(vertexFrame);
                    frameInd++;
                    frameBuf = new BufferReader(buf, 0);
                    vertexFrame = new ArrayList<Vertex>();
                    break;

                case 0x20:
                    // jump marker
                    frameBuf.offset += frameBuf.nextUShort() + 1;
                    break;

                case 0x1C:
                    // animated vertex list --> update frame number
                    frameCnt = frameBuf.nextUByte();

                    // go to frame offset
                    frameBuf.offset += frameInd * 2;
                    // get offset for vertex
                    frameBuf.offset += frameBuf.nextUShort() + 1;
                    break;

                default:
                    // unknow
                    System.err.println("Unknown vertex list type " + Integer.toHexString(listType) + " at address "
                            + Integer.toHexString(frameBuf.offset - 1));
                    valid = false;
                    return false;
            }

        }

        vertices = new Vertex[vertexAll.size()][];

        for (int i = 0; i < vertices.length; i++)
        {
            List<Vertex> vertexList = vertexAll.get(i);
            vertices[i] = vertexList.toArray(new Vertex[vertexList.size()]);
        }

        return true;
    }

    List<Vertex> buildVertexBloc(BufferReader buf)
    {
        List<Vertex> result = new ArrayList<Vertex>();

        while (true)
        {
            int listType = buf.getUByte();

            switch (listType)
            {
                case 0x04:
                case 0x08:
                case 0x34:
                case 0x38:
                    // add vertex bloc to result
                    result.addAll(buildVertexSubBloc(buf));
                    break;

                case 0x0C:
                    // end vertex list
                case 0x20:
                    // 16 bits offset jump 
                case 0x1C:
                    // animated vertex list (handled in parent)
                    return result;

                default:
                    // unknown
                    System.err.println("Unknown vertex list type " + Integer.toHexString(listType) + " at address "
                            + Integer.toHexString(buf.offset - 1));
                    valid = false;
                    return result;
            }
        }
    }

    List<Vertex> buildVertexSubBloc(BufferReader buf)
    {
        boolean xflip = false;
        boolean yflip = false;
        boolean sh = false;

        int listType = buf.nextUByte();

        switch (listType)
        {
            case 0x04:
                // normal vertex list
                break;

            case 0x08:
                // 16 bits vertex list
                sh = true;
                break;

            case 0x34:
                // 16 bits + X flip vertex list
                xflip = true;
                sh = true;
                break;

            case 0x38:
                // X flip vertex list
                xflip = true;
                break;
        }

        List<Vertex> result = new ArrayList<Vertex>();
        int num = buf.nextUByte();

        for (int i = 0; i < num; i++)
        {
            Vertex v = new Vertex(buf, sh);

            result.add(v);

            if (xflip)
                result.add(new Vertex(v, true, false, false));
            if (yflip)
                result.add(new Vertex(v, false, true, false));
        }

        return result;
    }

    void buildFace(BufferReader buf)
    {
        if (buf.offset >= buf.buffer.length)
        {
            System.err.println("Face data address incorrect : " + Integer.toHexString(buf.offset));
            valid = false;
            return;
        }

        List<FaceGroup> faceGroupList = new ArrayList<FaceGroup>();

        int listType = buf.nextUByte();

        // is there a triangle list ?
        if (listType == 0x30)
        {
            buildTriangle(buf);
            // get next list type
            listType = buf.nextUByte();
        }

        // is there a BSP Tree ?
        if (listType == 0x3C)
        {
            bspTree = new BSPTree(null, buf, triangles, faceGroups);
            buf.offset += bspTree.getTotalSize() - bspTree.size;
            // get next list type
            listType = buf.nextUByte();
        }

        // face group ?
        while (listType == 0x14)
        {
            faceGroupList.add(new FaceGroup(buf));
            // get next list type
            listType = buf.nextUByte();
        }

        if (faceGroupList.size() == 0x00)
        {
            System.err.println("No facegroup found !");
            valid = false;
        }

        faceGroups = faceGroupList.toArray(new FaceGroup[faceGroupList.size()]);

        // link facegroup address to facegroup object in BSP tree
        if (bspTree != null)
            bspTree.setFaceGroup(faceGroups);

        if (listType != 0x00)
        {
            System.err.println("00 end mark expected but " + Integer.toHexString(listType) + " found !");
            valid = false;
        }
    }

    void buildTriangle(BufferReader buf)
    {
        triangles = new Triangle[buf.nextUByte()];

        for (int i = 0; i < triangles.length; i++)
            triangles[i] = new Triangle(buf);
    }

    public int getFrameCount()
    {
        return vertices.length;
    }

    public int getVertexCount(int frame)
    {
        return vertices[frame].length;
    }

    public int getTriangleCount()
    {
        return triangles.length;
    }

    public int getFaceGroupCount()
    {
        return faceGroups.length;
    }

    public String getTriangleFormatedString()
    {
        String result = "";

        if (triangles != null)
            for (Triangle t : triangles)
                result += t.getFormatedString() + "\n";

        return result;
    }

    public String getTriangleRawString(int frame)
    {
        Vertex vtx[] = vertices[frame];
        String result = "";

        if (triangles != null)
            for (Triangle t : triangles)
                result += t.getRawString(vtx) + "\n";

        return result;
    }

    public String getVertexFormatedString(int frame)
    {
        String result = "";

        if (vertices != null)
            for (Vertex v : vertices[frame])
                result += v.getFormatedString() + "\n";

        return result;
    }

    public String getVertexRawString(int frame)
    {
        String result = "";

        if (vertices != null)
            for (Vertex v : vertices[frame])
                result += v.getRawString() + "\n";

        return result;
    }
}
