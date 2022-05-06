package com.FATCA.API.converter;
import java.util.List;
import java.util.ListIterator;

public class Utils {
    public static void listToLowerCase(List<String> strings)
    {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext())
        {
            iterator.set(iterator.next().toLowerCase());
        }
    }
}
