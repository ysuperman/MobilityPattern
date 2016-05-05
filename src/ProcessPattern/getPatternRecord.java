package ProcessPattern;

import Config.Config;
/*
 * author:youg
 * date:20160504
 * 从连续多天的StayRecord中提取每个用户的停留模式
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1fixed:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 * 8patternRecord:从连续多天的7stayRecord中提取出的每个用户的停留模式
 */
public class getPatternRecord {
	public static void main(String[] args)throws Exception{
		Config.init();
		
	}
}
