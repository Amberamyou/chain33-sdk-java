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
 * ƽ������token����������token��Ψһ������ƽ�����ǹ���Ӧ�ã���Ҫ���������� ����������������һ�����۵�ַ�ĸ��
 * ���ǻ��һ��token�Ľ��׺�һ��ͨ�õģ�none���Ľ��װ���һ�� �������ڣ�Ȼ���ô��۵�ַ��none��ʽ���ǩ����
 * �ڹ����ϣ�ֻ��Դ��۵�ַ��ȡ�����ѣ���token���׵��շѿ�����Ϊ0
 * 
 * @author fkeit
 *
 */
public class TokenParaTest {

	// ����ƽ�����ĵ�ַ
	RpcClient client = new RpcClient("localhost", 8801);

	// Ǯ������
	private static String passwd = "123456";

	// ƽ��������
	private final static String paraTitle = "user.p.developer.";

	// token symbol
	// ���ܵĻ���ÿ��symbol��Ҫ��������Ŀǰsymbolֻ֧�ִ�д��ĸ
	private static String symbol = "SYCOINA";

	// ���������ѵ�ַ
	// ������Ȼ��ƽ�����ϲ�����ȡ�����ѣ���������������Ҫ�۳�һ������bty��Ϊ�����ѣ�������Ҫһ���˻�����һ����BTY�ĵ�ַ��Ϊ�����˻������ڽ��������ѡ�
	// ��߹ٷ��ṩһ����ַ ��1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs
	private final static String payAddress = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	// �û���ַ
	private static String userAddress;

	// token finisher�ĵ�ַ
	// �������������ʹ�ø�������ͬ�ĵ�ַ�����Ըĳ�������ַ��
	private final static String tokenFinisher = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	public static void main(String[] args) throws Exception {

		TokenParaTest tokenTest = new TokenParaTest();

		// ����seed,����Ǯ��
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

				// �������õ�token-finisher�ĵ�ַ
				String tokenFinisher = tokenTest.importPriKey(
						"3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8", "tokenFinisher");
				System.out.println("Genesis's account is :" + tokenFinisher);

			} else {
				System.out.println("The wallet has been unlocked");
			}
		}

		// �����û���ַ
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

		System.out.println("\r\nTokenԤ���� ��ʼ============================");
		// Ԥ����token,����100��������ֽ�SYCOIN��token�����е�userAddress��Ӧ�ĵ�ַ�ϣ������ʽ𷽶�Ӧ���˻�����
		// ע���������ϵ�TOKEN��symbol��������������������ִ����Ҫ��symbol�ĵ��� symbolĿǰֻ֧�ִ�д��ĸ
		String unsignTxPre = tokenTest.createRawTokenPreCreateTx("��Դ��", symbol, userAddress, 10000000, 0, 100000);
		// ����������
		String nobalanceTxPre = tokenTest.createNoBalanceTx(unsignTxPre, payAddress);
		// ǩ�����ף�����token-finisher��ǩ����ע�⣺�����index������ָʾ�Ե�2��token�Ľ�����ǩ��
		String signTxPre = tokenTest.signRawTx(tokenFinisher, "", nobalanceTxPre, "1h", 2);
		String txhashPrecreate = tokenTest.submitTransaction(signTxPre);

		// Ԥ����������Ե�һ��
		tokenTest.querytxWithoutContent(txhashPrecreate);
		System.out.println("\r\nTokenԤ���� ���============================");

		System.out.println("\r\nToken Finish ��ʼ============================");
		// ���token
		String unsignTxFinish = tokenTest.createRawTokenFinishTx(100000, symbol, userAddress);
		// ����������
		String nobalanceFinish = tokenTest.createNoBalanceTx(unsignTxFinish, payAddress);
		// ǩ�����ף�����token-finisher��ǩ��
		String signTxFinish = tokenTest.signRawTx(tokenFinisher, "", nobalanceFinish, "1h", 2);
		String txhashFinish = tokenTest.submitTransaction(signTxFinish);

		// ���ݽ���hash��ѯ����
		tokenTest.querytxWithoutContent(txhashFinish);
		System.out.println("\r\nToken Finish ���============================");

		System.out.println("\r\n��ѯ��ַ�е�token���============================");
		// ��ѯ��ַ�е�token���
		List<String> addressList = new ArrayList<String>();
		addressList.add(userAddress);
		tokenTest.getTokenBalance(addressList, paraTitle + "token", symbol);

		System.out.println("\r\n tokenת��============================");
		// ����ת�˽���
		String unsignTransferTx = tokenTest.createRawTransaction("1NyDFjduMbYKZXMy14cqK3onZc5zLBdTc1", 100, true, false,
				symbol);
		// ����������
		String nobalanceTransferTx = tokenTest.createNoBalanceTx(unsignTransferTx, payAddress);
		// ǩ�����ף�����token-finisher��ǩ��
		String signTxTransfer = tokenTest.signRawTx(userAddress, "", nobalanceTransferTx, "1h", 2);
		String txhashTransfer = tokenTest.submitTransaction(signTxTransfer);

		// ���ݽ���hash��ѯ����
		tokenTest.querytxWithoutContent(txhashTransfer);

		System.out.println("\r\n ��ѯת���ߵ�ַ�е�token���============================");
		// ��ѯ��ַ�е�token���
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

	// ���ͽ���
	public String sendtx(String content, String Secret) throws Exception {
		// user.write�����Լ����
		String contractName = "user.write";
		String creareTx = TransactionUtil.createTx(Secret, contractName, content, 10000000);
		String txHash = client.submitTransaction(creareTx);

		return txHash;
	}


	public void querytxWithoutContent(String txHash) throws InterruptedException {

		QueryTransactionResult queryTransaction = new QueryTransactionResult();
		// ���������ܻ���һ����ʱ����ߵȴ�һ��
		for (int i = 0; i < 10; i++) {
			Thread.sleep(5000);
			queryTransaction = client.queryTransaction(txHash);
			if (null == queryTransaction) {
				Thread.sleep(5000);
			} else {
				break;
			}
		}

		// �������߶�
		System.out.println("����߶ȣ�" + queryTransaction.getHeight() + "����ʱ�䣺" + queryTransaction.getBlocktime()
				+ "����hash:" + client.getBlockHash(queryTransaction.getHeight()) + "����hash:" + txHash);

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
		// ��ȡ�˻��е�token���
		System.out.println("�ʻ��е�token�����:" + accList.get(0).getBalance());
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
