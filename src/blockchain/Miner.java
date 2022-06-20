package blockchain;

import utils.RandomUtils;

import java.util.Optional;

public class Miner extends Account implements Runnable, Updatable {

    private final BlockCreator blockCreator;
    private boolean active;

    public Miner(String name) {
        super(name);
        blockCreator = Blockchain.getInstance().getBlockCreator(this);
    }

    public void stop() {
        active = false;
        Blockchain.getInstance().deleteListener(this);
    }

    @Override
    public void run() {
        Blockchain.getInstance().addListener(this);
        active = true;
        while (active) {
            Optional<Block> block = blockCreator.tryToCreateNewBlock(RandomUtils.nextInt());
            block.ifPresent(value -> Blockchain.getInstance().addBlock(value));
        }
    }

    @Override
    public void update() {
        blockCreator.update();
    }

}
