import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();


    public static void main(String[] args) throws InterruptedException {

        List<Thread> threadList = new ArrayList<>();

        // 2й ПОТОК
        Thread thread2 = new Thread(() -> {
            while (!Thread.interrupted()) {
                int maxRepeatRInOneLine = 0;
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace(); //throw new RuntimeException(e) - если так, то можно без break
                        break; // добавила break, чтобы программа останавливалась после прерывания потоков
                    }
                    for (Map.Entry<Integer, Integer> kv : sizeToFreq.entrySet()) {
                        if (kv.getKey() > maxRepeatRInOneLine) {
                            maxRepeatRInOneLine = kv.getKey();
                        }
                    }
                    System.out.println("Максимальная ТЕКУЩАЯ частота повторений в одной строке " + maxRepeatRInOneLine);
                }
            }
        });
        thread2.start();

        // 1й ПОТОК
        for (int t = 0; t < 1000; t++) {
        Thread thread1 = new Thread(() -> {
                int numberOfR = 0;
                String result = generateRoute("RLRFR", 100);
                for (int i = 0; i < result.length(); i++) {
                    if (result.charAt(i) == 'R') {
                        numberOfR++;
                    }
                }
                synchronized (sizeToFreq) {
                    sizeToFreq.notify();
                    if (sizeToFreq.containsKey(numberOfR)) {
                        sizeToFreq.replace(numberOfR, sizeToFreq.get(numberOfR), (sizeToFreq.get(numberOfR) + 1));
                    } else {
                        sizeToFreq.put(numberOfR, 1);
                    }
                    System.out.println(result + " --->>> " + numberOfR);
                }
        });
        thread1.start();
        threadList.add(thread1);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }

        for (Thread thread : threadList) {
            thread.join();
        }

        toPrint();

        thread2.interrupt(); // сделала после toPrint(), чтобы вывод мапы не засорялся выбросом ошибки прерывания
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void toPrint() {
        int maxRepeatLines = 0;
        int keyRepeatRInOneLine = 0;
        for (Map.Entry<Integer, Integer> kv : sizeToFreq.entrySet()) {
            if (kv.getValue() > maxRepeatLines) {
                maxRepeatLines = kv.getValue();
                keyRepeatRInOneLine = kv.getKey();
            }
        }
        System.out.println("Самое частое количество повторений " + keyRepeatRInOneLine + " (встретилось " +
                maxRepeatLines + " раз)");
        sizeToFreq.remove(keyRepeatRInOneLine);
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> kv : sizeToFreq.entrySet()) {
            System.out.println("- " + kv.getKey() + " (" + kv.getValue() + ") раз");
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
