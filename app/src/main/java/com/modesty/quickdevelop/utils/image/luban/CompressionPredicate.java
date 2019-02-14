package com.modesty.quickdevelop.utils.image.luban;

/**
 * Created on 2018/1/3 19:43
 *
 * @author andy
 *
 * A functional interface (callback) that returns true or false for the given input path should be compressed.
 */

public interface CompressionPredicate {

    /**
     * Determine the given input path should be compressed and return MVPActivityModelImpl boolean.
     * @param path input path
     * @return the boolean result
     */
    boolean apply(String path);
}
