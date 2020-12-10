package cn.chain33.javasdk.ccidCases;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.protobuf.StorageProtobuf;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.enums.StorageEnum;
import cn.chain33.javasdk.model.gm.SM2KeyPair;
import cn.chain33.javasdk.model.gm.SM2Util;
import cn.chain33.javasdk.model.gm.SM3Util;
import cn.chain33.javasdk.model.gm.SM4Util;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 3.4	加密算法
 * @author fkeit
 *
 */
public class Case3_4 {

	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
    
	Account account = new Account();
    
    String content = "疫情发生后，NPO法人仁心会联合日本湖北总商会等四家机构第一时间向湖北捐赠3800套杜邦防护服，包装纸箱上用中文写有“岂曰无衣，与子同裳”。这句诗词出自《诗经·秦风·无衣》，翻译成白话的意思是“谁说我们没衣穿？与你同穿那战裙”。不料，这句诗词在社交媒体上引发热议，不少网民赞叹日本人的文学造诣。实际上，NPO法人仁心会是一家在日华人组织，由在日或有留日背景的医药保健从业者以及相关公司组成的新生公益组织。NPO法人仁心会事务局告诉环球时报-环球网记者，由于第一批捐赠物资是防护服，“岂曰无衣，与子同裳”恰好可以表达海外华人华侨与一线医护人员共同战胜病毒的同仇敌忾之情，流露出对同胞的守护之爱。";
        
    /**
     * 3.4.1 链上内容的加密传输与存储
     * @throws Exception 
     */
    @Test
    public void Case3_4_1() throws Exception {
    	
		// 存证智能合约的名称
		String execer = "storage";
		
   		System.out.println("=========================创建用户并赋予权限开始===========================");
   		String accountId = "testAccount99";

   		AccountInfo accountInfo = account.newAccountLocal();
//   		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
//   		System.out.println("publicKey is:" + accountInfo.getPublicKey());
//   		System.out.println("Address is:" + accountInfo.getAddress());
   		
   		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
   		
		String[] accountIds = new String[]{accountId};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "4";
		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		String level = "2";
		CommUtil.manageAccount(client, accountIds, op, level);
   		
   		System.out.println("=========================创建用户并赋予权限结束===========================");
		
		System.out.println("==================sha256后上链 开始==========================");
		String privateKey = accountInfo.getPrivateKey();
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
		String txEncode = StorageUtil.createHashStorage(contentHash, execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		System.out.println(submitTransaction);
		System.out.println("==================sha256后上链 结束==========================");
		
		System.out.println("==================sm3上链 开始==========================");
		privateKey = accountInfo.getPrivateKey();
		contentHash = SM3Util.hash(content.getBytes());
		txEncode = StorageUtil.createHashStorage(contentHash, execer, privateKey);
		submitTransaction = client.submitTransaction(txEncode);
		System.out.println("sm3上链hash:" + submitTransaction);
		System.out.println("==================sm3上链 结束==========================");
		
		System.out.println("==================AES加密后上链 开始==========================");
		// 生成AES加密KEY
		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(aesKeyHex);
		System.out.println("key:" + HexUtil.toHexString(key));
		// 生成iv
		byte[] iv = AesUtil.generateIv();
		// 对明文进行加密
		byte[] encrypt = AesUtil.encrypt(content, key, iv);
		contentHash = TransactionUtil.Sha256(content.getBytes("utf-8"));
		txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, privateKey);
		submitTransaction = client.submitTransaction(txEncode);
		System.out.println("AES加密上链hash:" + submitTransaction);
		
		Thread.sleep(5000);
		
		// 查询结果
		queryAesStorage(submitTransaction);
		
		System.out.println("==================AES加密后上链 结束==========================");
		
		System.out.println("==================SM4加密后上链 开始==========================");
		// 生成SM4加密KEY
		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		key = HexUtil.fromHexString(sm4KeyHex);
		System.out.println("key:" + HexUtil.toHexString(key));
		// 生成iv
		iv = SM4Util.generateKey();
		// 对明文进行加密
		byte[] cipherText = SM4Util.encryptCBC(key, iv, content.getBytes());
		System.out.println("SM4 CBC Padding encrypt result:\n" + Arrays.toString(cipherText));
		contentHash = SM3Util.hash(content.getBytes("utf-8"));
		txEncode = StorageUtil.createEncryptNotaryStorage(cipherText,contentHash, iv, "", "", execer, privateKey);
		submitTransaction = client.submitTransaction(txEncode);
		System.out.println("SM4加密上链hash:" + submitTransaction);
		
		Thread.sleep(5000);
		// 查询结果
		// querySM4Storage(submitTransaction);
		
		System.out.println("==================SM4加密后上链 结束==========================");
    	
    }
    
    /**
     * 3.4.2	平台加密算法的支持
     * @throws Exception 
     */
    @Test
    public void Case3_4_2() {

        try {
            SM2KeyPair keyPair = SM2Util.generateKeyPair();

            StorageProtobuf.ContentOnlyNotaryStorage.Builder contentOnlyNotaryStorageBuilder = StorageProtobuf.ContentOnlyNotaryStorage.newBuilder();
            contentOnlyNotaryStorageBuilder.setContent(ByteString.copyFrom("sm2 tx sign test".getBytes()));//内容小于512k;
            cn.chain33.javasdk.model.protobuf.StorageProtobuf.StorageAction.Builder storageActionBuilder = StorageProtobuf.StorageAction.newBuilder();
            storageActionBuilder.setContentStorage(contentOnlyNotaryStorageBuilder.build());
            storageActionBuilder.setTy(StorageEnum.ContentOnlyNotaryStorage.getTy());
            StorageProtobuf.StorageAction storageAction = storageActionBuilder.build();

            String transactionHash = TransactionUtil.createTxWithCert(HexUtil.toHexString(keyPair.getPrivateKey().toByteArray()), "storage", storageAction.toByteArray(), SignType.SM2, "".getBytes(), "ca test".getBytes());
            String hash = client.submitTransaction(transactionHash);
            Assert.assertNotNull(hash);
            System.out.println(hash);
           } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    /**
     * 查询AES
     * @throws Exception 
     */
	public void queryAesStorage(String hash) throws Exception {
		// contentStore
		JSONObject resultJson = client.queryStorage(hash);
		
		JSONObject resultArray;
        //隐私存证
        String desKey = "ba940eabdf09ee0f37f8766841eee763";
        resultArray = resultJson.getJSONObject("encryptStorage");
        String content = resultArray.getString("encryptContent");
        byte[] fromHexString = HexUtil.fromHexString(content);
        String decrypt = AesUtil.decrypt(fromHexString, desKey);
        //System.out.println("AES解密结果：" + decrypt);
     
	}
    
    /**
     * 查询SM4
     * @throws Exception 
     */
	public void querySM4Storage(String hash) throws Exception {
		// contentStore
		JSONObject resultJson = client.queryStorage(hash);
		
		JSONObject resultArray;
		// 生成SM4加密KEY
		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(sm4KeyHex);
		System.out.println("key:" + HexUtil.toHexString(key));
		// 生成iv
		byte[] iv = SM4Util.generateKey();
        //隐私存证
        resultArray = resultJson.getJSONObject("encryptStorage");
        String content = resultArray.getString("encryptContent");
        byte[] fromHexString = HexUtil.fromHexString(content);
        byte[] decryptedData = SM4Util.decryptCBC(key, iv, fromHexString);
        
        System.out.println("SM4解密结果:" + new String(decryptedData));
     
	}
    
}
