package com.zhanglei.JLDFS;
import java.util.ArrayList;
import java.util.List;
import ru.serce.jnrfuse.struct.FileStat;

public class FilePath extends AbstractPath{
    public FilePath(String name){
        super(name);
    }
    public FilePath(DirectoryPath parent, String name){
        super(parent, name);
    }
    public  void delete(String name){

    }
    public  AbstractPath find(String path){

    }
    public  void getattr(FileStat stat){

    }
    public synchronized void delete(AbstractPath p){
        if (getParent() == null){
            System.out.println("Can not delete " + p.getName());
            return ;
        }
        getParent().deleteChild(this);
        setParent(null);
    }
}