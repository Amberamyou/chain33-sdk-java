package cn.chain33.javasdk.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.model.RpcRequest;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.AccountResult;
import cn.chain33.javasdk.model.rpcresult.BooleanResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.model.rpcresult.TokenResult;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 平行链发token和联盟链发token，唯一区别是平行链是公链应用，需要缴纳手续费 所以在这里引入了一个代扣地址的概念，
 * 我们会把一笔token的交易和一笔通用的（none）的交易包进一个 交易组内，然后用代扣地址对none这笔交易签名，
 * 在公链上，只针对代扣地址收取手续费，而token交易的收费可以设为0
 * 
 * @author fkeit
 *
 */
public class TokenParaTest {

	// 连接平行链的地址
	RpcClient client = new RpcClient("localhost", 8801);

	// 钱包密码
	private static String passwd = "123456";

	// 平行链名称
	private final static String paraTitle = "user.p.developer.";

	// token symbol
	// 重跑的话，每次symbol都要改下名，目前symbol只支持大写字母
	private static String symbol = "SYCOINA";

	// 代扣手续费地址
	// 交易虽然在平行链上不会收取手续费，但是在主链上需要扣除一定量的bty作为手续费，所以需要一个账户中有一定量BTY的地址作为代扣账户，用于缴纳手续费。
	// 这边官方提供一个地址 ：1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs
	private final static String payAddress = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	// 用户地址
	private static String userAddress;

	// token finisher的地址
	// 方便起见，我们使用跟代扣相同的地址（可以改成其它地址）
	private final static String tokenFinisher = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	public static void main(String[] args) throws Exception {

		TokenParaTest tokenTest = new TokenParaTest();

		// 创建seed,解锁钱包
		BooleanResult booleanResult = tokenTest.unlock(passwd);
		if (null != booleanResult) {
			if (!booleanResult.isOK() && "ErrSaveSeedFirst".equals(booleanResult.getMsg())) {
				// generate the seed
				String seed = tokenTest.seedGen();
				System.out.println("seed is: " + seed);

				// save the seed
				tokenTest.seedSave(seed, passwd);
				System.out.println("seed has been saved! ");

				// unlock the wallet
				tokenTest.unlock(passwd);
				System.out.println("Wallet is unlocked! ");

				// 导入配置的token-finisher的地址
				String tokenFinisher = tokenTest.importPriKey(
						"3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8", "tokenFinisher");
				System.out.println("Genesis's account is :" + tokenFinisher);

			} else {
				System.out.println("The wallet has been unlocked");
			}
		}

		// 创建用户地址
		if (null != tokenTest.getAccounts()) {

			boolean flag = false;

			List<AccountResult> accountList = tokenTest.getAccounts();
			for (AccountResult accountResult : accountList) {
				if ("user".equals(accountResult.getLabel())) {
					userAddress = accountResult.getAcc().getAddr();
					flag = true;
				}
			}
			if (!flag) {
				// create account of user A
				userAddress = tokenTest.newAccount("user");

				System.out.println("User's address is :" + userAddress);
			}

		} else {
			// create account of user A
			userAddress = tokenTest.newAccount("user");
			System.out.println("User's address is :" + userAddress);
		}

		System.out.println("\r\nToken预创建 开始============================");
		// 预发行token,发行100万个，名字叫SYCOIN的token，发行到userAddress对应的地址上（比如资金方对应的账户？）
		// 注意区块链上的TOKEN的symbol不可以重名，所以重新执行需要将symbol改掉。 symbol目前只支持大写字母
		String unsignTxPre = tokenTest.createRawTokenPreCreateTx("溯源币", symbol, userAddress, 10000000, 0, 100000);
		// 构建交易组
		String nobalanceTxPre = tokenTest.createNoBalanceTx(unsignTxPre, payAddress);
		// 签名交易，是由token-finisher来签名，注意：这里的index参数是指示对第2笔token的交易来签名
		String signTxPre = tokenTest.signRawTx(tokenFinisher, "", nobalanceTxPre, "1h", 2);
		String txhashPrecreate = tokenTest.submitTransaction(signTxPre);

		// 预创建完后先稍等一下
		tokenTest.querytxWithoutContent(txhashPrecreate);
		System.out.println("\r\nToken预创建 完成============================");

		System.out.println("\r\nToken Finish 开始============================");
		// 完成token
		String unsignTxFinish = tokenTest.createRawTokenFinishTx(100000, symbol, userAddress);
		// 构建交易组
		String nobalanceFinish = tokenTest.createNoBalanceTx(unsignTxFinish, payAddress);
		// 签名交易，是由token-finisher来签名
		String signTxFinish = tokenTest.signRawTx(tokenFinisher, "", nobalanceFinish, "1h", 2);
		String txhashFinish = tokenTest.submitTransaction(signTxFinish);

		// 根据交易hash查询交易
		tokenTest.querytxWithoutContent(txhashFinish);
		System.out.println("\r\nToken Finish 完成============================");

		System.out.println("\r\n查询地址中的token余额============================");
		// 查询地址中的token余额
		List<String> addressList = new ArrayList<String>();
		addressList.add(userAddress);
		tokenTest.getTokenBalance(addressList, paraTitle + "token", symbol);

		System.out.println("\r\n token转账============================");
		// 构建转账交易
		String unsignTransferTx = tokenTest.createRawTransaction("1NyDFjduMbYKZXMy14cqK3onZc5zLBdTc1", 100, true, false,
				symbol);
		// 构建交易组
		String nobalanceTransferTx = tokenTest.createNoBalanceTx(unsignTransferTx, payAddress);
		// 签名交易，是由token-finisher来签名
		String signTxTransfer = tokenTest.signRawTx(userAddress, "", nobalanceTransferTx, "1h", 2);
		String txhashTransfer = tokenTest.submitTransaction(signTxTransfer);

		// 根据交易hash查询交易
		tokenTest.querytxWithoutContent(txhashTransfer);

		System.out.println("\r\n 查询转出者地址中的token余额============================");
		// 查询地址中的token余额
		tokenTest.getTokenBalance(addressList, paraTitle + "token", symbol);

	}

	/**
	 * generate seed
	 * 
	 * @return
	 */
	private String seedGen() {
		String seedGen;
		try {
			seedGen = client.seedGen(1);
			return seedGen;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * save seed
	 * 
	 * @param seed
	 */
	private void seedSave(String seed, String passwd) {
		BooleanResult booleanResult;
		try {
			booleanResult = client.seedSave(seed, passwd);
			System.out.println(booleanResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unlock the wallet
	 */
	private BooleanResult unlock(String passwd) {
		boolean walletorticket = false;
		int timeout = 0;
		BooleanResult unlock;
		try {
			unlock = client.unlock(passwd, walletorticket, timeout);
			return unlock;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create the new account
	 */
	private String newAccount(String label) {
		AccountResult newAccount;
		try {
			newAccount = client.newAccount(label);
			return newAccount.getAcc().getAddr();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the account list
	 */
	public List<AccountResult> getAccounts() {
		List<AccountResult> accountList;
		try {
			accountList = client.getAccountList();
			return accountList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Get the contract address
	 */
	public String converAddress(String execerName) {
		String address;
		try {
			address = client.convertExectoAddr(execerName);
			return address;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @param name
	 * @param symbol
	 * @param ownerAddr
	 * @param total
	 * @param price
	 * @param fee
	 * @return
	 */
	public String createRawTokenPreCreateTx(String name, String symbol, String ownerAddr, long total, long price,
			long fee) {
		String unsignTx;

		try {
			unsignTx = client.createRawTokenPreCreateTx(name, symbol, "", ownerAddr, total, price, fee);
			return unsignTx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param name
	 * @param symbol
	 * @param ownerAddr
	 * @param total
	 * @param price
	 * @param fee
	 * @return
	 */
	public String createRawTokenFinishTx(long fee, String symbol, String ownerAddr) {
		String unsignTx;

		try {
			unsignTx = client.createRawTokenFinishTx(fee, symbol, ownerAddr);
			return unsignTx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param addr
	 * @param key
	 * @param txhex
	 * @param expire
	 * @param index
	 * @return
	 */
	public String signRawTx(String addr, String key, String txhex, String expire, int index) {
		String signTx;
		try {
			signTx = client.signRawTx(addr, key, txhex, expire, index);
			return signTx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Send transaction
	 * 
	 * @param data
	 * @return
	 */
	public String submitTransaction(String data) {
		String sendResult;
		try {
			sendResult = client.submitTransaction(data);
			return sendResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<AccountAccResult> getAccountBalance(List<String> addressList, String execerName) {
		List<AccountAccResult> accList = client.queryBtyBalance(addressList, execerName);
		return accList;
	}

	// 发送交易
	public String sendtx(String content, String Secret) throws Exception {
		// user.write代表合约名称
		String contractName = "user.write";
		String creareTx = TransactionUtil.createTx(Secret, contractName, content, 10000000);
		String txHash = client.submitTransaction(creareTx);

		return txHash;
	}


	public void querytxWithoutContent(String txHash) throws InterruptedException {

		QueryTransactionResult queryTransaction = new QueryTransactionResult();
		// 打包区块可能会有一定延时，这边等待一会
		for (int i = 0; i < 10; i++) {
			Thread.sleep(5000);
			queryTransaction = client.queryTransaction(txHash);
			if (null == queryTransaction) {
				Thread.sleep(5000);
			} else {
				break;
			}
		}

		// 获得区块高度
		System.out.println("区块高度：" + queryTransaction.getHeight() + "区块时间：" + queryTransaction.getBlocktime()
				+ "区块hash:" + client.getBlockHash(queryTransaction.getHeight()) + "交易hash:" + txHash);

	}

	/**
	 * Import private key
	 * 
	 * @return
	 */
	private String importPriKey(String privateKey, String label) {
		String accResult;
		try {
			accResult = client.importPrivkey(privateKey, label);
			return accResult;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void getTokenBalance(List<String> addresses, String execer, String tokenSymbol) {

		List<AccountAccResult> accList = client.getTokenBalance(addresses, execer, tokenSymbol);
		// 获取账户中的token余额
		System.out.println("帐户中的token余额是:" + accList.get(0).getBalance());
	}

	public String createRawTransaction(String to, long amount, boolean isToken, boolean isWithdraw,
			String tokenSymbol) {
		String unsignTx;

		try {
			unsignTx = client.createRawTransaction(to, amount, 0, "", isToken, isWithdraw, tokenSymbol, "");
			return unsignTx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String createNoBalanceTx(String txHex, String payAddr) {
		String nobalanceTx;
		try {
			nobalanceTx = client.createNoBalanceTx(txHex, payAddr);
			return nobalanceTx;
		} catch (Exception e) {
			return null;
		}
	}

}
