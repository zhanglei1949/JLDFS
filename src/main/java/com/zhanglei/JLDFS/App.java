package com.zhanglei.JLDFS;
import java.net.URL;
//import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;

/**
 * Hello world!
 *
 */
public class App {
    

    private static Map parseYaml(String cf) {
        Yaml yaml = new Yaml();
        URL url = App.class.getClassLoader().getResource("config.yaml");
        //FileInputStream in = (FileInputStream)App.class.getResourceAsStream(cf);
        Map cfg = null;
        try {
            cfg = (Map) yaml.load(new FileInputStream(url.getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return cfg;
        
    }
    public static void main( String[] args )
    {
        String config_path = "config.yaml";
        System.out.println(parseYaml(config_path));
        System.out.println( "Hello World!" );
    }
}
