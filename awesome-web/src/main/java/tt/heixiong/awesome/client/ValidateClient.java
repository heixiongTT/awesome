package tt.heixiong.awesome.client;

import org.springframework.cloud.openfeign.FeignClient;
import tt.heixiong.awesome.contract.ValidateContract;

@FeignClient(value = "awesome")
public interface ValidateClient extends ValidateContract {
}
