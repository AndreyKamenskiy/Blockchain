package blockchain;

import utils.RandomUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkSimulator implements Updatable {
    private Miner[] miners;
    private Client[] clients;
    private long lastBlockId;
    private int blocksNum;
    private int lastN;
    private ExecutorService minersExecutor;
    private ExecutorService clientsExecutor;
    private boolean isMining = false;

    private final String[] names = {"Bob", "Rob", "John", "Ann", "Margareth",
            "Antony", "Mike", "Samanta", "Sam", "Piter", "Boris", "Sue", "Abdurahman",
            "Salma", "Sofia", "Miranda", "Teem", "Tom", "Mary", "Teddy", "Dallas", "Donald",
            "Vasiliy", "Andrew", "Miroslava", "Yan", "Artem", "Elena", "Olga", "Di", "Merlin"
    };

    WorkSimulator() {
        Blockchain.getInstance().addListener(this);
        lastN = Blockchain.getInstance().getZerosNum();
        lastBlockId = Blockchain.getInstance().getLastBlock().getId();
    }

    public void makeMiners(int minersNum) {
        miners = new Miner[minersNum];
        int shift = RandomUtils.nextIntRange(0, names.length);
        for (int i = 0; i < minersNum; ++i) {
            miners[i] = new Miner("Miner-" + names[(i + shift) % names.length]);
        }
        minersExecutor = Executors.newFixedThreadPool(minersNum);
    }

    public void makeClients(int clientsNum) {

        clients = new Client[clientsNum];
        for (int i = 0; i < clientsNum; ++i) {
            clients[i] = new Client(names[i % names.length]);
        }
        clientsExecutor = Executors.newFixedThreadPool(clientsNum);
    }

    public void mineBlocks(int blocksNum) {
        this.blocksNum = blocksNum;
        startMining();
        while (isMining) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMining() {
        for (Miner current : miners) {
            minersExecutor.submit(current);
        }
        for (Client current : clients) {
            clientsExecutor.submit(current);
        }
        isMining = true;
    }

    @Override
    public synchronized void update() {
        //todo update types like new message or new block
        long currentId = Blockchain.getInstance().getLastBlock().getId();
        if (currentId != lastBlockId) {
            // new block was created;
            lastBlockId = currentId;
            if (lastBlockId >= blocksNum) {
                stopMining();
            }
            System.out.println(Blockchain.getInstance().getLastBlock());
            System.out.printf("Block was generating for %d seconds\n",
                    Blockchain.getInstance().getLastBlockCreatingTime());
            int currentN = Blockchain.getInstance().getZerosNum();
            if (currentN > lastN) {
                System.out.printf("N was increased to %d\n", currentN);
            } else if (currentN < lastN) {
                System.out.println("N was decreased by 1");
            } else {
                System.out.println("N stays the same");
            }
            lastN = currentN;
            System.out.println();
        }
    }

    private void stopMining() {
        for (Miner current : miners) {
            current.stop();
        }
        for (Client current : clients) {
            current.stop();
        }
        minersExecutor.shutdown();
        clientsExecutor.shutdown();
        isMining = false;
    }
}
