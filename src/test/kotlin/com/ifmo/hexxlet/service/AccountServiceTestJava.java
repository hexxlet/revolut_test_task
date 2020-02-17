package com.ifmo.hexxlet.service;

import com.ifmo.hexxlet.exception.MoneyTransferException;
import com.ifmo.hexxlet.model.AccountVo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;

import static com.ifmo.hexxlet.ConfigurationKt.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AccountServiceTestJava {

    private static AccountService accountService;

    @BeforeClass
    public static void setUp() {
        Properties settings = getProperties("/h2.properties");
        initDatabase(settings);
        SqlSessionFactory sessionFactory = configureMyBatis(settings);
        accountService = new AccountService(sessionFactory.openSession());
    }

    @Test(expected = MoneyTransferException.class)
    public void createAccountNegativeBalance() {
        AccountVo accountVo = new AccountVo("", "-500");
        accountService.createAccount(accountVo);
    }

    @Test
    public void accountIdReturned(){
        AccountVo accountVo = new AccountVo("", "100");
        accountService.createAccount(accountVo);
        //init script creates 4 accounts, test creates 5ths
        assertEquals("5", accountVo.getId());
    }
}
