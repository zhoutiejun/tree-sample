import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * @author : zhoutiejun@youngyedu.com, 2020/5/8 0008 上午 10:29
 * @description :
 * @modified : zhoutiejun@youngyedu.com, 2020/5/8 0008 上午 10:29
 */
public class Tree extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new Tree().setVisible(true);
    }

    private ImageIcon[] image = new ImageIcon[3];
    private JButton button_add;
    private JButton button_update;
    private JButton button_delete;
    private JButton button_change;
    private JButton button_display;
    private JTextField textField;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JComboBox comboBox;
    private DefaultMutableTreeNode root;
    private final JScrollPane scrollPane_1;
    private JTextArea textArea;
    public Tree(){
        setTitle("树的综合应用");
        setBounds(100, 100, 400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.CENTER,3,3));
        JLabel label_1 = new JLabel("节点文字:");
        label_1.setFont(new Font("宋体", Font.BOLD, 10));
        panel_1.add(label_1);

        textField = new JTextField(10);
        textField.setFont(new Font("宋体", Font.BOLD, 10));
        panel_1.add(textField);

        button_add = new JButton("添加");
        button_add.setFont(new Font("宋体", Font.BOLD, 10));
        panel_1.add(button_add);
        button_add.addActionListener(this);

        button_update = new JButton("修改");
        button_update.setFont(new Font("宋体", Font.BOLD, 10));
        panel_1.add(button_update);
        button_update.addActionListener(this);

        button_delete = new JButton("删除");
        button_delete.setFont(new Font("宋体", Font.BOLD, 10));
        panel_1.add(button_delete);
        button_delete.addActionListener(this);

        getContentPane().add(panel_1,BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(100);
        root = new DefaultMutableTreeNode("部门分类");

        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("基础部");
        DefaultMutableTreeNode node11 = new DefaultMutableTreeNode("经理");
        DefaultMutableTreeNode node12 = new DefaultMutableTreeNode("员工A");
        DefaultMutableTreeNode node13 = new DefaultMutableTreeNode("员工B");

        node1.add(node11);
        node1.add(node12);
        node1.add(node13);

        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("设计部");
        DefaultMutableTreeNode node21 = new DefaultMutableTreeNode("经理");
        DefaultMutableTreeNode node22 = new DefaultMutableTreeNode("员工A");
        DefaultMutableTreeNode node23 = new DefaultMutableTreeNode("员工B");

        node2.add(node21);
        node2.add(node22);
        node2.add(node23);

        root.add(node1);
        root.add(node2);

        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        TreeSelectionModel selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);//设置为连选模式
        tree.setFont(new Font("宋体", Font.BOLD, 10));
        scrollPane_1 = new JScrollPane();
        scrollPane_1.setViewportView(tree);
        splitPane.setLeftComponent(scrollPane_1);

        final JScrollPane scrollPane_2 = new JScrollPane();
        textArea = new JTextArea();
        textArea.setFont(new Font("宋体", Font.BOLD, 10));
        scrollPane_2.setViewportView(textArea);//显示单击结点的路径

        splitPane.setRightComponent(scrollPane_2);

        getContentPane().add(splitPane,BorderLayout.CENTER);

        JPanel panel_3 = new JPanel(new FlowLayout(FlowLayout.CENTER,3,3));
        JLabel label_2 = new JLabel("选择图标:");
        label_2.setFont(new Font("宋体", Font.BOLD, 10));
        panel_3.add(label_2);

        System.out.println(this.getClass().getResource(""));
        for(int i=0;i<3;i++){
            System.out.println("02-"+(i+1)+".png");
            image[i] = new ImageIcon(this.getClass().getResource("02-"+(i+1)+".png"));
        }
        comboBox = new JComboBox(image);
        panel_3.add(comboBox);

        button_change = new JButton("更改叶子结点图标");
        button_change.setFont(new Font("宋体", Font.BOLD, 10));
        panel_3.add(button_change);
        button_change.addActionListener(this);

        button_display = new JButton("隐藏连接线");
        button_display.setFont(new Font("宋体", Font.BOLD, 10));
        panel_3.add(button_display);
        button_display.addActionListener(this);

        getContentPane().add(panel_3,BorderLayout.SOUTH);

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if(!tree.isSelectionEmpty()){
                    TreePath[] selectionPath = tree.getSelectionPaths();
                    for(int i=0;i<selectionPath.length;i++){
                        TreePath treePath = selectionPath[i];
                        Object[] path = treePath.getPath();
                        for(int j=0;j<path.length;j++){
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path[j];
//                            textArea.append(node.getUserObject()+(j==(path.length-1)?"":"->"));
                        }
                        textArea.append("\n");
                    }
                    textArea.append("\n");
                }
            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button_add){//添加
            String text = textField.getText();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(text);
            TreePath treePath = tree.getSelectionPath();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
            TreePath path = treePath.pathByAddingChild(node);
            if(!tree.isVisible()){
                tree.makeVisible(treePath);
            }
        }
        if(e.getSource() == button_delete){//删除
            //待删除的结点
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if(!node.isRoot()){
                DefaultMutableTreeNode nextsilbing = node.getNextSibling();
                //如果下一个结点不存在,则选中其父节点
                nextsilbing = (DefaultMutableTreeNode) node.getParent();
                treeModel.removeNodeFromParent(node);
                tree.setSelectionPath(new TreePath(nextsilbing.getPath()));
            }
        }
        if(e.getSource() == button_update){//修改
            //得到待修改结点的路径
            TreePath selectionPath = tree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            //	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            node.setUserObject(textField.getText());
            treeModel.nodeChanged(node);
            tree.setSelectionPath(selectionPath);
        }
        if(e.getSource() == button_change){//更改叶子节点的图标
            ImageIcon temp = (ImageIcon) comboBox.getSelectedItem();
            DefaultTreeCellRenderer treeCellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
            treeCellRenderer.setLeafIcon(temp);
            Enumeration enumeration = root.preorderEnumeration();
            while(enumeration.hasMoreElements()){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                if(node.isLeaf()){
                    treeModel.nodeChanged(node);
                }
            }
        }
        if(e.getActionCommand().equals("隐藏连接线")){
            System.out.println(button_display.getText());
            tree.putClientProperty("JTree.lineStyle", "None");
            scrollPane_1.setViewportView(tree);
            button_display.setText("显示连接线");
        }
        if(e.getActionCommand().equals("显示连接线")){
            System.out.println(button_display.getText());
            tree.putClientProperty("JTree.lineStyle", "Angled");
            scrollPane_1.setViewportView(tree);
            button_display.setText("隐藏连接线");
        }
    }
}
