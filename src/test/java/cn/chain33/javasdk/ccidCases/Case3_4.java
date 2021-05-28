package cn.chain33.javasdk.ccidCases;

import java.util.Arrays;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.gm.SM2KeyPair;
import cn.chain33.javasdk.model.gm.SM2Util;
import cn.chain33.javasdk.model.gm.SM3Util;
import cn.chain33.javasdk.model.gm.SM4Util;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 3.4	加密算法
 * @author fkeit
 *
 */
public class Case3_4 {

	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
    
	Account account = new Account();
    
    String content = "{\"档案编号\":\"ID0000001\",\"企业代码\":\"QY0000001\",\"业务标识\":\"DA000001\",\"来源系统\":\"OA\", \"文档摘要\",\"0x93689a705ac0bb4612824883060d73d02534f8ba758f5ca21a343beab2bf7b47\"}";
     
    
    /**
     * 3.4.1 链上内容的加密传输与存储
     * @throws Exception 
     */
    @Test
    public void Case3_4_1() throws Exception {
    	
    	// 存证智能合约的名称（简单存证，固定就用这个名称）
		String execer = "user.write";
		// 合约地址
		String contractAddress = client.convertExectoAddr(execer);
		
		// 获取签名用的私钥
		Account account = new Account();
		AccountInfo accountInfo = account.newAccountLocal();
		String privateKey = accountInfo.getPrivateKey();		
		
		System.out.println("==================sha256后上链 开始==========================");
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
		String txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, contentHash);
		String hash = client.submitTransaction(txEncode);
		System.out.println("sha256存证has: " + hash);
		System.out.println("==================sha256后上链 结束==========================");
		
		System.out.println("==================sm3上链 开始==========================");
		privateKey = accountInfo.getPrivateKey();
		contentHash = SM3Util.hash(content.getBytes());
		txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, contentHash);
		hash = client.submitTransaction(txEncode);
		System.out.println("sm3存证hash:" + hash);
		System.out.println("==================sm3上链 结束==========================");
		
		System.out.println("==================AES加密后上链 开始==========================");
		// 生成AES加密KEY
		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(aesKeyHex);
		// 生成iv
		byte[] iv = AesUtil.generateIv();
		// 对明文进行加密
		byte[] encrypt = AesUtil.encrypt(content, key, iv);
		txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, encrypt);
		hash = client.submitTransaction(txEncode);
		System.out.println("AES加密存证hash:" + hash);
		
		Thread.sleep(10000);
		
		// 查询结果
		queryAesStorage(hash);
		
		System.out.println("==================AES加密后上链 结束==========================");
		
		System.out.println("==================SM4加密后上链 开始==========================");
		// 生成SM4加密KEY
		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		key = HexUtil.fromHexString(sm4KeyHex);

		// 生成iv
		iv = SM4Util.generateKey();
		// 对明文进行加密
		byte[] cipherText = SM4Util.encryptECB(key, content.getBytes());
		txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, cipherText);
		hash = client.submitTransaction(txEncode);
		System.out.println("SM4加密上链hash:" + hash);
		
		Thread.sleep(5000);
		// 查询结果
		querySM4Storage(hash);
		
		System.out.println("==================SM4加密后上链 结束==========================");
    	
    }
    
    
//    /**
//     * 3.4.1 链上内容的加密传输与存储
//     * @throws Exception 
//     */
//    @Test
//    public void Case3_4_1() throws Exception {
//    	
//		// 存证智能合约的名称
//		String execer = "storage";
//		
//   		System.out.println("=========================创建用户并赋予权限开始===========================");
//   		String accountId = "testAccount199";
//
//   		AccountInfo accountInfo = account.newAccountLocal();
//
//   		
//   		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
//   		
//		String[] accountIds = new String[]{accountId};
//		// 1为冻结，2为解冻，3增加有效期,4为授权
//		String op = "4";
//		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
//		String level = "2";
//		CommUtil.manageAccount(client, accountIds, op, level);
//   		
//   		System.out.println("=========================创建用户并赋予权限结束===========================");
//		
//		System.out.println("==================sha256后上链 开始==========================");
//		String privateKey = accountInfo.getPrivateKey();
//		byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
//		String txEncode = StorageUtil.createHashStorage(contentHash, execer, privateKey);
//		String submitTransaction = client.submitTransaction(txEncode);
//		System.out.println(submitTransaction);
//		System.out.println("==================sha256后上链 结束==========================");
//		
//		System.out.println("==================sm3上链 开始==========================");
//		privateKey = accountInfo.getPrivateKey();
//		contentHash = SM3Util.hash(content.getBytes());
//		txEncode = StorageUtil.createHashStorage(contentHash, execer, privateKey);
//		submitTransaction = client.submitTransaction(txEncode);
//		System.out.println("sm3上链hash:" + submitTransaction);
//		System.out.println("==================sm3上链 结束==========================");
//		
//		System.out.println("==================AES加密后上链 开始==========================");
//		// 生成AES加密KEY
//		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
//		//可用该方法生成 AesUtil.generateDesKey(128);
//		byte[] key = HexUtil.fromHexString(aesKeyHex);
//		System.out.println("key:" + HexUtil.toHexString(key));
//		// 生成iv
//		byte[] iv = AesUtil.generateIv();
//		// 对明文进行加密
//		byte[] encrypt = AesUtil.encrypt(content, key, iv);
//		contentHash = TransactionUtil.Sha256(content.getBytes("utf-8"));
//		txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, privateKey);
//		submitTransaction = client.submitTransaction(txEncode);
//		System.out.println("AES加密上链hash:" + submitTransaction);
//		
//		Thread.sleep(10000);
//		
//		// 查询结果
//		queryAesStorage(submitTransaction);
//		
//		System.out.println("==================AES加密后上链 结束==========================");
//		
//		System.out.println("==================SM4加密后上链 开始==========================");
//		// 生成SM4加密KEY
//		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
//		//可用该方法生成 AesUtil.generateDesKey(128);
//		key = HexUtil.fromHexString(sm4KeyHex);
//		System.out.println("key:" + HexUtil.toHexString(key));
//		// 生成iv
//		iv = SM4Util.generateKey();
//		// 对明文进行加密
//		byte[] cipherText = SM4Util.encryptCBC(key, iv, content.getBytes());
//		System.out.println("SM4 CBC Padding encrypt result:\n" + Arrays.toString(cipherText));
//		contentHash = SM3Util.hash(content.getBytes("utf-8"));
//		txEncode = StorageUtil.createEncryptNotaryStorage(cipherText,contentHash, iv, "", "", execer, privateKey);
//		submitTransaction = client.submitTransaction(txEncode);
//		System.out.println("SM4加密上链hash:" + submitTransaction);
//		
//		Thread.sleep(10000);
//		// 查询结果
//		querySM4Storage(submitTransaction);
//		
//		System.out.println("==================SM4加密后上链 结束==========================");
//    	
//    }
    
    /**
     * 3.4.2	平台加密算法的支持
     * @throws Exception 
     */
    @Test
    public void Case3_4_2() {

        	// 存证智能合约的名称（简单存证，固定就用这个名称）
    		String execer = "user.write";
    		
            SM2KeyPair keyPair = SM2Util.generateKeyPair();
            System.out.println("SM2公钥:" + keyPair.getPublicKeyString() + " \r\nSM2私钥:" + keyPair.getPrivateKeyString());
            String txEncode = TransactionUtil.createTx(keyPair.getPrivateKey().toByteArray(), execer.getBytes(), content.getBytes(), SignType.SM2, TransactionUtil.DEFAULT_FEE);
    		String hash;
			try {
				hash = client.submitTransaction(txEncode);
	    		System.out.println(hash);
			} catch (Exception e) {
				e.printStackTrace();
			}

    }
    
    
//    @Test
//    public void Case3_4_2() {
//
//        try {
//        	// 存证智能合约的名称（简单存证，固定就用这个名称）
//    		String execer = "user.write";
//    		
//            SM2KeyPair keyPair = SM2Util.generateKeyPair();
//
//            StorageProtobuf.ContentOnlyNotaryStorage.Builder contentOnlyNotaryStorageBuilder = StorageProtobuf.ContentOnlyNotaryStorage.newBuilder();
//            contentOnlyNotaryStorageBuilder.setContent(ByteString.copyFrom("sm2 tx sign test".getBytes()));//内容小于512k;
//            cn.chain33.javasdk.model.protobuf.StorageProtobuf.StorageAction.Builder storageActionBuilder = StorageProtobuf.StorageAction.newBuilder();
//            storageActionBuilder.setContentStorage(contentOnlyNotaryStorageBuilder.build());
//            storageActionBuilder.setTy(StorageEnum.ContentOnlyNotaryStorage.getTy());
//            StorageProtobuf.StorageAction storageAction = storageActionBuilder.build();
//
//            
//            String txEncode = TransactionUtil.createTx(keyPair.getPrivateKey().toByteArray(), execer.getBytes(), content.getBytes(), SignType.SM2, TransactionUtil.DEFAULT_FEE);
//    		String hash = client.submitTransaction(txEncode);
//    		
//            String transactionHash = TransactionUtil.createTxWithCert(HexUtil.toHexString(keyPair.getPrivateKey().toByteArray()), "storage", storageAction.toByteArray(), SignType.SM2, "".getBytes(), "ca test".getBytes());
//            String hash = client.submitTransaction(transactionHash);
//            Assert.assertNotNull(hash);
//            System.out.println(hash);
//           } catch (Exception ex) {
//            ex.printStackTrace();
//            Assert.fail();
//        }
//    }
    
    
    /**
     * 查询AES
     * @throws Exception 
     */
	public void queryAesStorage(String hash) throws Exception {
		QueryTransactionResult queryTransaction;
		queryTransaction = client.queryTransaction(hash);
		
        //隐私存证
        String desKey = "ba940eabdf09ee0f37f8766841eee763";
        String result = queryTransaction.getTx().getRawpayload();
		if(result.startsWith("0x")) {
			result = result.substring(2);
		}
        byte[] fromHexString = HexUtil.hexStringToBytes(result);
        System.out.println(Arrays.toString(fromHexString));
        String decrypt = AesUtil.decrypt(fromHexString, desKey);
        System.out.println("AES解密结果：" + decrypt);
     
	}
    
//    /**
//     * 查询AES
//     * @throws Exception 
//     */
//	public void queryAesStorage(String hash) throws Exception {
//		// contentStore
//		JSONObject resultJson = client.queryStorage(hash);
//		
//		JSONObject resultArray;
//        //隐私存证
//        String desKey = "ba940eabdf09ee0f37f8766841eee763";
//        resultArray = resultJson.getJSONObject("encryptStorage");
//        String content = resultArray.getString("encryptContent");
//        byte[] fromHexString = HexUtil.fromHexString(content);
//        String decrypt = AesUtil.decrypt(fromHexString, desKey);
//        System.out.println("AES解密结果：" + decrypt);
//     
//	}
	
	
    /**
     * 查询SM4
     * @throws Exception 
     */
	public void querySM4Storage(String hash) throws Exception {
		QueryTransactionResult queryTransaction;
		queryTransaction = client.queryTransaction(hash);
		
		// 生成SM4加密KEY
		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(sm4KeyHex);
        String result = queryTransaction.getTx().getRawpayload();
		if(result.startsWith("0x")) {
			result = result.substring(2);
		}
        //隐私存证
        byte[] fromHexString = HexUtil.hexStringToBytes(result);
        byte[] decryptedData = SM4Util.decryptECB(key, fromHexString);
        
        System.out.println("SM4解密结果:" + new String(decryptedData));
     
	}
	
    
//    /**
//     * 查询SM4
//     * @throws Exception 
//     */
//	public void querySM4Storage(String hash) throws Exception {
//		// contentStore
//		JSONObject resultJson = client.queryStorage(hash);
//		
//		JSONObject resultArray;
//		// 生成SM4加密KEY
//		String sm4KeyHex = "ba940eabdf09ee0f37f8766841eee763";
//		//可用该方法生成 AesUtil.generateDesKey(128);
//		byte[] key = HexUtil.fromHexString(sm4KeyHex);
//		System.out.println("key:" + HexUtil.toHexString(key));
//		// 生成iv
//		byte[] iv = SM4Util.generateKey();
//        //隐私存证
//        resultArray = resultJson.getJSONObject("encryptStorage");
//        String content = resultArray.getString("encryptContent");
//        byte[] fromHexString = HexUtil.fromHexString(content);
//        byte[] decryptedData = SM4Util.decryptCBC(key, iv, fromHexString);
//        
//        System.out.println("SM4解密结果:" + new String(decryptedData));
//     
//	}
    
}
