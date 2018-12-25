package com.modesty.spi.creator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lixiang
 * on 2018/12/20
 */
public class InstanceCreatorList<T> implements InstanceCreator<T> {
    private final List<InstanceCreator<T>> instanceCreators = new LinkedList();

    public InstanceCreatorList(InstanceCreator... creators) {
        //this.instanceCreators.addAll(Arrays.asList(creators));
    }

    public InstanceCreatorList<T> add(InstanceCreator... creators) {
        //this.instanceCreators.addAll(Arrays.asList(creators));
        return this;
    }

    public InstanceCreatorList<T> clear() {
        this.instanceCreators.clear();
        return this;
    }

    public InstanceCreatorList<T> remove(InstanceCreator<T> creator) {
        this.instanceCreators.remove(creator);
        return this;
    }

    @Override
    public T createInstance(Class<T> clazz) throws Exception {
        Iterator var3 = this.instanceCreators.iterator();

        while(var3.hasNext()) {
            InstanceCreator creator = (InstanceCreator)var3.next();

            try {
                T res = (T) creator.createInstance(clazz);
                return res;
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        throw new NoSuchMethodException("Can't create " + clazz.getName() + " getInstance");
    }
}

