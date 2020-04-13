package io.github.thinkframework.generator.util;

import freemarker.cache.FileTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import io.github.thinkframework.generator.context.GeneratorContext;
import io.github.thinkframework.generator.exception.GeneratorRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker工具类
 *
 * @author lixiaobin
 */
@Slf4j
public class GeneratorFreeMarker {

    Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

    /**
     * FreeMarker配置
     *
     * @throws GeneratorRuntimeException
     */
    public GeneratorFreeMarker configuration(GeneratorContext generatorContext) throws GeneratorRuntimeException {
        try {

            Map map = new HashMap();
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel stringHelper = (TemplateHashModel) staticModels.get("io.github.thinkframework.generator.util.StringUtils");
            map.put("StringUtils", stringHelper);
            configuration.setSharedVaribles(map);
            configuration.setTemplateLoader(new FileTemplateLoader(new File(generatorContext.getGeneratorConfiguration().getTemplate())));
            configuration.setNumberFormat("###############");
            configuration.setBooleanFormat("true,false");
            configuration.setDefaultEncoding("UTF-8");
            return this;
        } catch (TemplateException | IOException e) {
            throw new GeneratorRuntimeException(e);
        }
    }

    /**
     * 输出一个文件
     *
     * @param model
     * @param input
     * @throws GeneratorRuntimeException
     */
    public File process(Map model, File input, File output) throws GeneratorRuntimeException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), "UTF-8")) {
            new Template(input.getPath(), new FileReader(input), configuration)
                .process(model, writer);
            return output;
        } catch (IOException | TemplateException e) {
            throw new GeneratorRuntimeException(e);
        }
    }


    /**
     * 输出一个字符串
     *
     * @param model
     * @param input
     * @param output
     * @throws GeneratorRuntimeException
     */
    public String process(Map model, String input, String output) throws GeneratorRuntimeException {
        try (Writer writer = new StringWriter();) {
            //模板出路径
            new Template(input, new StringReader(output), configuration)
                .process(model, writer);
            output = writer.toString();
            log.info(":{}\t > \t:{}", input, output);
            return output;
        } catch (IOException | TemplateException e) {
            throw new GeneratorRuntimeException(e);
        }
    }

}
