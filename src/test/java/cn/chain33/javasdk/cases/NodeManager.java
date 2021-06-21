package cn.chain33.javasdk.cases;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.protobuf.TransactionAllProtobuf;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class NodeManager {

	
	String ip = "2.20.105.227";
    RpcClient client = new RpcClient(ip, 8801);

	
    /**
     * Case01_08_Step1:创建管理员，用于授权新节点的加入
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
    	String privateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	// 构造并签名交易,使用链的管理员（superManager）进行签名， 
    	String txEncode = TransactionUtil.createManage(key, value, op, privateKey, execerName);
    	// 发送交易
    	String hash = client.submitTransaction(txEncode);
    	System.out.print(hash);
    }
    
    /**
	 * Case01_08_Step2:发送交易，通知全网加入新的共识节点
	 * 
	 * @throws Exception
	 */
	@Test
	public void addConsensusNode() throws Exception {
		String pubkey = "900217F0B6D31734B13FB8EE66DBC1A203F221F1BAFD3B6BDAC615219AED4AA637CA06616E8BEDC42C4AB034BD7304F4";
		// 投票权，范围从【1~~全网总power/3】
		int power = 10;
		
		String createTxWithoutSign = client.addConsensusNode("valnode", "NodeUpdate", pubkey, power);

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
	 * Case01_09：删除共识节点
	 * 
	 * @throws Exception
	 */
	@Test
	public void delConsensusNode() throws Exception {
		String pubkey = "900217F0B6D31734B13FB8EE66DBC1A203F221F1BAFD3B6BDAC615219AED4AA637CA06616E8BEDC42C4AB034BD7304F4";
		// 投票权设置成0，代表剔除出共识节点
		int power = 0;
		
		String createTxWithoutSign = client.addConsensusNode("valnode", "NodeUpdate", pubkey, power);

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
