package TestATM;

public class ATM_WithFee {

    private int account;
    private int amount;

    public void setAccount(int account) {
        this.account = account;
    }

    public int getAccount() {
        return account;
    }

    public void setAmount(int amt) {
        this.amount = amt;
    }

    public int getAmount() {
        return amount;
    }

    public void withDrawMoney(int amt) {
        this.setAmount(amt);
        this.setAccount(this.getAccount() - this.getAmount());
    }
}


