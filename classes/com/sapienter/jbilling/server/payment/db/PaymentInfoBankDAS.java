/*
 jBilling - The Enterprise Open Source Billing System
 Copyright (C) 2003-2009 Enterprise jBilling Software Ltd. and Emiliano Conde

 This file is part of jBilling.

 jBilling is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 jBilling is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with jBilling.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sapienter.jbilling.server.payment.db;

import com.sapienter.jbilling.server.util.db.AbstractDAS;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author P Maksymchuk
 *
 */
public class PaymentInfoBankDAS extends AbstractDAS<PaymentInfoBankDTO> {

    public PaymentInfoBankDTO create() {

        return new PaymentInfoBankDTO();
    }

    public PaymentInfoBankDTO findByPayment(PaymentDTO payment) {
        Criteria criteria = getSession().createCriteria(PaymentInfoBankDTO.class);
        criteria.add(Restrictions.eq("payment", payment));
        return (PaymentInfoBankDTO) criteria.uniqueResult();
    }
}
