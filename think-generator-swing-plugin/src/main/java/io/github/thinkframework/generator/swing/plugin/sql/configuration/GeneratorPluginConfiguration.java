package io.github.thinkframework.generator.swing.plugin.sql.configuration;

import io.github.thinkframework.generator.core.GeneratorFactoryBean;
import io.github.thinkframework.generator.boot.context.properties.GeneratorProperties;
import io.github.thinkframework.generator.swing.core.component.tree.GeneratorTree;
import io.github.thinkframework.generator.swing.core.component.tree.GeneratorTreeModel;
import io.github.thinkframework.generator.swing.core.frame.main.GeneratorMainFrame;
import io.github.thinkframework.generator.swing.plugin.sql.frame.GeneratorConfigurationFrame;
import io.github.thinkframework.generator.swing.plugin.sql.frame.GeneratorConfigurationPanel;
import io.github.thinkframework.generator.swing.core.util.GeneratorFileUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

@Configuration
public class GeneratorPluginConfiguration implements ApplicationContextAware, ResourceLoaderAware, MessageSourceAware,InitializingBean {

    private ApplicationContext applicationContext;

    private ResourceLoader resourceLoader;

    private MessageSource messageSource;

    private GeneratorProperties generatorProperties;

    GeneratorMainFrame generatorMainFrame;

    GeneratorTree generatorTree;

    GeneratorFactoryBean generatorFactoryBean;

    public GeneratorPluginConfiguration(GeneratorFactoryBean generatorFactoryBean,GeneratorProperties generatorProperties,GeneratorMainFrame generatorMainFrame,GeneratorTree generatorTree){
        this.generatorFactoryBean = generatorFactoryBean;
        this.generatorProperties = generatorProperties;
        this.generatorMainFrame = generatorMainFrame;
        this.generatorTree = generatorTree;
    }

    @Bean
    public GeneratorConfigurationFrame generatorConfigurationFrame(){
        GeneratorConfigurationFrame generatorConfigurationFrame = new GeneratorConfigurationFrame();
        generatorConfigurationFrame.setGeneratorConfigurePanel(generatorConfigurationPanel());
        return generatorConfigurationFrame;
    }

    @Bean
    public GeneratorConfigurationPanel generatorConfigurationPanel(){
        GeneratorConfigurationPanel generatorConfigurationPanel = new GeneratorConfigurationPanel();
        return generatorConfigurationPanel;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                generatorMainFrame.getJMenuBar().getMenu(1).add(new JMenuItem(new AbstractAction() {
                    private static final long serialVersionUID = 1L;
                    {
                        putValue(Action.NAME, "生成器设置");
                        try {
                            putValue(Action.SMALL_ICON, new ImageIcon(resourceLoader.getResource("general/settings.png").getURL()));
                        } catch (Exception e) {
                        }
                    }
                    public void actionPerformed(ActionEvent e) {
                        applicationContext.getBean(GeneratorConfigurationFrame.class).setVisible(true);
                    }
                }));

                if(generatorTree.getComponentPopupMenu() == null){
                    generatorTree.setComponentPopupMenu(new JPopupMenu());
                }
                generatorTree.getComponentPopupMenu().add(new JMenuItem(new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    {
                        putValue(Action.NAME, "生成文件");
                    }
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        generatorTable();
                    }
                }));
            }
        });
    }

    /**
     * 根据表生成文件
     */
    public void generatorTable() {
        TreePath[] treePaths = generatorTree.getSelectionPaths();
        try {
            if (treePaths == null) {
                return;
            }
            for (int i = 0; i < treePaths.length; i++) {
                TreePath treePath = treePaths[i];
                int count = treePath.getPathCount();
                for (int j = 1; j < count; j++) {
                    if (j != 3) {
                        continue;
                    }
                    GeneratorTreeModel.GeneratorTreeNode defaultMutableTreeNode = (GeneratorTreeModel.GeneratorTreeNode) treePath
                        .getPathComponent(j);

                    generatorFactoryBean.getObject()
                        .generate(applicationContext.getBean(defaultMutableTreeNode.getParent().getParent().getUserObject().toString()),
                            defaultMutableTreeNode.getUserObject()
                            );

                }
            }
            int confirm = JOptionPane.showConfirmDialog(generatorTree, "操作成功,是否打开输出目录?", "提示", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                GeneratorFileUtil.openDirectory(new File(System.getProperty("user.dir")));
            }
        } catch (Exception ex) {
            EventQueue.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    generatorTree,
                    ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
