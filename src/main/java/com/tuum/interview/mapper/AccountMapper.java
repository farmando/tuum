package com.tuum.interview.mapper;

import com.tuum.interview.model.Account;
import com.tuum.interview.model.Balance;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface AccountMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO account(customer_id, country) VALUES(#{customer_id}, #{country})")
    Long insert(Account account);

    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    @Select("SELECT id, customer_id, country FROM account where id=#{id}")
    @Results(value = {
            @Result(property="id", column = "id"),
            @Result(property="customer_id", column = "customer_id"),
            @Result(property="balanceList", column="id", javaType= Set.class, many = @Many(select = "getBalance"))
    })
    Optional<Account> findById(Long id);

    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    @Select("SELECT * FROM balance WHERE account_id = #{account_id}")
    @Results(value={
            @Result(property="account_id", column ="account_id" ),
            @Result(property="currency_id", column = "currency_id"),
            @Result(property="balance", column = "balance")
    })
    List<Balance> getBalance(Long account_id);
}
