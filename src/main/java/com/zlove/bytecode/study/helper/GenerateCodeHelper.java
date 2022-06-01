package com.zlove.bytecode.study.helper;

import com.zlove.bytecode.study.annotation.AnimalName;
import com.zlove.bytecode.study.annotation.AnimalWeight;
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

    public static void updateClass(CtClass clazz) {
        try {
            clazz.defrost();
            CtMethod ctMethod = clazz.getDeclaredMethod("printName");
            ctMethod.insertBefore("{System.out.println(\"Hello everyone, my name is:\");}");
            ctMethod.insertAfter("{System.out.println(\"miao miao miao...\");}");
            clazz.freeze();
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void addFieldAndConstructor() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Dog");
            CtField weightField = new CtField(CtClass.intType, "weight", clazz);
            weightField.setModifiers(Modifier.PRIVATE);
            clazz.addField(weightField, CtField.Initializer.constant(0));

            Class dog = clazz.toClass();
            Object obj = dog.newInstance();
            Field field = dog.getDeclaredField("weight");
            field.setAccessible(true);
            System.out.println(field.get(obj));
        } catch (NotFoundException | CannotCompileException
                | NoSuchFieldException | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void annotationTest() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Dog");
            Object[] annotations = clazz.getAnnotations();
            for (Object ann : annotations) {
                System.out.println(ann);
            }
            Object nameAnn = clazz.getAnnotation(AnimalName.class);
            AnimalName name = (AnimalName) nameAnn;
            System.out.println(name.value());

            Object weightAnn = clazz.getAnnotation(AnimalWeight.class);
            AnimalWeight weight = (AnimalWeight) weightAnn;
            System.out.println(weight.value());
        } catch (NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void constructorTest() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Snoopy");
            CtConstructor[] constructors = clazz.getConstructors();
            for (CtConstructor cons : constructors) {
                System.out.println(cons.getName());
                System.out.println(cons.getLongName());
            }
            CtConstructor constructor = clazz.getDeclaredConstructor(new CtClass[]{CtClass.intType});
            System.out.println("--->" + constructor.getLongName());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void fieldTest() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Snoopy");
            CtField wField = clazz.getDeclaredField("weight");
            System.out.println("weight--->" + wField.getName());
            CtField nField = clazz.getField("name");
            System.out.println("name--->" +nField.getName());

            CtField[] fields = clazz.getFields();
            for (CtField f : fields) {
                System.out.println("f ---> " + f.getName());
            }

            CtField[] declareFields = clazz.getDeclaredFields();
            for (CtField df : declareFields) {
                System.out.println("df ---> " + df.getName());
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
}
