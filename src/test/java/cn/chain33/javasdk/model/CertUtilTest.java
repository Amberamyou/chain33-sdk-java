package cn.chain33.javasdk.model;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.cert.CertObject;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.protobuf.CertService;
import cn.chain33.javasdk.utils.CertUtils;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;
import com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Test;

public class CertUtilTest {
    static RpcClient certclient = new RpcClient("http://2.20.105.227:11901");
    static RpcClient chain33client = new RpcClient("http://2.20.105.227:8801");

    public static final String AdminKey = "619044d303498246f8b39d8e38fe639be08ce5e213d490593ffcde8ee63beb79";
    public static final String UserName = "ycy";
    public static final String Identity = "101";
    public static final String UserPub = "03afccf0095ce8ac2021f77d032cdac1a670e77c4e7dca568f9bab935b84ab711e";
    public static final String UserKey = "03ffd7371d86246f9bf5682d4319c5767b051e2acf98f806fe724579d9124b53";

    @Test
    public void testCertUtilUser() {
        boolean result = certclient.certUserRegister(UserName, Identity, UserPub, AdminKey);
        System.out.println(result);
        Assert.assertEquals(result, true);

        CertObject.UserInfo user = certclient.certGetUserInfo(Identity, UserKey);
        System.out.println(user.name);
        Assert.assertEquals(UserName, user.name);

        result = certclient.certRevoke("", Identity, AdminKey);
        System.out.println(result);
        Assert.assertEquals(result, true);

        result = certclient.certUserRevoke(Identity, AdminKey);
        System.out.println(result);
        Assert.assertEquals(result, true);

        user = certclient.certGetUserInfo(Identity, UserKey);
        System.out.println(user);
        Assert.assertNull(user);
    }

    @Test
    public void testCertUtilEnroll() {
    	
    	String wrongCertCode = "2d2d2d2d2d424547494e2043455254494649434154452d2d2d2d2d0a4d4949426f7a434341557167417749424167495159304a4566776558336f394c4468687158314a6a6d7a414b4267677167527a5056514744645442444d5173770a435159445651514745774a44546a454c4d416b474131554543424d43576b6f78437a414a42674e5642416354416b68614d526f7747415944565151444578466a0a61474670626a4d7a4c574e684c584e6c636e5a6c636a4165467730794d4445774d6a67784e4445344e445261467730794d5441794d4455784e4445344e4452610a4d454d78437a414a42674e5642415954416b4e4f4d517377435159445651514945774a61536a454c4d416b474131554542784d4353466f78476a415942674e560a42414d5445574e6f59576c754d7a4d745932457463325679646d56794d466b77457759484b6f5a497a6a3043415159494b6f45637a31554267693044516741450a72387a7743567a6f72434168393330444c4e7242706e446e66453539796c61506d367554573453726352356a634f356f6e5a347072477245742b6f75754435640a383539566c51634936386a4f6e2f7a414842586f76364d674d42347744675944565230504151482f42415144416765414d41774741315564457745422f7751430a4d414177436759494b6f45637a315542673355445277417752414967535835753149612b736d3848774e48715a32616f7768615a634b2b74394c4938386b44420a68474b59545977434944596b384751706c7a386a3251686b754749307934453330516468667971476767723943415931504e49410a2d2d2d2d2d454e442043455254494649434154452d2d2d2d2d0a";
        boolean result = certclient.certUserRegister(UserName, Identity, UserPub, AdminKey);
        Assert.assertEquals(result, true);

        CertObject.CertEnroll cert = certclient.certEnroll(Identity, UserKey);
        Assert.assertNotNull(cert);
        Assert.assertNotNull(cert.serial);
        Assert.assertNotNull(cert.cert);

        CertObject.CertInfo certInfo = certclient.certGetCertInfo(cert.serial, UserKey);
        Assert.assertNotNull(certInfo);
        Assert.assertEquals(Identity, certInfo.identity);

        CertService.CertNormal.Builder normal = CertService.CertNormal.newBuilder();
        normal.setValue(ByteString.copyFrom("value123".getBytes()));
        normal.setKey("key123");
        CertService.CertNormal normalAction = normal.build();

        CertService.CertAction.Builder builder = CertService.CertAction.newBuilder();
        builder.setTy(CertUtils.CertActionNormal);
        builder.setNormal(normalAction);

        // 交易中带正确的证书
        byte[] reqBytes = builder.build().toByteArray();
        String transactionHash = TransactionUtil.createTxWithCert(UserKey, "cert", reqBytes, SignType.SM2, cert.getCert(), "ca test".getBytes());
        String hash = chain33client.submitTransaction(transactionHash);
        System.out.println(hash);
        
        // 交易中带格式错误的证书
        String wrongFormat = TransactionUtil.createTxWithCert(UserKey, "cert", reqBytes, SignType.SM2, "This is a wrong cert".getBytes(), "ca test".getBytes());
        String hash1 = chain33client.submitTransaction(wrongFormat);
        System.out.println(hash1);
        
        // 交易中带非法的证书
        String wrongCert = TransactionUtil.createTxWithCert(UserKey, "cert", reqBytes, SignType.SM2, HexUtil.fromHexString(wrongCertCode), "ca test".getBytes());
        String hash2 = chain33client.submitTransaction(wrongCert);
        System.out.println(hash2);



//        result = certclient.certRevoke(cert.serial, "", AdminKey);
//        Assert.assertEquals(result, true);
//
//        certInfo = certclient.certGetCertInfo(cert.serial, UserKey);
//        Assert.assertNotNull(certInfo);
//        Assert.assertEquals(1, certInfo.status);
//
//        byte[] crl = certclient.certGetCRL(Identity, UserKey);
//        Assert.assertNotNull(crl);
//
//        CertObject.CertEnroll certReEnroll = certclient.certReEnroll(Identity, AdminKey);
//        Assert.assertNotNull(certReEnroll);
//        Assert.assertNotNull(certReEnroll.serial);
//        Assert.assertNotNull(certReEnroll.cert);
//
//        certInfo = certclient.certGetCertInfo(cert.serial, UserKey);
//        Assert.assertNotNull(certInfo);
//        Assert.assertEquals(Identity, certInfo.identity);
   }

}
