package tt.heixiong.awesome.client;

import org.springframework.cloud.openfeign.FeignClient;
import tt.heixiong.awesome.contract.RequirementContract;

@FeignClient(value = "awesome", path = "/requirements")
public interface RequirementClient extends RequirementContract {
}
