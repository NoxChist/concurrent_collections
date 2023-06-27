import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    private static final String TEXT_GEN = "abc";
    private static final int STR_LENGTH = 100_000;
    private static final int STR_AMOUNT = 10_000;
    private static final int QUEUE_CAPACITY = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static List<BlockingQueue<String>> queue = new ArrayList<>(TEXT_GEN.length());

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < TEXT_GEN.length(); i++) {
            queue.add(new ArrayBlockingQueue<>(QUEUE_CAPACITY));
        }
        new Thread(() -> {
            for (int i = 0; i < STR_AMOUNT; i++) {
                String text = generateText(TEXT_GEN, STR_LENGTH);
                try {
                    for (var q : queue) {
                        q.put(text);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        List<Future<String>> resStr = new ArrayList<>(TEXT_GEN.length());
        for (int i = 0; i < TEXT_GEN.length(); i++) {
            resStr.add(executorService.submit(new FindMaxLetterCallable(queue.get(i), TEXT_GEN.charAt(i), STR_AMOUNT)));
        }
        executorService.shutdown();

        for (int i = 0; i < TEXT_GEN.length(); i++) {
            char letter = TEXT_GEN.charAt(i);
            String text = resStr.get(i).get();
            int count = (int) text.chars().filter(ch -> ch == (int) letter).count();
            System.out.println("---------------------------------------------------------------");
            System.out.println(text.substring(0, 100) + " (" + letter + ")--> " + count);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }


}
