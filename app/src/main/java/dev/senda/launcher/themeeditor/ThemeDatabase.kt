package dev.senda.launcher.themeeditor

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName="theme_variants")
data class ThemeVariantEntity(@PrimaryKey val id:String,val name:String,val baseTheme:String,val payloadVersion:Int,val snapshotJson:String,val updatedAt:Long)
@Entity(tableName="theme_drafts")
data class ThemeDraftEntity(@PrimaryKey val id:String="editor",val snapshotJson:String,val updatedAt:Long)
@Entity(tableName="active_variant")
data class ActiveVariantEntity(@PrimaryKey val id:Int=1,val variantId:String?)
@Dao
interface ThemeDao {
    @Query("SELECT * FROM theme_variants ORDER BY updatedAt DESC") fun variants():Flow<List<ThemeVariantEntity>>
    @Query("SELECT v.* FROM theme_variants v INNER JOIN active_variant a ON a.variantId=v.id WHERE a.id=1") fun active():Flow<ThemeVariantEntity?>
    @Query("SELECT * FROM theme_drafts WHERE id='editor'") suspend fun draft():ThemeDraftEntity?
    @Query("SELECT v.* FROM theme_variants v INNER JOIN active_variant a ON a.variantId=v.id WHERE a.id=1") suspend fun activeOnce():ThemeVariantEntity?
    @Transaction suspend fun updateAppearance(text:Int,icon:Int,spacing:Int):Boolean {
        val entity=activeOnce() ?: return false
        val snapshot=ThemeSnapshot.parse(entity.snapshotJson).copy(textSize=text,iconSize=icon,spacing=spacing).validated()
        save(entity.copy(snapshotJson=snapshot.json(),updatedAt=System.currentTimeMillis()));return true
    }
    @Transaction suspend fun updatePack(pack:String?):Boolean {
        val entity=activeOnce() ?: return false
        val snapshot=ThemeSnapshot.parse(entity.snapshotJson).copy(pack=pack).validated()
        save(entity.copy(snapshotJson=snapshot.json(),updatedAt=System.currentTimeMillis()));return true
    }
    @Upsert suspend fun save(entity:ThemeVariantEntity)
    @Upsert suspend fun saveDraft(entity:ThemeDraftEntity)
    @Upsert suspend fun setActive(entity:ActiveVariantEntity)
    @Query("DELETE FROM theme_drafts") suspend fun clearDraft()
    @Transaction suspend fun saveAndActivate(entity:ThemeVariantEntity) { save(entity);setActive(ActiveVariantEntity(variantId=entity.id));clearDraft() }
}
@Database(entities=[ThemeVariantEntity::class,ThemeDraftEntity::class,ActiveVariantEntity::class],version=1,exportSchema=true)
abstract class ThemeDatabase:RoomDatabase() {
    abstract fun themes():ThemeDao
    companion object {
        @Volatile private var instance:ThemeDatabase?=null
        fun get(context:Context):ThemeDatabase=instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext,ThemeDatabase::class.java,"theme-variants.db").build().also { instance=it }
        }
    }
}
