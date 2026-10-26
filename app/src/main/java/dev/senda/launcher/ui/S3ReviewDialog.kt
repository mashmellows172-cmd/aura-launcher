package dev.senda.launcher.ui
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.icons.s3AppArtwork

@Composable
fun S3ReviewDialog(iconSize:Int=42,onDismiss:()->Unit) {
    val context=LocalContext.current
    var rows by remember{mutableStateOf<List<S3ReviewEntry>>(emptyList())}
    var error by remember{mutableStateOf<String?>(null)}
    var pending by remember{mutableStateOf(false)}
    var detail by remember{mutableStateOf<S3ReviewEntry?>(null)}
    LaunchedEffect(Unit){try{rows=S3ReviewCatalog.load(context)}catch(e:Exception){if(e is kotlinx.coroutines.CancellationException)throw e;error="No se pudo cargar el catálogo"}}
    val shown=remember(rows,pending){if(pending)rows.filter{it.status=="PENDING"}else rows}
    val size=iconSize.coerceIn(32,60).dp
    AuraAlertDialog(onDismissRequest=onDismiss,title={Text("Galaxy SIII · revisión manual")},text={Column {
        Text("${rows.count{it.status!="PENDING"}} disponibles · ${rows.count{it.status=="PENDING"}} pendientes")
        Row(verticalAlignment=Alignment.CenterVertically){Switch(pending,{pending=it});Text("Solo pendientes")}
        error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        LazyVerticalGrid(GridCells.Adaptive(92.dp),Modifier.fillMaxWidth().heightIn(max=390.dp)) {
            items(shown,key={it.packageName},contentType={"s3-review"}){entry->
                TextButton(onClick={detail=entry},modifier=Modifier.heightIn(min=100.dp)) {
                    Column(horizontalAlignment=Alignment.CenterHorizontally){
                        val resource=remember(entry.packageName){s3AppArtwork(entry.packageName)}
                        if(resource!=null)Image(painterResource(resource),null,Modifier.size(size))else Box(Modifier.size(size),contentAlignment=Alignment.Center){Text("?")}
                        Text(entry.label,maxLines=2,overflow=TextOverflow.Ellipsis,style=MaterialTheme.typography.labelSmall)
                        Text(if(resource==null)"Pendiente" else if(entry.kind=="AI")"Interpretación IA" else "Referencia de época",style=MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }},confirmButton={TextButton(onClick=onDismiss){Text("Cerrar")}})
    detail?.let{entry->AuraAlertDialog(onDismissRequest={detail=null},title={Text(entry.label)},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
        Text(entry.packageName);Text(entry.confidence);if(entry.reference.isNotBlank())Text(entry.reference,style=MaterialTheme.typography.bodySmall)
        Text("La revisión de este catálogo no sustituye iconos ni aprueba recursos automáticamente.",style=MaterialTheme.typography.bodySmall)
    }},confirmButton={TextButton(onClick={detail=null}){Text("Volver")}})}
}
