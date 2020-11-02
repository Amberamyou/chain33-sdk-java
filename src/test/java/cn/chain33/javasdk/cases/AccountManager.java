package cn.chain33.javasdk.cases;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.protobuf.TransactionProtoBuf;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class AccountManager {
	
    String ip = "2.20.105.227";
    RpcClient client = new RpcClient(ip, 8801);

	Account account = new Account();

	/**
	 * Case01_01
	 * 
	 * @description Case01_01：创建账户
	 *
	 */
	@Test
	public void createAccountLocal() {
		AccountInfo accountInfo = account.newAccountLocal();
		System.out.println("privateKey is:" + accountInfo.getPrivateKey());
		System.out.println("publicKey is:" + accountInfo.getPublicKey());
		System.out.println("Address is:" + accountInfo.getAddress());
	}

	/**
	 * Case01_02
	 * 
	 * @throws InterruptedException
	 * @description Case01_02：激活账户
	 */
	@Test
	public void createCoinTransferTxMain() throws InterruptedException {
		
		String to = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";

		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();

		// 转账说明
		transferBalanceRequest.setNote("转账说明");
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(1 * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo(to);
		// 签名私私钥,对应的测试地址是：14KEKbYtKKQm4wMthSK9J4La4nAiidGozt
		transferBalanceRequest.setFromPrivateKey("22eb43497b25eeb879c81726afb75e7eaaef9ee6ceb6f29bf3b3bb26b3c30e76");
		// 执行器名称，主链主积分固定为coins
		transferBalanceRequest.setExecer("coins");
		// 签名类型 (支持SM2, SECP256K1, ED25519)
		transferBalanceRequest.setSignType(SignType.SECP256K1);
		// 构造好，并本地签好名的交易
		String createTransferTx = TransactionUtil.transferBalanceMain(transferBalanceRequest);
		// 交易发往区块链
		String txHash = client.submitTransaction(createTransferTx);
		System.out.println(txHash);

		List<String> list = new ArrayList<>();
		list.add("12QpSFb6xxybc2Gj1NrpfrL2uCNUhYwFMQ");
		list.add(to);

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(5000);
			} else {
				break;
			}
		}

		List<AccountAccResult> queryBtyBalance;
		queryBtyBalance = client.getCoinsBalance(list, "coins");
		if (queryBtyBalance != null) {
			for (AccountAccResult accountAccResult : queryBtyBalance) {
				System.out.println(accountAccResult);
			}
		}
	}
	
	/**
	 * Case01_03
	 * 
	 * Case01_03_step1: 账户创建
	 * 
	 * @throws Exception
	 */
	@Test
	public void registeAndQueryAccount() throws Exception {
		
		String accountId = "testAccount1";

		AccountInfo accountInfo = account.newAccountLocal();
		String createTxWithoutSign = client.registeAccount("accountmanager", "Register", accountId);

		byte[] fromHexString = HexUtil.fromHexString(createTxWithoutSign);
		TransactionProtoBuf.Transaction parseFrom = null;
		try {
			parseFrom = TransactionProtoBuf.Transaction.parseFrom(fromHexString);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		TransactionProtoBuf.Transaction signProbuf = TransactionUtil.signProbuf(parseFrom, accountInfo.getPrivateKey());
		String hexString = HexUtil.toHexString(signProbuf.toByteArray());

		String submitTransaction = client.submitTransaction(hexString);
		System.out.println(submitTransaction);

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 5; i++) {
			queryTransaction1 = client.queryTransaction(submitTransaction);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}

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
	}
	
	/**
	 * Case01_03
	 * 
     * Case01_03_step2: 创建管理员，用于系统权限授权操作
     * 
     * @throws Exception 
     * @description 创建自定义积分的黑名单
     *
     */
    @Test
    public void createManager() throws Exception {

    	// 管理合约名称
    	String execerName = "manage";
    	// 管理合约:配置管理员key
    	String key = "accountmanager-managerAddr";
    	// 管理合约:配置管理员VALUE, 对应的私钥：3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8 
    	String value = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	// 管理合约:配置操作符
    	String op = "add";
    	// 当前链管理员私钥（superManager）
    	String privateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, privateKey, execerName);
    	// 发送交易
    	String hash = client.submitTransaction(txEncode);
    	System.out.print(hash);
    }
    
	/**
	 * Case01_03
	 * 
	 * Case01_03_step3: 对账户进行授权
	 * @throws Exception 
	 * 
	 */
    @Test
	public void authAndQueryAccount() throws Exception {
		String[] accountIds = new String[]{"testAccount1"};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "4";
		//0普通,后面根据业务需要可以自定义，有管理员授予不同的权限
		String level = "2";
		manageAccount(accountIds, op, level);
		
	}
    
    /**
	 * Case01_04：账户冻结
	 * 
	 * @throws Exception 
	 * 
	 */
    @Test
	public void frozenAndQueryAccount() throws Exception {
		String[] accountIds = new String[]{"testAccount1"};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "1";
		// level填空
		manageAccount(accountIds, op, "");

	}
    
	/**
	 * Case01_05：账户解冻
	 * 
	 * @throws Exception 
	 * 
	 */
    @Test
	public void unfrozenAndQueryAccount() throws Exception {
		String[] accountIds = new String[]{"testAccount1"};
		// 1为冻结，2为解冻，3增加有效期,4为授权
		String op = "2";
		// level填空
		manageAccount(accountIds, op, "");

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
		for (int i = 0; i < 5; i++) {
			queryTransaction1 = client.queryTransaction(submitTransaction);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
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
}
