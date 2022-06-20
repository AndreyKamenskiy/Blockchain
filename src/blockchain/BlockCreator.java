package blockchain;

import utils.ByteUtils;
import utils.TimeUtils;

import java.io.IOException;
import java.util.Optional;

public class BlockCreator implements Updatable {
    //todo add field lastActivity to auto deleting from receivers;

    private final Account miner;
    private final Blockchain blockchain = Blockchain.getInstance();
    private int zerosNum;
    private Block lastBlock;
    private Transaction[] transactions;

    public BlockCreator(Miner miner) {
        this.miner = miner;
        zerosNum = blockchain.getZerosNum();
        lastBlock = blockchain.getLastBlock();
        transactions = blockchain.getTransactions();
    }

    public Optional<Block> tryToCreateNewBlock(int magicNumber) {
        //todo make more effective. change only the magic number;
        if (transactions.length == 0) {
            return Optional.empty();
        }
        try {
            long timestamp = TimeUtils.getTimestamp();
            int reward = blockchain.getBlockCreatorReward();
            Block block = new Block(lastBlock, miner.getPerson(), reward, timestamp, magicNumber, transactions);
            if (ByteUtils.isLeadsNZeros(block.getBlockHash(), zerosNum)) {
                return Optional.of(block);
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public void update() {
        lastBlock = blockchain.getLastBlock();
        zerosNum = blockchain.getZerosNum();
        transactions = blockchain.getTransactions();
    }

}
