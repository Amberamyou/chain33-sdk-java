package cn.chain33.javasdk.ccidCases;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 共识机制
 * @author fkeit
 *
 */
public class Case3_3 {
	
	String ip = "119.45.1.41";
    RpcClient client = new RpcClient(ip, 8801);
	
	// 创世地址私钥
	String genesisKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";

    /**
     * 3.3.1_1	无故障、无欺诈的共识(合法交易)
     * @throws InterruptedException 
     */
    @Test
	public void Case3_3_1() throws InterruptedException {
    	System.out.println("=====================合法交易开始=============================");
    	rightTx();
    	System.out.println("=====================合法交易结束=============================");
    	System.out.println("=====================非法交易开始=============================");
    	wrongTx();
    	System.out.println("=====================非法交易结束=============================");
		
	}
    
    
    /**
	 * 3.3.2	双花攻击防范
	 */
	@Test
	public void Case3_3_2() throws InterruptedException {

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
     * 3.3.1_1	无故障、无欺诈的共识(合法交易)
     */
	private void rightTx() throws InterruptedException {

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
	 * 3.3.1_2	无故障、无欺诈的共识(非法交易 from==to)
	 */
	private void wrongTx() throws InterruptedException {

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
		

}
