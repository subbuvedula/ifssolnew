package com.kickass.ifssol.valueprovider;

import java.util.Calendar;
import java.util.Map;
import java.util.function.Function;
public class CurrentTimeProvider<T,R> implements Function<Map, Calendar>{

    @Override
    public Calendar apply(Map context) {
        Calendar cal = Calendar.getInstance();
        System.out.println("CurrentTimeProvider Called ++++++++++++++++++++ " + cal);
        return cal;
    }

    @Override
    public <V> Function<V, Calendar> compose(Function<? super V, ? extends Map> before) {
        return Function.super.compose(before);
    }

    @Override
    public <V> Function<Map, V> andThen(Function<? super Calendar, ? extends V> after) {
        return Function.super.andThen(after);
    }
}
