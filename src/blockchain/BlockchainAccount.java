package blockchain;

public class BlockchainAccount extends Account{

    protected BlockchainAccount() {
        super(0,"Blockchain");
    }

    public void makeBonus(Account receiver, int amount)
            throws Exception {
        // Bonus from blockchain to receiver;
        Blockchain blockchain = Blockchain.getInstance();
        long id = blockchain.getTransactionId();
        final String transactionForSign = Transaction.getStringForSign(id, getPerson(), receiver.getPerson(), amount);
        byte[] sign = getSignature(transactionForSign);
        Transaction transfer = new Transaction(
                TransactionType.BONUS,
                getPerson(),
                receiver.getPerson(),
                amount,
                sign,
                id,
                getPublicKey());
        if (!blockchain.addTransaction(transfer)) {
            throw new Exception("error adding the bonus");
        }
    }

}
