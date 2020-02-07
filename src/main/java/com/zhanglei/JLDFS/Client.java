package com.zhanglei.JLDFS;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import jnr.ffi.types.mode_t;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.FuseStubFS;
public class Client extends FuseStubFS{
    //CLient is the role to mount the JLDFS to disk and access it.
    //We leverage fuse to implement POSIX interface in user mode file system.
    public Client() {
        
    }
}