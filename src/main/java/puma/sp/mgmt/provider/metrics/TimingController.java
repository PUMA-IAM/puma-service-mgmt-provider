/*******************************************************************************
 * Copyright 2014 KU Leuven Research and Developement - iMinds - Distrinet 
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 *    Administrative Contact: dnet-project-office@cs.kuleuven.be
 *    Technical Contact: maarten.decat@cs.kuleuven.be
 *    Author: maarten.decat@cs.kuleuven.be
 ******************************************************************************/
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
	
	@RequestMapping(value = "/metrics/central-pdp/reset", method = RequestMethod.GET)
	public @ResponseBody void reset() {
		CentralPUMAPDPManager.getInstance().resetMetrics();
	}
}
