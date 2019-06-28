package com.apt_processor;

import com.apt_annotation.BindViewC;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    /**
     * 用于指定该 AbstractProcessor 的目标注解对象
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> hashSet = new HashSet<>();
        hashSet.add(BindViewC.class.getCanonicalName());
        return hashSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 用于处理包含指定注解对象的代码元素
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //获取所有包含Bindview得注解元素，Element 用于代表程序的一个元素，这个元素可以是：包、类、接口、变量、方法等多种概念
        Set<? extends Element> elementsSet = roundEnvironment.getElementsAnnotatedWith(BindViewC.class);
        //这里Activity作为key
        Map<TypeElement, Map<Integer, VariableElement>> typeElementMapMap = new HashMap<>();
        for (Element typeElement : elementsSet) {
            //因为 BindView 的作用对象是 FIELD，因此 element 可以直接转化为 VariableElement变量元素
            VariableElement variableElement = (VariableElement) typeElement;
            //getEnclosingElement 方法返回封装此 Element 的最里层元素
            //如果 Element 直接封装在另一个元素的声明中，则返回该封装元素
            //此处表示的即 Activity 类对象
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
            Map<Integer, VariableElement> variableElementMap = typeElementMapMap.get(enclosingElement);
            if (variableElementMap == null) {
                variableElementMap = new HashMap<>();
                typeElementMapMap.put(enclosingElement, variableElementMap);
            }
            //获取注解值，即 ViewId
            BindViewC annotation = variableElement.getAnnotation(BindViewC.class);
            int viewId = annotation.value();
            //将每个包含了 BindView注解的字段对象以及其注解值保存起来
            System.out.println(variableElement.getConstantValue() + ","
                    + variableElement.getEnclosingElement()
                    + variableElement.getSimpleName());
            variableElementMap.put(viewId, variableElement);
        }

        for (TypeElement key : typeElementMapMap.keySet()) {
            Map<Integer, VariableElement> variableElementMap = typeElementMapMap.get(key);
            //获取包的信息全名
            String packageName = elementUtils.getPackageOf(key).toString();
            JavaFile javaFile = JavaFile.builder(packageName, generateCodeByPoet(key, variableElementMap)).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {

            }
        }
        return false;
    }

    /**
     * @param typeElement        注解对象上层元素对象，即 Activity 对象
     * @param variableElementMap 包含的注解对象以及注解的目标对象
     * @return
     */
    private TypeSpec generateCodeByPoet(TypeElement typeElement, Map<Integer, VariableElement> variableElementMap) {
        //自动生成的文件以 Activity名 + ViewBinding 进行命名
        //获取不带包名得类名
        String name = typeElement.getSimpleName().toString();
        return TypeSpec
                .classBuilder(name + "ViewBinding")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethodByPoet(typeElement, variableElementMap))
                .build();
    }

    /**
     * 生成方法
     *
     * @param typeElement        注解对象上层元素对象，即 Activity 对象
     * @param variableElementMap Activity 包含的注解对象以及注解的目标对象
     * @return
     */
    private MethodSpec generateMethodByPoet(TypeElement typeElement, Map<Integer, VariableElement> variableElementMap) {
        ClassName className = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        //方法参数名
        //String parameter = "_" + StringUtils.toLowerCaseFirstChar(className.simpleName());
        String parameter = className.simpleName();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(className, parameter);

        for (int viewId : variableElementMap.keySet()) {
            VariableElement element = variableElementMap.get(viewId);
            //被注解的字段名
            String name = element.getSimpleName().toString();
            //被注解的字段的对象类型的全名称
            String type = element.asType().toString();
            String text = "{0}.{1}=({2})({3}.findViewById({4}));";
            methodBuilder.addCode(MessageFormat.format(text, parameter, name, type, parameter, String.valueOf(viewId)));

        }
        return methodBuilder.build();
    }


}
