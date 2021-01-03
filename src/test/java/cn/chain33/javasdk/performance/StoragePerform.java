package cn.chain33.javasdk.performance;

import java.util.Random;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.utils.StorageUtil;

public class StoragePerform {

	RpcClient client = null;
	
	Account account = new Account();

	
	/**
	 * 
	 * @param args
	 */
	public void runTest(String ip, String port, String num) {

		int nThreads = Runtime.getRuntime().availableProcessors();
		System.out.println("线程数" + nThreads);

		try {

			client = new RpcClient(ip, Integer.parseInt(port));
			String privateKey = null;
			// 25*4个线程，每个线程构造并签名1万笔交易
			for (int i = 0; i < nThreads; i++) {
				privateKey = account.newAccountLocal().getPrivateKey();
				// 构造交易
				startthread1(privateKey, Integer.parseInt(num), client);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * 用于构造交易
	 * 
	 * @param client
	 * @param privateKey
	 * @param toaddress
	 */
	private void startthread1(String privateKey, int num, RpcClient client) {
		Thread1 st = new Thread1(privateKey, num, client);
		Thread t = new Thread(st);
		t.start();
	}


	/**
	 * 
	 * @author fkeit
	 *
	 */
	class Thread1 implements Runnable {

		String privateKey;
		int num;
		RpcClient client;

		public Thread1(String privateKey, int num, RpcClient client) {
			this.privateKey = privateKey;
			this.num = num;
			this.client = client;
		}

		@Override
		public void run() {
			String execer = "storage";
			String payLoad = getRamdonString(32);
			String txEncode = null;
			String hash = null;
			int count = 0;
			long start = System.currentTimeMillis();
			for (int i = 0; i < num; i++) {
				txEncode = StorageUtil.createOnlyNotaryStorage(payLoad.getBytes(), execer, privateKey);
				try {
					hash = client.submitTransaction(txEncode);
					if (hash != null) {
						count++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			
			long end = System.currentTimeMillis();
			System.out.println("构造及发送交易花费时间" + (end - start) + " 毫秒, 总共发送" + count + "笔交易");
		}

		/**
		 * 获取随机值
		 * 
		 * @param length
		 * @return
		 */
		public String getRamdonString(int length) {
			StringBuffer sb = new StringBuffer();
			String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			Random random = new Random();
			for (int i = 0; i < length; i++) {
				sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
			}

			return sb.toString();
		}

	}


	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("请带上[IP][端口号]以及[发送的交易数目]这三个参数");
			return;
		}
		String ip = args[0];
		String port = args[1];
		String num = args[2];
		StoragePerform test = new StoragePerform();
		test.runTest(ip, port, num);
	}

}
