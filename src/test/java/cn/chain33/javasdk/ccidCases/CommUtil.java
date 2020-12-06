package cn.chain33.javasdk.ccidCases;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.protobuf.TransactionProtoBuf;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class CommUtil {
	
	public static final String ip = "119.45.1.41";
	
	public static final int port = 8801;
	
	
	/**
     * 注册账户
     * @param accountId
     * @param privateKey
     * @throws Exception 
     */
    public static void registerAccount(RpcClient client, String accountId, String privateKey) throws Exception {
    	
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

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 5; i++) {
			queryTransaction1 = client.queryTransaction(submitTransaction);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
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
    
    /**
     * 账户操作
     * 
     * @param accountIds
     * @param op
     * @param level
     * @throws Exception
     */
    public static void manageAccount(RpcClient client, String[] accountIds, String op, String level) throws Exception {
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

}
