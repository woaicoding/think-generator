package io.github.thinkframework.generator.core.design.chainofresponsibility;

import io.github.thinkframework.generator.core.context.GeneratorContext;
import io.github.thinkframework.generator.core.util.BeanUtils;
import io.github.thinkframework.generator.core.util.StringUtils;
import org.springframework.core.Ordered;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * 默认
 *
 * @author hdhxby
 * @since 2017/3/24
 */
public class ConfigurationGeneratorResponsibility implements GeneratorResponsibility, Ordered {

    @Override
    public GeneratorContext process(GeneratorContext generatorContext) {

        BeanUtils.describe(generatorContext.getGeneratorConfiguration()).forEach((key, value) -> {
            //设置GeneratorConfiguration属性
            generatorContext.getProperties().put(key, value);
            if (value instanceof String) {//转换成路径
                generatorContext.getProperties().put(key+"_"+"path", value.toString().replace(".", Matcher.quoteReplacement(File.separator)));
            }
        });

        Optional.ofNullable(generatorContext.getTarget()).ifPresent(target -> {
            String name = target.toString();
            generatorContext.getProperties().put("tableName", StringUtils.underScoreCase(name));
            generatorContext.getProperties().put("className", StringUtils.camelCase(name));

        });
        return generatorContext;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
