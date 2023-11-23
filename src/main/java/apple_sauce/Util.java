package apple_sauce;

public class Util {
    public static void printInfo(String text) {
        System.out.println("[\u001B[33mINFO\u001B[0m]: " + text);
    }

public static void printError(String text) {
        System.out.println("[\u001B[31mERROR\u001B[0m]: " + text);
    }
}
