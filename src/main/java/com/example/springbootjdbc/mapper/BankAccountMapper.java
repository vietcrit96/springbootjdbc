package com.example.springbootjdbc.mapper;

import com.example.springbootjdbc.model.BankAccountInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccountMapper implements RowMapper<BankAccountInfo> {
    public static final String BASE_SQL = "Select ba.Id, ba.Full_Name, ba.Balance From Bank_Account ba";
    @Override
    public BankAccountInfo mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        long id = resultSet.getLong("Id");
        String fullName = resultSet.getString("Full_Name");
        double balance = resultSet.getDouble("Balance");

        return new BankAccountInfo(id,fullName,balance);
    }
}
