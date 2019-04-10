package org.corgi.remotefile.controller;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RemoteFileController {
    private static final String DOWNLOAD = "DOWNLOAD";
    private static final String SEARCH = "SEARCH";
    private static final String CHECK = "CHECK";
    private static final String DELETE = "DELETE";

    public static void main(String[] args) {
        if (args.length < 1 || !(args[0].equals(SEARCH) || args[0].equals(DOWNLOAD) || args[0].equals(CHECK) || args[0].equals(DELETE))) {
            System.err.println("请输入请求类型：");
            System.err.println("    查看：SEARCH");
            System.err.println("    下载：DOWNLOAD");
            System.err.println("    检查：CHECK");
            System.err.println("    删除：DELETE");
        }
        if (args.length < 2) {
            System.err.println("请输入远程资源路径，格式为：用户名:密码@IP地址/资源");
        }
        if (args.length < 3) {
            System.err.println("请输入本地存储目录");
            return;
        }
        // PUT
        String requestType = args[0];
        // guest:gantang@192.168.1.240/StudyStore/IT技术培训视频/
        String remoteFileName = args[1];
        // G:/MyDocument/
        String localDirectoryName = args[2];

        try {
            String remoteFileUrl = "smb://" + remoteFileName;
            SmbFile remoteFile = new SmbFile(remoteFileUrl);
            if (requestType.equals(SEARCH) || requestType.equals(DOWNLOAD)) {
                createFile(remoteFile, localDirectoryName, requestType.equals(DOWNLOAD));
            }
            if (requestType.equals(CHECK) || requestType.equals(DELETE)) {
                deleteFile(remoteFile, localDirectoryName, requestType.equals(DELETE));
            }
        } catch (Exception e) {
            System.err.println("访问远程文件出错：" + e.getMessage());
        }
    }

    private static void createFile(SmbFile remoteFile, String localDirectoryName, boolean creatable) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (remoteFile.isDirectory()) {
                String subLocalDirectoryName = localDirectoryName + remoteFile.getName();
                File localDirectory = new File(subLocalDirectoryName);
                if (!localDirectory.exists()) {
                    if (localDirectory.mkdir()) {
                        System.out.println(df.format(new Date()) + " 创建文件夹：" + subLocalDirectoryName);
                    }
                }
                for (SmbFile subFile : remoteFile.listFiles()) {
                    createFile(subFile, subLocalDirectoryName, creatable);
                }
            } else {
                System.out.println(df.format(new Date()) + " 搜索到文件：" + remoteFile.getUncPath() + " " + remoteFile.length() / 1024 / 1024 + "M");
                File localFile = new File(localDirectoryName + remoteFile.getName());
                if (!localFile.exists()) {
                    if (creatable) {
                        System.out.println(df.format(new Date()) + " 开始下载文件：" + localDirectoryName + remoteFile.getName());
                        try {
                            Files.copy(new SmbFileInputStream(remoteFile), Paths.get(localDirectoryName + remoteFile.getName()));
                        } catch (Exception e) {
                            System.out.println(df.format(new Date()) + " 拷贝远程文件到本地目录失败：" + e.getMessage());
                            e.printStackTrace();
                        }
                        System.out.println(df.format(new Date()) + " 下载成功");
                    } else {
                        System.out.println(df.format(new Date()) + " 本地文件不存在");
                    }
                } else {
                    System.out.println(df.format(new Date()) + " 本地文件：" + localFile.getPath() + localFile.getName() + " " + localFile.length() / 1024 / 1024 + "M");
                }
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(SmbFile remoteFile, String localDirectoryName, boolean deletable) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (remoteFile.isDirectory()) {
                String subLocalDirectoryName = localDirectoryName + remoteFile.getName();
                for (SmbFile subFile : remoteFile.listFiles()) {
                    deleteFile(subFile, subLocalDirectoryName, deletable);
                }
            } else {
                File localFile = new File(localDirectoryName + remoteFile.getName());
                if (localFile.exists()) {
                    if (localFile.length() != remoteFile.length()) {
                        System.out.println(df.format(new Date()) + " 当前目录：" + localDirectoryName);
                        System.out.println(df.format(new Date()) + " 远程文件：" + remoteFile.getName() + " " + remoteFile.length() / 1024 / 1024 + "M");
                        System.out.println(df.format(new Date()) + " 本地文件：" + localFile.getName() + " " + localFile.length() / 1024 / 1024 + "M");
                        if (deletable) {
                            System.out.println(df.format(new Date()) + " 文件非正常，准备删除：" + localFile.getName());
                            if (localFile.delete()) {
                                System.out.println(df.format(new Date()) + " 删除成功");
                            }
                        }
                        System.out.println("-----------------------------------------------------------------------------------------------------------");
                    }
                } else {
                    System.out.println(df.format(new Date()) + " 远程文件：" + remoteFile.getUncPath() + " " + remoteFile.length() / 1024 / 1024 + "M");
                    System.out.println(df.format(new Date()) + " 本地文件不存在");
                    System.out.println("-----------------------------------------------------------------------------------------------------------");
                }
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}
