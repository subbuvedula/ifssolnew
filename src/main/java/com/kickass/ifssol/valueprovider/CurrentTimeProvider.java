package com.kickass.ifssol.valueprovider;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;
import java.util.function.Function;

@Component
public class CurrentTimeProvider<T,R> implements Function<Map, Calendar>{

    @Override
    public Calendar apply(Map context) {
        Calendar cal = Calendar.getInstance();
        int dom = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);

        cal.clear();
        cal.set(Calendar.DAY_OF_MONTH, dom);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, mon);
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

    public static void main(String[] args) {
        CurrentTimeProvider ct = new CurrentTimeProvider();
        ct.apply(null);
    }
}
