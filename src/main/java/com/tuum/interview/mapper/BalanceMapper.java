package com.tuum.interview.mapper;

import com.tuum.interview.model.Balance;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.Optional;

@Mapper
public interface BalanceMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO balance(account_id, currency_id, balance) VALUES(#{account_id}, #{currency_id}, #{balance})")
    Long insert(Balance balance);

    @Select("SELECT * FROM balance WHERE account_id=#{account_id} AND currency_id=#{currency_id}")
    Optional<Balance> findByAccountIdAndCurrency(long account_id, String currency_id);

    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    @Update("UPDATE balance SET balance = balance + #{amount} WHERE account_id = #{account_id} and currency_id = #{currency_id}")
    void update(long account_id, BigDecimal amount, String currency_id);
}
