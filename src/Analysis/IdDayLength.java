package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import Config.Config;

public class IdDayLength {
	public static void main(String[] args)throws Exception{
		Config.init();
		Set<String> d1 = new HashSet<String>();
		Set<String> d2 = new HashSet<String>();
		Set<String> d3 = new HashSet<String>();
		Set<String> d4 = new HashSet<String>();
		String d1ps = Config.getAttr(Config.WorkPath)+File.separator+"20130225"+File.separator+Config.getAttr(Config.StayRecordPath).substring(21);
		String d2ps = Config.getAttr(Config.WorkPath)+File.separator+"20130226"+File.separator+Config.getAttr(Config.StayRecordPath).substring(21);
		String d3ps = Config.getAttr(Config.WorkPath)+File.separator+"20130227"+File.separator+Config.getAttr(Config.StayRecordPath).substring(21);
		String d4ps = Config.getAttr(Config.WorkPath)+File.separator+"20130228"+File.separator+Config.getAttr(Config.StayRecordPath).substring(21);
		File d1p = new File(d1ps);
		File d2p = new File(d2ps);
		File d3p = new File(d3ps);
		File d4p = new File(d4ps);
		File[] d1fs = d1p.listFiles();
		File[] d2fs = d2p.listFiles();
		File[] d3fs = d3p.listFiles();
		File[] d4fs = d4p.listFiles();
		System.out.println(d1p.getAbsolutePath());
		for(File file:d1fs){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				String afn = af.substring(0,19);
				if(!d1.contains(afn))
					d1.add(afn);
			}
			br.close();
		}
		System.out.println(d2p.getAbsolutePath());
		for(File file:d2fs){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				String afn = af.substring(0,19);
				if(d1.contains(afn) && !d2.contains(afn))
					d2.add(afn);
			}
			br.close();
		}
		System.out.println(d3p.getAbsolutePath());
		for(File file:d3fs){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				String afn = af.substring(0,19);
				if(d2.contains(afn) && !d3.contains(afn))
					d3.add(afn);
			}
			br.close();
		}
		System.out.println(d4p.getAbsolutePath());
		for(File file:d4fs){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				String afn = af.substring(0,19);
				if(d3.contains(afn) && !d4.contains(afn))
					d4.add(afn);
			}
			br.close();
		}
		System.out.println("D1 user size = "+d1.size());
		System.out.println("D2 user size = "+d2.size());
		System.out.println("D3 user size = "+d3.size());
		System.out.println("D4 user size = "+d4.size());
		int num=0;
		//for(String id:d1){
		//	if(d2.contains(id) && d3.contains(id) && d4.contains(id))
		//		num+=1;
		//}
		System.out.println("jiaoji = "+num);
	}
}
