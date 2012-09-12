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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@TableGenerator(
        name = "payment_info_cash_GEN",
table = "jbilling_seqs",
pkColumnName = "name",
valueColumnName = "next_id",
pkColumnValue = "payment_info_cash",
allocationSize = 100)
@Table(name = "payment_info_cash")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
/**
 *
 * @author Pavlo Maksymchuk 11 Sep 2012
 *
 */
public class PaymentInfoCashDTO implements Serializable {

    private int id;
    private PaymentDTO payment;
    private String customerRef;
    private Date date;
    private int versionNum;

    public PaymentInfoCashDTO() {
    }

    public PaymentInfoCashDTO(int id) {
        this.id = id;
    }

    public PaymentInfoCashDTO(int id, PaymentDTO payment, String customerRef,
            Date cashDate) {
        this.id = id;
        this.payment = payment;
        this.customerRef = customerRef;
        this.date = cashDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "payment_info_cash_GEN")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    public PaymentDTO getPayment() {
        return this.payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
    }

    @Column(name = "cust_ref", length = 50)
    public String getCustomerRef() {
        return this.customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    @Column(name = "cash_date", length = 13)
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date chequeDate) {
        this.date = chequeDate;
    }

    @Version
    @Column(name = "OPTLOCK")
    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }
}