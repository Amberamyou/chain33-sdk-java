package cn.chain33.javasdk.ccidCases;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.protobuf.TransactionProtoBuf;
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
		
		registerAccount(accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================初始创建结束===========================");
		
		System.out.println("=========================重复创建开始===========================");
		// 重复再创建账户
		accountId = "testAccount1";
		registerAccount(accountId, accountInfo.getPrivateKey());
		
		System.out.println("=========================重复创建结束===========================");
		
		System.out.println("=========================写数据上链开始===========================");
		// 存证智能合约的名称
		String execer = "storage";
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, accountInfo.getPrivateKey());
		String submitTransaction = client.submitTransaction(txEncode);
		
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

	}
	
	/**
	 * 3.2.2 账户信息修改
	 * @throws Exception 
	 * 
	 */
	@Test
	public void case3_2_2() throws Exception {
		createManager();
		
		String[] accountIds = new String[]{"testAccount11"};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "4";
		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		String level = "3";
		manageAccount(accountIds, op, level);
	}
	
    /**
	 * 3.2.3 账户冻结
	 * @throws Exception 
	 * 
	 */
    @Test
	public void case3_2_3() throws Exception {
		String[] accountIds = new String[]{"testAccount1"};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "1";
		// level填空
		manageAccount(accountIds, op, "");
		
		System.out.println("=========================写数据上链开始===========================");
		// 存证智能合约的名称
		String execer = "storage";
		//String privateKey = "case3_2_1中生成";
		String privateKey = "128d4e6ed7ce4e7c5fb7d7bf64d33e7825214fe1fd0d3a62ab33d4f8446f05bb";
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		
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

	}
    
    
    /**
   	 * 3.2.4 账户权限控制
   	 * @throws Exception 
   	 * 
   	 */
       @Test
   	public void case3_2_4() throws Exception {
    	   
   		System.out.println("=========================初始创建开始===========================");
   		String accountId = "testAccount12";

   		AccountInfo accountInfo = account.newAccountLocal();
   		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
   		System.out.println("publicKey is:" + accountInfo.getPublicKey());
   		System.out.println("Address is:" + accountInfo.getAddress());
   		
   		registerAccount(accountId, accountInfo.getPrivateKey());
   		
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
		String decrypt = AesUtil.decrypt(encrypt, HexUtil.toHexString(key));
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes("utf-8"));
		String txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, accountInfo.getPrivateKey());
		String submitTransaction = client.submitTransaction(txEncode);
   		
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
    	// 当前链管理员私钥（superManager）
    	String privateKey = "7dff23427231e231556d15a6cdb19e0a481ca4074794975a59064c510e423aa0";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, privateKey, execerName);
    	// 发送交易
    	String hash = client.submitTransaction(txEncode);
    	System.out.println(hash);
    }
    
    /**
     * 账户操作
     * 
     * @param accountIds
     * @param op
     * @param level
     * @throws Exception
     */
    private void manageAccount(String[] accountIds, String op, String level) throws Exception {
		String createTxWithoutSign = client.authAccount("accountmanager", "Supervise", accountIds, op, level);

		byte[] fromHexString = HexUtil.fromHexString(createTxWithoutSign);
		TransactionProtoBuf.Transaction parseFrom = null;
		try {
			parseFrom = TransactionProtoBuf.Transaction.parseFrom(fromHexString);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		TransactionProtoBuf.Transaction signProbuf = TransactionUtil.signProbuf(parseFrom, "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8");
		String hexString = HexUtil.toHexString(signProbuf.toByteArray());

		String submitTransaction = client.submitTransaction(hexString);
		System.out.println(submitTransaction);

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		Thread.sleep(3000);
		for (int i = 0; i < 5; i++) {
			queryTransaction1 = client.queryTransaction(submitTransaction);
			if (null == queryTransaction1) {
				Thread.sleep(3000);
			} else {
				break;
			}
		}

		// 根据accountId查询账户信息
		JSONObject resultJson = client.queryAccountById(accountIds[0]);
		
    	System.out.println("账户ID:" + resultJson.getString("accountID"));
    	System.out.println("过期时间:" + resultJson.getString("expireTime"));
    	System.out.println("创建时间:" + resultJson.getString("createTime"));
    	// 账户状态 0 正常， 1表示冻结, 2表示锁定 3,过期注销
    	System.out.println("账户状态:" + resultJson.getString("status"));
    	//等级权限 0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
    	System.out.println("等级权限:" + resultJson.getString("level"));
    	// 账户地址
    	System.out.println("地址:" + resultJson.getString("addr"));
    }
    
    /**
     * 注册账户
     * @param accountId
     * @param privateKey
     * @throws Exception 
     */
    private void registerAccount(String accountId, String privateKey) throws Exception {
    	
    	String createTxWithoutSign = client.registeAccount("accountmanager", "Register", accountId);

		byte[] fromHexString = HexUtil.fromHexString(createTxWithoutSign);
		TransactionProtoBuf.Transaction parseFrom = null;
		try {
			parseFrom = TransactionProtoBuf.Transaction.parseFrom(fromHexString);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		TransactionProtoBuf.Transaction signProbuf = TransactionUtil.signProbuf(parseFrom, privateKey);
		String hexString = HexUtil.toHexString(signProbuf.toByteArray());

		String submitTransaction = client.submitTransaction(hexString);
		System.out.println(submitTransaction);

		Thread.sleep(3000);
		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 5; i++) {
			queryTransaction1 = client.queryTransaction(submitTransaction);
			if (null == queryTransaction1) {
				Thread.sleep(3000);
			} else {
				// 根据accountId查询账户信息
				JSONObject resultJson = client.queryAccountById(accountId);
				
		    	System.out.println("账户ID:" + resultJson.getString("accountID"));
		    	System.out.println("过期时间:" + resultJson.getString("expireTime"));
		    	System.out.println("创建时间:" + resultJson.getString("createTime"));
		    	// 账户状态 0 正常， 1表示冻结, 2表示锁定 3,过期注销
		    	System.out.println("账户状态:" + resultJson.getString("status"));
		    	//等级权限 0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		    	System.out.println("等级权限:" + resultJson.getString("level"));
		    	// 账户地址
		    	System.out.println("地址:" + resultJson.getString("addr"));
		    	
		    	// 根据状态查账户信息
		    	String status = "0";
		    	resultJson = client.queryAccountByStatus(status);
		    	System.out.println(resultJson);
				break;
			}
		}
    }
}
