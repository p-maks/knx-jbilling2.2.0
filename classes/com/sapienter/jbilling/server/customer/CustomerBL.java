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

package com.sapienter.jbilling.server.customer;

import java.sql.SQLException;

import sun.jdbc.rowset.CachedRowSet;

import com.sapienter.jbilling.server.list.ResultList;
import com.sapienter.jbilling.server.user.UserBL;
import com.sapienter.jbilling.server.user.db.CustomerDAS;
import com.sapienter.jbilling.server.user.db.CustomerDTO;
import com.sapienter.jbilling.server.util.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil
 */
public final class CustomerBL extends ResultList implements CustomerSQL {
    
    private CustomerDTO customer = null;
    //private final Logger LOG = Logger.getLogger(CustomerBL.class);
    
    public CustomerBL() {};
    
    public CustomerBL(Integer id) {
        customer = new CustomerDAS().find(id);
    }
    
    public CustomerDTO getEntity() {
        return customer;
    }

    public CachedRowSet getList(int entityID, Integer userRole,
            Integer userId) 
            throws SQLException, Exception{
        
        if(userRole.equals(Constants.TYPE_ROOT)) {
            prepareStatement(CustomerSQL.listRoot); 
            cachedResults.setInt(1,entityID);
        } else if(userRole.equals(Constants.TYPE_CLERK)) {
            prepareStatement(CustomerSQL.listClerk);
            cachedResults.setInt(1,entityID);
        } else if(userRole.equals(Constants.TYPE_PARTNER)) {
            prepareStatement(CustomerSQL.listPartner);
            cachedResults.setInt(1, entityID);
            cachedResults.setInt(2, userId.intValue());
        } else {
            throw new Exception("The user list for the type " + userRole + 
                    " is not supported");
        }
        
        execute();
        conn.close();
        return cachedResults;
    }

    // this is the list for the Customer menu option, where only
    // customers/partners are listed. Meant for the clients customer service
    public CachedRowSet getCustomerList(int entityID, Integer userRole,
            Integer userId) 
            throws SQLException, Exception {
        
        if(userRole.equals(Constants.TYPE_INTERNAL) || 
                userRole.equals(Constants.TYPE_ROOT) || 
                userRole.equals(Constants.TYPE_CLERK)) {
            prepareStatement(CustomerSQL.listCustomers);
            cachedResults.setInt(1,entityID);
        } else if(userRole.equals(Constants.TYPE_PARTNER)) {
            prepareStatement(CustomerSQL.listPartner);
            cachedResults.setInt(1, entityID);
            cachedResults.setInt(2, userId.intValue());
        } else {
            throw new Exception("The user list for the type " + userRole + 
                    " is not supported");
        }
        
        execute();
        conn.close();
        return cachedResults;
    }
    
    public CachedRowSet getSubAccountsList(Integer userId) 
            throws SQLException, Exception {
        
        // find out the customer id of this user
        UserBL user = new UserBL(userId);
        
        prepareStatement(CustomerSQL.listSubaccounts);
        cachedResults.setInt(1,user.getEntity().getCustomer().getId());
        
        execute();
        conn.close();
        return cachedResults;
    }
    
    /**
     * Search for customers only, including sub-accounts and checks only primary
     * contact for user. Fields that are compared to: email, organisation, first
     * name, last name and login name.
     *
     * @param entityId the entity id that users belong to
     * @param searchValue the string value to search for
     * @return
     */
    public CachedRowSet searchCustomer(Integer entityId, String searchValue) throws SQLException, Exception {

        prepareStatement(CustomerSQL.searchCustomer);
        cachedResults.setInt(1, entityId.intValue());
        cachedResults.setString(2, searchValue);
        cachedResults.setString(3, searchValue);
        cachedResults.setString(4, searchValue);
        cachedResults.setString(5, searchValue);
        cachedResults.setString(6, searchValue);
        execute();
        conn.close();
        return cachedResults;
    }
    
    /**
     * Get a list of user ids in given status
     *
     * @param entityID
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public Integer[] getCustomersInStatus(Integer statusId, Integer entityId)
            throws SQLException, Exception {

        prepareStatement(CustomerSQL.findCustomersInStatus);
        cachedResults.setInt(1, statusId);
        cachedResults.setInt(2, entityId);
        execute();
        conn.close();

        // get ids for return
        List ids = new ArrayList();
        while (cachedResults.next()) {
            ids.add(new Integer(cachedResults.getInt(1)));
        }
        Integer[] retValue = new Integer[ids.size()];
        if (retValue.length > 0) {
            ids.toArray(retValue);
        }

        return retValue;
    }

}
