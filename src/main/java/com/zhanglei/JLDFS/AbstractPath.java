package com.zhanglei.JLDFS;
import com.zhanglei.JLDFS.utils;
import ru.serce.jnrfuse.struct.FileStat;
public abstract class AbstractPath{
    private String name;
    private DirectoryPath parent;

    public AbstractPath(String name){
        this.name = name;
        this.parent = null;
    }
    public AbstractPath(DirectoryPath parent, String name){
        this.name = name;
        this.parent = parent;
    }

    public abstract void delete();
    protected abstract AbstractPath find(String path);
    public abstract void getattr(FileStat stat);
    public void rename(String name2){
        //strip name2 including // infront and end.
        this.name = utils.strip(name2);
    }
    public void setParent(DirectoryPath parent){
        this.parent = parent;
    }
    public String getName(){
        return this.name;
    }
    public DirectoryPath getParent(){
        return this.parent;
    }
}
    // public synchronized void delete(){
    //     if (parent == null) return null;
    //     try {
    //         parent.deleteChild(this);
    //     }
    //     catch(Exception e){
    //         e.printStackTrace();
    //     }
    // }