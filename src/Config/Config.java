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
	public static final String Date = "date";//日期
	public static final String WorkPath = "workPath";//信令数据路径
	public static final String PatternPath = "patternPath";//模式路径
	public static final String BaseFile = "baseFile";//基站数据路径
	public static final String IdLength = "idLength";//ID长度
	public static final String CityMaxLon = "cityMaxLon";//城市经度最大值
	public static final String CityMinLon = "cityMinLon";//城市经度最小值
	public static final String CityMaxLat = "cityMaxLat";//城市纬度最大值
	public static final String CityMinLat = "cityMinLat";//城市纬度最小值
	//WorkPath子目录
	public static final String RawPath = "0raw";
	public static final String FixedPath = "1fixed";
	public static final String TimeSpanPath = "2timeSpan";
	public static final String TimeLinePath = "3timeLine";
	public static final String GoodUserPath = "4goodUser";
	public static final String GoodRecordPath = "5goodRecord";
	public static final String MergeSameLocPath = "6mergeSameLoc";
	public static final String StayRecordPath = "7stayRecord";
	public static final String ODPath = "8OD";
	//PatternPath子目录
	public static final String PatternRecordPath = "0patternRecord";
	
	private static final String[] configKeys = {
		Date,
		WorkPath,
		PatternPath,
		BaseFile,
		IdLength,
		CityMaxLon,
		CityMinLon,
		CityMaxLat,
		CityMinLat
	};
	private static final String[] WorkPathGenerateKeys = {
		RawPath,
		FixedPath,
		TimeSpanPath,
		TimeLinePath,
		GoodUserPath,
		GoodRecordPath,
		MergeSameLocPath,
		StayRecordPath,
		ODPath
	};
	private static final String[] PatternPathGenerateKeys = {
		PatternRecordPath
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
        for(String key:WorkPathGenerateKeys){
        	String value = configs.get(WorkPath)+File.separator+configs.get(Date)+File.separator+key;
        	configs.put(key, value);
        }
        for(String key:PatternPathGenerateKeys){
        	String value = configs.get(PatternPath)+File.separator+configs.get(Date)+File.separator+key;
        	configs.put(key, value);
        }
        return ret;
    }
	public static boolean loadProgramConfig(String contextPath){
    	Config.setContextPath(contextPath);
    	return Config.loadProgramConfig();
    }
    public static void init() throws Exception{
    	Config.loadProgramConfig("."+File.separator+"input");
	}
    
    public static void main(String[] args)throws Exception{
    	init();
    	for(String key:configs.keySet()){
    		System.out.println(configs.get(key));
    	}
    }
}
