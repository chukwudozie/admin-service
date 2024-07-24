package com.stitch.admin.model.dto;

import com.stitch.admin.model.enums.PaymentMode;
import com.stitch.admin.model.enums.TransactionStatus;
import com.stitch.admin.model.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionDto {

    private String reference;
    private BigDecimal amount;
    private String gatewayResponse;
    private String paidAt;
    private String createdAt;
    private String channel;
    private String currency;
    private String ipAddress;
    private String transactionId;
    private String orderId;
    private String walletTransactionId;
    private String userId;
    private String walletId;
    private String paymentCardId;
    private String cardTransactionId;
    private PaymentMode paymentMode;
    private BigDecimal fee;
    private String productCategory;
    private String narration;
    private String description;
    private TransactionStatus status;
    private TransactionType transactionType;
    protected Instant dateCreated;
    protected Instant lastUpdated;
}
