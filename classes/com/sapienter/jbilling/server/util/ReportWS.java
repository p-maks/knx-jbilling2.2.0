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

/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sapienter.jbilling.server.util;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Pavlo Maksymchuk
 */
public class ReportWS implements Serializable {

    private int id;
    private String organizationName;
    private String lastName;
    private String firstName;
    private String username;
    private BigDecimal totalAsDecimal;
    private BigDecimal balanceAsDecimal;
    private String currencySymbol;
    private Date createDateTime;
    private int intValue;// free field to use with report

    /**
     *
     */
    public ReportWS() {
        super();
    }

    public ReportWS(Integer id, String organizationName,
            String lastName, String firstName, String username,
            BigDecimal totalAsDecimal, BigDecimal balanceAsDecimal, String currencySymbol, Date createDateTime) {
        this.id = id;
        this.organizationName = organizationName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.totalAsDecimal = totalAsDecimal;
        this.balanceAsDecimal = balanceAsDecimal;
        this.currencySymbol = currencySymbol;
        this.createDateTime = createDateTime;
    }     
    
    public ReportWS(Integer id, String organizationName,
            String lastName, String firstName, String username,
            BigDecimal totalAsDecimal, BigDecimal balanceAsDecimal, String currencySymbol, Date createDateTime, int intValue) {
        this.id = id;
        this.organizationName = organizationName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.totalAsDecimal = totalAsDecimal;
        this.balanceAsDecimal = balanceAsDecimal;
        this.currencySymbol = currencySymbol;
        this.createDateTime = createDateTime;
        this.intValue = intValue;
    }  
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the totalAsDecimal
     */
    public BigDecimal getTotalAsDecimal() {
        return totalAsDecimal;
    }

    /**
     * @param totalAsDecimal the totalAsDecimal to set
     */
    public void setTotalAsDecimal(BigDecimal totalAsDecimal) {
        this.totalAsDecimal = totalAsDecimal;
    }

    /**
     * @return the balanceAsDecimal
     */
    public BigDecimal getBalanceAsDecimal() {
        return balanceAsDecimal;
    }

    /**
     * @param balanceAsDecimal the balanceAsDecimal to set
     */
    public void setBalanceAsDecimal(BigDecimal balanceAsDecimal) {
        this.balanceAsDecimal = balanceAsDecimal;
    }

    /**
     * @return the currencySymbol
     */
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    /**
     * @param currencySymbol the currencySymbol to set
     */
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    /**
     * @return the createDateTime
     */
    public Date getCreateDateTime() {
        return createDateTime;
    }

    /**
     * @param createDateTime the createDateTime to set
     */
    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }
    
    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

   
}
