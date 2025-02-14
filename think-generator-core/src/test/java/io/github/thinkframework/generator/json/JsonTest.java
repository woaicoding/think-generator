package io.github.thinkframework.generator.json;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTest {

    @Test
    public void test(){
        String s = "{\"id\":1,\"name\":\"martin\"}";
        json(s);
    }

    private void json(String raw){
        System.out.println(raw.replaceAll("\\{","\\{\n")
            .replaceAll("\\s*(\\\"(?<key>\\w+)\\\")\\s*:\\s*(\\\"(?<value>\\w+)\\\"|\\d+|null)\\s*\\,?","$1:$3\n"));

        Pattern pattern = Pattern.compile("\\s*(\\\"?(?<key>\\w+)\\\"?)\\s*:\\s*(\\\"?(?<value>\\w+)\\\"?)\\s*\\,?");
        Matcher matcher = pattern.matcher(raw);
        System.out.println("----");
        while(matcher.find()){
            System.out.printf("%s : %s\n",matcher.group(1),matcher.group(3));
            System.out.printf("%s : %s\n",matcher.group("key"),matcher.group("value"));
        }
    }
}
