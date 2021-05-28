package cn.chain33.javasdk.ccidCases;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.protobuf.TransactionAllProtobuf;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class Case3_7 {

	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);

	// 链超级管理员地址
	String supermanager = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944"; 
	
    /**
     * Step1:创建管理员，用于授权新节点的加入
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
    	String key = "qbft-manager";
    	// 管理合约:配置管理员VALUE, 对应的私钥：3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8 
    	String value = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	// 管理合约:配置操作符
    	String op = "add";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, supermanager, execerName);
    	// 发送交易
    	String hash = client.submitTransaction(txEncode);
    	System.out.print(hash);
    }
    
    /**
	 * 3.7.1 新增共识节点
	 * 
	 * @throws Exception
	 */
	@Test
	public void Case3_7_1() throws Exception {
		String pubkey = "A2CE9A7070C88FD09632593BD785C135E40B676D658354704BADC30DB36B7F158FB6AEA29F8964809844F4F567D71832";
		// 投票权，范围从【1~~全网总power/3】
		int power = 10;
		
		String createTxWithoutSign = client.addConsensusNode("qbftNode", "NodeUpdate", pubkey, power);

		byte[] fromHexString = HexUtil.fromHexString(createTxWithoutSign);
		TransactionAllProtobuf.Transaction parseFrom = null;
		try {
			parseFrom = TransactionAllProtobuf.Transaction.parseFrom(fromHexString);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		TransactionAllProtobuf.Transaction signProbuf = TransactionUtil.signProbuf(parseFrom, "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8");
		String hexString = HexUtil.toHexString(signProbuf.toByteArray());

		String submitTransaction = client.submitTransaction(hexString);
		System.out.println(submitTransaction);

	}
	
    /**
	 * 3.7.2 删除共识节点
	 * 
	 * @throws Exception
	 */
	@Test
	public void Case3_7_2() throws Exception {
		String pubkey = "A2CE9A7070C88FD09632593BD785C135E40B676D658354704BADC30DB36B7F158FB6AEA29F8964809844F4F567D71832";
		// 投票权设置成0，代表剔除出共识节点
		int power = 0;
		
		String createTxWithoutSign = client.addConsensusNode("qbftNode", "NodeUpdate", pubkey, power);

		byte[] fromHexString = HexUtil.fromHexString(createTxWithoutSign);
		TransactionAllProtobuf.Transaction parseFrom = null;
		try {
			parseFrom = TransactionAllProtobuf.Transaction.parseFrom(fromHexString);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		TransactionAllProtobuf.Transaction signProbuf = TransactionUtil.signProbuf(parseFrom, "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8");
		String hexString = HexUtil.toHexString(signProbuf.toByteArray());

		String submitTransaction = client.submitTransaction(hexString);
		System.out.println(submitTransaction);

	}
}
