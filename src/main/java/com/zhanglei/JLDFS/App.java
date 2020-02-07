package com.zhanglei.JLDFS;

import java.net.URL;
//import java.util.HashMap;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

/**
 * Hello world!
 *
 */
public class App {
    

    private static Map<String, Object> parseYaml(String cf) {
        Yaml yaml = new Yaml();
        //URL url = App.class.getClassLoader().getResource("config.yaml");
        InputStream in = App.class.getClassLoader().getResourceAsStream(cf);
        Map<String, Object> cfg = (Map) yaml.loadAs(in, Map.class);
        return cfg;
        
    }
    private static String getEntry(Map<String, Object> cfg, String... strs){
        for (int i = 0; i < strs.length - 1; ++i){
            cfg = (Map<String, Object>) cfg.get(strs[i]);
        }
        return cfg.get(strs[strs.length-1]).toString();
    }
    public static void main( String[] args )
    {
        String config_path = "config.yaml";
        Map<String, Object> config = parseYaml(config_path);
        //System.out.println(config.get("self.type"));
        if (config == null){
            System.out.println("Error Specificaiton.");
        }
        String machineType = getEntry(config, "self", "type");
        if (machineType.equals("client")){
            System.out.println("Initializing Client app on this machine.");
        }
        else if (machineType.equals("server")){
            System.out.println("Initializing Server app on this machine.");
        }
        else {
            System.out.println("Machine type specification is wrong.pls check config.yaml.");
        }
    }
}
