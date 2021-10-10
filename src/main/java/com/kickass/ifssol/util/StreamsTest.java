package com.kickass.ifssol.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsTest {

    public static void main(String[] args) {

        List<Product> products = new ArrayList<>();

        for(int i=0; i<100; i++) {
            Product p = new Product();
        }

        String[] arr = {"x", "a", "c", "d"};
        String[] arr2 = new String[]{"a", "b", "c"};
        Stream<String> stream = Arrays.stream(arr2);
        stream = Stream.of("a", "b", "c");
        ArrayList<String> list = new ArrayList<>();
        list.add("One");
        list.add("OneAndOnly");
        list.add("Derek");
        list.add("Change");
        list.add("factory");
        list.add("justBefore");
        list.add("Italy");
        list.add("Italy");
        list.add("Thursday");
        list.add("");
        list.add("");
       System.out.println(Arrays.stream(arr).anyMatch(e->e.equals("a")));

        System.out.println(Arrays.stream(arr).distinct().sorted().collect(Collectors.toList()));


        System.out.println(stream.distinct().count());
        System.out.println(list.stream().filter(e->e.contains("One")).collect(Collectors.toList()));

    }

    static class Product {
        String cat;
        float price;
    }

}
