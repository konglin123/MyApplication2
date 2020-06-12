package com.example.libnetwork;

import java.lang.reflect.Type;

public interface Convert<T> {
    /**
     * 这种是带泛型的
     * @param response
     * @param type
     * @return
     */
    T convert(String response, Type type);
    /**
     * 这种是不带泛型的普通bean
     * @param response
     * @param claz
     * @return
     */
    T convert(String response, Class claz);
}
