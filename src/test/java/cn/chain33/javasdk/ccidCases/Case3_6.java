package cn.chain33.javasdk.ccidCases;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.StringUtil;

public class Case3_6 {

	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
	
	RpcClient client1 = new RpcClient("132.232.87.45", 8801);

	Account account = new Account();
	
	String content = "存证内容";
	
	/**
	 * 3.6.3	交易确认时延
	 * @throws Exception 
	 */
	@Test
	public void Case3_6_3_1() throws Exception {
		
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		
		String txEncode = null;
		String submitTransaction = null;
		long duration = 0;
		long total = 0;
		int term = 100;
		for (int i = 0; i < term; i++) {
			txEncode = StorageUtil.createOnlyNotaryStorage((content + "_" + i).getBytes(), execer, privateKey);
			submitTransaction = client.submitTransaction(txEncode);
			
			long start = System.currentTimeMillis();
			System.out.println("第" + i + "笔交易上链hash为：" + submitTransaction + "; 上链开始时间为：" + start);
			while (true) {
				try {
					String result = queryStorage(submitTransaction, i);
					if (StringUtil.isNotEmpty(result)) {
		        		long end = System.currentTimeMillis();
		        		duration = end - start;
						System.out.println("第" + i + "笔交易上链hash为：" + submitTransaction + "; 上链成功时间为：" + System.currentTimeMillis() + "消耗时间为：" + duration + "毫秒");
		        		total = total + duration;
						break; 
					}
				} catch (Exception e) {
					
				}
			}
		}
		long average = total/term;
		System.out.println("用户平均消耗时间为:" + average + "毫秒");
	}
	
	/**
	 * 3.6.3	交易确认时延
	 * @throws Exception 
	 */
	@Test
	public void Case3_6_3_2() throws Exception {
		
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey1 = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		
		// 签名用的私钥
		String privateKey2 = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
		
		String txEncode1 = null;
		String submitTransaction1 = null;
		
		String txEncode2 = null;
		String submitTransaction2 = null;
		long duration1 = 0;
		long total1 = 0;
		
		long duration2 = 0;
		long total2 = 0;
		int term = 100;
		for (int i = 0; i < term; i++) {
			txEncode1 = StorageUtil.createOnlyNotaryStorage((content + "_" + i).getBytes(), execer, privateKey1);
			submitTransaction1 = client.submitTransaction(txEncode1);
			
			txEncode2 = StorageUtil.createOnlyNotaryStorage((content + "_" + i).getBytes(), execer, privateKey2);
			submitTransaction2 = client1.submitTransaction(txEncode2);
			
			long start = System.currentTimeMillis();
			System.out.println("用户1第" + i + "笔交易上链hash为：" + submitTransaction1 + "; 上链开始时间为：" + start);
			System.out.println("用户2第" + i + "笔交易上链hash为：" + submitTransaction2 + "; 上链开始时间为：" + start);
			
			while (true) {
				try {
					String result1 = queryStorage(submitTransaction1, i);
					if (StringUtil.isNotEmpty(result1)) {
		        		long end = System.currentTimeMillis();
		        		duration1 = end - start;
						System.out.println("用户1第" + i + "笔交易上链hash为：" + submitTransaction1 + "; 上链成功时间为：" + System.currentTimeMillis() + "消耗时间为：" + duration1 + "毫秒");
		        		total1 = total1 + duration1;
					}
					
					String result2 = queryStorage(submitTransaction2, i);
					if (StringUtil.isNotEmpty(result2)) {
		        		long end = System.currentTimeMillis();
		        		duration2 = end - start;
						System.out.println("用户2第" + i + "笔交易上链hash为：" + submitTransaction2 + "; 上链成功时间为：" + System.currentTimeMillis() + "消耗时间为：" + duration2 + "毫秒");
						total2 = total2 + duration2;
						break;
					}
				} catch (Exception e) {
					
				}
			}
		}
		
		long average1 = total1/term;
		System.out.println("用户1平均消耗时间为:" + average1 + "毫秒");
		
		long average2 = total2/term;
		System.out.println("用户2平均消耗时间为:" + average2 + "毫秒");
	}
	
	
	public String queryStorage(String hash, int i) throws Exception {
		// contentStore
		JSONObject resultJson = client.queryStorage(hash);
		
		JSONObject resultArray;
    	// 内容型存证解析
    	resultArray = resultJson.getJSONObject("contentStorage");
    	String content = resultArray.getString("content");
    	byte[] contentByte = HexUtil.fromHexString(content);
    	String result = new String(contentByte,"UTF-8");
    	return result;

	}
}