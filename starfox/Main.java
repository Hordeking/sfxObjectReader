package starfox;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import starfox.struc.BSPTree;
import starfox.struc.FaceGroup;
import starfox.struc.SFXObject;
import starfox.struc.SFXObjectHeader;
import starfox.struc.SFXObjectId;

public class Main implements Runnable, ListSelectionListener
{
    public static final int BASE_LIST_ADR = 0x2E15;

    /**
     * GUI
     */
    private JFrame frmStarfoxBojectDecoder;
    private JList<SFXObjectId> objectList;
    private JTextField bankField;
    private JTextField vertexAddressField;
    private JTextField faceAddressField;
    private JTextField rawHeaderField;
    private JTextField vertexNumberField;
    private JTextArea vertexRawArea;
    private JTextField triangleNumberField;
    private JTextArea triangleFormatedArea;
    private JTextArea triangleRawArea;
    private JTextArea vertexFormatedArea;
    private JList<FaceGroup> faceGroupList;
    private JTree bspTreeTree;
    private JTextArea normalFormatedArea;
    private JTextArea faceFormatedArea;
    private JTextArea extraDataFormatedArea;
    private JTextField searchObjectField;
    private JSpinner vertexFrameSpinner;
    private JLabel statusLabel;

    // internal
    private byte[] rom;
    private SFXObjectId[] objectsId;
    private JLabel vertexFrameCountLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    Main window = new Main();
                    window.frmStarfoxBojectDecoder.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main()
    {
        super();

        rom = new byte[0x100000];

        initialize();

        objectList.getSelectionModel().addListSelectionListener(this);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmStarfoxBojectDecoder = new JFrame();
        frmStarfoxBojectDecoder.setTitle("Starfox object decoder");
        frmStarfoxBojectDecoder.setBounds(100, 100, 831, 459);
        frmStarfoxBojectDecoder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmStarfoxBojectDecoder.getContentPane().setLayout(null);

        JButton btnLoadRom = new JButton("Load Rom...");
        btnLoadRom.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser jfc = new JFileChooser(".");

                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    File f = jfc.getSelectedFile();

                    try
                    {
                        FileInputStream fis = new FileInputStream(f);

                        if (fis.read(rom, 0, 0x100000) != 0x100000)
                            JOptionPane.showMessageDialog(null, "Invalid rom file !");
                        else
                            initRom();
                    }
                    catch (FileNotFoundException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e2)
                    {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                }
            }
        });
        btnLoadRom.setBounds(6, 14, 127, 23);
        frmStarfoxBojectDecoder.getContentPane().add(btnLoadRom);

        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new TitledBorder(null, "Object list", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_4.setBounds(6, 100, 184, 310);
        frmStarfoxBojectDecoder.getContentPane().add(panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel_4.add(scrollPane);

        objectList = new JList<SFXObjectId>();
        objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(objectList);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Object header", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(200, 0, 605, 65);
        frmStarfoxBojectDecoder.getContentPane().add(panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] {60, 0, 60, 0, 60, 0, 0};
        gbl_panel.rowHeights = new int[] {0, 0, 0};
        gbl_panel.columnWeights = new double[] {0.0, 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblBank = new JLabel("bank");
        GridBagConstraints gbc_lblBank = new GridBagConstraints();
        gbc_lblBank.insets = new Insets(0, 0, 5, 5);
        gbc_lblBank.anchor = GridBagConstraints.EAST;
        gbc_lblBank.gridx = 0;
        gbc_lblBank.gridy = 0;
        panel.add(lblBank, gbc_lblBank);

        bankField = new JTextField();
        bankField.setEditable(false);
        GridBagConstraints gbc_bankField = new GridBagConstraints();
        gbc_bankField.insets = new Insets(0, 0, 5, 5);
        gbc_bankField.fill = GridBagConstraints.HORIZONTAL;
        gbc_bankField.gridx = 1;
        gbc_bankField.gridy = 0;
        panel.add(bankField, gbc_bankField);
        bankField.setColumns(10);

        JLabel lblVertex = new JLabel("vertex");
        GridBagConstraints gbc_lblVertex = new GridBagConstraints();
        gbc_lblVertex.anchor = GridBagConstraints.EAST;
        gbc_lblVertex.insets = new Insets(0, 0, 5, 5);
        gbc_lblVertex.gridx = 2;
        gbc_lblVertex.gridy = 0;
        panel.add(lblVertex, gbc_lblVertex);

        vertexAddressField = new JTextField();
        vertexAddressField.setEditable(false);
        GridBagConstraints gbc_vertexAddressField = new GridBagConstraints();
        gbc_vertexAddressField.insets = new Insets(0, 0, 5, 5);
        gbc_vertexAddressField.fill = GridBagConstraints.HORIZONTAL;
        gbc_vertexAddressField.gridx = 3;
        gbc_vertexAddressField.gridy = 0;
        panel.add(vertexAddressField, gbc_vertexAddressField);
        vertexAddressField.setColumns(10);

        JLabel lblFaces = new JLabel("face");
        GridBagConstraints gbc_lblFaces = new GridBagConstraints();
        gbc_lblFaces.anchor = GridBagConstraints.EAST;
        gbc_lblFaces.insets = new Insets(0, 0, 5, 5);
        gbc_lblFaces.gridx = 4;
        gbc_lblFaces.gridy = 0;
        panel.add(lblFaces, gbc_lblFaces);

        faceAddressField = new JTextField();
        faceAddressField.setEditable(false);
        GridBagConstraints gbc_faceAddressField = new GridBagConstraints();
        gbc_faceAddressField.insets = new Insets(0, 0, 5, 0);
        gbc_faceAddressField.fill = GridBagConstraints.HORIZONTAL;
        gbc_faceAddressField.gridx = 5;
        gbc_faceAddressField.gridy = 0;
        panel.add(faceAddressField, gbc_faceAddressField);
        faceAddressField.setColumns(10);

        JLabel lblNewLabel = new JLabel("raw");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        panel.add(lblNewLabel, gbc_lblNewLabel);

        rawHeaderField = new JTextField();
        rawHeaderField.setEditable(false);
        GridBagConstraints gbc_rawHeaderField = new GridBagConstraints();
        gbc_rawHeaderField.gridwidth = 5;
        gbc_rawHeaderField.insets = new Insets(0, 0, 0, 5);
        gbc_rawHeaderField.fill = GridBagConstraints.HORIZONTAL;
        gbc_rawHeaderField.gridx = 1;
        gbc_rawHeaderField.gridy = 1;
        panel.add(rawHeaderField, gbc_rawHeaderField);
        rawHeaderField.setColumns(10);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBorder(new TitledBorder(null, "Object data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        tabbedPane.setBounds(200, 66, 605, 319);
        frmStarfoxBojectDecoder.getContentPane().add(tabbedPane);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Vertex", null, panel_1, null);
        panel_1.setLayout(null);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 63, 248, 194);
        panel_1.add(scrollPane_1);

        vertexRawArea = new JTextArea();
        scrollPane_1.setViewportView(vertexRawArea);

        JLabel lblNewLabel_1 = new JLabel("Vertex number");
        lblNewLabel_1.setBounds(10, 38, 121, 14);
        panel_1.add(lblNewLabel_1);

        vertexNumberField = new JTextField();
        lblNewLabel_1.setLabelFor(vertexNumberField);
        vertexNumberField.setBounds(158, 35, 102, 20);
        panel_1.add(vertexNumberField);
        vertexNumberField.setColumns(10);

        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(268, 11, 310, 246);
        panel_1.add(scrollPane_2);

        vertexFormatedArea = new JTextArea();
        scrollPane_2.setViewportView(vertexFormatedArea);

        vertexFrameSpinner = new JSpinner();
        vertexFrameSpinner.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                vertexFrameSpinnerChanged();
            }
        });
        vertexFrameSpinner.setBounds(159, 7, 43, 20);
        panel_1.add(vertexFrameSpinner);

        JLabel lblFrame = new JLabel("Frame");
        lblFrame.setLabelFor(vertexFrameSpinner);
        lblFrame.setBounds(10, 11, 121, 14);
        panel_1.add(lblFrame);

        vertexFrameCountLabel = new JLabel("/ 0");
        vertexFrameCountLabel.setBounds(212, 10, 46, 14);
        panel_1.add(vertexFrameCountLabel);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Triangle", null, panel_2, null);
        panel_2.setLayout(null);

        JLabel lblTriangleNumber = new JLabel("Triangle number");
        lblTriangleNumber.setBounds(10, 11, 101, 14);
        panel_2.add(lblTriangleNumber);

        triangleNumberField = new JTextField();
        triangleNumberField.setBounds(185, 8, 66, 20);
        panel_2.add(triangleNumberField);
        triangleNumberField.setColumns(10);

        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(10, 36, 241, 221);
        panel_2.add(scrollPane_3);

        triangleRawArea = new JTextArea();
        scrollPane_3.setViewportView(triangleRawArea);

        JScrollPane scrollPane_4 = new JScrollPane();
        scrollPane_4.setBounds(261, 11, 317, 246);
        panel_2.add(scrollPane_4);

        triangleFormatedArea = new JTextArea();
        scrollPane_4.setViewportView(triangleFormatedArea);

        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("BSP Tree / Face", null, panel_3, null);
        panel_3.setLayout(null);

        JScrollPane scrollPane_5 = new JScrollPane();
        scrollPane_5.setBounds(194, 11, 137, 246);
        panel_3.add(scrollPane_5);

        faceGroupList = new JList<FaceGroup>();
        faceGroupList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                faceGroupListChanged();
            }
        });
        faceGroupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane_5.setViewportView(faceGroupList);

        JScrollPane scrollPane_6 = new JScrollPane();
        scrollPane_6.setBounds(10, 11, 174, 246);
        panel_3.add(scrollPane_6);

        bspTreeTree = new JTree();
        bspTreeTree.setModel(new DefaultTreeModel(new BSPTree()));
        bspTreeTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                faceGroupTreeChanged();
            }
        });
        scrollPane_6.setViewportView(bspTreeTree);

        JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane_1.setBounds(341, 11, 237, 246);
        panel_3.add(tabbedPane_1);

        JScrollPane scrollPane_7 = new JScrollPane();
        tabbedPane_1.addTab("Face", null, scrollPane_7, null);

        faceFormatedArea = new JTextArea();
        faceFormatedArea.setEditable(false);
        scrollPane_7.setViewportView(faceFormatedArea);

        JScrollPane scrollPane_8 = new JScrollPane();
        tabbedPane_1.addTab("Normal", null, scrollPane_8, null);

        normalFormatedArea = new JTextArea();
        normalFormatedArea.setEditable(false);
        scrollPane_8.setViewportView(normalFormatedArea);

        JScrollPane scrollPane_9 = new JScrollPane();
        tabbedPane_1.addTab("Extra", null, scrollPane_9, null);

        extraDataFormatedArea = new JTextArea();
        extraDataFormatedArea.setEditable(false);
        scrollPane_9.setViewportView(extraDataFormatedArea);

        statusLabel = new JLabel("---");
        statusLabel.setBounds(200, 396, 595, 14);
        frmStarfoxBojectDecoder.getContentPane().add(statusLabel);

        JButton btnNewButton = new JButton("Load extras");
        btnNewButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser jfc = new JFileChooser(".");

                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    File f = jfc.getSelectedFile();

                    try
                    {
                        initObjectsName(Files.readAllLines(Paths.get(f.getAbsolutePath()), Charset.forName("UTF-8")));
                    }
                    catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }
                    catch (IOException e2)
                    {
                        e2.printStackTrace();
                    }
                }
            }
        });
        btnNewButton.setBounds(6, 42, 127, 23);
        frmStarfoxBojectDecoder.getContentPane().add(btnNewButton);

        searchObjectField = new JTextField();
        searchObjectField.setEditable(false);
        searchObjectField.setToolTipText("Search object by name (press enter to validate)");
        searchObjectField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectObject(searchObjectField.getText());
            }
        });
        searchObjectField.setBounds(6, 69, 184, 20);
        frmStarfoxBojectDecoder.getContentPane().add(searchObjectField);
        searchObjectField.setColumns(10);
    }

    void initObjectsName(List<String> allLines)
    {
        for (int i = 0; i < allLines.size(); i++)
        {
            Scanner sc = new Scanner(allLines.get(i));
            int id = sc.nextInt(16);

            // reverse byte
            id = ((id & 0xFF) << 8) | (id >> 8);

            try
            {
                String name = sc.nextLine().trim();

                SFXObjectId obj = findObject(id);
                if (obj != null)
                    obj.name = name;
            }
            catch (NoSuchElementException e)
            {
                // ignore
            }
        }

        objectList.repaint();
    }

    SFXObjectId findObject(int id)
    {
        for (int i = 0; i < objectsId.length; i++)
        {
            SFXObjectId obj = objectsId[i];

            if (obj.header.id == id)
                return obj;
        }

        return null;
    }

    void selectObject(String text)
    {
        String s = text.trim().toLowerCase();
        SFXObjectId selectedObj = objectList.getSelectedValue();

        for (int i = (selectedObj != null) ? objectList.getSelectedIndex() + 1 : 0; i < objectsId.length; i++)
        {
            SFXObjectId obj = objectsId[i];

            if (obj.name.toLowerCase().indexOf(s) != -1)
            {
                objectList.setSelectedIndex(i);
                objectList.ensureIndexIsVisible(i);
                return;
            }
        }

        if (selectedObj != null)
        {
            for (int i = 0; i < objectList.getSelectedIndex(); i++)
            {
                SFXObjectId obj = objectsId[i];

                if (obj.name.toLowerCase().indexOf(s) != -1)
                {
                    objectList.setSelectedIndex(i);
                    objectList.ensureIndexIsVisible(i);
                    return;
                }
            }
        }
    }

    void initRom()
    {
        // asynch loading
        new Thread(this).start();
    }

    void vertexFrameSpinnerChanged()
    {
        setSelectedVertexFrame(objectList.getSelectedValue(), ((Integer) vertexFrameSpinner.getValue()).intValue());
    }

    void faceGroupListChanged()
    {
        setSelectedFaceGroup((FaceGroup) faceGroupList.getSelectedValue());
    }

    void faceGroupTreeChanged()
    {
        TreePath tp = bspTreeTree.getSelectionPath();
        if (tp != null)
        {
            BSPTree bspTree = (BSPTree) tp.getLastPathComponent();

            if (bspTree != null)
                faceGroupList.setSelectedValue(bspTree.faceGroup, true);
        }
    }

    void setSelectedObject(SFXObjectId obj)
    {
        statusLabel.setText("");

        if (obj != null)
        {
            setHeaderInfo(obj.header);
            setVertexInfo(obj.object);
            setTriangleInfo(obj.object);
            setBSPTreeFaceInfo(obj.object);
            objectList.setToolTipText(obj.name);

            if ((obj.object != null) && !obj.object.valid)
                statusLabel.setText("Some data are missing for this object as they were not correctly parsed.");
        }
        else
        {
            setHeaderInfo(null);
            setVertexInfo(null);
            setTriangleInfo(null);
            setBSPTreeFaceInfo(null);
            objectList.setToolTipText("");
        }
    }

    void setSelectedVertexFrame(SFXObjectId obj, int frame)
    {
        if (obj != null)
        {
            setVertexInfo(obj.object, frame);
            setTriangleInfo(obj.object, frame);
        }
        else
        {
            setVertexInfo(null, -1);
            setTriangleInfo(null, -1);
        }
    }

    void setSelectedFaceGroup(FaceGroup fc)
    {
        if (fc != null)
        {
            faceFormatedArea.setText(fc.getFormatedVertexString());
            normalFormatedArea.setText(fc.getFormatedNormalString());
            extraDataFormatedArea.setText(fc.getFormatedExtraString());
        }
        else
        {
            faceFormatedArea.setText("");
            normalFormatedArea.setText("");
            extraDataFormatedArea.setText("");
        }
    }

    void setVertexInfo(SFXObject object)
    {
        if ((object != null) && (object.vertices != null))
        {
            int frameMax = Math.max(0, object.getFrameCount() - 1);
            vertexFrameSpinner.setModel(new SpinnerNumberModel(0, 0, frameMax, 1));
            vertexFrameCountLabel.setText("/ " + frameMax);
        }
        else
        {
            vertexFrameSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 1));
            vertexFrameCountLabel.setText("/ 0");
        }

        vertexFrameSpinnerChanged();
    }

    void setVertexInfo(SFXObject object, int frame)
    {
        if ((object != null) && (frame < object.vertices.length) && (object.vertices[frame] != null))
        {
            vertexNumberField.setText(Integer.toString(object.vertices[frame].length));
            vertexFormatedArea.setText(object.getVertexFormatedString(frame));
            vertexRawArea.setText(object.getVertexRawString(frame));
        }
        else
        {
            vertexNumberField.setText("");
            vertexFormatedArea.setText("");
            vertexRawArea.setText("");
        }
    }

    void setBSPTreeFaceInfo(SFXObject object)
    {
        if ((object != null) && (object.bspTree != null))
            bspTreeTree.setModel(new DefaultTreeModel(object.bspTree));
        else
            bspTreeTree.setModel(new DefaultTreeModel(new BSPTree()));

        if ((object != null) && (object.faceGroups != null))
            faceGroupList.setListData(object.faceGroups);
        else
            faceGroupList.setListData(new FaceGroup[0]);

        faceGroupListChanged();
    }

    void setTriangleInfo(SFXObject object)
    {
        if ((object != null) && (object.triangles != null))
        {
            triangleNumberField.setText(Integer.toString(object.triangles.length));
            triangleFormatedArea.setText(object.getTriangleFormatedString());
        }
        else
        {
            triangleNumberField.setText("");
            triangleFormatedArea.setText("");
        }
    }

    void setTriangleInfo(SFXObject object, int frame)
    {
        if ((object != null) && (object.triangles != null))
        {
            triangleRawArea.setText(object.getTriangleRawString(frame));
        }
        else
        {
            triangleRawArea.setText("");
        }
    }

    void setHeaderInfo(SFXObjectHeader header)
    {
        if (header != null)
        {
            bankField.setText(Integer.toHexString(header.bank));
            vertexAddressField.setText(Integer.toHexString(header.vertexAddress));
            faceAddressField.setText(Integer.toHexString(header.faceAddress));
            rawHeaderField.setText(header.getRawString());
            rawHeaderField.setToolTipText(header.getRawString());
        }
        else
        {
            bankField.setText("");
            vertexAddressField.setText("");
            faceAddressField.setText("");
            rawHeaderField.setText("");
            rawHeaderField.setToolTipText("");
        }
    }

    @Override
    public void run()
    {
        List<SFXObjectId> objectsIdList = new ArrayList<SFXObjectId>();

        int offset = BASE_LIST_ADR;
        while (true)
        {
            SFXObjectId obj = new SFXObjectId(rom, offset);

            if (obj.header.id == -1)
                break;

            objectsIdList.add(obj);
            offset += 23 + (obj.objectCount * 5);

            statusLabel.setText("loading " + objectsIdList.size());
        }

        objectsId = objectsIdList.toArray(new SFXObjectId[objectsIdList.size()]);
        Arrays.sort(objectsId);

        objectList.setListData(objectsId);

        statusLabel.setText("");
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        setSelectedObject(objectList.getSelectedValue());
    }
}
