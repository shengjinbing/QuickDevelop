package com.medi.asm.asm.method;

public class MyClassLoader extends ClassLoader {

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    /**
     * 根据二进制文件获取class对象
     * @param name
     * @param bytes
     * @return
     */
    public Class<?> defindClass(String name, byte[] bytes){
        return defineClass(name,bytes,0,bytes.length);
    }
}
