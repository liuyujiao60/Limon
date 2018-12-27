package com.limon.core;

import com.limon.core.ioc.BeanGraftedFactory;
import com.limon.core.proxy.ProxyHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanProvider {

    private Map<Class,Object> classProxyMap;

    @Value("#limonConfig[scanPackage]")
    private String scanPackageConfig;

    public BeanProvider(String scanPackageConfig) throws Exception {
        this.scanPackageConfig=scanPackageConfig;
        classProxyMap=new HashMap<>();
        this.init();
    }

    public void init() throws Exception {
        ClassScaner classScaner=new ClassScaner();
        String[] packageList=scanPackageConfig.split(",");
        for(String packageName:packageList){
            Set<Class<?>> classSet=classScaner.init(packageName);
            classSet.stream().forEach(clazz->{
                if(!clazz.isInterface()&&!clazz.isEnum()) {
                    try {
                        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), clazz.getInterfaces(),
                                ProxyHandlerFactory.createClassInvocationHandler(clazz));
                        Arrays.asList(clazz.getInterfaces()).stream().forEach(interfaze -> {
                            classProxyMap.put(interfaze, proxyInstance);
                        });
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error("proxy class " + clazz.getName() + " error! ");
                    }
                }
            });
        }

        BeanGraftedFactory beanGraftedFactory=new BeanGraftedFactory();
        beanGraftedFactory.graftAllBean(this);
    }

    public <T> T getBean(Class<T> classType){
        return (T) classProxyMap.get(classType);
    }

    public Set<Class> getRPCServiceList(){
        return classProxyMap.keySet();
    }
}
