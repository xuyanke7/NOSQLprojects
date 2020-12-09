package com.bjtu.redis;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;


/**
 * @author xuyifan
 */
public class MyFileAlterationListener implements FileAlterationListener {
    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {

    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onFileCreate(File file) {

    }

    @Override
    public void onFileChange(File file) {
        System.out.println("json文件更改，重新读取");
        try {
            RedisStartMain.readJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {

    }
}
