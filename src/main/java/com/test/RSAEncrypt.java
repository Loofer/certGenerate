package com.test;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class RSAEncrypt {

    /**
     * 指定加密算法为DESede
     */
    private String ALGORITHM = "RSA";

    public static void main(String[] args) throws Exception {
        // 要加密的字符串
        String source = "这是要加密的字符串";

        //签名测试
        sign(source);

        RSAEncrypt rsa = new RSAEncrypt();
        System.out.println("加密前：" + source);
        // 生成的密文
        String cryptograph = rsa.encrypt(source);
        System.out.println("加密后密文:\n" + cryptograph);
        System.out.println("密文长度: " + cryptograph.length());
        // 解密密文
        String target = rsa.decrypt(cryptograph);
        System.out.println("\n解密后明文:"+target);


    }


    /**
     * 获取私钥的对象
     *
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        InputStream is = new FileInputStream(CertGenerate.path + CertGenerate.jksName + ".jks");
        keyStore.load(is, CertGenerate.KEY_STORE_PASSWORD.toCharArray());
        PrivateKey key = (PrivateKey) keyStore.getKey(CertGenerate.KEY_ALAIS, CertGenerate.KEY_PASSWORD.toCharArray());
        return key;
    }

    /**
     * 获取公钥对象的方法一：从JKS文件中获取公钥的对象
     *
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(CertGenerate.path + CertGenerate.jksName + ".jks"), CertGenerate.KEY_PASSWORD.toCharArray());
        PublicKey pubKey = keyStore.getCertificate(CertGenerate.KEY_CERT_ALAIS).getPublicKey();
        return pubKey;
    }

    /**
     * 加密方法 source： 源数据
     */
    public String encrypt(String source) throws Exception {

        /** 将文件中的公钥对象读出 */
        try {
            PublicKey publicKey = getPublicKey();
            /** 得到Cipher对象来实现对源数据的RSA加密 */
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] b = source.getBytes();
            /** 执行加密操作 */
            byte[] b1 = cipher.doFinal(b);
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(b1);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 解密算法 cryptograph:密文
     */
    public String decrypt(String cryptograph) throws Exception {
        /** 将文件中的私钥对象读出 */
        try {
            PrivateKey privateKey = getPrivateKey();
            /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b1 = decoder.decodeBuffer(cryptograph);
            /** 执行解密操作 */
            byte[] b = cipher.doFinal(b1);
            return new String(b);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 签名、验签
     */
    public static void sign(String data) {

        try {
            PrivateKey privateKey = getPrivateKey();
            Signature dsa = Signature.getInstance("MD5withRSA");
            dsa.initSign(privateKey);
            dsa.update(data.getBytes());
            byte[] sig = dsa.sign();
            String hexStr = bytesToHexString(sig);
            System.out.println("签名字符串：\n" + hexStr);

            PublicKey publicKey = getPublicKey();
            Signature dsa1 = Signature.getInstance("MD5withRSA");
            dsa1.initVerify(publicKey);
            dsa1.update(data.getBytes());
            byte[] _sig = hexStringToBytes(hexStr);

            System.out.println("验证签名结果：" + dsa1.verify(_sig));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }




}
