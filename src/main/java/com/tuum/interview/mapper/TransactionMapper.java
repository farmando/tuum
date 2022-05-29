package com.tuum.interview.mapper;

import com.tuum.interview.model.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TransactionMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO transaction(account_id, trx_controller, amount, direction, currency_id, description) VALUES(#{account_id}, #{trx_controller}, #{amount}, #{direction}, #{currency_id}, #{description})")
    Long insert(Transaction trx);

    @Select("Select * from transaction where trx_controller =#{trx_controller}")
    Optional<Transaction> findByTrxController(String trx_controller);

    @Select("Select * from transaction where account_id =#{account_id}")
    List<Transaction> findByAccountId(long account_id);
}
