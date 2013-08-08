package com.matech.audit.pub.db;

/**
 * 
 * @author ymm
 * 根据一串中文，返回首字母
 * create on 2011-12-16
 */
public class GB2Alpha {
	private static final char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	/**
	 * 
	 * 汉字拼音首字母编码表，可以如下方法得到：
	 * 
	 * 字母Z使用了两个标签，这里有２７个值, i, u, v都不做声母, 跟随前面的字母(因为不可以出现，所以可以随便取)
	 * 
	 * private static final char[] chartable =
	 * 
	 * {
	 * 
	 * '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈',
	 * 
	 * '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然',
	 * 
	 * '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座'
	 * 
	 * };
	 * 
	 * 
	 * 
	 * private static final int[] table = new int[27];
	 * 
	 * static
	 * 
	 * {
	 * 
	 * for (int i = 0; i < 27; ++i) {
	 * 
	 * table[i] = gbValue(chartable[i]);
	 * 
	 * System.out.print(table[i]+" ");
	 * 
	 * }
	 * 
	 * }
	 */
	private static final int[] table = new int[] { 45217, 45253, 45761, 46318,
			46826, 47010, 47297, 47614, 47614, 48119, 49062, 49324, 49896,
			50371, 50614, 50622, 50906, 51387, 51446, 52218, 52218, 52218,
			52698, 52980, 53689, 54481, 55289 };

	public GB2Alpha() {
	}

	/**
	 * 
	 * 主函数, 输入字符, 得到他的声母, 英文字母返回对应的大写字母 其他非简体汉字返回 '*'
	 */
	public char Char2Alpha(char ch) {
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');
		if (ch >= 'A' && ch <= 'Z')
			return ch;
		int gb = gbValue(ch);
		if (gb < table[0])
			return '*';
		for (int i = 0; i < 26; ++i) {
			if (match(i, gb)) {
				if (i >= 26)
					return '*';
				else
					return alphatable[i];
			}
		}
		return '*';
	}

	/**
	 * 
	 * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
	 */
	public String String2Alpha(String str) {
		String Result = "";
		try {
			for (int i = 0; i < str.length(); i++) {
				Result += Char2Alpha(str.charAt(i));
			}
		} catch (Exception e) {
			Result = " ";
		}
		return Result;
	}

	private boolean match(int i, int gb) {
		if (gb < table[i])
			return false;
		int j = i + 1;
		// 字母Z使用了两个标签
		while (j < 26 && (table[j] == table[i]))
			++j;
		if (j == 26)
			return gb <= table[j];
		else
			return gb < table[j];
	}

	/**
	 * 
	 * 取出传入汉字的编码
	 */
	private static int gbValue(char ch) {
		String str = new String();
		str += ch;
		try {
			byte[] bytes = str.getBytes("GB2312");
			if (bytes.length < 2)
				return 0;
			return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
		} catch (Exception e) {
			return '*';
		}
	}

	/**
	 * 
	 * 测试输出
	 */
	public static void main(String[] args) {
		GB2Alpha gb2A = new GB2Alpha();
		System.out.println("喻明明的拼音首字母为:" + gb2A.String2Alpha("喻明明!").replace("*", ""));// 输出
																		// HSTF*
	}
}
