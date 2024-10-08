package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.config.PaypalPaymentIntent;
import com.itsthatjun.ecommerce.config.PaypalPaymentMethod;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {

    @Autowired
    private APIContext apiContext;

    @ApiOperation(value = "")
    public Payment createPayment(
            BigDecimal total,
            String currency,
            PaypalPaymentMethod method,
            PaypalPaymentIntent intent,
            String description,
            String cancelUrl,
            String successUrl,
            String orderSn) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        total = total.setScale(2, RoundingMode.HALF_UP);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setCustom(orderSn);     // order serial number

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // what happens after transaction
        // success and fail redirect.
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    @ApiOperation(value = "The actual transaction part, even though the GUI payment is complete, but they"
                        + "just return PaymentId , PayerID and token. These are saying the buyer and PayPal are"
                        + "agreed to pay the set amount. You take these \"receipt\" to PayPal and cash out.")
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    public Refund createRefund(String saleId, BigDecimal amount) throws PayPalRESTException {
        Sale sale = new Sale();
        sale.setId(saleId);

        Amount refundAmount = new Amount();
        refundAmount.setCurrency("USD");
        refundAmount.setTotal(String.valueOf(amount));

        Refund refund = new Refund();
        refund.setAmount(refundAmount);

        // Use the Sale object and RefundRequest to initiate the refund
        return sale.refund(apiContext, refund);
    }
}
