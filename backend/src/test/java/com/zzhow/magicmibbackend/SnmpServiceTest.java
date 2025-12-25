package com.zzhow.magicmibbackend;

import com.zzhow.magicmibbackend.config.SnmpConfiguration;
import com.zzhow.magicmibbackend.result.Result;
import com.zzhow.magicmibbackend.service.impl.SnmpServiceImpl;
import com.zzhow.magicmibbackend.util.Application;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SNMP 服务类的测试类
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/12/18
 */
@SpringBootTest(classes = Application.class)
@Import(SnmpConfiguration.class)
class SnmpServiceTest {

    @Autowired
    private SnmpServiceImpl snmpServiceImpl;

    private static final String TEST_AGENT_IP = "127.0.0.1"; // Agent IP
    private static final Integer TEST_AGENT_PORT = 161; // Agent PORT
    private static final String TEST_COMMUNITY = "network"; // Community
    private static final String SYS_DESCR_OID = "1.3.6.1.2.1.1.1.0"; // OID(System Description)

    @Test
    @DisplayName("测试成功的SNMP Get请求")
    void testSnmpGet() {
        try {
            Result<String> stringResult = snmpServiceImpl.performSnmpGet(TEST_AGENT_IP, TEST_AGENT_PORT, SYS_DESCR_OID, TEST_COMMUNITY);
            System.out.println("成功获取 sysDescr：" + stringResult.getData());
        } catch (Exception e) {
            fail("SNMP Get通信失败，异常信息：" + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试成功的SNMP GetNext请求")
    void testSnmpGetNext() {
        try {
            Result<String> stringResult = snmpServiceImpl.performSnmpGetNext(TEST_AGENT_IP, TEST_AGENT_PORT, SYS_DESCR_OID, TEST_COMMUNITY);
            System.out.println("成功获取 sysDescr：" + stringResult.getData());
        } catch (Exception e) {
            fail("SNMP Get通信失败，异常信息：" + e.getMessage());
        }
    }
}
