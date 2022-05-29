package com.zlove.bytecode.study.helper;

import com.zlove.bytecode.study.bean.Animal;
import javassist.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GenerateCodeHelper {

    public static CtClass generateCat() {
        ClassPool classPool = ClassPool.getDefault();
        CtClass clazz = null;
        try {
            clazz = classPool.makeClass("com.zlove.bytecode.study.bean.Cat");
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

            CtClass animalInterface = classPool.get("com.zlove.bytecode.study.bean.Animal");
            clazz.setInterfaces(new CtClass[]{ animalInterface });

            final String targetClassPath = classPool.getClass().getResource("/").getPath();
            clazz.writeFile(targetClassPath);
            System.out.println("Success");
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static void checkCatClass(CtClass clazz) {
        try {
            if (clazz == null) {
                return;
            }
            Class cat = clazz.toClass();
            Constructor constructor = cat.getConstructor(String.class);
            Object obj = constructor.newInstance("zhanglei");
            Field field = cat.getDeclaredField("name");
            field.setAccessible(true);
            System.out.println(field.get(obj));

            Method method = cat.getMethod("setName", String.class);
            method.invoke(obj, "zhangshengqi");

            Method print = cat.getMethod("printName");
            print.invoke(obj);
        } catch (NoSuchFieldException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException
                | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void efficientInterface(CtClass clazz) {
        try {
            if (clazz == null) {
                return;
            }

            Class cat = clazz.toClass();
            Constructor constructor = cat.getConstructor(String.class);
            Object obj = constructor.newInstance("zhanglei");
            Animal animal = (Animal) obj;
            System.out.println(animal.getName());
            animal.setName("zlove.zhang");
            animal.printName();
        } catch (CannotCompileException
                | InvocationTargetException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
