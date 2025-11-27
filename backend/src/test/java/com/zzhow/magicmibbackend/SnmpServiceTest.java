package com.zzhow.magicmibbackend;

import com.zzhow.magicmibbackend.config.SnmpConfiguration;
import com.zzhow.magicmibbackend.service.SnmpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ZZHow
 * create 2025/11/27
 * update 2025/11/27
 */
@SpringBootTest
@Import(SnmpConfiguration.class)
class SnmpServiceTest {

    @Autowired
    private SnmpService snmpService;

    private static final String TEST_AGENT_IP = "127.0.0.1"; // Agent IP
    private static final String TEST_COMMUNITY = "network"; // Community
    private static final String SYS_DESCR_OID = "1.3.6.1.2.1.1.1.0"; // OID(System Description)

    @Test
    @DisplayName("测试成功的SNMP GET请求")
    void testSnmpGet() {
        try {
            String result = snmpService.performSnmpGet(TEST_AGENT_IP, SYS_DESCR_OID, TEST_COMMUNITY);
            System.out.println("成功获取 sysDescr: " + result);
        } catch (Exception e) {
            fail("SNMP GET通信失败，异常信息: " + e.getMessage());
        }
    }
}
