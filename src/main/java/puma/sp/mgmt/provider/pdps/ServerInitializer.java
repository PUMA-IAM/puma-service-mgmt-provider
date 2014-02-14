package puma.sp.mgmt.provider.pdps;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import puma.rmi.pdp.mgmt.PDPRegistryRemote;
import puma.sp.mgmt.provider.pdps.app.ApplicationPDPManager;
import puma.sp.mgmt.provider.pdps.central.CentralPUMAPDPManager;

/**
 * Class used for initializing all servers (RMI and alike) when this project is
 * loaded into TomCat.
 * 
 * @author Maarten Decat
 * 
 */
public class ServerInitializer implements ServletContextListener {

	private static final int RMI_REGISITRY_PORT = 2050;

	private static final Logger logger = Logger
			.getLogger(ServerInitializer.class.getName());

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// 1. Set up the RMI registry and the Application PDP Registry listening
		// on it
		try {
			Registry registry;
			try {
				registry = LocateRegistry.createRegistry(RMI_REGISITRY_PORT);
				logger.info("Created new RMI registry");
			} catch (RemoteException e) {
				// MDC: I hope this means the registry already existed.
				registry = LocateRegistry.getRegistry(RMI_REGISITRY_PORT);
				logger.info("Reusing existing RMI registry");
			}
			PDPRegistryRemote stub = (PDPRegistryRemote) UnicastRemoteObject
					.exportObject(ApplicationPDPManager.getInstance(), 0);
			registry.bind("application-pdp-registry", stub);
			logger.info("Application PDP Registry up and running (available using RMI with name \"application-pdp-registry\")");
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Failed to set up the Application PDP Registry", e);
		}

		// 2. Connect to the central PUMA PDP over RMI
		CentralPUMAPDPManager.getInstance(); // just getting the instance calls
												// the constructor and
												// initializes the manager
	}

}
