package cn.chain33.javasdk.cases;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;

public class Chain33IntegTest {
	
	private static RpcClient CLIENT = new RpcClient("119.45.1.41", 8801);
    private static final Long TX_HEIGHT_OFFSET = 4611686018427387904L;
    private static final String CODE = "0x608060405234801561001057600080fd5b506298967f60005560ac806100266000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c806360fe47b11460375780636d4ce63c146053575b600080fd5b605160048036036020811015604b57600080fd5b5035606b565b005b60596070565b60408051918252519081900360200190f35b600055565b6000549056fea26469706673582212206850c96ceb3091ba2b0454750fbb02238fe0e3765327ec62c47ef4acf0b3ff2b64736f6c63430006000033";
    private static final String ABI = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"x\",\"type\":\"uint256\"}],\"name\":\"set\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    private static final String ACCOUNT = "16ui7XJ1VLM7YXcNhWwWsWS6CRC3ZA2sJ1";
    private static final String PRIVATE_KEY = "0x85bf7aa29436bb186cac45ecd8ea9e63e56c5817e127ebb5e99cd5a9cbfe0f23";
 
 
    @Test
    public void testEvmContract() throws Exception {
 
        // 部署合约
        String txEncode = EvmUtil.createEvmContract(HexUtil.fromHexString(CODE), "", "", ABI, PRIVATE_KEY);
        System.out.println("testEvmContract txEncode:");
        System.out.println(txEncode);
        String submitTransaction = CLIENT.submitTransaction(txEncode);
        System.out.println(submitTransaction);
        Thread.sleep(1000);
 
    }
 
    @Test
    public void testEvmContractAnotherWay() throws Exception {
 
        // 部署合约
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("execer", "evm");
        jsonObject.put("actionName", "CREATE_CALL");
        jsonObject.put("isCreate", true);
        jsonObject.put("code", CODE);
        jsonObject.put("abi", ABI);
        jsonObject.put("note", "");
        jsonObject.put("alias", "evm-sdk-test");
        jsonObject.put("fee", 1000000L);
        String rawTransaction = CLIENT.createTransaction("evm", "CreateCall", jsonObject);
        String txEncode = CLIENT.signRawTx(ACCOUNT, PRIVATE_KEY, rawTransaction, TX_HEIGHT_OFFSET.toString(), 0);
        System.out.println("testEvmContractAnotherWay txEncode:");
        System.out.println(txEncode);
        String submitTransaction = CLIENT.submitTransaction(txEncode);
        System.out.println(submitTransaction);
        Thread.sleep(1000);
    }

}
