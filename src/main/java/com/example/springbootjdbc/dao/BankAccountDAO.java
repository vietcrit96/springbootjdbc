package com.example.springbootjdbc.dao;

import com.example.springbootjdbc.exception.BankTransactionException;
import com.example.springbootjdbc.mapper.BankAccountMapper;
import com.example.springbootjdbc.model.BankAccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.prefs.BackingStoreException;

@Repository
@Transactional
public class BankAccountDAO extends JdbcDaoSupport {
    @Autowired
    public BankAccountDAO(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    public List<BankAccountInfo> getBankAccount() {
        String sql = BankAccountMapper.BASE_SQL;
        Object[] params = new Object[]{};
        BankAccountMapper mapper = new BankAccountMapper();
        List<BankAccountInfo> list = this.getJdbcTemplate().query(sql, params, mapper);
        return list;
    }

    public BankAccountInfo findBankAccount(Long id) {
        String sql = BankAccountMapper.BASE_SQL + "where ba.Id= ?";
        Object[] params = new Object[]{id};
        BankAccountMapper mapper = new BankAccountMapper();
        try {
            BankAccountInfo bankAccountInfo = this.getJdbcTemplate().queryForObject(sql, params, mapper);
            return bankAccountInfo;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    //    giao dich bat buoc phai duoc tao san
    @Transactional(propagation = Propagation.MANDATORY)
    public void addAmount(long id, double amount) throws BankTransactionException {
        BankAccountInfo bankAccountInfo = this.findBankAccount(id);
        if (bankAccountInfo==null) {
            throw new BankTransactionException("Account not found " + id);
        }
        double newBalance = bankAccountInfo.getBalance() + amount;
        if (bankAccountInfo.getBalance() + amount < 0) {
            throw new BankTransactionException("The money in the account" +id+ "is not enough ("+ bankAccountInfo.getBalance()+")");
        }
        bankAccountInfo.setBalance(newBalance);
        String sqlUpdate = "Update Bank_Account set Balance= ? where Id=?";
        this.getJdbcTemplate().update(sqlUpdate, bankAccountInfo.getBalance(), bankAccountInfo.getId());
    }
    // Không được bắt BankTransactionException trong phương thức này.
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = BankTransactionException.class)
    public void sendMoney(Long fromAccountId,Long toAccountId,double amount)throws BankTransactionException {
        addAmount(toAccountId,amount);
        addAmount(fromAccountId,-amount);
    }
}
