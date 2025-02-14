package io.github.thinkframework.generator.swing.core.frame.main;

import io.github.thinkframework.generator.swing.core.component.table.GeneratorTable;
import io.github.thinkframework.generator.swing.core.component.table.GeneratorTableFactoryBean;
import io.github.thinkframework.generator.swing.core.component.tree.GeneratorTree;
import io.github.thinkframework.generator.swing.core.component.tree.GeneratorTreeModel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hdhxby
 * @email hdhxby@qq.com
 */
public class GeneratorMainPanel extends JPanel implements ApplicationContextAware, MessageSourceAware, InitializingBean {

    private static final long serialVersionUID = 1L;

    private GeneratorTree generatorTree;

    private JTabbedPane centerTabbedPane = new JTabbedPane(JTabbedPane.TOP);

    private ApplicationContext applicationContext;

    private MessageSource messageSource;


    public JSplitPane centerPanel() {
        JSplitPane jSplitPane = new JSplitPane();
        jSplitPane.setLeftComponent(leftCenterPanel());
        jSplitPane.setRightComponent(rightCenterPanel());
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setContinuousLayout(true);
        return jSplitPane;
    }

    public JTabbedPane leftCenterPanel() {
        JTabbedPane westTabbedPane = new JTabbedPane();westTabbedPane.addTab("表信息", null, new JScrollPane(generatorTree), null);
        return westTabbedPane;
    }

    public JTabbedPane rightCenterPanel() {
        add(centerTabbedPane, BorderLayout.CENTER);
        centerTabbedPane.addTab("起始页", null, initFirstTable(), null);
        return centerTabbedPane;
    }

    private JEditorPane initFirstTable() {
        JEditorPane editorPane = new JEditorPane();
        try {
            editorPane.setPage(getClass().getClassLoader().getResource("help.html"));
        } catch (IOException e) {
        }
        return editorPane;
    }

    private JTabbedPane addTablePanel(String dataSourceName, String tableName) {
        int count = centerTabbedPane.getTabCount();
        boolean exists = false;
        for (int i = 0; i < count; i++) {
            if (centerTabbedPane.getTitleAt(i).equals(tableName)) {
                centerTabbedPane.setSelectedIndex(i);
                exists = true;
                break;
            }
        }
        if (!exists) {
            EventQueue.invokeLater(() ->{
                //设置FactoryBean
                GeneratorTableFactoryBean generatorTableFactoryBean = applicationContext.getBean(GeneratorTableFactoryBean.class);
                generatorTableFactoryBean.setDataSource(getDataSource(dataSourceName));
                generatorTableFactoryBean.setTableName(tableName);
                //获取GeneratorTable
                GeneratorTable generatorTabbedPanel = applicationContext.getBean(GeneratorTable.class);
                JScrollPane jScrollPane = new JScrollPane(generatorTabbedPanel);
                centerTabbedPane.addTab(tableName, null, jScrollPane, null);
                centerTabbedPane.setSelectedComponent(jScrollPane);
            });
        }
        return centerTabbedPane;
    }

    protected DataSource getDataSource(String id) {
        return applicationContext.getBean(id, DataSource.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.notNull(applicationContext, "");
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        setLayout(new BorderLayout());
        add(centerPanel());

        generatorTree.addTreeSelectionListener(e -> {
                if (generatorTree.getSelectionPath().getPathCount() == 4) {//获取表相关的列
                    GeneratorTreeModel.GeneratorTreeNode treeNode = (GeneratorTreeModel.GeneratorTreeNode) generatorTree.getSelectionPath().getLastPathComponent();
                    Map<String,String> map = new HashMap<>();
                    String dataSourceName = treeNode.getParent().getParent().getName();
                    String tableType = treeNode.getParent().getName();
                    String tableName = treeNode.getName();
                    addTablePanel(dataSourceName,tableName);
                }
            });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public GeneratorTree getGeneratorTree() {
        return generatorTree;
    }

    public void setGeneratorTree(GeneratorTree generatorTree) {
        this.generatorTree = generatorTree;
    }

    public JTabbedPane getCenterTabbedPane() {
        return centerTabbedPane;
    }

    public void setCenterTabbedPane(JTabbedPane centerTabbedPane) {
        this.centerTabbedPane = centerTabbedPane;
    }
}
