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


public class TokenParaTest {

	// json/rpc connection
	RpcClient client = new RpcClient("localhost", 8801);

	// Ǯ������
	private static String passwd = "123456";
	
	// token symbol
	// ���ܵĻ���ÿ��symbol��Ҫ��������Ŀǰsymbolֻ֧�ִ�д��ĸ
	private static String symbol = "SYCOINM";
	
	// ���������ѵ�ַ
	private final static String payAddress = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";

	// �û���ַ
	private static String userAddress;
	private static String userSecret;
	
	// token finisher�ĵ�ַ
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
				String tokenFinisher = tokenTest
						.importPriKey("3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8", "tokenFinisher");
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
		
		userSecret = tokenTest.dumpPriKey(userAddress);
		
		// ��Դ������������
		String content = "{\"ũ��Ʒ���\":\"TEST000001\",\"ũ��ƷͼƬHASH\":\"933a925767fe0ae387947f41690fc054\",\"ũ��Ʒ����\":\"����\",\"������\":\"2019-12-12\",\"������\":\"�Ϻ�xxxxxx���޹�˾\"}";
		String txHash = tokenTest.sendtx(content, userSecret);

		
		// ���ݽ���hash��ѯ����
		tokenTest.querytxWithContent(txHash);

		System.out.println("\r\nTokenԤ���� ��ʼ============================" );
		// Ԥ����token,����100��������ֽ�SYCOIN��token�����е�userAddress��Ӧ�ĵ�ַ�ϣ������ʽ𷽶�Ӧ���˻�����
		// ע���������ϵ�TOKEN��symbol��������������������ִ����Ҫ��symbol�ĵ��� symbolĿǰֻ֧�ִ�д��ĸ
		String unsignTxPre = tokenTest.createRawTokenPreCreateTx("��Դ��", symbol, userAddress, 10000000, 0, 100000);
		// ǩ�����ף�����token-finisher��ǩ��
		String signTxPre = tokenTest.signRawTx(tokenFinisher, "", unsignTxPre, "1h", 1);
		String txhashPrecreate = tokenTest.submitTransaction(signTxPre);
		
		// Ԥ����������Ե�һ��
		tokenTest.querytxWithoutContent(txhashPrecreate);
		System.out.println("\r\nTokenԤ���� ���============================" );
		
		System.out.println("\r\nToken Finish ��ʼ============================" );
		// ���token
		String unsignTxFinish = tokenTest.createRawTokenFinishTx(100000, symbol, userAddress);
		// ǩ�����ף�����token-finisher��ǩ��
		String signTxFinish = tokenTest.signRawTx(tokenFinisher, "", unsignTxFinish, "1h", 1);
		String txhashFinish = tokenTest.submitTransaction(signTxFinish);
		
		// ���ݽ���hash��ѯ����
		tokenTest.querytxWithoutContent(txhashFinish);
		System.out.println("\r\nToken Finish ���============================" );
		
		System.out.println("\r\n��ѯ��ַ�е�token���============================" );
		// ��ѯ��ַ�е�token���
		List<String> addressList = new ArrayList<String>();
		addressList.add(userAddress);
		tokenTest.getTokenBalance(addressList, "token", symbol);
		
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
	private String dumpPriKey(String addr) {
		String accResult;
		try {
			accResult = client.dumpPrivkey(addr);
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
	
	public void querytxWithContent(String txHash) throws InterruptedException {
		
		QueryTransactionResult queryTransaction1 = new QueryTransactionResult();
		// ���������ܻ���һ����ʱ����ߵȴ�һ��
		for (int i = 0; i < 50; i++) {
			Thread.sleep(1000);
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
		
		// �������߶�
		// TODO:�����Ҫ�������쳣���Է�queryTransaction1�ǿյ����쳣
		System.out.println("����߶ȣ�" + queryTransaction1.getHeight());
		// �������ʱ��
		System.out.println("����ʱ�䣺" + queryTransaction1.getBlocktime());
		// ���from��ַ
		System.out.println("from��ַ��" + queryTransaction1.getFromaddr());
		// ���to��ַ
		System.out.println("to��ַ��" + queryTransaction1.getTx().getTo());
		// �������hash
		System.out.println("����hash:" + client.getBlockHash(queryTransaction1.getHeight()));

		// ��ý�����	
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
		System.out.println("������Ϣ��" + s);
		
	}
	
	
public void querytxWithoutContent(String txHash) throws InterruptedException {
		
		QueryTransactionResult queryTransaction1 = new QueryTransactionResult();
		// ���������ܻ���һ����ʱ����ߵȴ�һ��
		for (int i = 0; i < 50; i++) {
			Thread.sleep(1000);
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
		
		// �������߶�
		// TODO:�����Ҫ�������쳣���Է�queryTransaction1�ǿյ����쳣
		System.out.println("����߶ȣ�" + queryTransaction1.getHeight());
		// �������ʱ��
		System.out.println("����ʱ�䣺" + queryTransaction1.getBlocktime());
		// �������hash
		System.out.println("����hash:" + client.getBlockHash(queryTransaction1.getHeight()));

		
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

}
