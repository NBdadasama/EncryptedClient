package com.middleware.demo.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import java.security.KeyPair;

public class SignUtil {
    public static AES generateAESKey(){
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();

        return SecureUtil.aes(key);
    }

    public static RSA generateRSAKey(){
        //随机生成密钥
        KeyPair pair = SecureUtil.generateKeyPair("RSA");

        return SecureUtil.rsa(pair.getPrivate().getEncoded(),pair.getPublic().getEncoded());
    }
}
