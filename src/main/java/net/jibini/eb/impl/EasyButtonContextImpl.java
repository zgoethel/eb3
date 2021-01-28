package net.jibini.eb.impl;

import org.jetbrains.annotations.NotNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides access to Spring beans to classes which are not subject to resource
 * injection. All beans will result in null-pointers if resource injection has
 * not yet been performed.
 *
 * @author Zach Goethel
 */
@Component
public class EasyButtonContextImpl implements ApplicationContextAware
{
    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
    }

    /**
     * @param componentType Class object for the requested bean.
     * @param <T> Type parameter of the requested bean.
     * @return The bean instance, or null if it is not yet created.
     */
    @NotNull
    public static <T> T getBean(Class<T> componentType)
    {
        if (context == null)
            throw new IllegalStateException("Context is null; context aware object is not ready");

        return context.getBean(componentType);
    }
}
