package cn.chain33.javasdk.ccidCases;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.protobuf.TransactionProtoBuf;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class CommUtil {
	
	public static final String ip = "132.232.76.48";
	
	public static final String ip1 = "132.232.76.48";
	public static final String ip2 = "132.232.87.45";
	public static final String ip3 = "139.155.31.59";
	public static final String ip4 = "139.155.34.60";
	
		
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

		String submitTransaction;
		try {
			submitTransaction = client.submitTransaction(hexString);
			System.out.println("注册用户hash:"  + submitTransaction);
			
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
					
			    	System.out.println("账户ID:" + resultJson.getString("accountID")+ ", 过期时间:" + resultJson.getString("expireTime") + ", 创建时间:" + resultJson.getString("createTime") + ", 账户状态:" + resultJson.getString("status") + ", 等级权限:" + resultJson.getString("level") + "地址:" + resultJson.getString("addr"));
			    	
//			    	// 根据状态查账户信息
//			    	String status = "0";
//			    	resultJson = client.queryAccountByStatus(status);
//			    	System.out.println(resultJson);
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("RPC ERROR:" + e.getMessage());
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
		
		System.out.println("账户ID:" + resultJson.getString("accountID")+ ", 过期时间:" + resultJson.getString("expireTime") + ", 创建时间:" + resultJson.getString("createTime") + ", 账户状态:" + resultJson.getString("status") + ", 等级权限:" + resultJson.getString("level") + "地址:" + resultJson.getString("addr"));
    }

}
