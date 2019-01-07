package cn.chain33.javasdk.client;

import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class MedicalClient {

	// ������������jsonRPC�ӿ�
	RpcClient client = new RpcClient("62.234.214.30",8801);
	
	// ҩƷ������1�˻���Ӧ��˽Կ���滻��ʵ�����������ɵ��Ǹ�˽Կ��
	// ˽Կ��������ǩ������
	private static String producerSecret = "0x17328cd89c38416b19ca135224942d3968897ce3752bfa3e4e63f4553f9ebfef";
	
	// ҽԺ1�˻���Ӧ��˽Կ���滻��ʵ�����������ɵ��Ǹ�˽Կ��
	private static String hospitalSecret = "0xac1a788374a130d7c476467e0112c681366c9e6fa8fc60f3080005394e7a2899";
	
	// �����̵��˻���Ӧ��˽Կ���滻��ʵ�����������ɵ��Ǹ�˽Կ��
	private static String agencySecret = "0xa82018ace74abb73bcacad99a86262574315572db7c000a47f01cdc77d6c351c";

	public static void main(String []args) throws Exception {
		
		MedicalClient test = new MedicalClient();
		// ҩƷ������������������
		String content = "{\"ҩƷ���\":\"MEDICAL000001\",\"ҩƷͼƬHASH\":\"933a925767fe0ae387947f41690fc054\",\"ҩƷ����\":\"�Ϻ�\",\"������\":\"2019-12-12\",\"������\":\"�Ϻ�xxxxxx���޹�˾\"}";
		String txHash = test.sendtx(content, producerSecret);
		
		System.out.println("����hash��" + txHash);
		
		// ���ݽ���hash��ѯ����
		test.querytx(txHash);
		
		// ҽԺ������������
		content = "{\"ҩƷ���\":\"MEDICAL000001\",\"ҩƷͼƬHASH\":\"933a925767fe0ae387947f41690fc054\",\"ҩƷ����\":\"�Ϻ�\",\"������\":\"2019-12-12\",\"ҽԺ����\":\"�Ϻ�xxxxxxҽԺ\"}";
		txHash = test.sendtx(content, hospitalSecret);
		
		System.out.println("����hash��" + txHash);
		
		// ���ݽ���hash��ѯ����
		test.querytx(txHash);
		
		// ��������������
		content = "{\"ҩƷ���\":\"MEDICAL000001\",\"ҩƷͼƬHASH\":\"933a925767fe0ae387947f41690fc054\",\"ҩƷ����\":\"�Ϻ�\",\"������\":\"2019-12-12\",\"����������\":\"xxxxxx������\"}";
		txHash = test.sendtx(content, agencySecret);
		
		System.out.println("����hash��" + txHash);
		
		// ���ݽ���hash��ѯ����
		test.querytx(txHash);
	}
	
	// ���ͽ���
	public String sendtx(String content, String Secret) throws Exception {
		// user.write�����Լ����
		String contractName = "user.write";
		String creareTx = TransactionUtil.createTx(Secret, contractName, content, 10000000);
		String txHash = client.submitTransaction(creareTx);
		
		return txHash;
	}
	
	// ��ѯ�����Ǻ�����
	public void querytx(String txHash) throws InterruptedException {
		
		QueryTransactionResult queryTransaction1 = new QueryTransactionResult();
		// ���������ܻ���һ����ʱ����ߵȴ�һ��
		for (int i = 0; i < 10; i++) {
			queryTransaction1 = client.queryTransaction(txHash);
			if (null == queryTransaction1) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
		
		// �������߶�
		// TODO:�����Ҫ�������쳣���Է�queryTransaction1�ǿյ����쳣
		System.out.println("����߶ȣ�" + queryTransaction1.getHeight());
		// �������ʱ��
		System.out.println("����ʱ�䣺" + queryTransaction1.getBlocktime());
		// ���from��ַ
		System.out.println("from��ַ��" + queryTransaction1.getFromaddr());
		// ���to��ַ
		System.out.println("to��ַ��" + queryTransaction1.getTx().getTo());
		// �������hash
		System.out.println("����hash:" + client.getBlockHash(queryTransaction1.getHeight()));

		// ��ý�����	
		String payload = queryTransaction1.getTx().getRawpayload();
		String s = payload.substring(2, payload.length());
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b .length; i++) {
			try {
				b [i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(b, "GB2312");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.out.println("������Ϣ��" + s);
		
	}
}
