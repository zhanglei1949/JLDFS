package com.zhanglei.JLDFS;

import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
public class utils {
    /** 
     * strip the string,i.e. delte '/' in two sides. Finally no '/' on each side.
     * @name the relative path
     * @return the striped string
    */
    public static String strip(String name){
        int index = 0;
        while (index < name.length() && name.charAt(index) == '/') index += 1;
        name = name.substring(index);
        index = name.length() - 1;
        while (index >= 0 && name.charAt(index) == '/') index -= 1;
        //if (index < name.length() - 1) index += 1;
        return name.substring(0, index+1);
    }
    public static Map<String, Object> parseYaml(String cf) {
        Yaml yaml = new Yaml();
        //URL url = App.class.getClassLoader().getResource("config.yaml");
        InputStream in = utils.class.getClassLoader().getResourceAsStream(cf);
        Map<String, Object> cfg = (Map) yaml.loadAs(in, Map.class);
        return cfg;
    }
    public static String getEntry(Map<String, Object> cfg, String... strs){
        for (int i = 0; i < strs.length - 1; ++i){
            cfg = (Map<String, Object>) cfg.get(strs[i]);
        }
        return cfg.get(strs[strs.length-1]).toString();
    }
}