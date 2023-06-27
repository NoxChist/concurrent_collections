import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class FindMaxLetterCallable implements Callable<String> {
    private BlockingQueue<String> queue;
    private char letter;
    private int str_amount;

    protected FindMaxLetterCallable(BlockingQueue<String> queue, char letter, int str_amount) {
        this.queue = queue;
        this.letter = letter;
        this.str_amount = str_amount;
    }

    @Override
    public String call() {
        int maxCount = 0;
        String maxString = "";
        for (int i = 0; i < str_amount; i++) {
            try {
                String text = queue.take();
                int chVal = (int) text.chars().filter(ch -> ch == (int) letter).count();
                if (chVal > maxCount) {
                    maxString = text;
                    maxCount = chVal;
                    System.out.println(maxString.substring(0, 100) + " (" + letter + ")--> " + maxCount);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return maxString;
    }
}
