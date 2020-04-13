package io.github.thinkframework.generator.context;

import io.github.thinkframework.generator.config.GeneratorConfiguration;
import io.github.thinkframework.generator.exception.GeneratorRuntimeException;
import io.github.thinkframework.generator.util.BeanUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 配置文件解析.
 * 基于xpath
 * 实现原型设计模式
 *
 * @author lixiaobin
 * @since 1.0.0
 */
public class GeneratorProperties implements InitializingBean, Cloneable {

    private static final String CLASS_PATH = "classpath://";
    private static final String FILE = "file://";

    private String clonfigProperties;

    private Properties properties = new Properties();

    @SuppressWarnings("unused")
    private GeneratorProperties() {
        //设置系统变量
        Iterator iterator = System.getProperties().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            properties.put("env_" + ((String) entry.getKey()).replaceAll("\\.", "_"), entry.getValue());
        }
    }

    public GeneratorProperties(GeneratorConfiguration generatorConfiguration) {
        this();
        //解析GeneratorConfiguration
        BeanUtils.describe(generatorConfiguration).forEach((key, value) -> {
            //设置GeneratorConfiguration属性
            properties.put(key.toString().replace("generator.", ""), value);
            if (value instanceof String) {//转换成路径
                properties.put(key.toString().replace("generator.", "") + "_path",
                    value.toString().replace(".", "/"));
            }
        });
    }

    public GeneratorProperties setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public GeneratorProperties setProperties(String properties) {
        this.clonfigProperties = properties;
        return this;
    }

    public String getProperty(String key) {
        return getProperty(key, "");
    }

    public String getProperty(String key, String defaultValue) {
        return getProperties().getProperty(key, defaultValue);
    }

    public void setProperty(String key) {
        setProperty(key, "");
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public Properties getProperties() {
        return properties;
    }

    protected InputStream getInputStream(String configLocation) throws GeneratorRuntimeException {
        if (configLocation.startsWith(CLASS_PATH)) {
            return GeneratorProperties.class.getClassLoader().getResourceAsStream(configLocation.substring(CLASS_PATH.length()));
        } else if (configLocation.startsWith(FILE)) {
            try {
                return new FileInputStream(configLocation.substring(FILE.length()));
            } catch (FileNotFoundException e) {
                throw new GeneratorRuntimeException("文件未找到", e);
            }
        }
        return null;
    }

    @Override
    public GeneratorProperties clone() throws CloneNotSupportedException {
        GeneratorProperties clone = (GeneratorProperties) super.clone();
        clone.properties = (Properties) clone.properties.clone();
        return clone;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //如果设置了properties文件就加载
        Optional.ofNullable(clonfigProperties).ifPresent(properties -> {
            try (InputStream inputStream = getInputStream(properties)) {
                this.properties.load(inputStream);
            } catch (IOException e) {
                throw new GeneratorRuntimeException("IO异常", e);
            }
        });

        //添加自定义变量
        properties.put("now_yyyyMMddHHmmss", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }
}
