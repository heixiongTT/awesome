package tt.heixiong.awesome.api;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "awesome")
public interface ValidateApi {
}
