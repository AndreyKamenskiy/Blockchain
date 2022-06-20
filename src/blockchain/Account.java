package blockchain;

import utils.KeysGenerator;
import utils.SignatureUtils;

import java.security.*;

public class Account {
    private final Person person;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    protected Account(String name) {
        this.person = new Person(Blockchain.getInstance().getNewAccountId(), name);
        KeyPair keyPair = KeysGenerator.getNewKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        Blockchain.getInstance().getNewAccountBonus(this);
    }

    protected Account(long id, String name) {
        this.person = new Person(id, name);
        KeyPair keyPair = KeysGenerator.getNewKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }


    public Person getPerson() {
        return person;
    }

    protected PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public long getBalance() {
        return Blockchain.getInstance().getBalance(person);
    }

    public void makeTransfer(Person receiver, int amount) throws Exception {
        // transfer to receiver account some virtual coins
        if (receiver.equals(person)) {
            return;
        }
        Blockchain blockchain = Blockchain.getInstance();
        long id = blockchain.getTransactionId();
        final String transactionForSign = Transaction.getStringForSign(id, person, receiver, amount);
        byte[] sign = getSignature(transactionForSign);
        Transaction transfer = new Transaction(
                TransactionType.TRANSFER,
                person,
                receiver,
                amount,
                sign,
                id,
                publicKey);
        if (!blockchain.addTransaction(transfer)) {
            throw new Exception("error adding a transaction");
        }
    }

    protected byte[] getSignature(String text)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return SignatureUtils.signString(text, privateKey);
    }

    @Override
    public String toString() {
        return person.toString();
    }

    //todo: write load/save methods
}
