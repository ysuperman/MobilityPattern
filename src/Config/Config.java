package Config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Config {
	//Attrs
	public static final String CONFIG_Date = "date";
	public static final String CONFIG_WorkPath = "WorkPath";
	public static final String CONFIG_RawPath = "0raw";
	public static final String CONFIG_WithPosPath = "1WithPosPath";
	public static final String CONFIG_TimeSpan = "2TimeSpan";
	public static final String CONFIG_TimeLine = "3TimeLine";
	public static final String CONFIG_GoodUser = "4GoodUser";
	public static final String CONFIG_GoodRecord = "5GoodRecord";
	public static final String CONFIG_MergeSameLoc = "6MergeSameLoc";
	
	private static final String[] configKeys = {
		CONFIG_Date,
		CONFIG_WorkPath,
	};
	private static final String[] generateKeys = {
		CONFIG_RawPath,
		CONFIG_WithPosPath,
		CONFIG_TimeSpan,
		CONFIG_TimeLine,
		CONFIG_GoodUser,
		CONFIG_GoodRecord,
		CONFIG_MergeSameLoc,
	};
	private static Map<String, String> configs = new HashMap<String, String>();
	private static String contextPath = ".";
	
	
	public static String getAttr(String attrName){
		return configs.get(attrName);
	}
	public static void setContextPath(String contextPath) {
		Config.contextPath = contextPath;
	}
	public static boolean loadProgramConfig(){
    	//System.out.println(ProgramConfig.ContextPath);
    	String configFilePath = Config.contextPath + File.separator + "Config" + File.separator + "Config.xml";
        
        SAXBuilder sb = new SAXBuilder();
        Document doc;
		try {
			doc = sb.build(configFilePath);
		} catch (JDOMException e) {
			System.err.println("ProgramConfig::config file format error");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("ProgramConfig::error opening config file");
			e.printStackTrace();
			return false;
		}
        Element root = doc.getRootElement();
        boolean ret = true;
        for(String key : configKeys){
        	String value = root.getChildText(key);
        	if(value == null){
        		System.err.println("ProgramConfig::config property "+key+" missed");
        		ret = false;
        	}else{
            	configs.put(key, root.getChildText(key));
        	}
        }
        for(String key:generateKeys){
        	String value = configs.get(CONFIG_WorkPath)+File.separator+configs.get(CONFIG_Date)+File.separator+key;
        	configs.put(key, value);
        }
        return ret;
    }
	public static boolean loadProgramConfig(String contextPath){
    	Config.setContextPath(contextPath);
    	return Config.loadProgramConfig();
    }
    public static void init() throws Exception{
    	Config.loadProgramConfig(".\\input");
	}
    
    public static void main(String[] args)throws Exception{
    	init();
    }
}
