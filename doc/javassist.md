# Javassist API

## CtClass
> 生成新class文件并写入指定路径。

涉及的API
- addField
- addConstructor
- addMethod

示例代码：
```java
    public static void main(String[] args) {
        // 默认类池
        ClassPool classPool = ClassPool.getDefault();
        try {
            // 创建一个类
            CtClass clazz = classPool.makeClass("com.zlove.bytecode.study.bean.Cat");
            // 新增字段
            CtField nameField = new CtField(classPool.get("java.lang.String"), "name", clazz);
            // 访问域 private
            nameField.setModifiers(Modifier.PRIVATE);
            // 默认值 zlove
            clazz.addField(nameField, CtField.Initializer.constant("zlove"));
    
            // 新增有参构造函数，参数类型是String
            CtConstructor constructor = new CtConstructor(new CtClass[] {classPool.get("java.lang.String")}, clazz);
            // 构造函数体内容，$0=this，$1,$2,$3...代表方法参数
            constructor.setBody("{$0.name = $1;}");
            clazz.addConstructor(constructor);
    
            // 添加 set/get 方法
            clazz.addMethod(CtNewMethod.setter("setName", nameField));
            clazz.addMethod(CtNewMethod.getter("getName", nameField));
    
            // 添加 printName 方法
            CtMethod ctMethod = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, clazz);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{System.out.println(name);}");
            clazz.addMethod(ctMethod);
    
            // 将新增类写入指定路径
            final String targetClassPath = classPool.getClass().getResource("/").getPath();
            clazz.writeFile(targetClassPath);
            System.out.println("Success");
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace();
        }

        try {
            // 通过正常反射，验证上述生成的代码是否生效
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
```
- toClass
> 将 `Class cat = Class.forName("com.zlove.bytecode.study.bean.Cat");` 替换成 `Class cat = clazz.toClass();` 依然可行。
> 只不过需要注意的是一旦调用该方法，则无法继续修改已经被加载的class。

- setInterfaces
> 为例避免反射带来的效率问题，可以事先定义好接口
```java
public interface Animal {

    void setName(String name);

    String getName();

    void printName();
}
```
> 生成 class 文件时让其实现此接口
```java
CtClass animalInterface = classPool.get("com.zlove.bytecode.study.bean.Animal");
clazz.setInterfaces(new CtClass[]{ animalInterface });
```
> 在反射生成对象之后，就可以将该对象强转成接口类型，由此可以避免反射去获取相应的方法带来的开销。
```java
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
```
- defrost
- freeze
- CtMethod#insertBefore
- CtMethod#insertAfter
```java
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
```

- booleanType
- byteType
- voidType
- ......
> 指定类型
```java
CtMethod ctMethod = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, clazz);
CtField weightField = new CtField(CtClass.intType, "weight", clazz);
```

- getAnnotations
- getAnnotation

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface AnimalName {
    String value();
}

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface AnimalWeight {
    int value();
}
```

```java
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
```

- getConstructors
- getDeclaredConstructor

```java
public class Snoopy extends Dog {

    private int weight;

    public Snoopy() {
        super("snoopy");
    }

    public Snoopy(int weight) {
        super("snoopy");
        this.weight = weight;
    }

}
```

```java
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

输出结果：Snoopy
        com.zlove.bytecode.study.bean.Snoopy()
        Snoopy
        com.zlove.bytecode.study.bean.Snoopy(int)
        --->com.zlove.bytecode.study.bean.Snoopy(int)
```

- getDeclaredField: 获取当前类某个field
- getField: 可获取到当前类父类中的某个field
- getFields: 获取父类 public fields
- getDeclaredFields: 获取当前类所有fields
```java
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
```

- getMethods: 获取所有方法，包括所有父类，每一级父类，直到Object，祖坟都刨出来了
- getDeclaredMethods: 获取当前类的所有方法
- getDeclaredMethod: 获取当前类的某个方法
- getMethod: 不会用
```java
public static void testMethod() {
    try {
        ClassPool classPool = ClassPool.getDefault();
        CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Snoopy");
        CtMethod[] methods = clazz.getMethods();
        for (CtMethod m : methods) {
            System.out.println("m ---> " + m.getLongName());
        }
        CtMethod[] declaredMethods = clazz.getDeclaredMethods();
        for (CtMethod dm : declaredMethods) {
            System.out.println("dm ---> " + dm.getLongName());
        }

        CtMethod speakMethod = clazz.getDeclaredMethod("speak");
        System.out.println("speak ---> " + speakMethod.getLongName());

    } catch (NotFoundException e) {
        e.printStackTrace();
    }
}
```

- getGenericSignature: 获取泛型的签名
- getInterfaces: 获取当前类实现的所有接口
- getPackageName
- getSimpleName
- getSuperclass
- getURL
```java
public static void testOtherApi() {
    try {
        ClassPool classPool = ClassPool.getDefault();
        CtClass clazz = classPool.get("com.zlove.bytecode.study.bean.Dog");
        String signature = clazz.getGenericSignature();
        System.out.println("signature ---> " + signature);

        CtClass[] interfaces = clazz.getInterfaces();
        for (CtClass in : interfaces) {
            System.out.println(in.getName());
        }

        System.out.println("package name ---> " + clazz.getPackageName());
        System.out.println("simple name ---> " + clazz.getSimpleName());
        System.out.println("super class ---> " + clazz.getSuperclass().getSimpleName());
        System.out.println("url ---> " + clazz.getURL());

    } catch (NotFoundException e) {
        e.printStackTrace();
    }
}
```




