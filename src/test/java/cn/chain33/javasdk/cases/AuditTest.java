package cn.chain33.javasdk.cases;

import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;

public class AuditTest {
	
	String ip = "2.20.105.227";
	//String ip = "132.232.76.48";
    RpcClient client = new RpcClient(ip, 8801);
	
	/**
	 * Case03_06：节点的审计性能
	 * 
	 * @throws InterruptedException
	 * @description 本地构造主链主积分转账交易
	 */
	@Test
	public void auditTxs() throws InterruptedException {
		
        long beginHeight = 1L;

        long currHeight = beginHeight;
        long txCount = 1;

        while (true) {
			long leastHeight = client.getLastHeader().getHeight();
            //如果当前的高度大于等于区块链最新高度
            if (currHeight >= leastHeight) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException ie) {
                }
                continue;
            }

            String blockHash = client.getBlockHash(currHeight);
            if (blockHash != null && blockHash.trim().length() > 0) {
                List<String> txHashs = client.getBlockOverview(blockHash).getTxHashes();
                for (String txHash : txHashs) {
                    if (txHash != null && txHash.trim().length() > 0) {
                    	QueryTransactionResult txDetail = client.queryTransaction(txHash);
                    	if (txDetail.getTx().getExecer().equals("storage")) {
                            System.out.println(txDetail);
                    	}
                    }
                	txCount++;
                	if (txCount%100 == 0) {
                		System.out.println("完成" + txCount + "笔交易的检查！" + System.currentTimeMillis());
                	}
                }
            }
                    	
            if (txCount >= 100000) {
            	break;
            }
            currHeight++;
        }
	}

}
