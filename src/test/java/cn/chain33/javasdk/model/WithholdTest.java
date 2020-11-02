package cn.chain33.javasdk.model;

import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.utils.StorageUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 *	代扣交易： 在BTY主链+平行链的架构下，平行链上的交易是需要在主链上支付手续费，这对于平行链上的用户并不友好，所以采用代扣的方式，平行链上所有用户在主链上的交易手续费都从代扣里出，用户可以不用关心主链手续费的事
 * @author fkeit
 */
public class WithholdTest {
	
    String ip = "47.89.12.159";
    RpcClient client = new RpcClient(ip, 8901);
    
    
    String content = "疫情发生后，NPO法人仁心会联合日本湖北总商会等四家机构第一时间向湖北捐赠3800套杜邦防护服，包装纸箱上用中文写有“岂曰无衣，与子同裳”。这句诗词出自《诗经·秦风·无衣》，翻译成白话的意思是“谁说我们没衣穿？与你同穿那战裙”。不料，这句诗词在社交媒体上引发热议，不少网民赞叹日本人的文学造诣。实际上，NPO法人仁心会是一家在日华人组织，由在日或有留日背景的医药保健从业者以及相关公司组成的新生公益组织。NPO法人仁心会事务局告诉环球时报-环球网记者，由于第一批捐赠物资是防护服，“岂曰无衣，与子同裳”恰好可以表达海外华人华侨与一线医护人员共同战胜病毒的同仇敌忾之情，流露出对同胞的守护之爱。";
	
    // 实际交易签名私钥
    String privateKey = "53a601fb5f6de0f4002397cdb7d1e0e6dc655392cacdbe36ede06353c444cfb2";
    
    // 代扣交易签名私钥
    String withHoldPrivateKey = "c0916148e5ccf080e3d1363d7ff854cd46dfb99a0886d1eee7df881165396756";
    
    // 存证智能合约的名称
    String execer = "user.p.healthylifechain.storage";
    
    /**
	 * 构造实际交易体（转积分，存证等），下面以存证为例
	 */
	private String contentStore(String privateKey) {
		String txEncode = StorageUtil.createOnlyNotaryStorage(content.getBytes(), execer, privateKey);
		return txEncode;
		
	}
	
	@Test
	public void createWithholdTx() {
		String contentTx = contentStore(privateKey);
		
        //create no balance 传入地址为空
        String createNoBalanceTx = client.createNoBalanceTx(contentTx, "");
        // 平行链转账时，实际to的地址填在payload中，外层的to地址对应的是合约的地址
        String contranctAddress = client.convertExectoAddr(execer);
        // 解析交易
        List<DecodeRawTransaction> decodeRawTransactions = client.decodeRawTransaction(createNoBalanceTx);
        
        String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contranctAddress, privateKey, withHoldPrivateKey);
        System.out.println(hexString);
        String submitTransaction = client.submitTransaction(hexString);
	}

}
