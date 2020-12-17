package cn.chain33.javasdk.performance;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import cn.chain33.javasdk.ccidCases.CommUtil;
import cn.chain33.javasdk.client.RpcClient;

public class Performance extends AbstractJavaSamplerClient{
	
    RpcClient client1 = new RpcClient(CommUtil.ip1, CommUtil.port);
    RpcClient client2 = new RpcClient(CommUtil.ip2, CommUtil.port);
    RpcClient client3 = new RpcClient(CommUtil.ip3, CommUtil.port);
    RpcClient client4 = new RpcClient(CommUtil.ip4, CommUtil.port);
                
    /**
     * 
     * @param args
     */
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {

    	String privateKey1 = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String privateKey2 = "27c4e203a8e7e91076d77d9a7d9b80e944c1ea444f0cab85f43ece57ef270cb2";
    	String privateKey3 = "62367161bbd106080e87b1d899fb1e9c26963916c59185b93b29b72b5235c264";
    	String privateKey4 = "5d8e063ea8f1d6f32809ddafe0e4777ab50c892d018a02a0e429c2c76485eef2";
    	
    	String toAddress;
		try {
			toAddress = client1.convertExectoAddr("user.writer");
	    	startThead(client1, privateKey1, toAddress);
	    	startThead(client2, privateKey2, toAddress);
	    	startThead(client3, privateKey3, toAddress);
	    	startThead(client4, privateKey4, toAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
    	return null;
    }
    
    private void startThead(RpcClient client, String privateKey, String toaddress) {
		SubmitTx st = new SubmitTx(client, privateKey, toaddress);
		Thread t = new Thread(st);
        t.run();
    }
    
    
    public static void main(String[] args) {

        Arguments params =new Arguments();
        JavaSamplerContext arg0 =new JavaSamplerContext(params);
        Performance test =new Performance();
        test.setupTest(arg0);
        test.runTest(arg0);
        test.teardownTest(arg0);
    }
    
}
