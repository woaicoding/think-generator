package io.github.thinkframework.generator.core.util;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;

public class Comp {

    public static void main(String[] args) {
        //获取系统Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //获取Java文件管理器
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        //定义要编译的源文件
        File file = new File("c:\\opt\\java\\Example.java");
        //通过源文件获取到要编译的Java类源码迭代器，包括所有内部类，其中每个类都是一个 JavaFileObject，也被称为一个汇编单元
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);
        //生成编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        //执行编译任务
        task.call();
    }

}
