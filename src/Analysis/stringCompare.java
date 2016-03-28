package Analysis;


public class stringCompare {
	//在动态规划矩阵生成方式当中，每生成一行，前面的那一行就已经没有用了，因此这里只需使用一维数组，而不是常用的二位数组
	public static void getLCString(char[] str1, char[] str2) {
		int len1, len2;
		len1 = str1.length;
		len2 = str2.length;
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// 保存最长子串长度的数组
		int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (str2[i] == str1[j]) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;//此时C[j-1]还是上次循环中的值，因为还没被重新赋值
				} else {
					c[j] = 0;
				}

				// 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
				if (c[j] > max[0]) {
					max[0] = c[j];
					maxIndex[0] = j;

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				}
				// 有多个是相同长度的子串
				else if (c[j] == max[0]) {
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break; // 在后面加一个就要退出循环了
						}
					}
				}
			}
			for (int temp : c) {
				System.out.print(temp);
			}
			System.out.println();
		}
        //打印最长子字符串
		for (j = 0; j < maxLen; j++) {
			if (max[j] > 0) {
				System.out.println("第" + (j + 1) + "个公共子串:");
				for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++)
					System.out.print(str1[i]);
				System.out.println(" ");
			}
		}
	}

	public static void main(String[] args) {

		String str1 = new String("abcxyzabcrst");
		String str2 = new String("opqrstabc");
		getLCString(str1.toCharArray(), str2.toCharArray());
	}
}