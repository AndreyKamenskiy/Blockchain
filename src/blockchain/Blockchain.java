package blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import utils.ByteUtils;
import utils.TimeUtils;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 6L;
    private static final Blockchain instance = new Blockchain(); // Singleton
    private static final int CREATION_TIME_DECREASE_THRESHOLD = 2; // in seconds
    private static final int CREATION_TIME_INCREASE_THRESHOLD = 1; // in seconds
    private static final int MAX_ZEROS = 4;
    private static final int CREATION_MINER_REWARD = 100; //later may be changeable
    private static final int NEW_ACCOUNT_BONUS = 100; //later may be changeable
    private static final int MAX_TRANSACTIONS_FOR_BLOCK = 5;

    private final Transaction[] EMPTY_TRANSACTION = {};
    private long transactionId = 1;
    private long accountId = 1;

    private final BlockchainAccount blockchainAccount = new BlockchainAccount();

    private final List<Block> chain = new ArrayList<>();
    private transient final List<Updatable> listeners = new ArrayList<>();
    private List<Transaction> unchainedTransactions = new ArrayList<>();
    private Transaction[] transactions = EMPTY_TRANSACTION;

    private int zerosNum = 0; // current zeros number;

    private transient final Map<Person, Integer> personsBalance = new HashMap<>();

    private Blockchain() {
    }

    /************************************************************
     *                  getters and setters
     ************************************************************/

    public static Blockchain getInstance() {
        return instance;
    }

    public synchronized int getZerosNum() {
        return zerosNum;
    }

    public synchronized Block getLastBlock() {
        if (chain.isEmpty()) {
            return Block.ZERO_BLOCK;
        }
        return chain.get(chain.size() - 1);
    }

    public BlockCreator getBlockCreator(Miner miner) {
        return new BlockCreator(miner);
    }

    public int getLastBlockCreatingTime() {
        //in seconds
        if (chain.size() < 2) {
            return 0;
        }
        return TimeUtils.secondsPassed(chain.get(chain.size() - 2).getTimestamp(),
                chain.get(chain.size() - 1).getTimestamp());
    }

    public Account getBlockchainAccount() {
        return blockchainAccount;
    }

    /************************************************************
     *                  messages work
     ************************************************************/

    public Transaction[] getTransactions() {
        return transactions;
    }

    public long getTransactionId() {
        return transactionId++;
    }

    public long getNewAccountId() {
        return accountId++;
    }

    public void updateTransactions() {
        if (unchainedTransactions.size() <= MAX_TRANSACTIONS_FOR_BLOCK) {
            this.transactions = unchainedTransactions.toArray(new Transaction[0]);
        } else {
            this.transactions = unchainedTransactions.subList(0, MAX_TRANSACTIONS_FOR_BLOCK).toArray(new Transaction[0]);
        }
    }

    public synchronized boolean addTransaction(Transaction transaction) {
        if (transaction.getTransactionId() > transactionId) {
            return false;
        }
        //todo:check to balance be greater or equal to amount;
        unchainedTransactions.add(transaction);

        try {
            changeBalance(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (unchainedTransactions.size() <= MAX_TRANSACTIONS_FOR_BLOCK) {
            updateTransactions();
            informAboutChanges();
        }
        return true;
    }

    /************************************************************
     *                  listeners
     ************************************************************/

    public void addListener(Updatable listener) {
        listeners.add(listener);
    }

    public void deleteListener(Updatable listener) {
        listeners.remove(listener);
    }

    public synchronized void addBlock(Block newBlock) {
        if (isValidTransaction(newBlock) && isValidBlock(getLastBlock(), newBlock)) {
            chain.add(newBlock);
            deleteChainedMessages(newBlock.getTransactions().length);
            updateTransactions();
            updateZerosNum();
            informAboutChanges();
        }
    }

    private void deleteChainedMessages(int numberDeleted) {
        unchainedTransactions = unchainedTransactions.subList(numberDeleted, unchainedTransactions.size());
    }

    public boolean isValid() {
        Block previous = null;
        long previousMaxId = 0;
        for (Block current : chain) {
            // check blockHash
            try {
                if (!current.verifyHash()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            // check transactions
            long currentMax = 0;
            for (Transaction transaction : current.getTransactions()) {
                long currId = transaction.getTransactionId();
                if (currId > currentMax) {
                    currentMax = currId;
                }
                if (currId <= previousMaxId || !transaction.verifySignature()) {
                    return false;
                }
            }
            previousMaxId = currentMax;

            if (previous == null) {
                previous = current;
                continue;
            }

            boolean res = Arrays.equals(current.getPreviousBlockHash(), previous.getBlockHash()) &&
                    previous.getId() + 1 == current.getId() &&
                    previous.getTimestamp() <= current.getTimestamp()
                    // todo: check zeros prevZeros - currentZeros = ???
                    ;
            if (!res) {
                return false;
            }
            previous = current;
        }
        return true;
    }


    // TODO add blockchain loading and saving

    /*private static Blockchain loadBlockchain(String filename) {
        try {
            Blockchain blockchain = (Blockchain) SerializationUtils.deserialize(filename);
            return blockchain;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    private static void saveBlockchain(String filename, Blockchain blockchain) {
        try {
            SerializationUtils.serialize(blockchain, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    @Override
    public String toString() {
        return chain.stream().map(Block::toString).collect(Collectors.joining("\n"));
    }

    private void informAboutChanges() {
        listeners.forEach(Updatable::update);
    }

    private synchronized boolean isValidTransaction(Block block) {
        // compare messages
        Transaction[] newBlockTransactions = block.getTransactions();
        if (newBlockTransactions.length > unchainedTransactions.size()) {
            return false;
        }
        for (int i = 0; i < newBlockTransactions.length; i++) {
            if (!newBlockTransactions[i].equals(unchainedTransactions.get(i))) {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean isValidBlock(Block block, Block nextBlock) {
        return Arrays.equals(nextBlock.getPreviousBlockHash(), block.getBlockHash()) &&
                block.getId() + 1 == nextBlock.getId() &&
                block.getTimestamp() <= nextBlock.getTimestamp() &&
                ByteUtils.isLeadsNZeros(nextBlock.getBlockHash(), zerosNum);
    }

    private void updateZerosNum() {
        int timePassed = getLastBlockCreatingTime();
        if (timePassed < CREATION_TIME_INCREASE_THRESHOLD && zerosNum < MAX_ZEROS) synchronized (this) {
            ++zerosNum;
        }
        else if (timePassed > CREATION_TIME_DECREASE_THRESHOLD) synchronized (this) {
            --zerosNum;
        }
    }

    public int getBlockCreatorReward() {
        return CREATION_MINER_REWARD;
    }

    public boolean getNewAccountBonus(Account account) {
        try {
            blockchainAccount.makeBonus(account, NEW_ACCOUNT_BONUS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getBalance(Person person) {
        return personsBalance.getOrDefault(person, 0);
    }

    private void changeBalance(Person person, int amount) throws Exception {
        if (personsBalance.containsKey(person)) {
            long balance = personsBalance.get(person) + amount;
            if (balance > Integer.MAX_VALUE) {
                throw new Exception("Balance overflow detected");
            }
            if (balance < 0) {
                throw new Exception("negative balance detected");
            }
            personsBalance.replace(person, (int) balance);
        } else {
            if (amount < 1) {
                throw new Exception("amount for the new person must be positive");
            }
            personsBalance.put(person, amount);
        }
    }

    private void changeBalance(Transaction t) throws Exception {
        if (t.getType() == TransactionType.TRANSFER) {
            changeBalance(t.getFrom(), -t.getAmount());
        }
        changeBalance(t.getTo(), t.getAmount());
    }

    public void calculateBalances(){
        personsBalance.clear();
        chain.forEach(this::updateBalances);
    }

    private synchronized void updateBalances(Block block) {
        try {
            changeBalance(block.getMiner(), block.getMinerReward());
            for (Transaction transaction : block.getTransactions()) {
                changeBalance(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Set<Map.Entry<Person, Integer>> getBalances() {
        return personsBalance.entrySet();
    }

}
