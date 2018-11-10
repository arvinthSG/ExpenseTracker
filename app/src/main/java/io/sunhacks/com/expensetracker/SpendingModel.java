
package io.sunhacks.com.expensetracker;

import java.util.Date;

import io.sunhacks.com.expensetracker.Sms;


public class SpendingModel {

    // Amount that was spent
    private Double _amount;
    // Where did I spend my Money!!?
    private String _merchant;
    // Well, what kind of place is that?
    private String _category;
    // Fine. Which bank did I use?
    private String _account;
    // Raw message body just in case we mess up parsing.
    private Sms _rawMessage;


    //OMG. When did I spend so much?
    private Date _smsTime;

    //Hold on, did I spend this, or did someone give me money?
    private boolean _debit; // True = I spent, False = I got money!


    public Date getSmsTime() {
        return _smsTime;
    }

    public void setSmsTime(Date _smsTime) {
        this._smsTime = _smsTime;
    }


    public Double getAmount() {
        return _amount;
    }

    public void setAmount(Double _amount) {
        this._amount = _amount;
    }

    public String getMerchant() {
        return _merchant;
    }

    public void setMerchant(String _merchant) {
        this._merchant = _merchant;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String _category) {
        this._category = _category;
    }

    public String getAccount() {
        return _account;
    }

    public void setAccount(String _account) {
        this._account = _account;
    }

    public Sms getRawMessage() {
        return _rawMessage;
    }

    public void setRawMessage(Sms _rawMessage) {
        this._rawMessage = _rawMessage;
    }


    public boolean isDebit() {
        return _debit;
    }

    public void setDebit(boolean _debit) {
        this._debit = _debit;
    }
}
