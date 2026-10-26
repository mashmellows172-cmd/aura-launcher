package dev.senda.launcher.data
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class S3ReviewEntry(val packageName:String,val label:String,val status:String,val kind:String,val reference:String,val confidence:String,val sha256:String?)
object S3ReviewCatalog {
    suspend fun load(context:Context):List<S3ReviewEntry> = withContext(Dispatchers.IO) {
        val text=context.assets.open("s3-review.json").bufferedReader().use{it.readText()};require(text.length<1_000_000)
        val root=JSONObject(text);require(root.getInt("schema")==1);val rows=root.getJSONArray("entries");require(rows.length()<=500)
        (0 until rows.length()).map{i->val r=rows.getJSONObject(i);S3ReviewEntry(r.getString("package"),r.getString("label"),r.getString("status"),r.getString("kind"),r.getString("reference"),r.getString("confidence"),if(r.isNull("sha256"))null else r.getString("sha256"))}
    }
}
