package cn.chain33.javasdk.cases;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class SecurityTest {
	
	
	String ip = "2.20.105.227";
	//String ip = "132.232.76.48";
    RpcClient client = new RpcClient(ip, 8801);
    
	// 创世地址私钥
	String genesisKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";

	
	/**
	 * Case02_01_step1：无故障与无欺诈的共识  发起一笔合法的转账交易
	 * @throws Exception 
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void createRightTransferTx() throws Exception {

		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();

		// 转账说明
		transferBalanceRequest.setNote("转账说明");
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(1 * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		// 签名私私钥
		transferBalanceRequest.setFromPrivateKey(genesisKey);
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
		list.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(3000);
			} else {
				System.out.println("交易执行结果：" + queryTransaction1.getReceipt().getTyname());
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
	 * Case02_01_step2: 发起一笔非法的转账交易（from地址==to地址）
	 * @throws Exception 
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void createWrongTransferTx() throws Exception {

		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();

		// 转账说明
		transferBalanceRequest.setNote("转账说明");
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(1 * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");
		// 签名私私钥
		transferBalanceRequest.setFromPrivateKey(genesisKey);
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
		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(3000);
			} else {
				System.out.println("交易执行结果：" + queryTransaction1.getReceipt().getTyname());
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
	 * Case02_05：转账情况下的双花攻击防范
	 * @throws Exception 
	 * 
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void createDoubleransferTx() throws Exception {

		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();

		// 转账说明
		transferBalanceRequest.setNote("转账说明");
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(1 * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		// 签名私私钥
		transferBalanceRequest.setFromPrivateKey(genesisKey);
		// 执行器名称，主链主积分固定为coins
		transferBalanceRequest.setExecer("coins");
		// 签名类型 (支持SM2, SECP256K1, ED25519)
		transferBalanceRequest.setSignType(SignType.SECP256K1);
		// 构造好，并本地签好名的交易
		String createTransferTx = TransactionUtil.transferBalanceMain(transferBalanceRequest);
		// 交易发往区块链
		String txHash = client.submitTransaction(createTransferTx);
		// 重复发送相同交易
		String txHash1 = client.submitTransaction(createTransferTx);
		System.out.println(txHash);
		System.out.println(txHash1);

		List<String> list = new ArrayList<>();
		list.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(3000);
			} else {
				System.out.println("交易执行结果：" + queryTransaction1.getReceipt().getTyname());
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
	 * Case02_06：消息篡改
	 * @throws Exception 
	 * 
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void createOverflowTransferTx() throws Exception {

		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();
		TransferBalanceRequest transferBalanceRequest1 = new TransferBalanceRequest();

		// 转账说明
		transferBalanceRequest.setNote("转1个积分");
		transferBalanceRequest1.setNote("转2亿个积分，超出最大值");
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		transferBalanceRequest1.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(1 * 100000000L);
		transferBalanceRequest1.setAmount(99999974 * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		transferBalanceRequest1.setTo("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		// 签名私私钥
		transferBalanceRequest.setFromPrivateKey(genesisKey);
		transferBalanceRequest1.setFromPrivateKey(genesisKey);
		// 执行器名称，主链主积分固定为coins
		transferBalanceRequest.setExecer("coins");
		transferBalanceRequest1.setExecer("coins");
		// 签名类型 (支持SM2, SECP256K1, ED25519)
		transferBalanceRequest.setSignType(SignType.SECP256K1);
		transferBalanceRequest1.setSignType(SignType.SECP256K1);
		// 构造好，并本地签好名的交易
		String createTransferTx = TransactionUtil.transferBalanceMain(transferBalanceRequest);
		String createTransferTx1 = TransactionUtil.transferBalanceMain(transferBalanceRequest1);
		// 交易发往区块链
		String txHash = client.submitTransaction(createTransferTx);
		String txHash1 = client.submitTransaction(createTransferTx1);
		System.out.println(txHash);
		System.out.println(txHash1);

		List<String> list = new ArrayList<>();
		list.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction;
		QueryTransactionResult queryTransaction1;
		for (int i = 0; i < 10; i++) {
			queryTransaction = client.queryTransaction(txHash);
			queryTransaction1 = client.queryTransaction(txHash1);
			if (null == queryTransaction) {
				Thread.sleep(3000);
			} else {
				System.out.println("第一笔交易执行结果：" + queryTransaction.getReceipt().getTyname());
				System.out.println("第二笔交易执行结果：" + queryTransaction1.getReceipt().getTyname());
				break;
			}
		}

	}
	
	
	/**
	 * Case02_06：消息篡改
	 * @throws Exception 
	 * 
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void createNoSignTx() throws Exception {


		// 未签名交易
		String txEncodeWithoutSgin = "0a0365766d12c00422d201608060405234801561001057600080fd5b506298967f60005560ac806100266000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c806360fe47b11460375780636d4ce63c146053575b600080fd5b605160048036036020811015604b57600080fd5b5035606b565b005b60596070565b60408051918252519081900360200190f35b600055565b6000549056fea26469706673582212206850c96ceb3091ba2b0454750fbb02238fe0e3765327ec62c47ef4acf0b3ff2b64736f6c634300060000332a0c65766d2d73646b2d746573743ada025b7b22696e70757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a22636f6e7374727563746f72227d2c7b22696e70757473223a5b5d2c226e616d65223a22676574222c226f757470757473223a5b7b22696e7465726e616c54797065223a2275696e74323536222c226e616d65223a22222c2274797065223a2275696e74323536227d5d2c2273746174654d75746162696c697479223a2276696577222c2274797065223a2266756e6374696f6e227d2c7b22696e70757473223a5b7b22696e7465726e616c54797065223a2275696e74323536222c226e616d65223a2278222c2274797065223a2275696e74323536227d5d2c226e616d65223a22736574222c226f757470757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a2266756e6374696f6e227d5d1a250801122102005d3a38feaff00f1b83014b2602d7b5b39506ddee7919dd66539b5428358f0820c0843d2880808080808080804030e1c992889ba7c5f14b3a223139746a5335316b6a7772436f535153313355336f7765376759424c6653666f466d";
		// 交易发往区块链
		String txHash = client.submitTransaction(txEncodeWithoutSgin);
		System.out.println(txHash);

		List<String> list = new ArrayList<>();
		list.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

		// 一般1秒一个区块
		QueryTransactionResult queryTransaction;
		for (int i = 0; i < 10; i++) {
			queryTransaction = client.queryTransaction(txHash);
			if (null == queryTransaction) {
				Thread.sleep(3000);
			} else {
				System.out.println("第一笔交易执行结果：" + queryTransaction.getReceipt().getTyname());
				break;
			}
		}

	}

}
