package cn.chain33.javasdk.client;

import java.util.ArrayList;
import java.util.List;

import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.AccountResult;
import cn.chain33.javasdk.model.rpcresult.BooleanResult;

public class GameTest {

	// json/rpc connection
	RpcClient client = new RpcClient("localhost", 8801);

	// password of wallet
	private static String passwd = "123456";

	private final static String paraTitle = "user.p.developer.";

	// ���������ѵ�ַ
	private final static String payAddress = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	// ƽ����������ַ�������ʼ��1�ڸ�����
	private final static String genesisAddress = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	// �û�A�ĵ�ַ
	private static String userAddressA;

	// �û�B�ĵ�ַ
	private static String userAddressB;

	public static void main(String[] args) throws Exception {

		GameTest gameTest = new GameTest();

		// ����seed, ����seed,���봴����ַ������Ǯ��
		BooleanResult booleanResult = gameTest.unlock(passwd);
		if (null != booleanResult) {
			if (!booleanResult.isOK() && "ErrSaveSeedFirst".equals(booleanResult.getMsg())) {
				// generate the seed
				String seed = gameTest.seedGen();
				System.out.println("seed is: " + seed);

				// save the seed
				gameTest.seedSave(seed, passwd);
				System.out.println("seed has been saved! ");

				// unlock the wallet
				gameTest.unlock(passwd);
				System.out.println("Wallet is unlocked! ");

				// ����˽Կ���봴����ַ����ַ�ǣ�1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs
				String accResult = gameTest
						.importPriKey("3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8", "genesis");
				System.out.println("Genesis's account is :" + accResult);

			} else {
				System.out.println("The wallet has been unlocked");
			}
		}

		// �����û�A���û�B�ĵ�ַ
		if (null != gameTest.getAccounts()) {

			boolean flagA = false;
			boolean flagB = false;

			List<AccountResult> accountList = gameTest.getAccounts();
			for (AccountResult accountResult : accountList) {
				if ("userA".equals(accountResult.getLabel())) {
					userAddressA = accountResult.getAcc().getAddr();
					flagA = true;
				}

				if ("userB".equals(accountResult.getLabel())) {
					userAddressB = accountResult.getAcc().getAddr();
					flagB = true;
				}
			}

			if (!flagA) {
				userAddressA = gameTest.newAccount("userA");
				System.out.println("UserA's address is :" + userAddressA);
			}

			if (!flagB) {
				userAddressB = gameTest.newAccount("userB");
				System.out.println("UserB's address is :" + userAddressB);
			}
		} else {
			userAddressA = gameTest.newAccount("userA");
			System.out.println("UserA's address is :" + userAddressA);

			userAddressB = gameTest.newAccount("userB");
			System.out.println("UserB's address is :" + userAddressB);
		}

		// �Ӵ�����ַ��תһ�������ıҸ��û�A
		gameTest.processGroupTx(userAddressA, 1000000000, 100000, false, false, "", "", genesisAddress, payAddress);

		// �Ӵ�����ַ��תһ�������ıҸ��û�B
		gameTest.processGroupTx(userAddressB, 1000000000, 100000, false, false, "", "", genesisAddress, payAddress);

		List<String> addressList = new ArrayList<String>();
		addressList.add(userAddressA);
		addressList.add(userAddressB);
		List<AccountAccResult> accList;
		// ��ѯ�û�A��ַ�е����
		while (true) {
			accList = gameTest.getAccountBalance(addressList, paraTitle + "coins");
			if (accList.get(0).getBalance() > 100000000) {
				System.out.println("Balance of coins is " + accList.get(0).getBalance());
				break;
			} else {
				Thread.sleep(1000);
			}
		}

		// ��ȡʯͷ��������Լ��Ӧ�ĵ�ַ
		String contractAddress = gameTest.converAddress(paraTitle + "game");
		System.out.println(contractAddress);

		// �û�A��˺�Լ��ַ�д���һ�������ı�
		gameTest.processGroupTx(contractAddress, 600000000, 100000, false, false, "", paraTitle + "game", userAddressA, payAddress);

		// �û�B��˺�Լ��ַ�д���һ�������ı�
		gameTest.processGroupTx(contractAddress, 600000000, 100000, false, false, "", paraTitle + "game", userAddressB, payAddress);

		// ��ѯ��Լ��ַ�е����
		while (true) {
			accList = gameTest.getAccountBalance(addressList, paraTitle + "game");
			if (accList.get(0).getBalance() > 0) {
				System.out.println("Balance of game is" + accList.get(0).getBalance());
				break;
			}
			Thread.sleep(1000);
		}
		
		// �û�A������Ϸ

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
	 * Create raw transaction
	 * 
	 * @param to
	 * @param amount
	 * @param fee
	 * @param isToken
	 * @param isWithdraw
	 * @param tokenSymbol
	 * @param execName
	 * @return
	 * @throws Exception
	 */
	public String createRawTransaction(String to, long amount, long fee, boolean isToken, boolean isWithdraw,
			String tokenSymbol, String execName) {
		String unsignTx;

		try {
			unsignTx = client.createRawTransaction(to, amount, fee, "", isToken, isWithdraw, tokenSymbol, "");
			return unsignTx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param txHex
	 * @param payAddr
	 * @return
	 */
	public String createNoBalanceTx(String txHex, String payAddr) {
		String nobalanceTx;
		try {
			nobalanceTx = client.createNoBalanceTx(txHex, payAddr);
			return nobalanceTx;
		} catch (Exception e) {
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

	public void processGroupTx(String toaddress, long amount, long fee, boolean isToken, boolean isWithdraw,
			String tokenSymbol, String execName, String fromAddress, String payAddress) {
		String unsignTx = createRawTransaction(toaddress, amount, fee, isToken, isWithdraw, tokenSymbol, execName);
		// �������������ѽ���
		String nobalanceTx = createNoBalanceTx(unsignTx, payAddress);
		String signTx = signRawTx(fromAddress, "", nobalanceTx, "300", 2);
		String txhash = submitTransaction(signTx);
		System.out.println(txhash);
	}

	public List<AccountAccResult> getAccountBalance(List<String> addressList, String execerName) {
		List<AccountAccResult> accList = client.queryBtyBalance(addressList, execerName);
		return accList;
	}

}
