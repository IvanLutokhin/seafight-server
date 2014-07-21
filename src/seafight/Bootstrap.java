package seafight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import seafight.beans.SelectorBean;
import seafight.communication.network.MasterSelectorThread;
import seafight.communication.network.PolicySelectorThread;
import seafight.managers.CombatManager;
import seafight.managers.ResourceManager;
import seafight.managers.ThreadPoolManager;

public class Bootstrap {
	private static final Logger logger = Logger.getLogger(Bootstrap.class);
	
	public Bootstrap() {
		// Initialization resources manager
		try {
			ResourceManager.getInstance().load("./conf/beans/");
			logger.info("Resource manager was initialized!");
		} catch (IOException e) { logger.error(e); }
		
		// Initialization thread pool manager
		ThreadPoolManager.getInstance();
		logger.info("Thread pool manager was initialized!");

		// Initialization combat manager
		CombatManager.getInstance().start();
		logger.info("Combat manager was initialized!");
		
		// Initialization policy selector
		try {
			SelectorBean policySelectorBean = (SelectorBean) ResourceManager.getInstance().get("selector-policy");
			if(policySelectorBean == null)
				throw new FileNotFoundException("Policy selector not found configuration file \"selector-policy.xml\"");
			
			PolicySelectorThread policySelectorThread = new PolicySelectorThread();
			policySelectorThread.bind(policySelectorBean.getHost(), policySelectorBean.getPort());
			policySelectorThread.start();
			
			logger.info("Policy selector was bound on " + policySelectorBean.getHost() + ":" + policySelectorBean.getPort());
		} catch (IOException e) { logger.error(e); }
		
		// Initialization master selector
		try {
			SelectorBean masterSelectorBean = (SelectorBean) ResourceManager.getInstance().get("selector-master");
			if(masterSelectorBean == null)
				throw new FileNotFoundException("Master selector not found configuration file \"selector-master.xml\"");
			
			MasterSelectorThread masterSelectorThread = new MasterSelectorThread();
			masterSelectorThread.bind(masterSelectorBean.getHost(), masterSelectorBean.getPort());
			masterSelectorThread.start();
			
			logger.info("Master selector was bound on " + masterSelectorBean.getHost() + ":" + masterSelectorBean.getPort());
		} catch (IOException e) { logger.error(e); }
	}
	
	public static void main(String[] args) {
		// make folder for logs
		new File("./logs/").mkdir();
		
		// configuration logger
		DOMConfigurator.configure("./conf/log4j.xml");
		
		new Bootstrap();
	}
}