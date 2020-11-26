package cn.chain33.javasdk.ccidCases;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.protobuf.TransactionProtoBuf;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class Case3_7 {

    String ip = "119.45.1.41";
    RpcClient client = new RpcClient(ip, 8801);

	
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
    	String key = "tendermint-manager";
    	// 管理合约:配置管理员VALUE, 对应的私钥：3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8 
    	String value = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	// 管理合约:配置操作符
    	String op = "add";
    	// 当前链管理员私钥（superManager）
    	String privateKey = "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, privateKey, execerName);
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
	public void addConsensusNode() throws Exception {
		String pubkey = "A4C6988F091892E025A1916B52D52F5045F7C94C71566B36000ACDA6E13AEEE3C0DFAD651B69461E2D64FE59DCBFE24B";
		// 投票权，范围从【1~~全网总power/3】
		int power = 10;
		
		String createTxWithoutSign = client.addConsensusNode("valnode", "NodeUpdate", pubkey, power);

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

	}
	
    /**
	 * 3.7.2 删除共识节点
	 * 
	 * @throws Exception
	 */
	@Test
	public void delConsensusNode() throws Exception {
		String pubkey = "A4C6988F091892E025A1916B52D52F5045F7C94C71566B36000ACDA6E13AEEE3C0DFAD651B69461E2D64FE59DCBFE24B";
		// 投票权设置成0，代表剔除出共识节点
		int power = 0;
		
		String createTxWithoutSign = client.addConsensusNode("valnode", "NodeUpdate", pubkey, power);

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

	}
}
