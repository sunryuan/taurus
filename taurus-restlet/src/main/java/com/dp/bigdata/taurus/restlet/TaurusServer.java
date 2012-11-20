package com.dp.bigdata.taurus.restlet;

import org.restlet.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.dp.bigdata.taurus.core.Engine;

/**
 * TaurusRestletServer mode: standalone | all
 * 
 * @author damon.zhu
 */
public class TaurusServer {
    public static final String ALONE = "standalone";
    public static final String ALL = "all";

    public static void main(String args[]) {
        ApplicationContext context = new FileSystemXmlApplicationContext("classpath:applicationContext-core.xml",
                "classpath:applicationContext-restlet.xml");
        Engine engine = (Engine) context.getBean("engine");
        Component restlet = (Component) context.getBean("component");
        if (args.length == 1) {
            if (args[0].equals(ALONE)) {
                try {
                    restlet.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals(ALL)) {
                try {
                    restlet.start();
                    engine.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
