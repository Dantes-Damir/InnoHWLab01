package program;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;

public class Occurrences implements OccurrencesContact {

    private static final int MAX_Treads = 20;
    private ExecutorService pool;

    public Occurrences() {
        pool = Executors.newFixedThreadPool(MAX_Treads);
    }

    @Override
    public void getOccurrences(String[] sources, String[] words, String res) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Thread fileWriter = new Thread(() -> {
            try (PrintWriter writer = new PrintWriter(new File(res))) {
                while (true) {
                    writer.println(queue.take());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("Программа завершила работу");
            }
        });
        fileWriter.start();
        for (String source : sources) {
            pool.execute(new ContentRequest(source, words, queue));
        }
        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        fileWriter.interrupt();
    }
}
