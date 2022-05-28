package com.zlove.bytecode.study;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            CtClass clazz = classPool.makeClass("com.zlove.bytecode.study.bean.Cat");
            CtField nameField = new CtField(classPool.get("java.lang.String"), "name", clazz);
            nameField.setModifiers(Modifier.PRIVATE);
            clazz.addField(nameField, CtField.Initializer.constant("zlove"));

            CtConstructor constructor = new CtConstructor(new CtClass[] {classPool.get("java.lang.String")}, clazz);
            constructor.setBody("{$0.name = $1;}");
            clazz.addConstructor(constructor);

            clazz.addMethod(CtNewMethod.setter("setName", nameField));
            clazz.addMethod(CtNewMethod.getter("getName", nameField));

            CtMethod ctMethod = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, clazz);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{System.out.println(name);}");
            clazz.addMethod(ctMethod);

            final String targetClassPath = classPool.getClass().getResource("/").getPath();
            clazz.writeFile(targetClassPath);
            System.out.println("Success");
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
        }

        try {
            Class cat = Class.forName("com.zlove.bytecode.study.bean.Cat");
            Constructor constructor = cat.getConstructor(String.class);
            Object obj = constructor.newInstance("zhanglei");
            Field field = cat.getDeclaredField("name");
            field.setAccessible(true);
            System.out.println(field.get(obj));

            Method method = cat.getMethod("setName", String.class);
            method.invoke(obj, "zhangshengqi");

            Method print = cat.getMethod("printName");
            print.invoke(obj);
        } catch (ClassNotFoundException | NoSuchFieldException
                | IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
