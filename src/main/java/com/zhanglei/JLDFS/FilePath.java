package com.zhanglei.JLDFS;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import ru.serce.jnrfuse.struct.FileStat;
import jnr.ffi.Pointer;
public class FilePath extends AbstractPath{
    private ByteBuffer contents = ByteBuffer.allocate(0);
    public FilePath(String name){
        super(name);
    }
    public FilePath(DirectoryPath parent, String name){
        super(parent, name);
    }
    public FilePath(String name, String text){
        super(name);
        try{
            byte[] contentBytes = text.getBytes("UTF-8");
            //return the correct decode in utf-8 for every system.
            contents = ByteBuffer.wrap(contentBytes);
        }
        catch (Exception e){
            System.out.println(e.getStackTrace());
        }
    }
    public  void delete(){
        if (getParent() != null){
            getParent().deleteChild(this);
            setParent(null);
        }
        else {
            System.out.println("Parent is null, delete " + getName() + "failed");
        }
    }
    public  AbstractPath find(String path){
        path = utils.strip(path);
        if (!path.contains("/") && path.equals(getName())) return this;
        return null;
    }
    public  void getattr(FileStat stat){
        stat.st_mode.set(FileStat.S_IFREG | 0777);
        stat.st_size.set(contents.capacity());
        //stat.st_uid.set(getContext().uid.get()); //????
        //stat.st_gid.set(getContext().gid.get()); //????
    }
    // public synchronized void delete(AbstractPath p){
    //     if (getParent() == null){
    //         System.out.println("Can not delete " + p.getName());
    //         return ;
    //     }
    //     getParent().deleteChild(this);
    //     setParent(null);
    // }
    /**
     * Read content of size from offset to buffer.
     * @param buffer a native memory address.
     * @param size the size of content to read.
     * @param offset offset.
     * @return the size of content read.
     */
    public int read(Pointer buffer, long size, long offset){
        int bytesToRead = (int) Math.min(contents.capacity() - offset, size);
        byte[] bytesRead = new byte[bytesToRead];
        synchronized (this) {
            contents.position((int) offset);
            contents.get(bytesRead, 0, bytesToRead);
            buffer.put(0, bytesRead, 0, bytesToRead);
            //why need a intermediate byte array
            contents.position(0); // Rewind lei@2.8 wht not content.rewind()
        }
        return bytesToRead;
    }
    public synchronized void truncate(long size){
        //Abandon the content after size
        if (size < contents.capacity()) {
            // Need to create a new, smaller buffer
            ByteBuffer newContents = ByteBuffer.allocate((int) size); //int API, size is of int type
            byte[] bytesRead = new byte[(int) size];
            contents.get(bytesRead);
            newContents.put(bytesRead);
            contents = newContents;
        }
        else {
            System.out.println("Provided size is no smaller than the original size");
        }
    }
    /**
     * Write
     * @param buffer Where the data comes from.
     * @param buffSize The size of the buffer.
     * @param offset The offset for this file to write.
     * @return buffer size.
     */
    public int write(Pointer buffer, long buffSize, long offset){
        //copy content from buffer to content;
        int maxWriteIndex = (int) (offset + buffSize);
        byte[] bytesToWrite = new byte[(int) buffSize];
        synchronized (this) {
            if (maxWriteIndex > contents.capacity()) {
                // Need to create a new, larger buffer
                ByteBuffer newContents = ByteBuffer.allocate(maxWriteIndex);
                newContents.put(contents);
                contents = newContents;
            }
            buffer.get(0, bytesToWrite, 0, (int) buffSize);
            contents.position((int) offset);
            contents.put(bytesToWrite);
            contents.position(0); // Rewind
        }
        return (int) buffSize;
    }
}