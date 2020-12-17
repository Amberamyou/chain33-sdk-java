package cn.chain33.javasdk.performance;

import java.util.Random;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.utils.TransactionUtil;

public class SubmitTx implements Runnable{
	    
	RpcClient client;
	String privateKey;
	String to;
    
    public SubmitTx(RpcClient client, String privateKey, String to) {
    	this.client = client;
    	this.privateKey = privateKey;
    	this.to = to;
    }

	@Override
	public void run() {
    	String execer = "user.writer";
    	String payLoad = getRamdonString(32);
    	long lastHeader;
		try {
			for (int i = 0; i < 100; i++) {
				lastHeader = client.getLastHeader().getHeight();
		    	String txEncode = TransactionUtil.createTransferTx(privateKey, to, execer, payLoad.getBytes(), TransactionUtil.DEFAULT_FEE, lastHeader);
		    	String hash = client.submitTransaction(txEncode);
		    	System.out.println(hash);
			}
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
