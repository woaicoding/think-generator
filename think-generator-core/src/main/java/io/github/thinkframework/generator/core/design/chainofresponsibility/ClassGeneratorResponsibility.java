package io.github.thinkframework.generator.core.design.chainofresponsibility;

import io.github.thinkframework.generator.core.context.GeneratorContext;
import io.github.thinkframework.generator.core.design.adapter.TableClassAdapter;
import io.github.thinkframework.generator.core.design.builder.BuilderFacade;
import io.github.thinkframework.generator.core.internal.ClassIntrospector;
import io.github.thinkframework.generator.core.internal.lang.Clazz;
import org.springframework.core.Ordered;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 适配器
 * @author hdhxby
 * @since 2017/3/24
 */
public class ClassGeneratorResponsibility implements GeneratorResponsibility, Ordered {

    @Override
    public GeneratorContext process(GeneratorContext generatorContext) {
        if(!(generatorContext.getSource() instanceof File) || !(generatorContext.getTarget().toString().contains("class"))){
            return generatorContext;
        }
        Map result = new HashMap();

        Class aClass = null;
        try {
            aClass = new FileLoader().findClass(((File) generatorContext.getSource()).getPath()+File.separator+generatorContext.getTarget());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Clazz clazz = ClassIntrospector.getClazz(aClass);
        String tableName = (String)generatorContext.getTarget();
        //设置表的属性

        //适配器,同时提供表和类的字段
        TableClassAdapter tableClassAdapter = new TableClassAdapter();
        tableClassAdapter.clazz(clazz);
        tableClassAdapter.setTable(BuilderFacade.generatorConfiguration(generatorContext.getGeneratorConfiguration()).build(clazz));
        result.put("table",tableClassAdapter);
        result.put("clazz",tableClassAdapter);


        generatorContext.getProperties().putAll(result);
        return generatorContext;
    }

    @Override
    public int getOrder() {
        return 0;
    }


    class FileLoader extends ClassLoader {

        @Override
        public Class findClass(String name) throws ClassNotFoundException {
            byte[] b = loadClassFromFile(name);
            return defineClass(name, b, 0, b.length);
        }

        private byte[] loadClassFromFile(String fileName)  {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            byte[] buffer;
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = 0;
            try {
                while ( (nextValue = inputStream.read()) != -1 ) {
                    byteStream.write(nextValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer = byteStream.toByteArray();
            return buffer;
        }
    }
}
