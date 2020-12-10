package cn.chain33.javasdk.ccidCases;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 3.2	账户管理
 * @author fkeit
 *
 */
public class Case3_2 {
	
	
    RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
    
	Account account = new Account();
	
	String content = "疫情发生后，NPO法人仁心会联合日本湖北总商会等四家机构第一时间向湖北捐赠3800套杜邦防护服，包装纸箱上用中文写有“岂曰无衣，与子同裳”。这句诗词出自《诗经·秦风·无衣》，翻译成白话的意思是“谁说我们没衣穿？与你同穿那战裙”。不料，这句诗词在社交媒体上引发热议，不少网民赞叹日本人的文学造诣。实际上，NPO法人仁心会是一家在日华人组织，由在日或有留日背景的医药保健从业者以及相关公司组成的新生公益组织。NPO法人仁心会事务局告诉环球时报-环球网记者，由于第一批捐赠物资是防护服，“岂曰无衣，与子同裳”恰好可以表达海外华人华侨与一线医护人员共同战胜病毒的同仇敌忾之情，流露出对同胞的守护之爱。";
	
	// 链超级管理员地址
	String supermanager = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944"; 
	
	/**
	 * 3.2.1:账户注册
	 * 
	 * @throws Exception
	 */
	@Test
	public void case3_2_1() throws Exception {
		
		System.out.println("=========================初始创建开始===========================");
		String accountId = "testAccount11";

		AccountInfo accountInfo = account.newAccountLocal();
		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
		System.out.println("publicKey is:" + accountInfo.getPublicKey());
		System.out.println("Address is:" + accountInfo.getAddress());
		
		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================初始创建结束===========================");
				
		System.out.println("=========================写数据上链开始===========================");
		// 存证智能合约的名称
		String execer = "storage";
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, accountInfo.getPrivateKey());
		String submitTransaction = client.submitTransaction(txEncode);
		
		System.out.println("数据上链hash:" + submitTransaction);
		
		Thread.sleep(3000);

		for (int tick = 0; tick < 5; tick++){
			QueryTransactionResult result = client.queryTransaction(submitTransaction);
			if(result == null) {
				Thread.sleep(5000);
				continue;
			}

			System.out.println("执行结果:" + result.getReceipt().getTyname());
			
			break;
		}
		System.out.println("=========================写数据上链结束===========================");
		
		System.out.println("=========================重复创建开始===========================");
		
		// 重复再创建账户
		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================重复创建结束===========================");

	}
	
	/**
	 * 3.2.2 账户信息修改
	 * @throws Exception 
	 * 
	 */
	@Test
	public void case3_2_2() throws Exception {
		createManager();
		
		System.out.println("=========================修改前账户信息开始===========================");
		String accountId = "testAccount21";

		AccountInfo accountInfo = account.newAccountLocal();
//		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
//		System.out.println("publicKey is:" + accountInfo.getPublicKey());
//		System.out.println("Address is:" + accountInfo.getAddress());
		
		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================修改前账户信息结束===========================");
		
		System.out.println("=========================修改后账户信息开始===========================");
		String[] accountIds = new String[]{accountId};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "4";
		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		String level = "3";
		CommUtil.manageAccount(client,accountIds, op, level);
		System.out.println("=========================修改后账户信息结束===========================");
	}
	
    /**
	 * 3.2.3 账户冻结
	 * @throws Exception 
	 * 
	 */
    @Test
	public void case3_2_3() throws Exception {
    	
		System.out.println("=========================生成账户信息开始===========================");
		String accountId = "testAccount31";

		AccountInfo accountInfo = account.newAccountLocal();
//		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
//		System.out.println("publicKey is:" + accountInfo.getPublicKey());
//		System.out.println("Address is:" + accountInfo.getAddress());
		
		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================生成账户信息结束===========================");
		
		System.out.println("=========================冻结账户信息开始===========================");
		String[] accountIds = new String[]{accountId};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "1";
		// level填空
		CommUtil.manageAccount(client,accountIds, op, "");
		System.out.println("=========================冻结账户信息结束===========================");
		
		System.out.println("=========================写数据上链开始===========================");
		// 存证智能合约的名称
		String execer = "storage";
		String privateKey = accountInfo.getPrivateKey();
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, privateKey);
		try {
			client.submitTransaction(txEncode);
		} catch (Exception e) {
			System.out.println("RPC ERROR:" + e.getMessage());
		}
		System.out.println("=========================写数据上链结束===========================");
		
		
//		Thread.sleep(3000);
//
//		for (int tick = 0; tick < 5; tick++){
//			QueryTransactionResult result = client.queryTransaction(submitTransaction);
//			if(result == null) {
//				Thread.sleep(5000);
//				continue;
//			}
//
//			System.out.println("执行结果:" + result.getReceipt().getTyname());
//			break;
//		}
//		System.out.println("=========================写数据上链结束===========================");

	}
    
    
    /**
   	 * 3.2.4 账户权限控制
   	 * @throws Exception 
   	 * 
   	 */
       @Test
   	public void case3_2_4() throws Exception {
    	   
   		System.out.println("=========================初始创建开始===========================");
   		String accountId = "testAccount41";

   		AccountInfo accountInfo = account.newAccountLocal();
//   		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
//   		System.out.println("publicKey is:" + accountInfo.getPublicKey());
//   		System.out.println("Address is:" + accountInfo.getAddress());
   		
   		CommUtil.registerAccount(client, accountId, accountInfo.getPrivateKey());
   		
   		System.out.println("=========================初始创建结束===========================");
   		
   		
   		System.out.println("=========================写数据上链开始===========================");
   		// 存证智能合约的名称
		String execer = "storage";
		
		// 生成AES加密KEY
		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(aesKeyHex);
		System.out.println("key:" + HexUtil.toHexString(key));
		// 生成iv
		byte[] iv = AesUtil.generateIv();
		// 对明文进行加密
		byte[] encrypt = AesUtil.encrypt(content, key, iv);
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes("utf-8"));
		String txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, accountInfo.getPrivateKey());
		String submitTransaction = null;
		try {
			client.submitTransaction(txEncode);
		} catch (Exception e) {
			System.out.println("RPC ERROR:" + e.getMessage());
		}

   		
   		Thread.sleep(3000);

//   		for (int tick = 0; tick < 5; tick++){
//   			QueryTransactionResult result = client.queryTransaction(submitTransaction);
//   			if(result == null) {
//   				Thread.sleep(5000);
//   				continue;
//   			}
//
//   			System.out.println("执行结果:" + result.getReceipt().getTyname());
//   			break;
//   		}
   		System.out.println("=========================写数据上链结束===========================");
   		
   		System.out.println("=========================增加账户权限开始===========================");
		String[] accountIds = new String[]{accountId};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "4";
		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		String level = "2";
		CommUtil.manageAccount(client,accountIds, op, level);
   		System.out.println("=========================增加账户权限结束===========================");
   		
   		System.out.println("=========================再次写数据上链开始===========================");
 		txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, accountInfo.getPrivateKey());
		submitTransaction = client.submitTransaction(txEncode);
		System.out.println("写数据上链hash:" + submitTransaction);
		
   		Thread.sleep(3000);

   		for (int tick = 0; tick < 5; tick++){
   			QueryTransactionResult result = client.queryTransaction(submitTransaction);
   			if(result == null) {
   				Thread.sleep(5000);
   				continue;
   			}

   			System.out.println("执行结果:" + result.getReceipt().getTyname());
   			break;
   		}
   		System.out.println("=========================再次写数据上链结束===========================");

   	}
	
	/**
 	 * 创建管理员，用于系统权限授权操作
     * 
     * @throws Exception 
     * @description 创建自定义积分的黑名单
     *
     */
    private void createManager() throws Exception {

    	// 管理合约名称
    	String execerName = "manage";
    	// 管理合约:配置管理员key
    	String key = "accountmanager-managerAddr";
    	// 管理合约:配置管理员VALUE, 对应的私钥：3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8 
    	String value = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	// 管理合约:配置操作符
    	String op = "add";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, supermanager, execerName);
    	// 发送交易
    	String hash = client.submitTransaction(txEncode);
    	System.out.println(hash);
    }
        
}
