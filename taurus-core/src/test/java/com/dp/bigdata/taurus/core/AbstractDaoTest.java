package com.dp.bigdata.taurus.core;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * TableCreatorTest
 * 
 * @author damon.zhu
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext-test.xml" })
public abstract class AbstractDaoTest {

    @Autowired
    protected SqlSessionFactoryBean sqlSessionFactoryBean;

    //@Before
    public void loadDatabase() {
        createTable();
        loadData();
    }

    public void createTable() {
        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
            SqlSession session = sqlSessionFactory.openSession();
            session.update("createHost");
            session.update("createInstanceCounter");
            session.update("createPool");
            session.update("createTask");
            session.update("createTaskAttempt");
            session.update("createTaskIDCounter");
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void loadData();

    @After
    public void destroyDatabase() {
        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = sqlSessionFactoryBean.getObject();
            SqlSession session = sqlSessionFactory.openSession();
            session.delete("dropHost");
            session.update("dropInstanceCounter");
            session.update("dropPool");
            session.update("dropTask");
            session.update("dropTaskAttempt");
            session.update("dropTaskIDCounter");
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
