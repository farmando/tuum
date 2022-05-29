package com.tuum.interview.mapper;

import com.tuum.interview.model.Currency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.Optional;

@Mapper
public interface CurrencyMapper {
    @Select("Select * from currency where id =#{id}")
    Optional<Currency> findById(String id);
}
