package com.medi.asm.asm.method;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class CostTime {

/*    public void redefinePersonClass() {
        String className = "com.medi.asm.asm.method.HelloWorld";

        try {
            InputStream inputStream = new FileInputStream("/Users/lixiang/Desktop/HelloWorld.class");
            ClassReader reader = new ClassReader(inputStream);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);// 2. 创建 ClassWriter 对象，将操作之后的字节码的字节数组回写
            ChanageVisitor visitor = new ChanageVisitor(writer);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);// 4. 将 ClassVisitor 对象传入 ClassReader 中

            Class<?> aClass = new MyClassLoader().defindClass(className, writer.toByteArray());
            Object o = aClass.newInstance();
            Method sayHello = aClass.getDeclaredMethod("sayHello", null);
            sayHello.invoke(o,null);

            System.out.println("Success");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failure");
        }
    }*/

}
