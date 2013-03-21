/**
 * 
 */
package org.pidster.java.lang.instrument;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author pidster
 *
 */
public class Reflector {

    public static Object instance(@Nonnull String className, Object... constructorArgs) throws ReflectorException {
        return instance(className, constructorArgs);
    }

    public static Object instance(@Nonnull String className, @Nullable ClassLoader loader, Object... constructorArgs) throws ReflectorException {

        try {
            if (loader == null) {
                loader = Thread.currentThread().getContextClassLoader();
            }

            Class<?> clazz = loader.loadClass(className);
            if (constructorArgs.length == 0) {
                return clazz.newInstance();
            }

            Class<?>[] argTypes;
            if (constructorArgs == null || constructorArgs.length == 0) {
                argTypes = new Class<?>[0];
            }
            else {
                argTypes = new Class<?>[constructorArgs.length];
            }

            int index = 0;
            for (Object arg : constructorArgs) {
                argTypes[index] = arg.getClass();
                index++;
            }

            Constructor<?> constructor = clazz.getConstructor(argTypes);
            return constructor.newInstance(constructorArgs);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ReflectorException("Class handling exception", e);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(@Nonnull Object target, @Nonnull String methodName, Object... methodArgs) throws ReflectorException {

        try {

            Class<?>[] parameterTypes;
            if (methodArgs == null || methodArgs.length == 0) {
                parameterTypes = new Class<?>[0];
            }
            else {
                parameterTypes = new Class<?>[methodArgs.length];
            }

            int index = 0;
            for (Object arg : methodArgs) {
                parameterTypes[index] = arg.getClass();
                index++;
            }

            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return (T) method.invoke(target, methodArgs);

        } catch (NoSuchMethodException | SecurityException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(@Nonnull String className, @Nonnull String methodName, Object... methodArgs) throws ReflectorException {
        return (T) invokeStatic(className, null, methodName, methodArgs);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(@Nonnull String className, @Nullable ClassLoader loader, @Nonnull String methodName, Object... methodArgs) throws ReflectorException {

        try {
            if (loader == null) {
                loader = Thread.currentThread().getContextClassLoader();
            }

            Class<?> clazz = loader.loadClass(className);

            Class<?>[] parameterTypes = new Class<?>[methodArgs.length];
            int index = 0;
            for (Object arg : methodArgs) {
                parameterTypes[index] = arg.getClass();
                index++;
            }

            Method method = clazz.getMethod(methodName, parameterTypes);
            return (T) method.invoke(null, methodArgs);

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

}
