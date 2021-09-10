package com.kickass.ifssol.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.function.Function;
@Component
public class TypeConverter {
    public Object convert(Object  from, Object defaultValue, String valueProviderClass, Class type) {

        if (StringUtils.isEmpty(from)) {
            from = defaultValue;
        }
        if (StringUtils.isEmpty(from) && StringUtils.isEmpty(defaultValue)) {
            from = getFromValueProvider(valueProviderClass);
        }

        if (StringUtils.isEmpty(from)) {
            return null;
        }

        if (type.isAssignableFrom(String.class)) {
            return from.toString();
        }
        if (type.isAssignableFrom(Double.class)) {
            return Double.parseDouble(from.toString().trim());
        }
        if (type.isAssignableFrom(Long.class)) {
            return Long.parseLong(from.toString().trim());
        }
        return from;
    }

    private Object getFromValueProvider(String providerClassName) {
        if (!StringUtils.isEmpty(providerClassName)) {
            try {
                Class providerClass = Class.forName(providerClassName);
                Function function = (Function)providerClass.getDeclaredConstructor().newInstance();
                return function.apply(null);
            }
            catch (Exception ex) {
                ex.printStackTrace();//TODO fix
            }
        }
        return null;
    }


}
