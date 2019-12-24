package com.medi.asm.asm.field;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Transform extends ClassVisitor {
    public Transform(int api,ClassVisitor cv) {
        super(api,cv);
    }


    @Override
    public void visitEnd() {
        //向类中添加一个属性
        cv.visitField(Opcodes.ACC_PUBLIC, "age", Type.getDescriptor(int.class), null, null);
    }

    /**
     * 每访问一次属性就会调用一次，不适合添加属性，会重复添加属性
     * @param access
     * @param name
     * @param descriptor
     * @param signature
     * @param value
     * @return
     */
    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }
}
