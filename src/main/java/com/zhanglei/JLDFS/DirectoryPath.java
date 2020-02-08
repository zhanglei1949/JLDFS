package com.zhanglei.JLDFS;

import java.util.ArrayList;
import java.util.List;
import com.zhanglei.JLDFS.utils;
import ru.serce.jnrfuse.struct.FileStat;
import jnr.ffi.Pointer;
import ru.serce.jnrfuse.FuseFillDir;
import jnr.ffi.Platform;
public class DirectoryPath extends AbstractPath{
    private List<AbstractPath> childs = new ArrayList<AbstractPath>();

    public DirectoryPath(String name){
        super(name);
    }
    public DirectoryPath(DirectoryPath parent, String name){
        super(parent, name);
    }
    /**
     * Get the file status; Note that getcontext is implemented in AbstractFuseFS.
     */
    public  void getattr(FileStat stat){
        stat.st_mode.set(FileStat.S_IFDIR | 0777);
        //stat.st_uid.set(getContext().uid.get()); //????
        //stat.st_gid.set(getContext().gid.get()); //????

    }
    // distinct functions
    public synchronized void add(AbstractPath p){
        childs.add(p);
        p.setParent(this);
    }
    // public synchronized void delete(){
    //     if (getParent() == null || childs.size() > 0){
    //         System.out.println("Can not delete " + getParent().getName());
    //         return ;
    //     }
    //     getParent().deleteChild(this);
    //     setParent(null);
    // }
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
    public synchronized void mkdir(String lastPart){
        childs.add(new DirectoryPath(this, lastPart));
    }
    public synchronized void mkfile(String lastPart){
        childs.add(new FilePath(this, lastPart));
    }
    /**
     * Read all the sub directory's info.
     * @param buf A Nativate memory addres
     * @param filler Provide function to add an entry.
     */
    public synchronized void read(Pointer buf, FuseFillDir filter){
        for (AbstractPath p : childs){
            filter.apply(buf, p.getName(), null, 0); 
        }
    }
    public boolean deleteAble(){
        if (childs.size() > 0) return false;
        return true;
    }
}