package com.ssm.mail.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.sys.dto.BaseDTO;

/**
 */
@Table(name = "tb_MESSAGE_TRANSACTION")
public class MessageTransaction extends BaseDTO {
    
    private static final long serialVersionUID = 6726130570559853932L;

    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long transactionId;

    private Long messageId;

    private String transactionStatus;

    private String transactionMessage;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage == null ? null : transactionMessage.trim();
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
    
}