package com.zhanglei.JLDFS;

import java.util.Map;

import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Statvfs;
import java.nio.file.Paths;
import com.zhanglei.JLDFS.utils;

public class JLDFS extends FuseStubFS {
    // CLient is the role to mount the JLDFS to disk and access it.
    // We leverage fuse to implement POSIX interface in user mode file system.
    private DirectoryPath rootDir = new DirectoryPath("");

    public JLDFS() {
        rootDir.add(new FilePath("File1"));
        DirectoryPath dir1 = new DirectoryPath("Folder1");
        rootDir.add(dir1);
        dir1.add(new FilePath("File2"));
    }

    /**
     * Create a new file, not directory
     */
    @Override
    public int create(String path, @mode_t long mode, FuseFileInfo fi) {
        path = utils.strip(path);
        if (getPath(path) != null) return -ErrorCodes.EEXIST();
        AbstractPath lastParent = rootDir;
        if (path.contains("/")) lastParent = rootDir.find(path.substring(0, path.lastIndexOf('/')));
        if (lastParent == null || !(lastParent instanceof DirectoryPath)) return -ErrorCodes.ENOENT();
        String t = path.substring(path.lastIndexOf('/') + 1);
        ((DirectoryPath) lastParent).mkfile(t);
        return 0;
        
    }

    @Override
    public int getattr(String path, FileStat stat) {
        path = utils.strip(path);
        AbstractPath target = getPath(path);
        System.out.println("getattr for " + path + " : " + target);
        if (target == null) return -ErrorCodes.ENOENT();
        long uid = getContext().uid.get();
        long gid = getContext().gid.get();
        target.getattr(stat, uid, gid);
        return 0;
    }

    /**
     * Take an absolote path as input, return the finded node.
     * @param path
     * @return
     */
    private AbstractPath getPath(String path){
        return rootDir.find(path);
    }
    @Override
    public int mkdir(String path, @mode_t long mode){
        path = utils.strip(path);
        if (getPath(path) != null) return -ErrorCodes.EEXIST();
        AbstractPath lastParent = getPath(path.substring(0, path.lastIndexOf(path)));
        if (lastParent == null || !(lastParent instanceof DirectoryPath)) return -ErrorCodes.ENOENT();
        String t = path.substring(path.lastIndexOf('/') + 1);
        ((DirectoryPath) lastParent).mkdir(t);
        return 0;
    }
    
    @Override
    public int read(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi){
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        if (node instanceof DirectoryPath) return -ErrorCodes.EISDIR();
        ((FilePath) node).read(buf, size, offset);
        return 0;
        
    }
    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, @off_t long offset, FuseFileInfo fi){
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        if (node instanceof FilePath) return -ErrorCodes.ENOTDIR();
        filter.apply(buf, ".", null, 0);
        filter.apply(buf, "..", null, 0);
        ((DirectoryPath) node).read(buf, filter);
        return 0;
    }
    @Override
    public int statfs(String path, Statvfs stbuf){
        path = utils.strip(path);
        return super.statfs(path, stbuf);
    }
    @Override
    /**
     * Rename path to newName. Notice that newName may be in another directory;
     * @param path the original path
     * @param newName the new absolute path.
     */
    public int rename(String path, String newName){
        path = utils.strip(path);
        newName= utils.strip(path);
        AbstractPath oriNode = getPath(path);
        if (oriNode == null) return -ErrorCodes.ENOENT();
        if (getPath(newName) != null) return -ErrorCodes.EEXIST();
        AbstractPath newParent = getPath(newName.substring(0, newName.lastIndexOf('/')));
        if (newParent == null)  return -ErrorCodes.ENOENT();
        if (!(newParent instanceof DirectoryPath)) return -ErrorCodes.ENOTDIR();

        oriNode.delete();
        oriNode.rename(newName.substring(newName.lastIndexOf('/') + 1));
        ((DirectoryPath) newParent).add(oriNode);
        return 0;
    }
    @Override
    public int rmdir(String path){
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        if (!(node instanceof DirectoryPath)) return -ErrorCodes.ENOTDIR();
        DirectoryPath Dnode = (DirectoryPath) node;
        if (Dnode.deleteAble()){
            Dnode.delete();
            return 0;
        }
        else {
            System.out.println("Delete failed : Directory not empty!");
            return -ErrorCodes.ENOTEMPTY();
        }
    }
    @Override
    public int truncate(String path, long offset) {
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        if (!(node instanceof FilePath)) return -ErrorCodes.EISDIR();
        ((FilePath) node).truncate(offset);
        return 0;
    }
    @Override
    public int unlink(String path) {
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        node.delete();
        return 0;
    }
    @Override
    public int open(String path, FuseFileInfo fi) {
        return 0;
    }
    @Override
    public int write(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
        path = utils.strip(path);
        AbstractPath node = getPath(path);
        if (node == null) return -ErrorCodes.ENOENT();
        if (!(node instanceof FilePath)) return -ErrorCodes.EISDIR();
        return ((FilePath) node).write(buf, size, offset);
    }
    public static void main( String[] args )
    {
        String config_path = "config.yaml";
        Map<String, Object> config = utils.parseYaml(config_path);
        //System.out.println(config.get("self.type"));
        if (config == null){
            System.out.println("Error Specificaiton.");
        }
        String machineType = utils.getEntry(config, "self", "type");
        if (machineType.equals("client")){
            System.out.println("Initializing Client app on this machine.");
        }
        else if (machineType.equals("server")){
            System.out.println("Initializing Server app on this machine.");
        }
        else {
            System.out.println("Machine type specification is wrong.pls check config.yaml.");
        }

        JLDFS fs = new JLDFS();
        try {
            String path;
            switch (Platform.getNativePlatform().getOS()) {
                case WINDOWS:
                    path = "J:\\";
                    break;
                default:
                    path = "/tmp/jldfs";
            }
            fs.mount(Paths.get(path), true, true);
        } finally {
            fs.umount();
        }
    }
}
/**
     * Provided with a path, return the leaf node(file or dir).
     * @param path path.
     * @return the name of leaf node.
    private String getLeafNode(String path){
        int index = path.length() - 1;
        while (index >= 0 && path.charAt(index) == '/'){
            index --;
        }
        if (index < 0) return "";
        return path.substring(path.lastIndexOf("/") + 1);
    }
 */