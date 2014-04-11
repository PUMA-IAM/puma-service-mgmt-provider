package puma.sp.mgmt.provider.metrics;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import puma.sp.mgmt.provider.pdps.central.CentralPUMAPDPManager;

@Controller
public class TimingController {

	@RequestMapping(value = "/metrics/central-pdp/results", method = RequestMethod.GET, produces="text/plain")
	public @ResponseBody String results() {
		return CentralPUMAPDPManager.getInstance().getMetrics();
	}
}