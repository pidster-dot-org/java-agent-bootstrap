/**
 * 
 */
package org.pidster.java.lang.instrument;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author pidster
 *
 */
public class Reflector {

    public static Object instance(String className, Object... constructorArgs) throws ReflectorException {
        return instance(className, constructorArgs);
    }

    public static Object instance(String className, ClassLoader loader, Object... constructorArgs) throws ReflectorException {

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

        } catch (ClassNotFoundException e) {
            throw new ReflectorException("Class handling exception", e);
        } catch (InstantiationException e) {
            throw new ReflectorException("Class handling exception", e);
        } catch (IllegalAccessException e) {
            throw new ReflectorException("Class handling exception", e);
        } catch (NoSuchMethodException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (SecurityException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (IllegalArgumentException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object target, String methodName, Object... methodArgs) throws ReflectorException {

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

        } catch (NoSuchMethodException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (SecurityException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (IllegalAccessException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (IllegalArgumentException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(String className, String methodName, Object... methodArgs) throws ReflectorException {
        return (T) invokeStatic(className, null, methodName, methodArgs);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(String className, ClassLoader loader, String methodName, Object... methodArgs) throws ReflectorException {

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

        } catch (ClassNotFoundException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (NoSuchMethodException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (SecurityException e) {
            throw new ReflectorException("Method selection exception", e);
        } catch (IllegalAccessException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (IllegalArgumentException e) {
            throw new ReflectorException("Invocation exception", e);
        } catch (InvocationTargetException e) {
            throw new ReflectorException("Invocation exception", e);
        }
    }

}
