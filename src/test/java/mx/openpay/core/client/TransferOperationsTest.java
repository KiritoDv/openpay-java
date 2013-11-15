/*
 * COPYRIGHT © 2012-2013. OPENPAY.
 * PATENT PENDING. ALL RIGHTS RESERVED.
 * OPENPAY & OPENCARD IS A REGISTERED TRADEMARK OF OPENCARD INC.
 *
 * This software is confidential and proprietary information of OPENCARD INC.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the company policy.
 */
package mx.openpay.core.client;

import static mx.openpay.client.utils.SearchParams.search;
import static mx.openpay.core.client.TestConstans.API_KEY;
import static mx.openpay.core.client.TestConstans.ENDPOINT;
import static mx.openpay.core.client.TestConstans.MERCHANT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mx.openpay.client.Transfer;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.enums.TransactionType;
import mx.openpay.client.exceptions.OpenpayServiceException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author elopez
 */
public class TransferOperationsTest {

    String customerId = "afk4csrazjp1udezj1po";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Before
    public void setUp() throws Exception {
        OpenpayAPI.configure(ENDPOINT, API_KEY, MERCHANT_ID);
    }

    @Test
    public void testList_Merchant() throws Exception {
        List<Transfer> transfers = Transfer.list(search().limit(2));
        assertEquals(2, transfers.size());
    }

    @Test
    public void testList_Merchant_Empty() throws Exception {
        List<Transfer> transfers = Transfer.list(search().limit(2).offset(10000));
        assertEquals(0, transfers.size());
    }

    @Test
    public void testGet_Merchant() throws Exception {
        String transactionId = "tvyuad0uzf5mtq4uvywd";
        Transfer transfer = Transfer.get(transactionId);
        assertEquals(transactionId, transfer.getId());
        assertEquals("OID12345", transfer.getOrderId());
        assertEquals(TransactionType.TRANSFER_FROM.name().toLowerCase(), transfer.getTransactionType());
    }

    @Test
    public void testGet_Merchant_DoesNotExist() throws Exception {
        String transactionId = "tvyuad0uzf3mdq4uvywd";
        try {
            Transfer.get(transactionId);
            fail();
        } catch (OpenpayServiceException e) {
            assertEquals(404, e.getHttpCode().intValue());
        }
    }

    @Test
    public void testGet_MerchantTransferTo() throws Exception {
        String transactionId = "tvyuad0uzf5mtq4uvywd";
        Transfer transfer = Transfer.get(transactionId, TransactionType.TRANSFER_TO);
        assertEquals(transactionId, transfer.getId());
        assertNull(transfer.getOrderId());
        assertEquals(TransactionType.TRANSFER_TO.name().toLowerCase(), transfer.getTransactionType());
    }

    @Test
    public void testGet_MerchantTransferInvalidType() throws Exception {
        String transactionId = "tvyuad0uzf5mtq4uvywd";
        try {
            Transfer.get(transactionId, TransactionType.SPEI);
            fail();
        } catch (OpenpayServiceException e) {
            assertEquals(404, e.getHttpCode().intValue());
        }
    }

    @Test
    public void testCreate() throws Exception {
        String orderId = this.dateFormat.format(new Date());
        Transfer transfer = Transfer.create(this.customerId, "agdn58ngcnogqmzruz1i", new BigDecimal("10.0"),
                "Una descripcion", orderId);
        assertNotNull(transfer.getId());
        assertNotNull(transfer.getCreationDate());
        assertNull(transfer.getCard());
        assertNull(transfer.getBankAccount());
        assertEquals(orderId, transfer.getOrderId());
    }

    @Test
    public void testCreate_NoDestination() throws Exception {
        String orderId = this.dateFormat.format(new Date());
        try {
            Transfer.create(this.customerId, null, new BigDecimal("10.0"), "Una descripcion", orderId);
            fail();
        } catch (OpenpayServiceException e) {
            assertEquals(400, e.getHttpCode().intValue());
        }
    }

    @Test
    public void testCreate_InvalidDestination() throws Exception {
        String orderId = this.dateFormat.format(new Date());
        try {
            Transfer.create(this.customerId, "", new BigDecimal("10.0"), "Una descripcion", orderId);
            fail();
        } catch (OpenpayServiceException e) {
            assertEquals(404, e.getHttpCode().intValue());
        }
    }

    @Test
    public void testGet_Customer() throws Exception {
        String transactionId = "tvyuad0uzf5mtq4uvywd";
        Transfer transfer = Transfer.get(this.customerId, transactionId);
        assertEquals(transactionId, transfer.getId());
        assertEquals("OID12345", transfer.getOrderId());
    }

    @Test
    public void testGet_Customer_NotFound() throws Exception {
        String transactionId = "tvyua24uzf5mtq4uvywd";
        try {
            Transfer.get(this.customerId, transactionId);
            fail();
        } catch (OpenpayServiceException e) {
            assertEquals(404, e.getHttpCode().intValue());
        }
    }

    @Test
    public void testList_Customer() throws Exception {
        List<Transfer> transfers = Transfer.list(this.customerId, search().limit(2));
        assertEquals(2, transfers.size());
    }

    @Test
    public void testList_Customer_Empty() throws Exception {
        List<Transfer> transfers = Transfer.list(this.customerId, search().limit(2).offset(10000));
        assertEquals(0, transfers.size());
    }
}