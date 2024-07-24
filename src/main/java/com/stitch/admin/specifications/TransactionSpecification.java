package com.stitch.admin.specifications;

import com.stitch.admin.model.entity.Transaction;
import com.stitch.admin.model.enums.PaymentMode;
import com.stitch.admin.model.enums.TransactionStatus;
import com.stitch.admin.model.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TransactionSpecification {

    public static Specification<Transaction> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(status)) {
                try {
                    TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
                    return criteriaBuilder.equal(root.get("status"), transactionStatus);
                } catch (IllegalArgumentException e) {
                    System.err.println("Exception in hasStatus ==> "+e.getMessage());
                    return criteriaBuilder.conjunction();
                }
            }
            return criteriaBuilder.conjunction();
        };
    }


    public static Specification<Transaction> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(type)) {
                try {
                    TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                    return criteriaBuilder.equal(root.get("transactionType"), transactionType);
                } catch (IllegalArgumentException e) {
                    System.err.println("Exception in hasType ==> " + e.getMessage());
                    return criteriaBuilder.conjunction();
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Transaction> hasPaymentMode(String paymentMode) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(paymentMode)) {
                try {
                    PaymentMode mode = PaymentMode.valueOf(paymentMode.toUpperCase());
                    return criteriaBuilder.equal(root.get("paymentMode"), mode);
                } catch (IllegalArgumentException e) {
                    System.err.println("Exception in hasPaymentMode ==> " + e.getMessage());
                    return criteriaBuilder.conjunction();
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Transaction> isCreatedBetween(Instant start, Instant end) {
        return (root, query, criteriaBuilder) -> {
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("dateCreated"), start, end);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Transaction> isCreatedInMonthYear(String monthYear) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(monthYear)) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                    LocalDate firstDayOfMonth = LocalDate.parse(monthYear, formatter).withDayOfMonth(1);
                    Instant start = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
                    Instant end = firstDayOfMonth.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                    return criteriaBuilder.between(root.get("dateCreated"), start, end);
                } catch (Exception e) {
                    System.err.println("Exception in month year --> "+e.getMessage());
                    return criteriaBuilder.conjunction();
                }
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Transaction> isCreatedSince(Instant start) {
        return (root, query, criteriaBuilder) -> {
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), start);
            }
            return criteriaBuilder.conjunction();
        };
    }

}
