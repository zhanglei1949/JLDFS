package com.zhanglei.JLDFS;
public class utils{
    /** 
     * strip the string,i.e. delte '/' in the front and end
     * @return the striped string
    */
    public static String strip(String name){
        int index = 0;
        while (index < name.length() && name.charAt(index) == '/') index += 1;
        name = name.substring(index);
        index = name.length() - 1;
        while (index >= 0 && name.charAt(index) == '/') index -= 1;
        return name.substring(0, index+1);
    }
}