package com.limon.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationScaner {

    private Annotation annotation;

    public AnnotationScaner(Annotation annotation){
        this.annotation=annotation;
    }

    public synchronized Set<Class<?>> init(String packageName) throws Exception {
        Set<Class<?>> classRepository=new LinkedHashSet<>();
        scanClassInPackage(packageName.replaceAll("\\.","/"),classRepository);
        return classRepository;
    }

    public void scanClassInPackage(String packagePath,Set<Class<?>> classRepository) throws Exception {

        URL packageUrl=Thread.currentThread().getContextClassLoader().getResource(packagePath);

        if(packageUrl.getProtocol().equals("file")){
            File packageDir=new File(packageUrl.toURI());
            if(!packageDir.isDirectory()){
                throw new Exception("Viewer just could scan directory!");
            }

            File[] childFiles=packageDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if(file.isDirectory())
                        return true;
                    else{
                        String suffix=getFileSuffix(file);
                        if(suffix!=null) {
                            switch (suffix) {
                                case "class":
                                    return true;
                                case "jar":
                                    return true;
                            }
                        }
                    }
                    return false;
                }
            });

            Arrays.stream(childFiles).forEach(file->{
                String suffix=getFileSuffix(file);
                switch (suffix){
                    case "jar":
                        classRepository.addAll(getClassesFromJar(packagePath+"/"+file.getName()));
                        break;
                    case "class":
                        try {
                            String className=packagePath.replaceAll("/",".")+"."+file.getName().replace(".class","");
                            classRepository.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                        } catch (ClassNotFoundException e) {
                            log.error("Class "+packagePath+"/"+file.getName()+" load into classLoader error!");
                            e.printStackTrace();
                        }finally {
                            break;
                        }
                    case "":
                        try {
                            scanClassInPackage(packagePath+"/"+file.getName(),classRepository);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }finally {
                            break;
                        }
                }
            });
        }else{
            throw new Exception("Illegal dir protocol!");
        }
    }

    private static String getFileSuffix(File file){
        if(file.isDirectory())
            return "";
        String fileName=file.getName();
        if(fileName.contains(".")) {
            String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
            return suffix;
        }
        return null;
    }

    private Set<Class<?>> getClassesFromJar(String jarPackageName){
        Set<Class<?>> classSet=new LinkedHashSet<>();
        JarFile jar;
        try {
            URL jarUrl = Thread.currentThread().getContextClassLoader().getResource(jarPackageName.replaceAll("/","\\."));
            jar=((JarURLConnection)jarUrl.openConnection()).getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jar.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if(jarEntryName.contains(jarPackageName) && !jarEntryName.equals(jarPackageName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    String clazzName = jarEntry.getName().replace("/", "\\.");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    getClassesFromJar(prefix);
                }
                if(jarEntry.getName().endsWith("\\.class")){
                    Class<?> clazz = null;
                    try {
                        clazz = Thread.currentThread().getContextClassLoader().loadClass(jarEntry.getName().replace("/", "\\.").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    classSet.add(clazz);
                }
            }

        }
        return classSet.stream().filter(clazz->clazz.getAnnotation(annotation.getClass())!=null).collect(Collectors.toSet());
    }
}
