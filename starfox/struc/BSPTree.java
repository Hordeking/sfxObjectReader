package starfox.struc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import math.Triangle;
import util.BufferReader;

public class BSPTree implements TreeNode
{
    public BSPTree parent;
    public Triangle triangle;
    public int faceGroupAddress;
    public FaceGroup faceGroup;
    public BSPTree front;
    public BSPTree back;
    public int size;

    public BSPTree()
    {
        super();

        parent = null;
        triangle = null;
        faceGroupAddress = 0;
        faceGroup = null;
        front = null;
        back = null;
        size = 0;
    }

    public BSPTree(BSPTree parent, BufferReader buf, Triangle trianglesBase[], FaceGroup faceGroupsBase[])
    {
        super();

        this.parent = parent;

        int nodeType = buf.nextUByte();
        int off;

        switch (nodeType)
        {
            case 0x28:
                // node
                int ind = buf.nextUByte();
                
                // ind == 0xFF for point and wireframe
                if (ind != 0xFF)
                    triangle = trianglesBase[ind];

                off = buf.nextUShort();
                faceGroupAddress = buf.offset + off;

                int frontAdr = buf.nextUByte();

                // null front node
                if (frontAdr == 0x00)
                    front = null;
                else
                    front = new BSPTree(this, new BufferReader(buf, frontAdr - 1), trianglesBase, faceGroupsBase);

                // special value for null back child
                if (buf.getByte() == 0x40)
                {
                    // waste byte
                    buf.nextByte();
                    back = null;
                    size = 6;
                }
                else
                {
                    back = new BSPTree(this, new BufferReader(buf, 0), trianglesBase, faceGroupsBase);
                    size = 5;
                }
                break;

            case 0x44:
                // leaf
                triangle = null;
                off = buf.nextUShort();
                faceGroupAddress = buf.offset + off;
                front = null;
                back = null;
                size = 3;
                break;

            default:
                System.err.println("Unsupported node type " + Integer.toHexString(nodeType) + " in BSP tree");
                size = 0;
                break;
        }
    }

    public void setFaceGroup(FaceGroup faceGroupsBase[])
    {
        if (front != null)
            front.setFaceGroup(faceGroupsBase);
        if (back != null)
            back.setFaceGroup(faceGroupsBase);

        for (FaceGroup fc : faceGroupsBase)
        {
            if (fc.address == faceGroupAddress)
            {
                faceGroup = fc;
                return;
            }
        }

        System.err
                .println("Cannot link facegroup address to facegroup object " + Integer.toHexString(faceGroupAddress));
    }

    public int getTotalSize()
    {
        int result = size;

        if (front != null)
            result += front.getTotalSize();
        if (back != null)
            result += back.getTotalSize();

        return result;
    }

    public String getRawString()
    {
        String res;

        if (faceGroup != null)
            res = faceGroup.toString() + "\n";
        else
            res = "empty\n";

        if (front != null)
            res += front.getRawString();
        if (back != null)
            res += back.getRawString();

        return res;
    }

    public List<BSPTree> getChildren()
    {
        final List<BSPTree> result = new ArrayList<BSPTree>();

        if (front != null)
            result.add(front);
        if (back != null)
            result.add(back);

        return result;
    }

    @Override
    public boolean isLeaf()
    {
        return triangle == null;
    }

    @Override
    public TreeNode getChildAt(int childIndex)
    {
        switch (childIndex)
        {
            case 0:
                return front;
            case 1:
                return back;
            default:
                return null;
        }
    }

    @Override
    public int getChildCount()
    {
        int result = 0;

        if (front != null)
            result++;
        if (back != null)
            result++;

        return result;
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node)
    {
        if (node == front)
            return 0;
        if (node == back)
            return 1;

        return -1;
    }

    @Override
    public boolean getAllowsChildren()
    {
        return false;
    }

    @Override
    public Enumeration children()
    {
        return Collections.enumeration(getChildren());
    }

    @Override
    public String toString()
    {
        if (faceGroup == null)
            return "empty";

        return faceGroup.toString();
    }

}
