package blockchain;
import utils.RandomUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// class for user that will send a messages
public class Client extends Account implements Runnable {
    final int maxSleepTime = 500; //1000 ms = 1 sec

    private final Blockchain blockchain = Blockchain.getInstance();

    private boolean active;

    public Client(String name) {
        super(name);
    }

    public void stop() {
        active = false;
    }

    private Person getRandomPerson() {
        Set<Map.Entry<Person, Integer>> balances = blockchain.getBalances();
        if (balances.size() < 2) return null;
        int num = RandomUtils.nextIntRange(0, balances.size() - 1);
        Iterator<Map.Entry<Person, Integer>> it = balances.iterator();
        Person randomPerson = it.next().getKey();
        for (int i = 0; i < num; ++i) {
            randomPerson = it.next().getKey();
        }
        return randomPerson;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            int sleepTime = RandomUtils.nextIntRange(0, maxSleepTime);
            try {
                Thread.sleep(sleepTime);
                int balance = blockchain.getBalance(getPerson());
                Person randomPerson = getRandomPerson();
                if (randomPerson != null && balance > 2) {
                    makeTransfer(randomPerson, RandomUtils.nextIntRange(1, balance));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
