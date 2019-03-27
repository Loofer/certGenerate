package com.test;

import com.sun.org.glassfish.gmbal.Description;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CertGenerateUtil {

    /**
     * 一般的数字证书产品的主题通常含有如下字段：
     * 公用名称 (Common Name) 简称：CN 字段，对于 SSL 证书，一般为网站域名或IP地址；而对于代码签名证书则为申请单位名称；而对于客户端证书则为证书申请者的姓名；
     * 单位名称 (Organization Name) ：简称：O 字段，对于 SSL 证书，一般为网站域名；而对于代码签名证书则为申请单位名称；而对于客户端单位证书则为证书申请者所在单位名称；
     * 证书申请单位所在地：
     * 所在城市 (Locality) 简称：L 字段
     * 所在省份 (State/Provice) 简称：S 字段
     * 所在国家 (Country) 简称：C 字段，只能是国家字母缩写，如中国：CN
     * 其他一些字段：
     * 电子邮件 (Email) 简称：E 字段
     * 多个姓名字段 简称：G 字段
     * 介绍：
     * Description 字段
     * 电话号码：Phone 字段，格式要求 + 国家区号 城市区号 电话号码，如： +86 732 88888888
     * 地址：STREET  字段
     * 邮政编码：PostalCode 字段
     * 显示其他内容 简称：OU 字段
     */

    private static final String ISSUER_TEMPLATE = "C=CN, ST=HN, L=ZZ, CN={caHost}";
    private static final String SUBJECT_TEMPLATE = "C=CN, ST=HN, L=ZZ, CN={host}";

    private static KeyPair DEFAULT_KEY_PAIR;

    static {
        try {
            DEFAULT_KEY_PAIR = generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();
    }

    public static CertResult generateCaCert(String caHost) throws Exception {
        KeyPair caKeyPair = generateKeyPair();
        JcaX509v3CertificateBuilder certBuilder = generateCertBuilder(caHost, caHost, caKeyPair.getPublic());
        certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caKeyPair.getPrivate());
        return new CertResult(
                caKeyPair.getPrivate(),
                new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer))
        );
    }

    public static CertResult generateCert(String caHost, String host, PrivateKey caPriKey) throws Exception {
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(caPriKey);
        return new CertResult(
                DEFAULT_KEY_PAIR.getPrivate(),
                new JcaX509CertificateConverter().getCertificate(generateCertBuilder(caHost, host, DEFAULT_KEY_PAIR.getPublic()).build(signer))
        );
    }


    /**
     * 根据如下参数获取对应base64编码格式的证书文件字符串
     * issuerName 与 reqName 对象是同一个则认为生成的是CA证书
     *
     * @param caHost         颁发者信息
     * @param caHost         请求证主题信息
     *                       <br> issuerName == reqName ---> CA
     * @param serial         证书序列号
     *                       <br>eg: BigInteger serial = BigInteger.valueOf(System.currentTimeMillis() / 1000);
     * @param notBefore      有效期开始时间  2018-08-01 00:00:00
     * @param notAfter       有效期截至时间   2028-08-01 00:00:00
     * @param userPublicKey  请求者主题公钥信息
     * @param rootPrivateKey 颁发者私钥信息
     * @return String
     * @throws CertificateException
     */
    private static JcaX509v3CertificateBuilder generateCertBuilder(String caHost, String host, PublicKey publicKey) throws CertIOException {
        String issuer = ISSUER_TEMPLATE.replace("{caHost}", caHost);
        String subject = SUBJECT_TEMPLATE.replace("{host}", host);
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                new X500Name(issuer),
                BigInteger.valueOf(System.currentTimeMillis() / 1000),
                Date.from(Instant.now().plus(30, ChronoUnit.DAYS)),
                Date.from(Instant.now().plus(3650, ChronoUnit.DAYS)),
                new X500Name(subject),
                publicKey
        );

        certBuilder.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(new GeneralName[]{new GeneralName(GeneralName.dNSName, host)}));
        return certBuilder;
    }

    public static class CertResult {

        private PrivateKey privateKey;

        private X509Certificate certificate;

        CertResult(PrivateKey privateKey, X509Certificate certificate) {
            this.privateKey = privateKey;
            this.certificate = certificate;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }
    }

}
