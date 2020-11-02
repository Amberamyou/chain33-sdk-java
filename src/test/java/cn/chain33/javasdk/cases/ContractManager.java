package cn.chain33.javasdk.cases;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class ContractManager {
	
	
	String ip = "2.20.105.227";
	// String ip = "132.232.76.48";
    RpcClient client = new RpcClient(ip, 8801);
    
    String content = "疫情发生后，NPO法人仁心会联合日本湖北总商会等四家机构第一时间向湖北捐赠3800套杜邦防护服，包装纸箱上用中文写有“岂曰无衣，与子同裳”。这句诗词出自《诗经·秦风·无衣》，翻译成白话的意思是“谁说我们没衣穿？与你同穿那战裙”。不料，这句诗词在社交媒体上引发热议，不少网民赞叹日本人的文学造诣。实际上，NPO法人仁心会是一家在日华人组织，由在日或有留日背景的医药保健从业者以及相关公司组成的新生公益组织。NPO法人仁心会事务局告诉环球时报-环球网记者，由于第一批捐赠物资是防护服，“岂曰无衣，与子同裳”恰好可以表达海外华人华侨与一线医护人员共同战胜病毒的同仇敌忾之情，流露出对同胞的守护之爱。";
    
    @Test
    public void testEvmContract() throws InterruptedException {
        String privateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";

        String code = "0x608060405234801561001057600080fd5b506298967f60005560ac806100266000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c806360fe47b11460375780636d4ce63c146053575b600080fd5b605160048036036020811015604b57600080fd5b5035606b565b005b60596070565b60408051918252519081900360200190f35b600055565b6000549056fea26469706673582212206850c96ceb3091ba2b0454750fbb02238fe0e3765327ec62c47ef4acf0b3ff2b64736f6c63430006000033";

        String abi = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"}],\"name\":\"set\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        // 部署合约
        String txEncode = EvmUtil.createEvmContract(HexUtil.fromHexString(code), "", "evm-sdk-test", abi, privateKey);
        String submitTransaction = client.submitTransaction(txEncode);
        String contractName = submitTransaction;
        System.out.println(submitTransaction);
        Thread.sleep(60000);

        // 调用合约
        txEncode = EvmUtil.callEvmContract("".getBytes(),"", 0, "get()", contractName, privateKey);
        submitTransaction = client.submitTransaction(txEncode);
        System.out.println(submitTransaction);
        Thread.sleep(60000);

        // 销毁合约
        txEncode = EvmUtil.destroyEvmContract(contractName, privateKey);
        submitTransaction = client.submitTransaction(txEncode);
        System.out.println(submitTransaction);
        Thread.sleep(60000);

        // 再次调用合约
        txEncode = EvmUtil.callEvmContract("".getBytes(),"", 0, "get()", contractName, privateKey);
        submitTransaction = client.submitTransaction(txEncode);
        System.out.println(submitTransaction);
    }
    
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
     * 隐私存证模型
     * @throws Exception 
     */
	@Test
	public void EncryptNotaryStorePara() throws Exception {
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
		JSONObject resultJson = client.queryStorage("0xdd60c2f507673e3a29c9eb548b07f6d2789ec701de813a44a7bbafac9c3711f1");
		
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
