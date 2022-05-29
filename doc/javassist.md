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

