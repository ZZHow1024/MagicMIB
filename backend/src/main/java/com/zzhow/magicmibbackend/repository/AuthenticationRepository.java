package com.zzhow.magicmibbackend.repository;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 认证信息存储库
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Data
@Component
public class AuthenticationRepository {
    public static String address = "127.0.0.1";
    public static int port = 161;
    public static String readCommunity = "public";
    public static String writeCommunity = "public";
    public static int snmpVersion = 1;
}
