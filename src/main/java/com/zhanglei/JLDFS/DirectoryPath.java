package com.zhanglei.JLDFS;

import java.util.ArrayList;
import java.util.List;
import com.zhanglei.JLDFS.utils;
import ru.serce.jnrfuse.struct.FileStat;

public class DirectoryPath extends AbstractPath{
    private List<AbstractPath> childs = new ArrayList<AbstractPath>();

    public DirectoryPath(String name){
        super(name);
    }
    public DirectoryPath(DirectoryPath parent, String name){
        super(parent, name);
    }
    
    public  void delete(String name){

    }
    public  void getattr(FileStat stat){

    }
    // distinct functions
    public synchronized void add(AbstractPath p){
        childs.add(p);
        p.setParent(this);
    }
    public synchronized void deleteChild(AbstractPath p){
        childs.remove(p);
    }
    protected synchronized AbstractPath find(String path){
        path = utils.strip(path);
        if (path.contains("/")){
            //it must be in the middel
            String s = path.substring(0, path.indexOf('/'));
            String t = path.substring(path.indexOf('/')+1);
            for (int i = 0; i < childs.size(); ++i){
                if (childs.get(i).getName().equals(s)){
                    if (childs.get(i).getClass().getName() == "DirectoryPath") return childs.get(i).find(t);
                }
            }
        }
        else {
            for (int i = 0; i < childs.size(); ++i){
                if (childs.get(i).getName().equals(path)){
                    return childs.get(i);
                }
            }
        }
        return null;
    }
}