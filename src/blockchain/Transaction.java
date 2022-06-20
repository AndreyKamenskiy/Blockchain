package blockchain;

import utils.SignatureUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;

public class Transaction {

    private final TransactionType type;
    private final Person from;
    private final Person to;
    private final int amount;
    private final byte[] signature;
    private final long transactionId;
    private final PublicKey publicKey;

    public Transaction(TransactionType type,
                       final Person from,
                       final Person to,
                       final int amount,
                       final byte[] signature,
                       final long transactionId,
                        final PublicKey fromPublicKey
    ) throws IllegalArgumentException {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        this.type = type;
        this.from = from;
        this.to = to;
        this.signature = signature;
        this.transactionId = transactionId;
        this.amount = amount;
        publicKey = fromPublicKey;
        if (!verifySignature()) {
            throw new IllegalArgumentException("The signature does not match the message");
        }
    }

    public int getAmount() {
        return amount;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public boolean verifySignature() {
        String messageForSign = getStringForSign(transactionId, from, to, amount);
        try {
            return SignatureUtils.verifyStringSignature(messageForSign, signature, publicKey);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Person getFrom() {
        return from;
    }

    public Person getTo() {
        return to;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case BONUS: return "Bonus " + amount + " VC to " + to.getName();
            case TRANSFER: return from.getName() + " sent " + amount + " VC to " + to.getName();
        }
        return "Unknown transaction";
    }

    public static String getStringForSign(long transactionId, Person from, Person to, int amount) {
        return transactionId + from.toString() + to.toString() + amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (amount != that.amount) return false;
        if (transactionId != that.transactionId) return false;
        if (type != that.type) return false;
        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        if (!Arrays.equals(signature, that.signature)) return false;
        return publicKey.equals(that.publicKey);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + amount;
        result = 31 * result + Arrays.hashCode(signature);
        result = 31 * result + (int) (transactionId ^ (transactionId >>> 32));
        result = 31 * result + publicKey.hashCode();
        return result;
    }
}
