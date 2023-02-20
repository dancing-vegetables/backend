package com.dv.dancingvegetables.repository;

import com.dv.dancingvegetables.domain.Lank;
import com.dv.dancingvegetables.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface LankRepository extends JpaRepository<Lank,Long> {
    Optional<Lank> findByMember(Member member);

    @Query(value ="select * from lank l order by lank_point desc limit 0,10;", nativeQuery = true)
    Optional<ArrayList<Lank>> LankTen();


}
