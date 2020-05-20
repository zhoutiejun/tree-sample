import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author : zhoutiejun@youngyedu.com, 2020/5/8 0008 上午 10:29
 * @description :
 * @modified : zhoutiejun@youngyedu.com, 2020/5/8 0008 上午 10:29
 */
public class Tree2 extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new Tree2().setVisible(true);
    }

    private ImageIcon[] image = new ImageIcon[3];
    private JButton button_log;
    private JButton button_celue;
    private JButton button_simple;
    private JTextField textField;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private final JScrollPane scrollPane_1;
    private JTextArea textArea;
    private File file;

    // 存储策略的树形结构，就是去重，和去掉非法路径之后的
    private HashMap<String, Path> celueTreeMap = new HashMap<>();
    // 存储最后的树状结构
    private HashMap<String, Path> muluTreeMap = new HashMap<>();

    //文件选择器
    JFileChooser chooser = new JFileChooser();

    public Tree2(){
        setTitle("精简算法");
        setBounds(300, 300, 1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.CENTER,4,4));
        JLabel label_1 = new JLabel("文件路径:");
        panel_1.add(label_1);

        textField = new JTextField(10);
        panel_1.add(textField);

        // 日志导入按钮
        button_log = new JButton("导入日志");
        panel_1.add(button_log);
        button_log.addActionListener(this);


        // 日志导入按钮
        button_celue = new JButton("生成策略");
        panel_1.add(button_celue);
        button_celue.addActionListener(this);

        // 日志导入按钮
        button_simple = new JButton("精简");
        panel_1.add(button_simple);
        button_simple.addActionListener(this);

        getContentPane().add(panel_1,BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(100);
        root = new DefaultMutableTreeNode("暂无数据");
        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        TreeSelectionModel selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        //设置为连选模式
        scrollPane_1 = new JScrollPane();
        scrollPane_1.setViewportView(tree);
        splitPane.setLeftComponent(scrollPane_1);

        final JScrollPane scrollPane_2 = new JScrollPane();
        textArea = new JTextArea();
        //显示单击结点的路径
        scrollPane_2.setViewportView(textArea);

        splitPane.setRightComponent(scrollPane_2);

        getContentPane().add(splitPane,BorderLayout.CENTER);

        // 读取目录文件到内存 C:\Users\Administrator\Desktop\路径精简算法
        readTreeIntoMemory();

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
//                        textArea.append("\n");
                    }
//                    textArea.append("\n");
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 导入日志文件，并且展示在右侧文本框
        if(e.getSource() == button_log){
            // 选择文件
            chooser.setFileSelectionMode(0);
            int status = chooser.showOpenDialog(null);
            if(status == 1){
                return;
            }
            file = chooser.getSelectedFile();
            textArea.append("日志文件读入:\n");
            viewFileContent();
            textArea.append("日志文件读取完毕\n");
            //获取文件绝对路径并写入到文本框内
            textField.setText(file.getAbsolutePath());
        }

        // 根据指定的策略文件，读取生成树， 还有显示文本
        if(e.getSource() == button_celue){
            // 初始化目录树的map
            recursionInitMap(muluTreeMap);

            //  初始化节点数据
            recursion(muluTreeMap);

            // 这里需要读取生成的策略文件，文件路径需要去重和校验错误，然后展示树 还有文件内容
            root = new DefaultMutableTreeNode("精简前结构");
            generateTreeAndText();

            // 展示树
            treeModel = new DefaultTreeModel(root);
            tree = new JTree(treeModel);
            TreeSelectionModel selectionModel = tree.getSelectionModel();
            selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
            scrollPane_1.setViewportView(tree);
        }

        // 精简策略按钮
        if(e.getSource() == button_simple){
            List<String> pathList = new ArrayList<>();
            recursionPathList(muluTreeMap, pathList);
            String str;
            Set<String> result = new HashSet<>();
            BufferedReader bf= null ;
            try{
                bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                while ((str = bf.readLine()) != null) {
                    // 去掉可以省略的
                    boolean startFlag = false;
                    for(String path :pathList){
                        if(str.equals(path)){
                            break;
                        }
                        if(str.startsWith(path)){
                            // 输出 符合
                            startFlag = true;
                            break;
                        }
                    }
                    if(!startFlag){
                        result.add(str);
                    }
                }
            }catch (Exception ex){

            }finally {
                try {
                    bf.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            result.addAll(pathList);
            generateFile(result);
        }
    }

    private void generateTreeAndText() {
        // 读取策略文件用于进入内存，并且展示
        file = new File("C:\\Users\\Administrator\\Desktop\\路径精简算法\\celue.txt");
        textArea.append("策略文件内容为：\n");
        // 使用ArrayList来存储每行读取到的字符串
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "GBK");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                Map<String, Path> tempMap = celueTreeMap;
                textArea.append(str+"\n");
                String[] arr = generateMapbyString(str, tempMap);


                // 去重检测 用于记录节点数的子节点访问次数
                Map<String, String> repeatMap = new HashMap<>();
                if(repeatMap.containsKey(str)){
                    continue;
                }else{
                    repeatMap.put(str, null);
                }
                recursionTree(muluTreeMap, arr, 0);
            }

            // 用于生成树状结构图
            recursion(celueTreeMap, root);
            bf.close();
            inputReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        System.out.println(celueTreeMap.size());
    }

    private String[] generateMapbyString(String str, Map<String, Path> tempMap) {
        // 处理路径  将路径拆分生成json对象
        String[] arr = str.split("\\\\");
        // 循环填充数据
        String path_prefix = "";
        for(String childStr : arr){
            if(!childStr.trim().equals("")){
                path_prefix += "\\"+childStr;
                if(tempMap.containsKey(childStr)){
                    tempMap = tempMap.get(childStr).getChild();
                }else{
                    Map<String, Path> tempChildMap = new HashMap<>();
                    Path path = new Path(path_prefix, 0, 0 , tempChildMap);
                    tempMap.put(childStr, path);
                    tempMap = tempChildMap;
                }
            }
        }
        return arr;
    }

    private void readTreeIntoMemory() {
        // 读取指定目录文件 用于进入内存
        file = new File("C:\\Users\\Administrator\\Desktop\\路径精简算法\\tree.txt");
        // 使用ArrayList来存储每行读取到的字符串
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "GBK");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                Map<String, Path> tempMap = muluTreeMap;
                generateMapByString(str, tempMap);

            }
            bf.close();
            inputReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void generateMapByString(String str, Map<String, Path> tempMap) {
        // 处理路径  将路径拆分生成json对象
        String[] arr = str.split("\\\\");
        // 循环填充数据
        String path_prefix = "";
        for(String childStr : arr){
            if(!childStr.trim().equals("")){
                path_prefix += "\\"+childStr;
                if(tempMap.containsKey(childStr)){
                    tempMap = tempMap.get(childStr).getChild();
                }else{
                    Map<String, Path> tempChildMap = new HashMap<>();
                    Path path = new Path(path_prefix, 0, 0 , tempChildMap);
                    tempMap.put(childStr, path);
                    tempMap = tempChildMap;
                }
            }
        }
    }

    private void generateFile(Set<String> pathList){
        textArea.append("精简后\n");
        File file = new File("c://1.txt");
        try {
            if(!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            Map <String, Path> result = new HashMap<>();
            for(String path : pathList) {
                textArea.append(path+"\n");
                bw.write(path);
                bw.newLine();

                Map <String, Path> tempMap = result;
                generateMapByString( path,  tempMap);
            }
            bw.flush();
            bw.close();
            fw.close();

            root = new DefaultMutableTreeNode("精简后结构");
            recursion(result, root);


            // 展示树
            treeModel = new DefaultTreeModel(root);
            tree = new JTree(treeModel);
            TreeSelectionModel selectionModel = tree.getSelectionModel();
            selectionModel.setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
            scrollPane_1.setViewportView(tree);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Write is over");
    }

    private int recursionTree(Map<String, Path> temp, String[] arr, int index) {
        String level = arr[index];

        // 空路径忽略
        if(level.trim().equals("")){
            recursionTree(temp, arr, index+1);
        }

        // 防止产生错误目录
        if(!temp.containsKey(level)){
            return 0;
        }

        Path path = temp.get(level);

        // 到达日志的路径
        if(index == (arr.length-1)){
            path.setCurNum(path.getMaxNum());
            // 清空子节点
            path.setChild(new HashMap<>());
            return path.getMaxNum();
        }

        int num = recursionTree(path.getChild(), arr, index+1);
        path.setCurNum(path.getCurNum()+num);
        return num;
    }

    private void recursionPathList(Map<String, Path> map, List<String> pathList){
        for(String key :  map.keySet()){
            Path path = map.get(key);
            if(path.getMaxNum().compareTo(path.getCurNum()) <= 0){
                pathList.add(path.getValue());
            }else{
                recursionPathList(path.getChild(), pathList);
            }
        }
    }

    private int recursion(Map<String, Path> map, DefaultMutableTreeNode root) {
        // 遍历他的节点 顺便初始化数据
        if(map.size() == 0){
            return 1;
        }

        int total = 0;
        for(String key :  map.keySet()){
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
            root.add(childNode);
            Path path = map.get(key);
            int num = recursion(path.getChild(), childNode);
            path.setMaxNum(num);
            total += num;
        }
        return total;
    }

    private int recursion(Map<String, Path> map) {
        // 遍历他的节点 顺便初始化数据
        if(map.size() == 0){
            return 1;
        }

        int total = 0;
        for(String key :  map.keySet()){
            Path path = map.get(key);
            int num = recursion(path.getChild());
            path.setMaxNum(num);
            total += num;
        }
        return total;
    }


    private void recursionInitMap(Map<String, Path> map) {
        // 遍历他的节点 顺便初始化数据
        if(map.size() == 0){
            return ;
        }
        for(String key :  map.keySet()){
            Path path = map.get(key);
            path.setCurNum(0);
            recursionInitMap(path.getChild());
        }
    }

    // 读取文件 并且输出到文本框
    private void viewFileContent(){
        // 使用ArrayList来存储每行读取到的字符串
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "GBK");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                textArea.append(str+"\n");
            }
            bf.close();
            inputReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
