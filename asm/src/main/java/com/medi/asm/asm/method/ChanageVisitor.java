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

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("<init>")) {
            return methodVisitor;
        }
        return new ChangeAdapter(Opcodes.ASM4, methodVisitor, access, name, descriptor);    }
}
