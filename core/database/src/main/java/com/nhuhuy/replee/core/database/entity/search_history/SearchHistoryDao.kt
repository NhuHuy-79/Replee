package com.nhuhuy.replee.core.database.entity.search_history

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao : BaseDao<SearchHistoryEntity> {

    @Query("SELECT searchResultId FROM search_history_table WHERE ownerId = :ownerId")
    suspend fun getSearchHistoryByOwner(ownerId: String): List<String>

    @Query(
        """
SELECT
    a.uid AS uid,
    a.name AS name,
    a.imageUrl AS imgUrl
FROM search_history_table s
JOIN accounts a
ON s.searchResultId = a.uid
WHERE s.ownerId = :ownerId
ORDER BY s.searchAt DESC
"""
    )
    fun getSearchHistory(ownerId: String): Flow<List<SearchHistoryResult>>
}