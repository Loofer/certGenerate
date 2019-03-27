package com.test;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertGenerate {


    public static final String KEY_STORE_PASSWORD = "2078888";
    public static final String KEY_PASSWORD = "2078888";

    public static final String KEY_ALAIS = "mykey";
    public static final String KEY_CERT_ALAIS = "single_cert";

    public static final String path = "D:/";
    public static final String jksName = "user_test";
    public static final String pfxName = "user_test";

    public static final String publicKey = "D:/public.txt";
    public static final String publicPath = "D:/pub";


    public static void main(String[] args) {

        CertGenerate certGenerate = new CertGenerate();
        // 生成公钥
        boolean createPublicKey = certGenerate.createPublicKey();
        System.out.println("PUBLIC KEY CREATE OK, result==" + createPublicKey);

        boolean createPublicKeyBYDecode = certGenerate.createPublicKeyBYDecode();
        System.out.println("PUBLIC KEY BY BASE64Encoder CREATE OK, result==" + createPublicKeyBYDecode);

        boolean createPrivateKey = certGenerate.createPrivateKey();
        System.out.println("PRIVATE KEY CREATE OK, result==" + createPrivateKey);

        Boolean pfx = certGenerate.toPFX();
        System.out.println("transToPFX OK, result==" + pfx);
    }


    /**
     * 公钥方法
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     * 创建空的jks文件 String[]
     * info长度为9，分别是{cn,ou,o,c,l,st,starttime,endtime,serialnumber}
     */
    public void generateJKS(String path, String jksName) {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(null, null);
            keyStore.store(new FileOutputStream(path + jksName + ".jks"), KEY_STORE_PASSWORD.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用空的jks创建自己的jks String[]
     * info长度为9，分别是{cn,ou,o,c,l,st,starttime,endtime,serialnumber}
     */
    public void storeJKS(String path, String jksName) {
        KeyStore keyStore;
        try {
            // use exited jks file
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(path + jksName + ".jks"), KEY_STORE_PASSWORD.toCharArray());

            CertGenerateUtil.CertResult result = CertGenerateUtil.generateCaCert("china");
            X509Certificate cert = result.getCertificate();

            X509Certificate[] chain = new X509Certificate[1];
            chain[0] = cert;
            keyStore.setKeyEntry(KEY_ALAIS, result.getPrivateKey(), KEY_PASSWORD.toCharArray(), chain);
            keyStore.setCertificateEntry(KEY_CERT_ALAIS, cert);
            keyStore.store(new FileOutputStream(path + jksName + ".jks"), KEY_STORE_PASSWORD.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 转换成pfx格式
     *
     * @param info
     */
    public Boolean toPFX() {
        try {
            String pfxKeystoreFile = path + pfxName + ".pfx";
            String jkxKeystoreFile = path + jksName + ".jks";
            KeyStore inputKeyStore = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(jkxKeystoreFile);
            char[] nPassword;
            nPassword = KEY_STORE_PASSWORD.toCharArray();
            inputKeyStore.load(fis, nPassword);
            fis.close();
            KeyStore outputKeyStore = KeyStore.getInstance("PKCS12");
            outputKeyStore.load(null, KEY_STORE_PASSWORD.toCharArray());
            Enumeration enums = inputKeyStore.aliases();
            while (enums.hasMoreElements()) {
                String keyAlias = (String) enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, nPassword);
                    java.security.cert.Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);
                    outputKeyStore.setKeyEntry(keyAlias, key, KEY_STORE_PASSWORD.toCharArray(), certChain);
                }
            }
            FileOutputStream out = new FileOutputStream(pfxKeystoreFile);
            outputKeyStore.store(out, nPassword);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("toPFX :" + e.getMessage());
            return false;
        }
    }

    public boolean createPublicKey() {
        try {
            CertGenerateUtil.CertResult result = CertGenerateUtil.generateCaCert("china");
            X509Certificate cert = result.getCertificate();

            String certPath = publicPath + ".cer";
            FileOutputStream fos = new FileOutputStream(certPath);
            BASE64Encoder encoder = new BASE64Encoder();
            String string = encoder.encode(cert.getEncoded());

            FileOutputStream publicOut = new FileOutputStream(publicKey);
            publicOut.write(string.getBytes());
            publicOut.close();
            System.out.println(string);

            fos.write(cert.getEncoded());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("public key :" + e.getMessage());
            return false;
        }
    }

    public boolean createPublicKeyBYDecode() {
        try {
            CertGenerateUtil.CertResult result = CertGenerateUtil.generateCaCert("china");
            String certPath = publicPath + "_base.cer";
            FileWriter wr = new java.io.FileWriter(new File(certPath));
            String encode = new BASE64Encoder().encode(result.getCertificate().getEncoded());
            String strCertificate = "-----BEGIN CERTIFICATE-----\r\n" + encode + "\r\n-----END CERTIFICATE-----\r\n";
            // 给证书编码
            wr.write(strCertificate);
            wr.flush();
            wr.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("public key :" + e.getMessage());
            return false;
        }
    }

    public boolean createPrivateKey() {
        generateJKS(path, jksName);
        storeJKS(path, jksName);
        return true;
    }


}
