/*
 * 创建日期：2012-10-9
 */
package com.modesty.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {
	/**
	 * 匹配：Email地址
	 */
	public static final String REGEX_EMAIL = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
	/**
	 * 匹配：手机号码
	 */
	public static final String REGEX_MOBILE_NUMBER = "^[1][3-9]\\d{9}$";

	/**
	 * 正则表达式中使用的特殊的计算符号，如：'$', '*', ...
	 */
	public static final char[] PATTERN_REGEX_SPECIAL_CHARACTERS = { '$', // 匹配输入字符串的结尾位置。如果设置了 RegExp 对象的 Multiline 属性，则 $ 也匹配 '\n' 或 '\r'。要匹配 $ 字符本身，请使用 \$。
			'(', ')', // 标记一个子表达式的开始和结束位置。子表达式可以获取供以后使用。要匹配这些字符，请使用 \( 和 \)。
			'*', // 匹配前面的子表达式零次或多次。要匹配 * 字符，请使用 \*。
			'+', // 匹配前面的子表达式一次或多次。要匹配 + 字符，请使用 \+。
			'.', // 匹配除换行符 \n之外的任何单字符。要匹配 .，请使用 \。
			'[', // 标记一个中括号表达式的开始。要匹配 [，请使用 \[。
			'?', // 匹配前面的子表达式零次或一次，或指明一个非贪婪限定符。要匹配 ? 字符，请使用 \?。
			'\\', // 将下一个字符标记为或特殊字符、或原义字符、或向后引用、或八进制转义符。例如， 'n' 匹配字符 'n'。'\n' 匹配换行符。序列 '\\' 匹配 "\"，而 '\(' 则匹配 "("。
			'^', // 匹配输入字符串的开始位置，除非在方括号表达式中使用，此时它表示不接受该字符集合。要匹配 ^ 字符本身，请使用 \^。
			'{', // 标记限定符表达式的开始。要匹配 {，请使用 \{。
			'|', // 指明两项之间的一个选择。要匹配 |，请使用 \|。
	};

	/** 英文标点符号 */
	private static final int TYPE_PUNCT = 1;
	/** 数字 */
	private static final int TYPE_DIGIT = 2;
	/** 小写字母字符 */
	private static final int TYPE_LOWER_LETTER = 3;
	/** 大写字母字符 */
	private static final int TYPE_UPPER_LETTER = 4;
	/** 汉字字符串包括了中文标点符号 */
	private static final int TYPE_CJK = 5;
	/** 汉字字符串剔除了中文标点符号 */
	private static final int TYPE_NO_PUNCTUATION_CJK = 6;
	/** 是否是空白字符 */
	private static final int TYPE_WHITESPACE = 7;
	/** 不是字母 数字 英文标点符号 */
	private static final int TYPE_NOT_PUNCT_DIGIT_LETTER = 8;

	private static boolean isMatchesChar(int type, char ch) {
		switch (type) {
			case TYPE_PUNCT:
				return isPunct(ch);
			case TYPE_DIGIT:
				return Character.isDigit(ch);
			case TYPE_LOWER_LETTER:
				return isLowerLetter(ch);
			case TYPE_UPPER_LETTER:
				return isUpperLetter(ch);
			case TYPE_CJK:
				return isCJK(ch);
			case TYPE_NO_PUNCTUATION_CJK:
				return isNoPunctuationCJK(ch);
			case TYPE_WHITESPACE:
				return Character.isWhitespace(ch);
			case TYPE_NOT_PUNCT_DIGIT_LETTER:
				return !(Character.isDigit(ch) || isLowerLetter(ch) || isUpperLetter(ch) || isPunct(ch));
			default:
				return false;
		}
	}

	private static boolean isMatches(int type, String input) {
		if (input != null && input.length() > 0) {
			for (int i = input.length() - 1; i >= 0; i--) {
				if (!isMatchesChar(type, input.charAt(i))) {
					return false;
				} else if (i == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isMatchesInclude(int type, String input) {
		if (input != null && input.length() > 0) {
			for (int i = input.length() - 1; i >= 0; i--) {
				if (isMatchesChar(type, input.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 匹配正则表达式
	 * 
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean isMatches(String regex, String input) {
		return input == null ? false : Pattern.compile(regex).matcher(input).find();
	}

	/**
	 * 匹配正则表达式（忽略大小写）
	 * 
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean isMatchesIgnoreCase(String regex, String input) {
		return input == null ? false : Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(input).find();
	}

	/**
	 * 是否是正则表达式中使用的特殊的计算符号，如：'$', '*', ...
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isPatternRegexSpecialCharacter(char ch) {
		for (int i = PATTERN_REGEX_SPECIAL_CHARACTERS.length - 1; i >= 0; i--) {
			if (ch == PATTERN_REGEX_SPECIAL_CHARACTERS[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是标点符号，（POSIX 字符类（仅 US-ASCII） \p{Punct} 标点符号：!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~）
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isPunct(char ch) {
		return (ch >= 0x21 && ch <= 0x2F) // !"#$%&'()*+,-./
				|| (ch >= 0x3A && ch <= 0x40) // :;<=>?@
				|| (ch >= 0x5B && ch <= 0x60) // [\]^_`
				|| (ch >= 0x7B && ch <= 0x7E);// {|}~
	}

	/**
	 * 是否是标点符号字符串 中文标点识别不了
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isPunct(String input) {
		return isMatches(TYPE_PUNCT, input);
	}

	/**
	 * 是否包含标点符号字符串
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludePunct(String input) {
		return isMatchesInclude(TYPE_PUNCT, input);
	}

	/**
	 * 是否包含非字母，数字以及Punct规定的字符串
	 * 
	 * @param input
	 * @return 含有则返回true，否则 返回false
	 */
	public static boolean isIncludeInvalidChar(String input) {
		return isMatchesInclude(TYPE_NOT_PUNCT_DIGIT_LETTER, input);
	}

	/**
	 * 是否是数字字符串
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isDigit(String input) {
		return isMatches(TYPE_DIGIT, input);
	}

	/**
	 * 是否包含数字字符串
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludeDigit(String input) {
		return isMatchesInclude(TYPE_DIGIT, input);
	}

	/**
	 * 是否是小写字母字符（[a-z]）
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isLowerLetter(char ch) {
		return ch >= 0x61 && ch <= 0x7A;
	}

	/**
	 * 是否是小写字母字符串（[a-z]）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isLowerLetter(String input) {
		return isMatches(TYPE_LOWER_LETTER, input);
	}

	/**
	 * 是否包含小写字母字符串（[a-z]）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludeLowerLetter(String input) {
		return isMatchesInclude(TYPE_LOWER_LETTER, input);
	}

	/**
	 * 是否是大写字母字符（[A-Z]）
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isUpperLetter(char ch) {
		return ch >= 0x41 && ch <= 0x5A;
	}

	/**
	 * 是否是大写字母字符串（[A-Z]）
	 * 
	 * @param
	 * @return
	 */
	public static boolean isUpperLetter(String input) {
		return isMatches(TYPE_UPPER_LETTER, input);
	}

	/**
	 * 是否包含大写字母字符串（[A-Z]）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludeUpperLetter(String input) {
		return isMatchesInclude(TYPE_UPPER_LETTER, input);
	}

	/**
	 * 是否是字母字符串（[A-Za-z]）
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isLetter(char ch) {
		return isLowerLetter(ch) || isUpperLetter(ch);
	}

	/**
	 * 是否是汉字字符（广义上的汉字字符，很多都无法用输入法输出的汉字）
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCJK(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS // CJK统一汉字 \\u4E00-\\u9fAF
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A // CJK统一汉字扩展-A \\u3400-\\u4dBF
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS // CJK兼容汉字 \\uF900-\\uFAFF
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION // CJK符号和标点 \\u3000-\\u303F
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION // 广义标点 \\u2000-\\u206F
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) { // 半形及全形字符 \\uFF00-\\uFFEF
			return true;
		}
		return false;
	}

	/**
	 * 是否是汉字字符（不包含标点）
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isNoPunctuationCJK(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS // CJK统一汉字 \\u4E00-\\u9fAF
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A // CJK统一汉字扩展-A \\u3400-\\u4dBF
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) { // CJK兼容汉字 \\uF900-\\uFAFF
			return true;
		}
		return false;
	}

	/**
	 * 是否是汉字字符（不包含标点）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNoPunctuationCJK(String input) {
		return isMatches(TYPE_NO_PUNCTUATION_CJK, input);
	}

	/**
	 * 是否是汉字字符串（广义上的汉字字符，很多都无法用输入法输出的汉字）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isCJK(String input) {
		return isMatches(TYPE_CJK, input);
	}

	/**
	 * 是否包含汉字字符串（广义上的汉字字符，很多都无法用输入法输出的汉字）
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludeCJK(String input) {
		return isMatchesInclude(TYPE_CJK, input);
	}

	/**
	 * 是否是空白字符
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isWhitespace(String input) {
		return isMatches(TYPE_WHITESPACE, input);
	}

	/**
	 * 是否包含空白字符
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isIncludeWhitespace(String input) {
		return isMatchesInclude(TYPE_WHITESPACE, input);
	}

	/**
	 * 是否符合密码规则
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isFetionPassword(String input) {
		// 密码必须含有字母和数字，符号可选，但是必须符合Punct标准,否则返回false
		// 首先判断是否含有无效字符，含有，则返回false
		if (isIncludeInvalidChar(input)) {
			return false;
		}
		// 长度不符合要求，返回 false
		if (input.length() < 6 || input.length() > 16) {
			return false;
		}
		int tmpValue = 0;
		if (isMatches("[A-Za-z]{1,15}", input)) {
			tmpValue++;
		}
		if (isMatches("[0-9]{1,15}", input)) {
			tmpValue++;
		}
		if (tmpValue >= 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 1个或者多个0-9组成的数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
