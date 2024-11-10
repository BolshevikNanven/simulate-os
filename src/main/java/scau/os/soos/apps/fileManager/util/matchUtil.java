package scau.os.soos.apps.fileManager.util;
public class matchUtil {
    private matchUtil() {
    }
    public static double matchLevel(String s, String t,double w1,double w2,double w3){
        return (levenshteinDistance(s, t) * w1 + longestCommonSubsequence(s, t) * -w2 + longestCommonSubstring(s, t) * -w3);
    }
    public static int levenshteinDistance(String s, String t) {
        // 获取字符串长度
        int len1 = s.length();
        int len2 = t.length();
        int[][] d = new int[len1 + 1][len2 + 1];
        // 初始化
        for (int i = 0; i <= len1; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            d[0][j] = j;
        }
        // 动态计算
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                d[i][j] = Math.min(
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), // 删除或插入
                        d[i - 1][j - 1] + cost // 替换
                );
            }
        }
        // 返回Levenshtein距离
        return d[len1][len2];
    }
    public static int longestCommonSubsequence(String s, String t) {
        // 获取字符串长度
        int len1 = s.length();
        int len2 = t.length();
        if (len1 == 0 || len2 == 0) {
            return 0;
        }
        int[][] d = new int[len1 + 1][len2 + 1];
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1] + 1;
                } else {
                    d[i][j] = Math.max(d[i - 1][j], d[i][j - 1]);
                }
            }
        }
        return d[len1][len2];
    }
    public static int longestCommonSubstring(String s, String t) {
        // 获取字符串长度
        int len1 = s.length();
        int len2 = t.length();
        if (len1 == 0 || len2 == 0) {
            return 0;
        }
        int[][] d = new int[len1 + 1][len2 + 1];
        int maxLen = 0;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1] + 1;
                    maxLen = Math.max(maxLen, d[i][j]);
                } else {
                    d[i][j] = 0;
                }
            }
        }
        return maxLen;
    }
}
