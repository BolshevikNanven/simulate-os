package scau.os.soos;

public class test0 {
    public static void main(String[] args) {
        String str = p();
        String[] parts = str.split(" ",3);
        System.out.println(parts[2]);
        boolean isTrue = Boolean.parseBoolean(str);
        boolean isBoolean = str.matches("^(true|false)$");
        int size = Integer.parseInt("12");
        System.out.println(isTrue);
        System.out.println(size);
        System.out.println(isBoolean    );
    }

    public static String p() {
        return "true flase";
    }
}