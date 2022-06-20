package blockchain;

// TODO: for whole project: add logging.

// todo: make class checker to verify everything: block, transaction, blockchain, account - publicKey, accountsBalance
// main idea: complex loaded blockchain control;

// TODO: 19.06.2022 make some classes unchangeable;

import java.util.Map;
import java.util.Set;

// TODO: 19.06.2022 make own exceptions;

// TODO: 20.06.2022 provide builder template to work simulator to change constants to variable;

public class Main {

    static void printBalances(Set<Map.Entry<Person, Integer>> balances) {
        System.out.println("Balances:");
        for (var curr : balances) {
            System.out.printf("%s have %d VC\n", curr.getKey().toString(), curr.getValue());
        }
        System.out.println();
    }

    public static void main(String[] args) {

        final int minersNum = 10;
        final int clientsNum = 10;
        WorkSimulator miningController = new WorkSimulator();
        miningController.makeClients(clientsNum);
        miningController.makeMiners(minersNum);
        miningController.mineBlocks(15);

        if (!Blockchain.getInstance().isValid()) {
            System.out.println("invalid blockchain!!!");
        }
        /*printBalances(Blockchain.getInstance().getBalances());
        Blockchain.getInstance().calculateBalances();
        printBalances(Blockchain.getInstance().getBalances());*/
    }

}
