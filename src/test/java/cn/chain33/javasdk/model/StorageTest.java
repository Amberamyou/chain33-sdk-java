package cn.chain33.javasdk.model;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 *	包含内容存证, 哈希存证,链接存证,隐私存证,分享隐私存证几个接口（联盟链场景）
 * @author fkeit
 */
public class StorageTest {
	
	// 联盟链节点IP
	String ip = "联盟链节点IP";
	// 平行链服务端口
	int port = 8801;
    RpcClient client = new RpcClient(ip, port);
	    
    String content = "疫情发生后，NPO法人仁心会联合日本湖北总商会等四家机构第一时间向湖北捐赠3800套杜邦防护服，包装纸箱上用中文写有“岂曰无衣，与子同裳”。这句诗词出自《诗经·秦风·无衣》，翻译成白话的意思是“谁说我们没衣穿？与你同穿那战裙”。不料，这句诗词在社交媒体上引发热议，不少网民赞叹日本人的文学造诣。实际上，NPO法人仁心会是一家在日华人组织，由在日或有留日背景的医药保健从业者以及相关公司组成的新生公益组织。NPO法人仁心会事务局告诉环球时报-环球网记者，由于第一批捐赠物资是防护服，“岂曰无衣，与子同裳”恰好可以表达海外华人华侨与一线医护人员共同战胜病毒的同仇敌忾之情，流露出对同胞的守护之爱。";
    
	/**
	 * 内容存证
	 */
	@Test
	public void contentStore() {
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		System.out.println(submitTransaction);
		
	}

	/**
	 * 哈希存证模型，推荐使用sha256哈希，限制256位得摘要值
	 */
	@Test
	public void hashStore() {
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
		String txEncode = StorageUtil.createHashStorage(contentHash, execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		System.out.println(submitTransaction);
		
	}
	
    /**
     * 链接存证模型
     */
	@Test
	public void hashAndLinkStore() {
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		String link = "https://cs.33.cn/product?hash=13mBHrKBxGjoyzdej4bickPPPupejAGvXr";
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
		String txEncode = StorageUtil.createLinkNotaryStorage(link.getBytes(), contentHash, execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		System.out.println(submitTransaction);
		
	}
	
    /**
     * 隐私存证模型
     * @throws Exception 
     */
	@Test
	public void EncryptNotaryStore() throws Exception {
		// 存证智能合约的名称
		String execer = "storage";
		// 签名用的私钥
		String privateKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
		
		// 生成AES加密KEY
		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(aesKeyHex);
		System.out.println("key:" + HexUtil.toHexString(key));
		// 生成iv
		byte[] iv = AesUtil.generateIv();
		// 对明文进行加密
		byte[] encrypt = AesUtil.encrypt(content, key, iv);
		String decrypt = AesUtil.decrypt(encrypt, HexUtil.toHexString(key));
		System.out.println("decrypt:" + decrypt);
		byte[] contentHash = TransactionUtil.Sha256(content.getBytes("utf-8"));
		String txEncode = StorageUtil.createEncryptNotaryStorage(encrypt,contentHash, iv, "", "", execer, privateKey);
		String submitTransaction = client.submitTransaction(txEncode);
		System.out.println(submitTransaction);
		
	}
	
	
	/**
	 * 根据hash查询存证结果
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void queryStorage() throws UnsupportedEncodingException {
		// contentStore
		JSONObject resultJson = client.queryStorage("0x401f043696500030d49a511505b5c703e943382082b1154880e753acacb3d443");
		
		JSONObject resultArray;
        if (resultJson.containsKey("linkStorage")) {
        	// hash及link型存证
        	resultArray = resultJson.getJSONObject("linkStorage");
        	String link = resultArray.getString("link");
        	String hash = resultArray.getString("hash");
        	byte[] linkByte = HexUtil.fromHexString(link);
        	String linkresult = new String(linkByte,"UTF-8");
        	System.out.println("存证link是:" + linkresult);
        	System.out.println("存证hash是:" + hash);
        } else if (resultJson.containsKey("hashStorage")) {
        	// hash型存证解析
        	resultArray = resultJson.getJSONObject("hashStorage");
        	String hash = resultArray.getString("hash");
        	System.out.println("链上读取的hash是:" + hash);
        	byte[] contentHash = TransactionUtil.Sha256(content.getBytes());
        	String result = HexUtil.toHexString(contentHash);
    		System.out.println("存证前的hash是:" + result);
        } else if (resultJson.containsKey("encryptStorage")) {
            //隐私存证
            String desKey = "ba940eabdf09ee0f37f8766841eee763";
            resultArray = resultJson.getJSONObject("encryptStorage");
            String content = resultArray.getString("encryptContent");
            byte[] fromHexString = HexUtil.fromHexString(content);
            String decrypt = AesUtil.decrypt(fromHexString, desKey);
            System.out.println(decrypt);
        } else {
        	// 内容型存证解析
        	resultArray = resultJson.getJSONObject("contentStorage");
        	String content = resultArray.getString("content");
        	byte[] contentByte = HexUtil.fromHexString(content);
        	String result = new String(contentByte,"UTF-8");
        	System.out.println("存证内容是:" + result);
        }
	}

}
