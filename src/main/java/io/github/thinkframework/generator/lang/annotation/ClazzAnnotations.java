package io.github.thinkframework.generator.lang.annotation;

import io.github.thinkframework.generator.lang.annotation.impl.ClazzAnnotationsImpl;

/**
 * 有序集合
 *
 * @author lixiaobin
 * @since 2017/5/25.
 */
public class ClazzAnnotations {
    ClazzAnnotationsImpl clazzAnnotations = new ClazzAnnotationsImpl();

    public boolean add(ClazzAnnotation clazzAnnotation) {
        return clazzAnnotations.add(clazzAnnotation);
    }

    @Override
    public String toString() {
        return clazzAnnotations.toString();
    }

}
