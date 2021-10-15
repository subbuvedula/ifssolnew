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
            if (i%2 == 0) {
                p.cat = "Books";
            }
            else if (i%3 == 0)
                p.cat = "Comedy";
            else if (i%4 == 0)
                p.cat = "Drama";
            else
                p.cat = "Horror";

            p.price = (i+1)*3.3;
            products.add(p);
        }

        System.out.println("Total Products : " + products.size());

        List<Product> filterList = products.stream().filter(e->e.cat.equals("Books") && e.price > 100.0).collect(Collectors.toList());
        System.out.println("Books Priced > 100 : " + filterList.size());

        filterList = products.stream().filter(e->e.cat.equals("Books")).map(e-> {e.price = e.price-(e.price*50/100f); return e;}).collect(Collectors.toList());
        System.out.println("Books Priced > 100 : " + filterList.size());
        filterList = filterList.stream().filter(e->e.price <= 100.0).collect(Collectors.toList());
        System.out.println("Books Priced > 100 : " + filterList.size());



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
        double price;
    }

}
