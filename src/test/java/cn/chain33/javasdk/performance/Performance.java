package cn.chain33.javasdk.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import cn.chain33.javasdk.ccidCases.CommUtil;
import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.rpcresult.BlockResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class Performance extends AbstractJavaSamplerClient {

	RpcClient client1 = null;
	RpcClient client2 = null;
	RpcClient client3 = null;
	RpcClient client4 = null;

	long blockheight = 0;

	// 交易encode队列
	private static Queue<String> queue = new LinkedBlockingQueue<String>();
	
	Account account = new Account();

	/**
	 * 
	 * @param args
	 */
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {

		String privateKey1 = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
//		String privateKey2 = "27c4e203a8e7e91076d77d9a7d9b80e944c1ea444f0cab85f43ece57ef270cb2";
//		String privateKey3 = "62367161bbd106080e87b1d899fb1e9c26963916c59185b93b29b72b5235c264";
//		String privateKey4 = "5d8e063ea8f1d6f32809ddafe0e4777ab50c892d018a02a0e429c2c76485eef2";

		String toAddress;
		try {
			// toAddress = client1.convertExectoAddr("user.writer");
			toAddress = "1GQ4PREPvvnjyG1uiBjBqBHvVD37DN4W2e";

			client1 = new RpcClient(CommUtil.ip1, CommUtil.port);
//			client2 = new RpcClient(CommUtil.ip2, CommUtil.port);
//			client3 = new RpcClient(CommUtil.ip3, CommUtil.port);
//			client4 = new RpcClient(CommUtil.ip4, CommUtil.port);

			// 读区块高度
			startthread1(client1);
			Thread.sleep(1000);

			// 构造交易及签名
//			for (int i = 0; i < 2; i++) {
				startthread2(privateKey1, toAddress, "node1");
//				startthread2(privateKey2, toAddress, "node2");
//				startthread2(privateKey3, toAddress, "node3");
//				startthread2(privateKey4, toAddress, "node4");
//			}

			// 发磅交易
//			for (int j = 0; j < 2; j++) {
				startthread3(client1, "node1");
//				startthread3(client2, "node2");
//				startthread3(client3, "node3");
//				startthread3(client4, "node4");
//			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 用于读取区块高度
	 */
	private void startthread1(RpcClient client) {
		System.out.println("Thead1启动，获取区块高度，建立rpc连接");
		Thread1 st = new Thread1(client);
		Thread t = new Thread(st);
		t.start();
	}

	/**
	 * 用于构造交易
	 * 
	 * @param client
	 * @param privateKey
	 * @param toaddress
	 */
	private void startthread2(String privateKey, String toaddress, String threadName) {
		Thread2 st = new Thread2(privateKey, toaddress, threadName);
		Thread t = new Thread(st);
		t.start();
	}

	/**
	 * 用于构造交易
	 * 
	 * @param client
	 * @param privateKey
	 * @param toaddress
	 */
	private void startthread3(RpcClient client, String threadName) {
		Thread3 st = new Thread3(client, threadName);
		Thread t = new Thread(st);
		t.start();
	}

	/**
	 * 建立连接，取区块高度
	 * 
	 * @author fkeit
	 *
	 */
	class Thread1 implements Runnable {

		RpcClient client;

		public Thread1(RpcClient client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				while (true) {
					BlockResult result = client.getLastHeader();
					if (result == null) {
						Thread.sleep(1000);
					} else {
						blockheight = result.getHeight();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @author fkeit
	 *
	 */
	class Thread2 implements Runnable {

		String privateKey;
		String to;
		String threadName;

		public Thread2(String privateKey, String to, String threadName) {
			this.privateKey = privateKey;
			this.to = to;
			this.threadName = threadName;
		}

		@Override
		public void run() {
			String execer = "user.writer";
			String payLoad = getRamdonString(32);
			List<String> encodeList = new ArrayList<String>();
			String txEncode = null;
			try {
				long start = System.currentTimeMillis();
				for (int i = 0; i < 100000; i++) {
					txEncode = TransactionUtil.createTransferTx(privateKey, to, execer, payLoad.getBytes(),
							TransactionUtil.DEFAULT_FEE, blockheight);
					queue.offer(txEncode);
					encodeList.add(txEncode);
					if (i == 99999) {
						queue.offer("exit");
					}
					
					synchronized (queue) {
						queue.notify();
					}
				}
				long end = System.currentTimeMillis();
				System.out.println(threadName + "构造交易花费时间" + (end - start));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	/**
	 * 发送交易
	 * 
	 * @author fkeit
	 *
	 */
	class Thread3 implements Runnable {
		RpcClient client;
		String threadName;

		public Thread3(RpcClient client, String threadName) {
			this.client = client;
			this.threadName = threadName;
		}

		@Override
		public void run() {

			String hash = null;
			List<String> resultList = new ArrayList<String>();
			List<String> errList = new ArrayList<String>();
			List<String> encodeList = new ArrayList<String>();
			String txEncode = null;
			long start = System.currentTimeMillis();
			while (true) {
				try {
					if (queue.size() > 0) {
						// System.out.println(queue.size());
						txEncode = queue.poll();
						encodeList.add(txEncode);
						if ("exit".equals(txEncode)) {
							break;
						}

						hash = client.submitTransaction(txEncode);
						resultList.add(hash);
						

						// System.out.println(hash);
					} else {
						synchronized (queue) {
							try {
								queue.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e) {
					errList.add(e.getMessage() + ":" + txEncode);
					continue;
					
				}
			}
			
			long end = System.currentTimeMillis();
			System.out.println(threadName + "发送交易花费时间" + (end - start));
			
			System.out.println(threadName + "成功的交易数目是：" + resultList.size());
			System.out.println(threadName + "失败的交易数目是：" + errList.size());

		}

	}

	public static void main(String[] args) {

		Arguments params = new Arguments();
		JavaSamplerContext arg0 = new JavaSamplerContext(params);
		Performance test = new Performance();
		test.setupTest(arg0);
		test.runTest(arg0);
		test.teardownTest(arg0);
	}

}
