package com.sensorsdata.analytics.android.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class SensorsAnalyticsTransform extends Transform {
    private static Project project

    public SensorsAnalyticsTransform(Project project) {
        this.project = project

    }

    @Override
    String getName() {
        return "SensorsAnalyticsAutoTrack"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES 代表处理的 java 的 class 文件，RESOURCES 代表要处理 java 的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 打印提示信息
     */
    static void printCopyRight() {
        println()
        println("####################################################################")
        println("########                                                    ########")
        println("########                                                    ########")
        println("########         欢迎使用 SensorsAnalytics® 编译插件        ########")
        println("########          使用过程中碰到任何问题请联系我们          ########")
        println("########                                                    ########")
        println("########                                                    ########")
        println("####################################################################")
        println()
    }

    /**
     *
     * @param context
     * @param inputs 有两种类型，一种目录一种jar包，要分开遍历
     * @param referencedInputs
     * @param outputProvider
     * @param isIncremental
     * @throws IOException
     * @throws TransformException
     * @throws InterruptedException
     */
    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        printCopyRight()

        if (!incremental){
            outputProvider.deleteAll()
        }

        // Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历
        inputs.each { TransformInput input ->
            //遍历目录
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //获取 output 目录,当前这个Transform输出目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                File dir = directoryInput.file
                if (dir){
                    HashMap<String,File> modifyMap = new HashMap<>()
                    /**遍历以某一种扩展名结尾的文件*/
                    dir.traverse(type: FileType.FILES,nameFilter:~/.*\.class/){
                        File classFile ->
                            if (SensorsAnalyticsClaaModifier.isShouldModify(classFile.name)){
                                //需要修改的文件
                                File modified = SensorsAnalyticsClaaModifier.modifyClassFile(
                                        dir,classFile,context.getTemporaryDir())
                                if (modified != null){
                                    /**
                                     * key 为包名+类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class
                                     */
                                    String ke = classFile.absolutePath.replace(dir.absolutePath,"")
                                    modifyMap.put(ke,modifyMap)
                                }
                            }
                    }

                    // 将 input 的目录复制到 output 指定目录
                    FileUtils.copyDirectory(directoryInput.file, dest)
                    modifyMap.entrySet().each {
                        Map.Entry<String,File> en ->
                            File target = New File(dest.absolutePath+en.getKey())
                            if (target.exists()){
                                target.delete()
                            }
                            FileUtils.copyFile(en.getValue(),target)
                            en.getValue().delete()
                    }
                }


            }

            //遍历 jar
            input.jarInputs.each { JarInput jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                String destName = jarInput.file.name
                String hexName = DigestUtils.md5Hex(jarInput.file.getAbsolutePath().substring(0,8))

                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }


                //生成输出路径
                File dest = outputProvider.getContentLocation(destName + "_"+hexName,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                // 将 input 的目录复制到 output 指定目录
                def modifiedJar = SensorsAnalyticsClaaModifier.modifyJar(
                        jarInput.file,context.getTemporaryDir(),true)
                if (modifiedJar == null){
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)
            }
        }
    }
}