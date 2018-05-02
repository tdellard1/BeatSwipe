package com.example.android.beatswipe.Model;

import java.util.List;

public interface BeatDao {

    @Query("SELECT * FROM beat WHERE beatType LIKE :typeBeat")
    List<Beat> getTypeBeat(String typeBeat);

    void 
}
