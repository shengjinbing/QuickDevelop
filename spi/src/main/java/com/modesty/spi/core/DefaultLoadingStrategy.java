package com.modesty.spi.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ServiceConfigurationError;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public final class DefaultLoadingStrategy<S> implements LoadingStrategy<S> {
    public DefaultLoadingStrategy() {
    }

    @Override
    public Collection<Class<S>> load(ClassLoader cl, InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        LinkedHashSet result = new LinkedHashSet();

        String line;
        while((line = reader.readLine()) != null) {
            int commentStart = line.indexOf(35);
            if(commentStart != -1) {
                line = line.substring(0, commentStart);
            }

            line = line.trim();
            if(!line.isEmpty()) {
                String className = line;
                checkValidJavaClassName(line);
                if(!result.contains(line)) {
                    try {
                        result.add(cl.loadClass(className));
                    } catch (ClassNotFoundException var9) {
                        var9.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    private void checkValidJavaClassName(String className) {
        for(int i = 0; i < className.length(); ++i) {
            char ch = className.charAt(i);
            if(!Character.isJavaIdentifierPart(ch) && ch != 46) {
                throw new ServiceConfigurationError("Bad character '" + ch + "' in class name");
            }
        }

    }
}