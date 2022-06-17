package cn.chain33.javasdk.server.http.handler;

import cn.chain33.javasdk.model.enums.EncodeType;
import cn.chain33.javasdk.model.protobuf.BlockchainProtobuf;
import com.googlecode.protobuf.format.JsonFormat;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @authoer lhl
 * @date 2022/6/15 上午9:32
 */
public class BlockHeaderHandler extends Handler implements HttpHandler {
    BlockHeaderHandler(EncodeType encodeType,Outflow outflow){
        this.setEncodeType(encodeType);
        this.setOutflow(outflow);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("---url:---- " + httpExchange.getRequestURI().getQuery());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream ungzip = new GZIPInputStream(httpExchange.getRequestBody());
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        //判断编码类型
        if (this.getEncodeType().equals(EncodeType.JSON)) {
            BlockchainProtobuf.HeaderSeqs.Builder builder = BlockchainProtobuf.HeaderSeqs.newBuilder();
            JsonFormat jsonFormat = new JsonFormat();
            //json编码转protobuf编码
            ByteArrayInputStream inputStream= new ByteArrayInputStream(out.toByteArray());
            jsonFormat.merge(inputStream, builder);
            BlockchainProtobuf.HeaderSeqs headerSeqs = builder.build();
            this.getOutflow().process(headerSeqs);

        } else if (this.getEncodeType().equals(EncodeType.PROTOBUFF)) {
            //protobuf解析
            BlockchainProtobuf.HeaderSeqs headerSeqs = BlockchainProtobuf.HeaderSeqs.parseFrom(out.toByteArray());
            //向外输出解析结果
            this.getOutflow().process(headerSeqs);
        }
        httpExchange.getResponseBody().write("ok".getBytes());

    }

    @Override
    public String getURI() {
        return "/header/"+super.getURI();
    }
}
