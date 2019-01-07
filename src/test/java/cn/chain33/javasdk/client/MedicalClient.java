package cn.chain33.javasdk.client;

import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class MedicalClient {

	// 连接区块链的jsonRPC接口
	RpcClient client = new RpcClient("62.234.214.30",8801);
	
	// 药品生产商1账户对应的私钥（替换成实区块链上生成的那个私钥）
	// 私钥的作用是签名交易
	private static String producerSecret = "0x17328cd89c38416b19ca135224942d3968897ce3752bfa3e4e63f4553f9ebfef";
	
	// 医院1账户对应的私钥（替换成实区块链上生成的那个私钥）
	private static String hospitalSecret = "0xac1a788374a130d7c476467e0112c681366c9e6fa8fc60f3080005394e7a2899";
	
	// 代理商的账户对应的私钥（替换成实区块链上生成的那个私钥）
	private static String agencySecret = "0xa82018ace74abb73bcacad99a86262574315572db7c000a47f01cdc77d6c351c";

	public static void main(String []args) throws Exception {
		
		MedicalClient test = new MedicalClient();
		// 药品生产商数据上链内容
		String content = "{\"药品编号\":\"MEDICAL000001\",\"药品图片HASH\":\"933a925767fe0ae387947f41690fc054\",\"药品产地\":\"上海\",\"保质期\":\"2019-12-12\",\"生产商\":\"上海xxxxxx有限公司\"}";
		String txHash = test.sendtx(content, producerSecret);
		
		System.out.println("交易hash：" + txHash);
		
		// 根据交易hash查询交易
		test.querytx(txHash);
		
		// 医院数据上链内容
		content = "{\"药品编号\":\"MEDICAL000001\",\"药品图片HASH\":\"933a925767fe0ae387947f41690fc054\",\"药品产地\":\"上海\",\"保质期\":\"2019-12-12\",\"医院名称\":\"上海xxxxxx医院\"}";
		txHash = test.sendtx(content, hospitalSecret);
		
		System.out.println("交易hash：" + txHash);
		
		// 根据交易hash查询交易
		test.querytx(txHash);
		
		// 代理商上链内容
		content = "{\"药品编号\":\"MEDICAL000001\",\"药品图片HASH\":\"933a925767fe0ae387947f41690fc054\",\"药品产地\":\"上海\",\"保质期\":\"2019-12-12\",\"代理商名称\":\"xxxxxx代理商\"}";
		txHash = test.sendtx(content, agencySecret);
		
		System.out.println("交易hash：" + txHash);
		
		// 根据交易hash查询交易
		test.querytx(txHash);
	}
	
	// 发送交易
	public String sendtx(String content, String Secret) throws Exception {
		// user.write代表合约名称
		String contractName = "user.write";
		String creareTx = TransactionUtil.createTx(Secret, contractName, content, 10000000);
		String txHash = client.submitTransaction(creareTx);
		
		return txHash;
	}
	
	// 查询交易亚航空运
	public void querytx(String txHash) throws InterruptedException {
		
		QueryTransactionResult queryTransaction1 = new QueryTransactionResult();
		// 打包区块可能会有一定延时，这边等待一会
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
		
		// 获得区块高度
		// TODO:这边需要处理下异常，以防queryTransaction1是空导致异常
		System.out.println("区块高度：" + queryTransaction1.getHeight());
		// 获得区块时间
		System.out.println("区块时间：" + queryTransaction1.getBlocktime());
		// 获得from地址
		System.out.println("from地址：" + queryTransaction1.getFromaddr());
		// 获得to地址
		System.out.println("to地址：" + queryTransaction1.getTx().getTo());
		// 获得区块hash
		System.out.println("区块hash:" + client.getBlockHash(queryTransaction1.getHeight()));

		// 获得交易体	
		String payload = queryTransaction1.getTx().getRawpayload();
		String s = payload.substring(2, payload.length());
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b .length; i++) {
			try {
				b [i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(b, "GB2312");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("交易信息：" + s);
		
	}
}
