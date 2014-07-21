package seafight.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seafight.beans.IBean;
import seafight.helpers.BeanHelper;
import seafight.helpers.FileHelper;

public class ResourceManager {
	private static volatile ResourceManager instance = null;
	
	public synchronized static ResourceManager getInstance() {
		if(instance == null)
			instance = new ResourceManager();
		
		return instance;
	}
	
	private static final String DEFAULT_RESOURCE_PATH = "./conf/";
	
	private Map<String, IBean> beans = new HashMap<String, IBean>();
	
	private ResourceManager() { }
	
	public void load() throws IOException {
		this.load(DEFAULT_RESOURCE_PATH);
	}
	
	public void load(String resourcePath) throws IOException {
		List<File> files = FileHelper.dir(new File(resourcePath));
		
		for(File file : files) {
			if(file.isDirectory()) continue;
			
			Object bean = BeanHelper.get(file.getAbsolutePath());
			
			if(bean instanceof IBean)
				this.beans.put(file.getName(), (IBean) bean);
		}
	}
	
	public IBean get(String name) {
		return this.beans.get(name + ".xml");
	}
}