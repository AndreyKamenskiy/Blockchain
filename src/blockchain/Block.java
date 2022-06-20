package blockchain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

import utils.HashUtils;
import utils.ByteUtils;

import static utils.StringUtils.toHexString;

public final class Block implements Serializable {
    private static final long serialVersionUID = 3L;
    public static final Block ZERO_BLOCK = new Block();
    private final long id;
    private final long timestamp;
    private final Person miner;
    private final int minerReward;
    private final byte[] previousBlockHash;
    private final int magicNumber;
    private final byte[] blockHash;
    private final Transaction[] transactions;

    private Block() {
        //for zeroBlockCreation
        id = 0;
        timestamp = 0;
        miner = Blockchain.getInstance().getBlockchainAccount().getPerson();
        previousBlockHash = new byte[0];
        magicNumber = 0;
        blockHash = new byte[1];
        transactions = new Transaction[]{}; // empty data block, not null
        minerReward = 0;
    }

    Block(Block previous, Person miner, int minerReward, long timestamp, int magicNumber, Transaction[] transactions)
            throws IOException {
        previousBlockHash = previous.blockHash.clone();
        id = previous.getId() + 1;
        this.miner = miner;
        this.timestamp = timestamp;
        this.magicNumber = magicNumber;
        this.transactions = transactions;
        this.minerReward = minerReward;
        blockHash = HashUtils.getHash(getBytesToHash());
    }

    /************************************************************
     *                  getters and setters
     ************************************************************/

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getId() {
        return id;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public Person getMiner() {
        return miner;
    }

    public int getMinerReward() {
        return minerReward;
    }

    /************************************************************
     *                  private methods
     ************************************************************/

    private byte[] getBytesToHash() throws IOException {
        // todo: try to remove part of this to the blockchain class,
        //  because this method calls for every magic number
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(previousBlockHash);
        os.write(ByteUtils.longToBytes(id));
        os.write(ByteUtils.longToBytes(timestamp));
        os.write(miner.toString().getBytes());
        os.write(ByteUtils.intToBytes(minerReward));
        for (Transaction s : transactions) {
            os.write(Transaction.getStringForSign(s.getTransactionId(), s.getFrom(), s.getTo(), s.getAmount())
                    .getBytes());
        }
        os.write(magicNumber);
        return os.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Block:\n");
        res.append("Created by: ");
        res.append(miner);
        res.append('\n');
        res.append(miner);
        res.append(" gets ");
        res.append(minerReward);
        res.append(" VC\n");
        res.append("Id: ");
        res.append(id);
        res.append('\n');
        res.append("Timestamp: ");
        res.append(timestamp);
        res.append('\n');
        res.append("Magic number: ");
        res.append(magicNumber);
        res.append('\n');
        res.append("Hash of the previous block:\n");
        if (id == 1) {
            res.append('0');
        } else {
            res.append(toHexString(previousBlockHash));
        }
        res.append('\n');
        res.append("Hash of the block:\n");
        res.append(toHexString(blockHash));
        res.append("\n");
        res.append("Block data:");
        if (transactions.length == 0) {
            res.append("No transactions");
        } else {
            Stream.of(transactions).forEach((s -> {
                res.append("\n");
                res.append(s.toString());
            }));
        }

        return res.toString();
    }

    public boolean verifyHash() throws IOException {
        return Arrays.equals(blockHash, HashUtils.getHash(getBytesToHash()));
    }
}
