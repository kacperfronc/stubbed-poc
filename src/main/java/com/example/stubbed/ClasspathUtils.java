package com.example.stubbed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClasspathUtils {


    private ClasspathUtils() {
    }

    static List<String> listResources(String stubName, String exchangeName) {
        return Stream.concat(
                listResources("stubbed/" + stubName + "/" + exchangeName + "/").stream(),
                listResources("stubbed/" + stubName + "/").stream()
        ).collect(Collectors.toList());
    }

    private static List<String> listResources(String path) {
        try (InputStream in = ClasspathUtils.class.getClassLoader().getResourceAsStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            return br.lines()
                    .filter(line -> line.endsWith(".json"))
                    .map(it -> path + it)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
