package com.zzhow.magicmibbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * SNMP 服务配置类
 *
 * @author ZZHow
 * create 2025/11/27
 * update 2025/11/27
 */
@Slf4j
@Configuration
public class SnmpConfiguration {

    @Bean(destroyMethod = "close") // 应用关闭时调用 close()
    public Snmp snmp() throws IOException {
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmpInstance = new Snmp(transport);
        transport.listen();
        log.info("SNMP Listener started by Config Bean.");

        return snmpInstance;
    }
}