package tt.heixiong.awesome.domain;

import lombok.Getter;
import lombok.Setter;
import okhttp3.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Student {

    public static final Object classConstant = new Object();

    private Long id;
    private String name;
    private Integer age;

    public static void main(String[] args) throws Exception {

        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30 ,TimeUnit.SECONDS).build();
        MediaType mediaType = MediaType.parse("application/json");
        String ss = "{\"query\":\"query queryLogs($condition: LogQueryCondition) {\\n    queryLogs(condition: $condition) {\\n        logs {\\n          serviceName\\n          serviceId\\n          serviceInstanceName\\n          serviceInstanceId\\n          endpointName\\n          endpointId\\n          traceId\\n          timestamp\\n          contentType\\n          content\\n          tags {\\n            key\\n            value\\n          }\\n        }\\n        total\\n    }}\",\"variables\":{\"condition\":{\"serviceId\":\"Y2FsbGJhY2s=.1\",\"keywordsOfContent\":[\"" + "83c291659fec43ec8a4ebbf22b662f26.5977.16700530827327707:rent.dcentralize.room.add" +  "\"],\"excludingKeywordsOfContent\":[],\"tags\":[],\"paging\":{\"pageNum\":\"1\",\"pageSize\":22,\"needTotal\":true},\"queryDuration\":{\"start\":\"2022-11-26 162630\",\"end\":\"2022-12-03 162630\",\"step\":\"SECOND\"}}}}";
        System.out.println("网关请求：" + ss);
        RequestBody body = RequestBody.create(ss, mediaType);
        Request request = new Request.Builder()
                .url("https://trace.ebaas.com/graphql")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("响应状态码：" + response.code());
        String resp = response.body().string();
        System.out.println("网关响应结果：" + Objects.requireNonNull(resp));
    }

}
