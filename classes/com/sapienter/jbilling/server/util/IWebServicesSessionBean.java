/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2009 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jbilling.

 jbilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jbilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jbilling.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sapienter.jbilling.server.util;

import java.math.BigDecimal;
import java.util.Date;

import javax.jws.WebService;

import com.sapienter.jbilling.common.SessionInternalError;
import com.sapienter.jbilling.server.invoice.InvoiceWS;
import com.sapienter.jbilling.server.item.ItemDTOEx;
import com.sapienter.jbilling.server.item.ItemTypeWS;
import com.sapienter.jbilling.server.order.OrderLineWS;
import com.sapienter.jbilling.server.order.OrderWS;
import com.sapienter.jbilling.server.payment.PaymentAuthorizationDTOEx;
import com.sapienter.jbilling.server.payment.PaymentWS;
import com.sapienter.jbilling.server.user.ContactWS;
import com.sapienter.jbilling.server.user.CreateResponseWS;
import com.sapienter.jbilling.server.user.UserTransitionResponseWS;
import com.sapienter.jbilling.server.user.UserWS;
import com.sapienter.jbilling.server.user.ValidatePurchaseWS;
import com.sapienter.jbilling.server.entity.AchDTO;
import com.sapienter.jbilling.server.notification.MessageDTO;
import com.sapienter.jbilling.server.user.CompanyWS;
import com.sapienter.jbilling.server.util.audit.db.EventLogDTO;
import java.util.Collection;
import java.util.List;

/**
 * Interface for WebServicesSessionBean
 */
@WebService
public interface IWebServicesSessionBean {

    /*
     * INVOICES
     */
    /**
     * Generates invoices for orders not yet invoiced for this user. Optionally
     * only allow recurring orders to generate invoices. Returns the ids of the
     * invoices generated.
     */
    public Integer[] createInvoice(Integer userId, boolean onlyRecurring)
            throws SessionInternalError;

    public InvoiceWS getInvoiceWS(Integer invoiceId) throws SessionInternalError;

    /**
     * Deletes an invoice
     *
     * @param invoiceId The id of the invoice to delete
     */
    public void deleteInvoice(Integer invoiceId);

    public InvoiceWS getLatestInvoice(Integer userId) throws SessionInternalError;

    public Integer[] getLastInvoices(Integer userId, Integer number) throws SessionInternalError;

    public Integer[] getInvoicesByDate(String since, String until) throws SessionInternalError;

    public Integer[] getUserInvoicesByDate(Integer userId, String since,
            String until) throws SessionInternalError;

    /**
     * ------------------- INVOICE API EXTENSION --------------------------
     */
    /**
     * Generates a new invoice for an order, or adds the order to an existing
     * invoice.
     *
     * @param orderId order id to generate an invoice for
     * @param invoiceId optional invoice id to add the order to. If null, a new
     * invoice will be created.
     * @return id of generated invoice, null if no invoice generated.
     * @throws SessionInternalError if order id is null.
     */
    public Integer createInvoiceFromOrder(Integer orderId, Integer invoiceId) throws SessionInternalError;

    /**
     * Retrieves a list of all the {@link InvoiceWS invoices} in a given period
     * of time. The method will return an array of the InvoiceWS objects.<br>
     *
     * If no invoices where generated for the specified period, an empty array
     * is returned. If the parameters do not follow the required format
     * (yyyy-mm-dd), null is returned.
     *
     * @param since the starting date for the data extraction
     * @param until the ending date for the data extraction
     * @return an array of <code>InvoiceWS</code> objects or an empty array if
     * nothing found
     * @throws SessionInternalError when internal error occurs
     */
    public InvoiceWS[] getInvoiceListByDate(String since, String until) throws SessionInternalError;

    /**
     * Retrieves invoices belonging to a customer, starting from the last one.
     *
     * @param userId the id of the customer
     * @param number the number of invoices that are to be retrieved
     *
     * @return an array of invoices
     * @throws SessionInternalError
     */
    public InvoiceWS[] getLastInvoicesForUser(Integer userId, Integer number) throws SessionInternalError;

    /**
     * Retrieves an array of Overdue invoice ids.
     *
     * @param date the current date
     * @return an array of invoice ids or null if nothing found
     * @throws SessionInternalError
     */
    public Integer[] getOverdueInvoiceIds(String date) throws SessionInternalError;

    /**
     * Returns an array of all unpaid invoices for given user ID.
     *
     * @param userId user id
     * @return array of un-paid invoice IDs
     * @throws SessionInternalError
     */
    public InvoiceWS[] getUnpaidInvoices(Integer userId) throws SessionInternalError;

    /**
     * Retrieves an array of the Invoice ids by given Status
     *
     * @param status the status of the invoice
     * @return an array of Invoice ids or null if nothing found
     * @throws SessionInternalError
     */
    public Integer[] getInvoiceIdsByStatus(Integer status) throws SessionInternalError;

    /**
     * Search for Invoices by given string parameter.
     *
     * @param searchParam the search parameter string
     * @return an array of Invoice ids or null if nothing found
     * @throws SessionInternalError
     */
    public Integer[] searchInvoiceIds(String searchValue) throws SessionInternalError;

    /**
     * Generates and returns the paper invoice PDF for the given invoiceId.
     * TODO: Extra check might require to make sure invoice belongs to user.
     *
     * @param userId an id of the customer
     * @param invoiceId an id of the invoice that will be downloaded.
     * @return an array of bytes for PDF invoice or null if nothing found.
     * @throws SessionInternalError when internal error occurs
     */
    public byte[] getPaperInvoicePDF(Integer invoiceId) throws SessionInternalError;

    /**
     * Sends an email with the invoice to a customer. This API call is used to
     * manually send an email invoice to a customer. TODO: Extra check might
     * require to make sure invoice belongs to user.
     *
     * @param userId an id of the customer to send email to
     * @param invoiceId an id of the invoice that will be send out.
     * @return <code>true</code> when email was sent * * * * * * * *
     * successfully, <code>false</code> otherwise.
     * @throws SessionInternalError when internal error occurs
     */
    public Boolean emailInvoice(Integer invoiceId, Integer userId) throws SessionInternalError;

    /*
     * USERS
     */
    /**
     * Creates a new user. The user to be created has to be of the roles
     * customer or partner. The username has to be unique, otherwise the
     * creating won't go through. If that is the case, the return value will be
     * null.
     *
     * @param newUser The user object with all the information of the new user.
     * If contact or credit card information are present, they will be included
     * in the creation although they are not mandatory.
     * @return The id of the new user, or null if non was created
     */
    public Integer createUser(UserWS newUser) throws SessionInternalError;

    public void deleteUser(Integer userId) throws SessionInternalError;

    public void updateUserContact(Integer userId, Integer typeId,
            ContactWS contact) throws SessionInternalError;

    /**
     * @param user
     */
    public void updateUser(UserWS user) throws SessionInternalError;

    /**
     * Retrieves a user with its contact and credit card information.
     *
     * @param userId The id of the user to be returned
     */
    public UserWS getUserWS(Integer userId) throws SessionInternalError;

    /**
     * Retrieves all the contacts of a user
     *
     * @param userId The id of the user to be returned
     */
    public ContactWS[] getUserContactsWS(Integer userId)
            throws SessionInternalError;

    /**
     * Retrieves the user id for the given username
     */
    public Integer getUserId(String username) throws SessionInternalError;

    /**
     * Retrieves an array of users in the required status
     */
    public Integer[] getUsersInStatus(Integer statusId)
            throws SessionInternalError;

    /**
     * Retrieves an array of users in the required status
     */
    public Integer[] getUsersNotInStatus(Integer statusId)
            throws SessionInternalError;

    /**
     * Retrieves an array of users in the required status
     */
    public Integer[] getUsersByCustomField(Integer typeId, String value)
            throws SessionInternalError;

    /**
     * Retrieves an array of users in the required status
     */
    public Integer[] getUsersByCreditCard(String number)
            throws SessionInternalError;

    /**
     * Retrieves an array of users in the required status
     */
    public Integer[] getUsersByStatus(Integer statusId, Integer entityId,
            boolean in) throws SessionInternalError;

    /**
     * Creates a user, then an order for it, an invoice out the order and tries
     * the invoice to be paid by an online payment This is ... the mega call !!!
     */
    public CreateResponseWS create(UserWS user, OrderWS order)
            throws SessionInternalError;

    /**
     * Validates the credentials and returns if the user can login or not
     *
     * @param username
     * @param password
     * @return 0 if the user can login (success), or grater than 0 if the user
     * can not login. See the constants in WebServicesConstants (AUTH*) for
     * details.
     * @throws SessionInternalError
     */
    public Integer authenticate(String username, String password)
            throws SessionInternalError;

    /**
     * Updates a user's credit card.
     *
     * @param userId The id of the user updating credit card data.
     * @param creditCard The credit card data to be updated.
     */
    public void updateCreditCard(Integer userId,
            com.sapienter.jbilling.server.entity.CreditCardDTO creditCard)
            throws SessionInternalError;

    public void updateAch(Integer userId, AchDTO ach) throws SessionInternalError;

    public void setAuthPaymentType(Integer userId, Integer autoPaymentType, boolean use)
            throws SessionInternalError;

    public Integer getAuthPaymentType(Integer userId) throws SessionInternalError;

    /**
     * Implementation of the User Transitions List webservice. This accepts a
     * start and end date as arguments, and produces an array of data containing
     * the user transitions logged in the requested time range.
     *
     * @param from Date indicating the lower limit for the extraction of
     * transition logs. It can be <code>null</code>, in such a case, the
     * extraction will start where the last extraction left off. If no
     * extractions have been done so far and this parameter is null, the
     * function will extract from the oldest transition logged.
     * @param to Date indicatin the upper limit for the extraction of transition
     * logs. It can be <code>null</code>, in which case the extraction will have
     * no upper limit.
     * @return UserTransitionResponseWS[] an array of objects containing the
     * result of the extraction, or <code>null</code> if there is no data thas
     * satisfies the extraction parameters.
     */
    public UserTransitionResponseWS[] getUserTransitions(Date from, Date to)
            throws SessionInternalError;

    /**
     * @return UserTransitionResponseWS[] an array of objects containing the
     * result of the extraction, or <code>null</code> if there is no data thas
     * satisfies the extraction parameters.
     */
    public UserTransitionResponseWS[] getUserTransitionsAfterId(Integer id)
            throws SessionInternalError;

    /**
     * ------------------- USER API EXTENSION --------------------------
     */
    /**
     * Retrieves a list of all {@link UserWS users} in a given status.
     *
     * @param statusId the status id that will be used for extraction
     * @return an array of <code>UserWS</code> objects in a given status.
     * @throws SessionInternalError when internal error occurs
     */
    public UserWS[] getUserListInStatus(Integer statusId) throws SessionInternalError;

    /**
     * Retrieves a list of all {@link UserWS customers} in a given status
     * including sub-accounts. This call excludes any other users that are not
     * Customers.
     *
     * @param statusId the status id that will be used for extraction
     * @return a Collection of <code>UserWS</code> objects in a given status.
     * @throws SessionInternalError when internal error occurs
     */
    public Collection<UserWS> getCustomersInStatus(Integer statusId) throws SessionInternalError;

    /**
     * Retrieves a list of all Customer ids in a given status including
     * sub-accounts.
     *
     * @param statusId the status id that will be used for extraction
     * @return an array of user ids in a given status.
     * @throws SessionInternalError
     */
    public Integer[] getCustomerIdsInStatus(Integer statusId) throws SessionInternalError;

    /**
     * Search for {@link UserWS users}, including sub-accounts by given search
     * parameter. Only search users who are customers.
     *
     * @param searchValue the string parameter to search for
     * @return an array of <code>UserWS</code> objects if any match found, null
     * otherwise. Null also returned when search parameter is null
     * @throws SessionInternalError when internal error occurs
     */
    public UserWS[] searchCustomers(String searchValue) throws SessionInternalError;

    /**
     * Retrieves a list of customers by given search parameter.
     *
     * @param searchValue the search parameter string
     * @return an array of user ids or null if nothing found
     * @throws SessionInternalError
     */
    public Integer[] searchCustomerIds(String searchValue) throws SessionInternalError;

    /*
     * ORDERS
     */
    /**
     * @return the information of the payment aurhotization, or NULL if the user
     * does not have a credit card
     */
    public PaymentAuthorizationDTOEx createOrderPreAuthorize(OrderWS order)
            throws SessionInternalError;

    public Integer createOrder(OrderWS order) throws SessionInternalError;

    public OrderWS rateOrder(OrderWS order) throws SessionInternalError;

    public OrderWS[] rateOrders(OrderWS orders[]) throws SessionInternalError;

    public Integer createOrderAndInvoice(OrderWS order)
            throws SessionInternalError;

    public void updateOrder(OrderWS order) throws SessionInternalError;

    public OrderWS getOrder(Integer orderId) throws SessionInternalError;

    public Integer[] getOrderByPeriod(Integer userId, Integer periodId)
            throws SessionInternalError;

    public OrderLineWS getOrderLine(Integer orderLineId)
            throws SessionInternalError;

    public void updateOrderLine(OrderLineWS line) throws SessionInternalError;

    public OrderWS getLatestOrder(Integer userId) throws SessionInternalError;

    public Integer[] getLastOrders(Integer userId, Integer number)
            throws SessionInternalError;

    public void deleteOrder(Integer id) throws SessionInternalError;

    public OrderWS getCurrentOrder(Integer userId, Date date)
            throws SessionInternalError;

    public OrderWS updateCurrentOrder(Integer userId, OrderLineWS[] lines,
            String pricing, Date date, String eventDescription)
            throws SessionInternalError;

    /*
     * PAYMENT
     */
    /**
     * Pays given invoice, using the first credit card available for invoice'd
     * user.
     *
     * @return <code>null</code> if invoice has not positive balance, or if user
     * does not have credit card
     * @return resulting authorization record. The payment itself can be found
     * by calling getLatestPayment
     */
    public PaymentAuthorizationDTOEx payInvoice(Integer invoiceId)
            throws SessionInternalError;

    public Integer applyPayment(PaymentWS payment, Integer invoiceId)
            throws SessionInternalError;

    public PaymentWS getPayment(Integer paymentId) throws SessionInternalError;

    public PaymentWS getLatestPayment(Integer userId)
            throws SessionInternalError;

    public Integer[] getLastPayments(Integer userId, Integer number)
            throws SessionInternalError;

    public PaymentAuthorizationDTOEx processPayment(PaymentWS payment);

    /**
     * ------------------- PAYMENT API EXTENSION --------------------------
     */
    /**
     * Updates user's payment details.
     *
     * @param payment The payment data to be updated.
     * @throws SessionInternalError
     */
    public void updatePayment(PaymentWS payment) throws SessionInternalError;

    /**
     * Deletes payment for user.
     *
     * @param paymentId the id of the payment to delete
     * @throws SessionInternalError
     */
    public void deletePayment(Integer paymentId) throws SessionInternalError;

    /**
     * Retrieves several payments for a customer, starting from the last one.
     *
     * @param userId id of the customer whose payment information is to be
     * retrieved
     * @param number the number of payments to retrieve
     * @return an array of PaymentWS or null if none found. If the input
     * parameters are missing or incorrect, a SessionInternalError is issued.
     * @throws SessionInternalError
     *
     */
    public PaymentWS[] getLastUserPayments(Integer userId, Integer number) throws SessionInternalError;

    /**
     * Retrieve all the payments created in a given period of time for
     * organisation.
     *
     * @param since the starting date for the data extraction
     * @param until the ending date for the data extraction
     * @return an array of payment ids or null if none found. If the input
     * parameters are missing or are not in required format (yyyy-mm-dd), null
     * is returned.
     * @throws SessionInternalError
     */
    public Integer[] getPaymentIdsByDate(String since, String until) throws SessionInternalError;

    /**
     * Retrieve all the payments created in a given period of time for
     * organisation.
     *
     * @param since the starting date for the data extraction
     * @param until the ending date for the data extraction
     * @return an array of PaymentWS or null if none found. If the input
     * parameters are missing or are not in required format (yyyy-mm-dd), null
     * is returned.
     * @throws SessionInternalError
     */
    public PaymentWS[] getPaymentsByDate(String since, String until) throws SessionInternalError;

    /**
     * Retrieve all the payments created in a given period of time for user.
     *
     * @param since the starting date for the data extraction
     * @param until the ending date for the data extraction
     * @param userId id of the customer whose payment information is to be
     * retrieved
     * @return an array of PaymentWS or null if none found. If the input
     * parameters are missing or are not in required format (yyyy-mm-dd), null
     * is returned.
     * @throws SessionInternalError
     */
    public PaymentWS[] getUserPaymentsByDate(String since, String until, Integer userId) throws SessionInternalError;

    /**
     * Search all the payments by given search parameter for organisation. TODO:
     * This method is not secured or in a jUnit test
     *
     * @param searchValue the search parameter string
     *
     * @return an array of PaymentWS or null if none found. If the input
     * parameters are missing , null is returned.
     * @throws SessionInternalError
     */
    public PaymentWS[] searchPayments(String searchValue) throws SessionInternalError;

    /**
     * Un-links a payment from an invoice, effectively making the invoice
     * "unpaid" by removing the payment balance.
     *
     * If either invoiceId or paymentId parameters are null, no operation will
     * be performed.
     *
     * @param invoiceId target Invoice
     * @param paymentId payment to be unlink
     */
    public void removePaymentLink(Integer invoiceId, Integer paymentId);

    /**
     * Applies an existing payment to an invoice.
     *
     * If either invoiceId or paymentId parameters are null, no operation will
     * be performed.
     *
     * @param invoiceId target invoice
     * @param paymentId payment to apply
     */
    public void createPaymentLink(Integer invoiceId, Integer paymentId);

    /**
     * Sends a Payment email notification to a customer for given payment
     *
     * @param paymentId
     * @return true if email was successfully sent, false otherwise
     * @throws SessionInternalError
     */
    public boolean notifyPaymentByEmail(Integer paymentId) throws SessionInternalError;

    /*
     * ITEM
     */
    public Integer createItem(ItemDTOEx item) throws SessionInternalError;

    /**
     * Retrieves an array of items for the caller's entity.
     *
     * @return an array of items from the caller's entity
     */
    public ItemDTOEx[] getAllItems() throws SessionInternalError;

    public ItemDTOEx getItem(Integer itemId, Integer userId, String pricing);

    public void updateItem(ItemDTOEx item);

    public Integer createItemCategory(ItemTypeWS itemType)
            throws SessionInternalError;

    public void updateItemCategory(ItemTypeWS itemType)
            throws SessionInternalError;

    public InvoiceWS getLatestInvoiceByItemType(Integer userId,
            Integer itemTypeId) throws SessionInternalError;

    /**
     * Return 'number' most recent invoices that contain a line item with an
     * item of the given item type.
     */
    public Integer[] getLastInvoicesByItemType(Integer userId,
            Integer itemTypeId, Integer number) throws SessionInternalError;

    public OrderWS getLatestOrderByItemType(Integer userId, Integer itemTypeId)
            throws SessionInternalError;

    public Integer[] getLastOrdersByItemType(Integer userId, Integer itemTypeId,
            Integer number) throws SessionInternalError;

    public BigDecimal isUserSubscribedTo(Integer userId, Integer itemId);

    public Integer[] getUserItemsByCategory(Integer userId, Integer categoryId);

    public ItemDTOEx[] getItemByCategory(Integer itemTypeId);

    public ItemTypeWS[] getAllItemCategories();

    /**
     * Validate if the user can buy this. This depends on the balance type: -
     * none: can always buy - pre paid: if there is enough dynamic balance -
     * credit limit: only if credit limie - dynamix balance is enough
     *
     * @param userId
     * @param itemId
     * @param fields
     * @return 0, if she can not. >o the number of quantities that she can buy
     */
    public ValidatePurchaseWS validatePurchase(Integer userId, Integer itemId,
            String fields);

    public ValidatePurchaseWS validateMultiPurchase(Integer userId,
            Integer[] itemId, String[] fields);

    public void generateRules(String rulesData) throws SessionInternalError;

    /*
     * ORGANIZATION
     */
    /**
     * Retrieves caller's {@link CompanyWS company} details
     *
     * @return a <code>CompanyWS</code> details
     */
    public CompanyWS getCompany();

    /**
     * Updates company details for caller
     *
     * @param companyWS a company details to update
     */
    public void updateCompany(CompanyWS companyWS);

    /*
     * NOTIFICATIONS
     */
    /**
     * Retrieves a list of {@link MessageDTO natification} messages for
     * organisation.
     *
     * @return an array of <code>MessageDTO</code> objects or null if nothing
     * found.
     * @throws SessionInternalError
     */
    public MessageDTO[] getNotificationMessages() throws SessionInternalError;

    /**
     * Creates or updates {@link MessageDTO natification} messages
     *
     * @param messageId id of the notification message to update
     * @param dto the message to update or create
     * @return an id of newly created or updated notification message
     * @throws SessionInternalError
     */
    public Integer createUpdateNofications(Integer messageId, MessageDTO dto) throws SessionInternalError;

    /**
     *
     * ------------------- MISC API EXTENSION --------------------------
     */
    /**
     * Saves uploaded logo image file for the user's entity (company).
     *
     * @param inBytes an array of bytes for image to upload.
     * @return true if image was successfully saved, false otherwise
     * @throws SessionInternalError when internal error occurs
     */
    public boolean uploadLogo(byte[] inBytes) throws SessionInternalError;

    /**
     * Get the absolute logo image path for organisation.
     *
     * @return a string path for logo image
     */
    public String getLogoPath();

    /**
     * Retrieves an event logs for user account.
     *
     * @param userId the id of the user whose event log info to be retrieved
     * @return a list of <code>EventLogDTO</code>
     */
    public List<EventLogDTO> getUserEventLog(Integer userId);

    /**
     * Retrieves a list of {@link OptionDTO options} for used in forms fields
     * with a select box.
     *
     * @param type the type of select option to retrieve
     * @return a <code>Collection</code> of <code>OptionDTO</code> objects or
     * null if given type cannot be found
     * @throws SessionInternalError
     */
    public Collection<OptionDTO> getSelectOption(String type) throws SessionInternalError;

    /**
     * Generate age receivable report for organisation. TODO: This method is not
     * secured or in a jUnit test
     *
     * @param since the starting date for the data extraction
     * @param until the ending date for the data extraction
     * @return a list of ReportWS objects or null if nothing found
     * @throws SessionInternalError
     */
    public List<ReportWS> getAgeReceivableReport(String since, String until) throws SessionInternalError;
    
    /**
     * Utility method for importing invoices into JBilling.
     *
     * @param invoices a list of invoices to import
     * @throws SessionInternalError
     */
    public void importInvoices(Collection<InvoiceWS> invoices) throws SessionInternalError;
}
