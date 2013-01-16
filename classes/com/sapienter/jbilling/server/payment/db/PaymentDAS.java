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
package com.sapienter.jbilling.server.payment.db;

import com.sapienter.jbilling.common.CommonConstants;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.sapienter.jbilling.common.Constants;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.sapienter.jbilling.server.user.db.UserDAS;
import com.sapienter.jbilling.server.user.db.UserDTO;
import com.sapienter.jbilling.server.util.db.AbstractDAS;
import com.sapienter.jbilling.server.util.db.CurrencyDAS;
import com.sapienter.jbilling.server.util.db.CurrencyDTO;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentDAS extends AbstractDAS<PaymentDTO> {

    // used for the web services call to get the latest X
    public List<Integer> findIdsByUserLatestFirst(Integer userId, int maxResults) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                .add(Restrictions.eq("u.id", userId))
                .setProjection(Projections.id()).addOrder(Order.desc("id"))
                .setMaxResults(maxResults);
        return criteria.list();
    }

    // used for the web services call to get the X by period
    public List<Integer> findIdsByPeriod(Integer entityId, Date start, Date end) {
        String hql = "select p.id "
                + "  from payment p, base_user u "
                + " where p.user_id = u.id "
                + "   and p.id not in (select payment_id from partner_payout where payment_id is not null) "
                + "   and p.deleted = 0 "
                + "   and p.is_preauth = 0"
                + "   and p.method_id = pm.id "
                + "   and u.entity_id = :entityId "
                + "   and p.payment_date >= :start"
                + "   and p.payment_date <= :end "
                + "   ORDER by DESC p.payment_date";

        List<Integer> data = getSession()
                .createQuery(hql)
                .setParameter("entityId", entityId)
                .setParameter("start", start)
                .setParameter("end", end)
                .list();
        return data;
    }

    // used for the web services call to get the X by period for user
    public List<Integer> findIdsByPeriodForUser(Integer userID, Date start, Date end) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class)
                .add(Restrictions.eq("deleted", 0))
                .add(Restrictions.eq("isPreauth", 0))
                .createAlias("baseUser", "u")
                .add(Restrictions.eq("u.id", userID))
                .add(Restrictions.ge("paymentDate", start))
                .add(Restrictions.lt("paymentDate", end))
                .setProjection(Projections.id()).addOrder(Order.desc("paymentDate"));
        return criteria.list();
    }

    public PaymentDTO create(BigDecimal amount, PaymentMethodDTO paymentMethod,
            Integer userId, Integer attempt, PaymentResultDTO paymentResult,
            CurrencyDTO currency) {

        PaymentDTO payment = new PaymentDTO();
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setBaseUser(new UserDAS().find(userId));
        payment.setAttempt(attempt);
        payment.setPaymentResult(paymentResult);
        payment.setCurrency(new CurrencyDAS().find(currency.getId()));
        payment.setCreateDatetime(Calendar.getInstance().getTime());
        payment.setDeleted(new Integer(0));
        payment.setIsRefund(new Integer(0));
        payment.setIsPreauth(new Integer(0));

        return save(payment);

    }

    /**
     * * query="SELECT OBJECT(p) FROM payment p WHERE p.userId = ?1 AND
     * p.balance >= 0.01 AND p.isRefund = 0 AND p.isPreauth = 0 AND p.deleted =
     * 0"
     *
     * @param userId
     * @return
     */
    public Collection findWithBalance(Integer userId) {

        UserDTO user = new UserDAS().find(userId);

        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.ge("balance", Constants.BIGDECIMAL_ONE_CENT));
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.add(Restrictions.eq("isPreauth", 0));
        criteria.add(Restrictions.eq("deleted", 0));

        return criteria.list();
    }

    /**
     * Net revenue for user
     *
     * @param userId
     * @return total revenue amount for user
     */
    public BigDecimal findTotalRevenueByUser(Integer userId) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                .add(Restrictions.eq("u.id", userId))
                .createAlias("paymentResult", "pr")
                .add(Restrictions.ne("pr.id", CommonConstants.PAYMENT_RESULT_FAILED));
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.setProjection(Projections.sum("amount"));
        criteria.setComment("PaymentDAS.findTotalRevenueByUser-Gross Receipts");

        BigDecimal grossReceipts = criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult();

        Criteria criteria2 = getSession().createCriteria(PaymentDTO.class);
        criteria2.add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                .add(Restrictions.eq("u.id", userId))
                .createAlias("paymentResult", "pr")
                .add(Restrictions.ne("pr.id", CommonConstants.PAYMENT_RESULT_FAILED));
        criteria2.add(Restrictions.eq("isRefund", 1));
        criteria2.setProjection(Projections.sum("amount"));
        criteria2.setComment("PaymentDAS.findTotalRevenueByUser-Gross Refunds");

        BigDecimal refunds = criteria2.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria2.uniqueResult();

        //net revenue = gross - all refunds
        return (grossReceipts.subtract(refunds));
    }

    public BigDecimal findTotalBalanceByUser(Integer userId) {
        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("deleted", 0))
                .createAlias("baseUser", "u")
                .add(Restrictions.eq("u.id", userId));
        criteria.add(Restrictions.ne("balance", BigDecimal.ZERO));
        criteria.setProjection(Projections.sum("balance"));
        criteria.setComment("PaymentDAS.findTotalBalanceByUser");

        return (criteria.uniqueResult() == null ? BigDecimal.ZERO : (BigDecimal) criteria.uniqueResult());
    }

    /**
     *
     * query="SELECT OBJECT(p) FROM payment p WHERE p.userId = ?1 AND p.balance
     * >= 0.01 AND p.isRefund = 0 AND p.isPreauth = 1 AND p.deleted = 0"
     *
     * @param userId
     * @return
     */
    public Collection<PaymentDTO> findPreauth(Integer userId) {

        UserDTO user = new UserDAS().find(userId);

        Criteria criteria = getSession().createCriteria(PaymentDTO.class);
        criteria.add(Restrictions.eq("baseUser", user));
        criteria.add(Restrictions.ge("balance", Constants.BIGDECIMAL_ONE_CENT));
        criteria.add(Restrictions.eq("isRefund", 0));
        criteria.add(Restrictions.eq("isPreauth", 1));
        criteria.add(Restrictions.eq("deleted", 0));

        return criteria.list();

    }
}
