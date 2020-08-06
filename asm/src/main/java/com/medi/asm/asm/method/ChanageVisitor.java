package com.medi.asm.asm.method;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 1 ClassVisitor 抽象类:
 * 在 ClassVisitor 中提供了和类结构同名的一些方法，这些方法会对类中相应的部分进行操作，而且是有顺序的：visit
 * [ visitSource ] [ visitOuterClass ] ( visitAnnotation | visitAttribute )* (visitInnerClass
 * | visitField | visitMethod )* visitEnd
 *
 * 2.2 ClassReader 类:
 * 这个类会将 .class 文件读入到 ClassReader 中的字节数组中，它的 accept 方法接受一个 ClassVisitor 实现类，并按照顺序调用 ClassVisitor 中的方法
 *
 * 2.3 ClassWriter 类:
 * ClassWriter 是一个 ClassVisitor 的子类，是和 ClassReader 对应的类，ClassReader 是将 .class 文件读入到一个字节数组中，ClassWriter 是将修改后的类的字节码内容以字节数组的形式输出。
 *
 *4 MethodVisitor & AdviceAdapter:
 * MethodVisitor 是一个抽象类，当 ASM 的 ClassReader 读取到 Method 时就转入 MethodVisitor 接口处理。
 * AdviceAdapter 是 MethodVisitor 的子类，使用 AdviceAdapter 可以更方便的修改方法的字节码。
 * 其中比较重要的几个方法如下：
 * void visitCode()：表示 ASM 开始扫描这个方法
 * void onMethodEnter()：进入这个方法
 * void onMethodExit()：即将从这个方法出去
 * void onVisitEnd()：表示方法扫码完毕
 *
 *5 FieldVisitor 抽象类:
 * FieldVisitor 是一个抽象类，当 ASM 的 ClassReader 读取到 Field 时就转入 FieldVisitor 接口处理。和分析 MethodVisitor 的方法一样，也可以查看源码注释进行学习，这里不再详细介绍
 *
 *
 * 6 操作流程
 *
 * 需要创建一个 ClassReader 对象，将 .class 文件的内容读入到一个字节数组中
 * 然后需要一个 ClassWriter 的对象将操作之后的字节码的字节数组回写
 * 需要事件过滤器 ClassVisitor。在调用 ClassVisitor 的某些方法时会产生一个新的 XXXVisitor 对象，当我们需要修改对应的内容时只要实现自己的 XXXVisitor 并返回就可以了
 *
 */
public class ChanageVisitor extends ClassVisitor {
    public ChanageVisitor(int api) {
        super(api);
    }

    public ChanageVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM4, classVisitor);
    }

    /**
     *
     * 扫描类时第一个调用的方法，可以拿到类的详细信息，然后对满足条件的类进行过滤
     *
     * @param version  jdk版本
     * @param access 类的修饰符，在ASM中是以"ACC_"开头的常量
     * @param name 类的名称
     * @param signature 类泛型信息
     * @param superName 当前类所继承的父类
     * @param interfaces 表示类所实现的接口列表
     */
    @Override
    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     *
     * 访问内部类的信息
     *
     * @param name
     * @param outerName
     * @param innerName
     * @param access
     */
    @Override
    public void visitInnerClass(String name,
                                String outerName,
                                String innerName,
                                int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    /**
     *
     * 扫描到类的方法的时候调用
     * 拿到需要修改的方法，然后进行修改操作
     *
     * @param access 修饰符
     * @param name 方法名称
     * @param descriptor 方法签名
     * @param signature 泛型相关信息
     * @param exceptions 方法抛出异常
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {

        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("<init>")) {
            return methodVisitor;
        }
        return new ChangeAdapter(Opcodes.ASM4, methodVisitor, access, name, descriptor);
    }


    /**
     * 遍历类中的成员信息结束
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
