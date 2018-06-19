package org.think.swing.control.tree;

import org.think.swing.GeneratorContext;
import org.think.swing.jdbc.pdm.PDMDataSources;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author lixiaobin
 * @since 2017/3/24
 */
public class GeneratorTreePanel extends JPanel{
    private JTextField textField = new JTextField("");
    GeneratorTree generatorTree = new GeneratorTree();
    public GeneratorTreePanel(){
        setLayout(new BorderLayout());

        add(getToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(generatorTree));
    }

    /**
     * 初始化ToolBar
     * @return
     */
    public JToolBar getToolBar(){
        JToolBar toolBar = new JToolBar();
        JButton openPDMButton = new JButton( new AbstractAction() {
            private static final long serialVersionUID = 1L;
            {
//				putValue(Action.NAME, "打开目录");
                putValue(Action.SHORT_DESCRIPTION, "打开PDM文件");
                putValue(Action.SMALL_ICON, new ImageIcon(getClass().getClassLoader().getResource("general/openProject.png")));
            }
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int r = chooser.showOpenDialog(GeneratorTreePanel.this.getParent());
                if (r != JFileChooser.APPROVE_OPTION) return;
                File file = chooser.getSelectedFile();
                new PDMDataSources(file);
                DefaultMutableTreeNode rootMutableTreeNode = (DefaultMutableTreeNode)generatorTree.getModel().getRoot();
                rootMutableTreeNode.add(new DefaultMutableTreeNode(file.getName()));
//                generatorTree.expandPath(generatorTree.);
            }
        });
        toolBar.add(openPDMButton);
        return toolBar;
    }

    public void addTreeSelectionListener(TreeSelectionListener tsl){
        generatorTree.addTreeSelectionListener(tsl);
    }

    public GeneratorTree getJTree(){
        return generatorTree;
    }




    private java.util.List<String> getDataSourceNames() {
        return GeneratorContext.getInstance().getDataSourceNames();
    }

    protected DataSource getDataSource(String id){
        return GeneratorContext.getInstance().getDataSource(id);
    }
}
